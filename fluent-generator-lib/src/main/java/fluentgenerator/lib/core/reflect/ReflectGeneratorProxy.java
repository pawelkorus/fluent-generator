package fluentgenerator.lib.core.reflect;

import com.fasterxml.jackson.databind.JsonNode;
import fluentgenerator.core.Generator;
import fluentgenerator.core.GeneratorFactory;
import fluentgenerator.lib.core.GeneratorException;
import fluentgenerator.supplier.StaticValueSupplier;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides implementation for generator interfaces.
 * <p>
 * It is used by {@link GeneratorFactory} when creating instances implementing {@link Generator} interface.
 * <p>
 * Every method that has exactly one parameter is considered as setter method. Invoked method name is used to create
 * setter name for generated object. This setter is then executed when creating object with {@link Generator#build()}.
 * Declarations of these methods may also specify that generator instance is returned. It is useful for chaining
 * invocations.
 * <p>
 * Generator interface may also define {@code constructor()} method. This method gets one argument of type {@code
 * Supplier<T>} which is used in order to provide instance of generated object. This may be used when generating of
 * other objects than beans which doesn't have no-arg constructor or needs some additional initialization. Generated
 * instance is created just after executing generator {@link Generator#build()} method and before calling any setter
 * method.
 *
 * @see Generator
 * @see GeneratorFactory
 */
public class ReflectGeneratorProxy implements InvocationHandler {

	private final Class<?> currentInterface;
	private final Map<Property, Supplier<Object>> propertySuppliers = new HashMap<>();
	private Optional<ConstructorStrategy> constructorStrategy = Optional.empty();
	private Map<Class<?>, Supplier<GenerationVisitor>> targetTypeToVisitor = new HashMap<>();

	public ReflectGeneratorProxy(Class<?> currentInterface) {
		this.currentInterface = currentInterface;

		if(isPresent("com.fasterxml.jackson.databind.JsonNode")) {
			targetTypeToVisitor.put(JsonNode.class, JsonNodeObjectGenerationVisitor::new);
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws InstantiationException, InvocationTargetException, IllegalAccessException {
		String methodName = method.getName();

		if (methodName.compareTo("build") == 0
			|| methodName.compareTo("get") == 0) {

			if(args == null || args.length == 0) {
				Class<?> targetType = inferTargetClass(currentInterface);
				return invokeBuild(targetType);
			}

			if(args[0] instanceof Class) {
				Class targetType = (Class) args[0];
				return invokeBuild(targetType);
			}

			throw buildCantHandleBuildRequest(currentInterface, methodName, args);

		} else if (methodName.compareTo("constructor") == 0) {

			return invokeContructor(proxy, method, args);

		} else if (method.isDefault()) {

			return invokeDefaultMethod(proxy, method, args);

		} else {
			if (args == null || args.length != 1) {
				throw new GeneratorException(currentInterface, "Setter method should have exactly one parameter");
			}

			invokeSetter(methodName, method.getParameterTypes()[0], args[0]);
			return negotiateReturnValue(proxy, method, args);
		}
	}

	private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) {
		try {
			Constructor<MethodHandles.Lookup> LOOKUP_CONSTRUCTOR =
				MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
			if (!LOOKUP_CONSTRUCTOR.isAccessible()) {
				LOOKUP_CONSTRUCTOR.setAccessible(true);
			}

			return LOOKUP_CONSTRUCTOR.newInstance(method.getDeclaringClass(), MethodHandles.Lookup.PRIVATE)
				.unreflectSpecial(method, method.getDeclaringClass())
				.bindTo(proxy)
				.invokeWithArguments(args);
		} catch (Throwable throwable) {
			throw buildCantExecuteDefaultMethod(currentInterface, method.getName(), args, throwable);
		}
	}

	private void invokeSetter(String fieldName, Class<?> parameterType, Object value) {
		if (Supplier.class.isAssignableFrom(parameterType)) {
			storePropertyValueSupplier(fieldName, parameterType, (Supplier) value);
		} else {
			storePropertyValueSupplier(fieldName, parameterType, StaticValueSupplier.build(value));
		}
	}

	private Object invokeBuild(Class<?> targetType) {
		GenerationVisitor visitor = null;

		for(Class<?> mappingKey : targetTypeToVisitor.keySet()) {
			if(mappingKey.isAssignableFrom(targetType)) {
				visitor = targetTypeToVisitor.get(mappingKey).get();
			}
		}
		if(visitor == null) {
			visitor = new ObjectGenerationVisitor(targetType, currentInterface);
		}

		visitor.acceptConstructorStrategy(constructorStrategy.orElse(new DefaultConstructorStrategy(targetType)));

		for(Property p : propertySuppliers.keySet()) {
			visitor.acceptPropertySupplier(p, propertySuppliers.get(p));
		}

		return visitor.finishObject();
	}

	private Object invokeContructor(Object proxy, Method method, Object[] args) {
		if (args == null || args.length != 1) {
			throw new GeneratorException(currentInterface, "Constructor method should be called with exactly one parameter of type Supplier<Object>");
		}

		Class<?> argType = args[0].getClass();
		if (Supplier.class.isAssignableFrom(argType) == false) {
			throw new GeneratorException(currentInterface, "Constructor method should be called with exactly one parameter of type Supplier<Object>");
		}

		Supplier<Object> supplier = (Supplier<Object>) args[0];

		constructorStrategy = Optional.of(new SupplierConstructorStrategy(supplier));

		return null;
	}

	private Object negotiateReturnValue(Object proxy, Method method, Object[] args) {
		Class<?> returnType = method.getReturnType();

		if (returnType == currentInterface) {
			return proxy;
		} else if (returnType == Object.class) {
			return proxy;
		} else {
			return null;
		}
	}

	private void storePropertyValueSupplier(
		String propertyName, Class<?> propertyType, Supplier<Object> valueProvider) {
		propertySuppliers.put(Property.build(propertyName, propertyType), valueProvider);
	}

	private Class<?> inferTargetClass(Class<?> currentInterface) {
		Class<?> targetClass = Object.class;

		try {
			// Only build method defined in generator interface provides hints about
			// target type.
			Method buildMethod = currentInterface.getMethod("build");
			if (buildMethod.getReturnType() != Object.class) {
				targetClass = buildMethod.getReturnType();
			}
		} catch (NoSuchMethodException | SecurityException ex) {
			// there is no possibility to find build method and infer
			// generated object type.
			throw buildCantFindBuildMethodException(this.currentInterface, ex);
		}

		// This checks if return type was inferred correctly from provided
		// generator interface. If not then this will lead almost
		// for sure to other problems later on, it is worth to report
		// exception.
		if(targetClass == Object.class) {
			throw buildCantInferReturnTypeException(this.currentInterface);
		}

		return targetClass;
	}

	private class DefaultConstructorStrategy implements ConstructorStrategy {
		private final Class<?> _classs;

		DefaultConstructorStrategy(Class<?> classs) {
			_classs = classs;
		}

		@Override
		public Object get() throws
			InstantiationException, InvocationTargetException,
			IllegalAccessException {
			Object o = _classs.newInstance();
			return o;
		}
	}

	private class SupplierConstructorStrategy implements ConstructorStrategy {
		private final Supplier<Object> _supplier;

		public SupplierConstructorStrategy(Supplier<Object> supplier) {
			this._supplier = supplier;
		}

		@Override
		public Object get() throws
			InstantiationException, InvocationTargetException,
			IllegalAccessException {
			Object o = _supplier.get();
			if (o == null) {
				StringBuilder b = new StringBuilder();
				b.append("Error when instantiating object instance.")
					.append(" Provided supplier method returned null.");
				throw new InstantiationException(b.toString());
			}
			return o;
		}
	}

	private static GeneratorException buildCantInferReturnTypeException(Class<?> genIface) {
		StringBuilder b = new StringBuilder();
		b.append("Can't infer model type created by this generator. Is build method for generator interface declared?");
		return new GeneratorException(genIface, b.toString());
	}

	private static GeneratorException buildCantFindBuildMethodException(Class<?> genIface, Exception cause) {
		StringBuilder b = new StringBuilder();
		b.append("Can't find build method in provided generator interface. Does provided interface extends ")
			.append(Generator.class.toString()).append(" interface");
		return new GeneratorException(genIface, b.toString(), cause);
	}

	private static GeneratorException buildCantHandleBuildRequest(Class<?> genIface,
																  String buildMethodName, Object[] args) {
		StringBuilder b = new StringBuilder();
		b.append("Can't handle build request. Unsupported call to ").append(buildMethodName).append(" with arguments ")
			.append(Optional.ofNullable(args).map(Arrays::toString).orElse("no arguments"));
		return new GeneratorException(genIface, "Can't handle build request. Unsupported call");
	}

	private static GeneratorException buildCantExecuteDefaultMethod(Class<?> genIface,
																	String methodName, Object[] args, Throwable cause) {
		StringBuilder b = new StringBuilder();
		b.append("Can't execute default method ").append(methodName).append(" with arguments ")
			.append(Optional.ofNullable(args).map(Arrays::toString).orElse("no arguments"));
		return new GeneratorException(genIface, b.toString(), cause);
	}

	public static boolean isPresent(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (Throwable ex) {
			// Class or one of its dependencies is not present...
			return false;
		}
	}
}
