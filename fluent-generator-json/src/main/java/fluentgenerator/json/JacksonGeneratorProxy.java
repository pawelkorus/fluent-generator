package fluentgenerator.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fluentgenerator.lib.core.GeneratorException;
import fluentgenerator.supplier.StaticValueSupplier;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JacksonGeneratorProxy implements InvocationHandler {

	private final Map<String, Supplier<?>> fieldValueProviders = new HashMap<>();
	private final Class<?> currentInterface;
	private final ObjectMapper mapper = new ObjectMapper();

	public JacksonGeneratorProxy(Class<?> currentInterface) {
		this.currentInterface = currentInterface;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String methodName = method.getName();

		if (methodName.compareTo("build") == 0
			|| methodName.compareTo("get") == 0) {

			return invokeBuild();

		} else if (methodName.compareTo("toBytes") == 0) {

			return invokeToBytes();

		} else if (methodName.compareTo("constructor") == 0) {

			throw new UnsupportedOperationException(methodName);

		} else {
			if (args.length != 1) {
				throw new GeneratorException(currentInterface, "Setter method should have exactly one parameter");
			}

			invokeSetter(methodName, method.getParameterTypes()[0], args[0]);
			return negotiateReturnValue(proxy, method, args);
		}
	}

	private void invokeSetter(String methodName, Class<?> parameterType, Object value) {
		Supplier<?> valueProvider;

		if (Supplier.class.isAssignableFrom(parameterType)) {
			valueProvider = (Supplier) value;
		} else {
			valueProvider = StaticValueSupplier.build(value);
		}

		fieldValueProviders.put(methodName, valueProvider);
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

	private JsonNode invokeBuild() {
		HashMap<String, Object> values = new HashMap<>();

		Collection<String> fieldKeys = fieldValueProviders.keySet();
		for(String key : fieldKeys) {
			Object v = fieldValueProviders.get(key).get();
			values.put(key, v);
		}

		JsonNode node = mapper.convertValue(values, JsonNode.class);
		return node;
	}

	private byte[] invokeToBytes() {
		JsonNode node = invokeBuild();

		byte[] bytes = {};
		try {
			bytes = mapper.writeValueAsBytes(node);
		} catch (JsonProcessingException ex) {
			throw cantConvertJsonNodeToBytes(ex);
		}
		return bytes;
	}

	private GeneratorException cantConvertJsonNodeToBytes(Exception cause) {
		return new GeneratorException(currentInterface, "Can't convert JsonNode object to bytes.", cause);
	}
}
