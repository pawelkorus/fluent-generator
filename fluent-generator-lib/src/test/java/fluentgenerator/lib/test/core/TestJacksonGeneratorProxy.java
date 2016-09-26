package fluentgenerator.lib.test.core;

import com.fasterxml.jackson.databind.JsonNode;
import fluentgenerator.lib.core.Generator;
import fluentgenerator.lib.core.GeneratorFactory;
import fluentgenerator.lib.core.reflect.ReflectGeneratorProxyFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class TestJacksonGeneratorProxy {

	private GeneratorFactory genFactory = new ReflectGeneratorProxyFactory();

	private ModelGenerator gen;

	private static interface ModelGenerator extends Generator<JsonNode> {
		ModelGenerator stringField(String v);
		ModelGenerator intField(Integer v);
		ModelGenerator longField(Long v);
		ModelGenerator doubleField(Double v);
		ModelGenerator booleanField(Boolean v);
		ModelGenerator customClass(CustomTestClass v);

		ModelGenerator stringSupplier(Supplier<String> sup);
		ModelGenerator jsonNodeSupplier(Supplier<JsonNode> sup);

		JsonNode build();
	}

	private static class CustomTestClass {
		public String testString;
		private Integer testInt;

		public int getTestInt() { return this.testInt; }
		public void setTestInt(int v) { this.testInt = v; }
	}

	private final String testString = "testStringssdf";
	private final Integer testInt = 5;
	private final Long testLong = 233432L;
	private final Double testDouble = 7.0;
	private final Boolean testBoolean = true;

	@Before
	public void setup() {
		gen = genFactory.generatorInstance(ModelGenerator.class);
	}

	@Test
	public void chaining() {
		ModelGenerator chainTest = gen.stringField(testString);
		assertThat(chainTest).isSameAs(gen);
	}

	@Test
	public void fieldSetters() {
		gen
			.stringField(testString).intField(testInt).longField(testLong).doubleField(testDouble)
			.booleanField(testBoolean);

		JsonNode o = gen.build();

		assertThat(o.get("stringField").asText()).isEqualTo(testString);
		assertThat(o.get("intField").asInt()).isEqualTo(testInt);
		assertThat(o.get("longField").asLong()).isEqualTo(testLong);
		assertThat(o.get("doubleField").asDouble()).isEqualTo(testDouble);
		assertThat(o.get("booleanField").asBoolean()).isEqualTo(testBoolean);
	}

	@Test
	public void supplier() {
		gen.stringSupplier(() -> testString);

		JsonNode o = gen.build();

		assertThat(o.get("stringSupplier").asText()).isEqualTo(testString);
	}

	@Test
	public void jsonNodeSupplier() {
		ModelGenerator subGen = genFactory.generatorInstance(ModelGenerator.class);
		subGen.stringField(testString);

		gen.intField(testInt);
		gen.jsonNodeSupplier(subGen);

		JsonNode o = gen.build();

		assertThat(o.get("intField").asInt()).isEqualTo(testInt);
		assertThat(o.get("jsonNodeSupplier").get("stringField").asText()).isEqualTo(testString);
	}

	@Test
	public void custromTestClass() {
		CustomTestClass testObject = new CustomTestClass();
		testObject.testString = testString;
		testObject.setTestInt(testInt);

		gen.customClass(testObject);

		JsonNode o = gen.build();

		assertThat(o.get("customClass").get("testString").asText()).isEqualTo(testString);
		assertThat(o.get("customClass").get("testInt").asInt()).isEqualTo(testInt);
	}
}
