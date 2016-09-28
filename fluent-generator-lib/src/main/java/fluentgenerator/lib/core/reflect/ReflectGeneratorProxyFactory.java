package fluentgenerator.lib.core.reflect;

import fluentgenerator.core.Generator;
import fluentgenerator.core.GeneratorFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Creates generator interface implementations using Java Dynamic Proxies and {@link ReflectGeneratorProxy} as a backend
 * class.
 *
 * @see Generator
 * @see ReflectGeneratorProxy
 */
public class ReflectGeneratorProxyFactory implements GeneratorFactory {

	/**
	 * Creates generator implementation for a given generator interface
	 *
	 * @param generatorClass generator interface class
	 * @param <T>            generator interface
	 * @return new instance of given class
	 */
	@Override
	public <T> T generatorInstance(Class<T> generatorClass) {
		InvocationHandler handler = new ReflectGeneratorProxy(generatorClass);

		T obj = (T) Proxy.newProxyInstance(generatorClass.getClassLoader(),
			new Class<?>[]{generatorClass},
			handler);

		return (T) obj;
	}

}
