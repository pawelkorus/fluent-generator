package fluentgenerator.lib.core;

import fluentgenerator.lib.generator.SupplierGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

/**
 *
 * @author pkorus
 */
public class GeneratorFactory {
	
	public <T> T generatorInstance(Class<T> generatorClass) {
		InvocationHandler handler = new GeneratorProxy(generatorClass);
		
		T obj = (T) Proxy.newProxyInstance(generatorClass.getClassLoader(), 
			new Class<?>[] { generatorClass }, 
			handler);
		
		return (T) obj;
	}
	
	public <T> Generator<T> supplierGenerator(Supplier<T> supplier) {
		return new SupplierGenerator<>(supplier);
	}
	
}
