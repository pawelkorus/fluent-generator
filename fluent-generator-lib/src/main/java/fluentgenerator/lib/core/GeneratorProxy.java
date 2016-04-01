package fluentgenerator.lib.core;

import fluentgenerator.supplier.StaticValueSupplier;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

/**
 *
 * @author pkorus
 */
public class GeneratorProxy implements InvocationHandler {

	private final Collection<MethodInvocation> _invocations;
	private final Class<?> _currentInterface;
	private final Class<?> _targetClass;
	private ConstructorStrategy _constructorStrategy;

	public GeneratorProxy(Class<?> currentInterface) {
		_invocations = new ArrayList<>();
		_currentInterface = currentInterface;
		_targetClass = inferTargetClass(currentInterface);
		_constructorStrategy = new DefaultConstructorStrategy(_targetClass);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) 
		throws InstantiationException, InvocationTargetException, IllegalAccessException
	{
		String methodName = method.getName();

		if(methodName.compareTo("build") == 0 
			|| methodName.compareTo("get") == 0) {

			return invokeBuild(_targetClass);

		} else if(methodName.compareTo("constructor") == 0) {

			return invokeContructor(proxy, method, args);

        } else {
			if(args.length != 1) {
				throw new GeneratorException(_currentInterface, "Setter method should have exactly one parameter");
			}

			invokeSetter(methodName, method.getParameterTypes()[0], args[0]);
			return negotiateReturnValue(proxy, method, args);
		}
	}

	private void invokeSetter(String fieldName, Class<?> parameterType, Object value) {
		String methodName = fieldNameToSetterName(fieldName);
		
		if(Supplier.class.isAssignableFrom(parameterType)) {			
			storeMethodInvocation(methodName, (Supplier) value);
		} else {
			storeMethodInvocation(
				methodName, parameterType, StaticValueSupplier.build(value));
		}
	}

	private Object invokeBuild(Class<?> targetClass) {
		Object o = null;
		
		try {
			o = _constructorStrategy.get();
		} catch(InstantiationException | InvocationTargetException ex) {
			throw new GeneratorException(_currentInterface, "Can't instantiate object of type " + targetClass.toString(), ex);
		} catch(IllegalAccessException ex) {
			StringBuilder b = new StringBuilder();
			b.append("Can't instantiate object of type ")
			.append(targetClass.toString())
			.append(". There is no access to constructor.");
			throw new GeneratorException(_currentInterface, b.toString(), ex);
		}
		
		String currentMethodName = "";
		try {
			for (MethodInvocation mi : _invocations) {
				currentMethodName = mi.getMethodName();	
				mi.invoke(o);
			}
		} catch(IllegalAccessException ex) {
			StringBuilder b = new StringBuilder(); b
			.append("Can't execute method ").append(currentMethodName)
			.append(" . Method is inaccessible on instance of class ")
			.append(targetClass.toString()).append(" .");
			throw new GeneratorException(_currentInterface, b.toString(), ex);
		} catch(IllegalArgumentException ex) {
			StringBuilder b = new StringBuilder(); b
			.append("Can't execute method ").append(currentMethodName)
			.append(" . Invalid argument.");
			throw new GeneratorException(_currentInterface, b.toString(), ex);
		} catch(InvocationTargetException ex) {
			StringBuilder b = new StringBuilder(); b
			.append("Can't execute method ").append(currentMethodName)
			.append(" on instance of class ").append(targetClass.toString())
			.append(" .");
			throw new GeneratorException(_currentInterface, b.toString(), ex);
		}
		
		return o;
	}

	private Object invokeContructor(Object proxy, Method method, Object[] args) {
		if(args.length != 1) {
			throw new GeneratorException(_currentInterface, "Constructor method should be called with exactly one parameter of type Supplier<Object>");
		}		
		
		Class<?> argType = args[0].getClass();
		if(Supplier.class.isAssignableFrom(argType) == false) {
			throw new GeneratorException(_currentInterface, "Constructor method should be called with exactly one parameter of type Supplier<Object>");
		}
		
		Supplier<Object> supplier = (Supplier<Object>) args[0];
		
		_constructorStrategy = new SupplierConstructorStrategy(supplier);
		
		return null;
	}
	
