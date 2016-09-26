package fluentgenerator.lib.core.reflect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JsonNodeObjectGenerationVisitor implements GenerationVisitor {

	private Map<String, Object> values = new HashMap<>();
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void acceptConstructorStrategy(ConstructorStrategy constructorStrategy) {
		values.clear();
	}

	@Override
	public void acceptPropertySupplier(Property property, Supplier<Object> supplier) {
		values.put(property.getName(), supplier.get());
	}

	@Override
	public Object finishObject() {
		JsonNode node = mapper.convertValue(values, JsonNode.class);
		return node;
	}

}
