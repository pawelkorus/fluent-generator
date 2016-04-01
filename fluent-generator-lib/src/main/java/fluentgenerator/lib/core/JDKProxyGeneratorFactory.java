package fluentgenerator.lib.core;

import fluentgenerator.lib.generator.SupplierGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

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

	/**
	 * Creates instance of {@link Generator} which is an adapter for {@link Supplier} interface.
	 *
	 * @param supplier instance implementing {@link Supplier}
	 * @param <T>      type of object produced by given supplier object
	 * @return new instance of type {@link Generator}
	 */
	public <T> Generator<T> supplierGenerator(Supplier<T> supplier) {
		return new SupplierGenerator<>(supplier);
	}

}
