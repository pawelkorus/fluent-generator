package fluentgenerator.lib.core.reflect;

import fluentgenerator.lib.core.GeneratorException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;

public class ObjectGenerationVisitor implements GenerationVisitor{

	private Class<?> targetClass;
	private Class<?> generatorClass;
	private Object constructedObject;

	public ObjectGenerationVisitor(Class<?> targetClass, Class<?> generatorClass) {
		this.targetClass = targetClass;
		this.generatorClass = generatorClass;
	}

	@Override
	public void acceptConstructorStrategy(ConstructorStrategy constructorStrategy) {
		try {
			constructedObject = constructorStrategy.get();
		} catch (InstantiationException | InvocationTargetException ex) {
			throw new GeneratorException(generatorClass, "Can't instantiate object of type " + targetClass.toString(), ex);
		} catch (IllegalAccessException ex) {
			StringBuilder b = new StringBuilder();
			b.append("Can't instantiate object of type ")
				.append(targetClass.toString())
				.append(". There is no access to constructor.");
			throw new GeneratorException(generatorClass, b.toString(), ex);
		}
	}

	@Override
	public void acceptPropertySupplier(Property property, Supplier<Object> supplier) {
		String setterName = fieldNameToSetterName(property.getName());

		Object v = supplier.get();

		Optional<Method> method = Optional.empty();

		try {
			method = Optional.of(methodFromClass(targetClass, setterName, property.getType()));
		} catch (NoSuchMethodException ex) {
			// do nothing
		}

		if(!method.isPresent()) {
			try {
				method = Optional.ofNullable(methodFromClass(targetClass, setterName, v.getClass()));
			} catch (NoSuchMethodException ex) {
				//do nothing
			}
		}

		try {
			method
				.orElseThrow(() -> {
					StringBuilder b = new StringBuilder();
					b.append("Can't find method ")
						.append(setterName)
						.append(" which takes parameter ")
						.append(property.getType().toString()).append(" or ")
						.append(v.getClass().toString());
					return new GeneratorException(generatorClass, b.toString());
				})
				.invoke(constructedObject, v);
		} catch (IllegalAccessException ex) {
			StringBuilder b = new StringBuilder();
			b
				.append("Can't execute method ").append(setterName)
				.append(" . Method is inaccessible on instance of class ")
				.append(targetClass.toString()).append(" .");
			throw new GeneratorException(generatorClass, b.toString(), ex);
		} catch (IllegalArgumentException ex) {
			StringBuilder b = new StringBuilder();
			b
				.append("Can't execute method ").append(setterName)
				.append(" . Invalid argument.");
			throw new GeneratorException(generatorClass, b.toString(), ex);
		} catch (InvocationTargetException ex) {
			StringBuilder b = new StringBuilder();
			b
				.append("Can't execute method ").append(setterName)
				.append(" on instance of class ").append(targetClass.toString())
				.append(" .");
			throw new GeneratorException(generatorClass, b.toString(), ex);
		}
	}

	@Override
	public Object finishObject() {
		return constructedObject;
	}

	private String fieldNameToSetterName(String fieldName) {
		StringBuilder builder = new StringBuilder();
		return builder
			.append("set")
			.append(fieldName.substring(0, 1).toUpperCase())
			.append(fieldName.substring(1))
			.toString();
	}

	private Method methodFromClass(Class<?> source, String methodName, Class<?> argType) throws NoSuchMethodException {
		return source.getMethod(methodName, argType);
	}
}
