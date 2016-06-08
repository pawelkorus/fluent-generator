package fluentgenerator.json;

import fluentgenerator.lib.core.GeneratorFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class JacksonGeneratorProxyFactory implements GeneratorFactory {

	@Override
	public <T> T generatorInstance(Class<T> generatorClass) {
		InvocationHandler handler = new JacksonGeneratorProxy(generatorClass);

		T obj = (T) Proxy.newProxyInstance(generatorClass.getClassLoader(),
			new Class<?>[]{generatorClass},
			handler);

		return (T) obj;
	}

}
