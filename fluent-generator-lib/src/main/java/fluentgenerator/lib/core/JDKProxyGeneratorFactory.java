package fluentgenerator.lib.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Creates generator interface implementations using Java Dynamic Proxies and {@link GeneratorProxy} as a backend
 * class.
 *
 * @author pkorus
 * @see Generator
 * @see GeneratorProxy
 */
public class JDKProxyGeneratorFactory implements GeneratorFactory {

	/**
	 * Creates generator implementation for a given generator interface
	 *
	 * @param generatorClass generator interface class
	 * @param <T>            generator interface
	 * @return new instance of given class
	 */
	@Override
	public <T> T generatorInstance(Class<T> generatorClass) {
		InvocationHandler handler = new GeneratorProxy(generatorClass);

		T obj = (T) Proxy.newProxyInstance(generatorClass.getClassLoader(),
			new Class<?>[]{generatorClass},
			handler);

		return (T) obj;
	}

}
