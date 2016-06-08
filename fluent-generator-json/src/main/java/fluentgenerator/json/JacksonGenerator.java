package fluentgenerator.json;

import com.fasterxml.jackson.databind.JsonNode;
import fluentgenerator.lib.core.Generator;

public interface JacksonGenerator extends Generator<JsonNode> {

	byte[] toBytes();

}