	private Object negotiateReturnValue(Object proxy, Method method, Object[] args) {
		Class<?> returnType = method.getReturnType();

		if(returnType == _currentInterface) {
			return proxy;
		} else if(returnType == Object.class) {
			return proxy;
		} else {
			return null;
		}
	}
	
	private void storeMethodInvocation(
		String methodName, Supplier<?> valueProvider) 
	{
		Method[] methods = _targetClass.getMethods();
		Class<?> argType = null;
		Method method = null;
		
		for (Method temp : methods) {
			if(temp.getName().compareTo(methodName) == 0
				&& temp.getParameterCount() == 1) 
			{
				method = temp;
				argType = temp.getParameterTypes()[0];
				break;
			}
		}
		
		if(argType == null || method == null) {
			StringBuilder b = new StringBuilder(); b
				.append("Can't infer argument type because method ")
				.append(methodName)
				.append(" that takes only one argument can't be found. ");

				throw new GeneratorException(_currentInterface, b.toString());
		}
		
		_invocations.add(
			MethodInvocation.build(method, argType, valueProvider));
	}
	
	private void storeMethodInvocation(
		String methodName, Class<?> argType, Supplier<?> valueProvider) 
	{
		Method method = methodFromClass(_targetClass, methodName, argType);
		_invocations.add(
			MethodInvocation.build(method, argType, valueProvider));
	}
	
	private Class<?> inferTargetClass(Class<?> currentInterface) {
		Class<?> targetClass = Object.class;
		
		try {
			Method buildMethod = currentInterface.getMethod("build");
			if( buildMethod.getReturnType() != Object.class ) {
				targetClass = buildMethod.getReturnType();
			}
		} catch(NoSuchMethodException | SecurityException ex) {
			// if exception was thrown we can't infer return type
			// from build method because it was not overriden in
			// generator interface. We lieave Object as a return
			// type, but it will propably lead to exception
		}
		
		return targetClass;
	}
	
	private String fieldNameToSetterName(String fieldName) {
		StringBuilder builder = new StringBuilder();
		return builder
			.append("set")
			.append(fieldName.substring(0, 1).toUpperCase())
			.append(fieldName.substring(1))
			.toString();
	}
	
	private Method methodFromClass(
		Class<?> source, String methodName, Class<?> argType) 
	{
		Method method = null;
		
		try {
			
			method = source.getMethod(methodName, argType);
		
		} catch(NoSuchMethodException ex) {
			StringBuilder b = new StringBuilder();
				b.append("Can't find method ")
				.append(methodName)
				.append(" which takes parameter ")
				.append(argType.toString());

				throw new GeneratorException(_currentInterface, b.toString(), ex);
		}
		
		return method;
	}
	
	
	private interface ConstructorStrategy {
		public Object get() throws 
			InstantiationException, InvocationTargetException, 
			IllegalAccessException;
	}
	
	private class DefaultConstructorStrategy implements ConstructorStrategy
	{
		private final Class<?> _classs;
		
		DefaultConstructorStrategy(Class<?> classs) {
			_classs = classs;
		}
		
		@Override
		public Object get() throws 
			InstantiationException, InvocationTargetException, 
			IllegalAccessException
		{
			Object o = _classs.newInstance();
			return o;
		}
	}
	
	private class SupplierConstructorStrategy implements ConstructorStrategy
	{
		private final Supplier<Object> _supplier;

		public SupplierConstructorStrategy(Supplier<Object> supplier) {
			this._supplier = supplier;
		}

		@Override
		public Object get() throws 
			InstantiationException, InvocationTargetException, 
			IllegalAccessException 
		{
			Object o = _supplier.get();
			if(o == null) {
				StringBuilder b = new StringBuilder();
				b.append("Error when instantiating object instance.")
				.append(" Provided supplier method returned null.");
				throw new InstantiationException(b.toString());
			}
			return o;
		}
		
		
	}
}