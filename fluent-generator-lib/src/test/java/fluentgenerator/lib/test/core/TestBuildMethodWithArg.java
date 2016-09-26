package fluentgenerator.lib.test.core;

import com.fasterxml.jackson.databind.JsonNode;
import fluentgenerator.lib.core.Generator;
import fluentgenerator.lib.core.GeneratorFactory;
import fluentgenerator.lib.core.reflect.ReflectGeneratorProxyFactory;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestBuildMethodWithArg {

	private GeneratorFactory genFactory = new ReflectGeneratorProxyFactory();
	private ModelGenerator gen = null;
	private static final String TEST_STRING = "alamakota";

	public static class Model {
		private String testString;

		public String getTestString() {
			return testString;
		}

		public void setTestString(String v) {
			this.testString = v;
		}
	}

	public interface ModelGenerator extends Generator<Model> {
		ModelGenerator testString(String v);
		@Override Model build();
	}

	@Before
	public void setup() {
		gen = genFactory.generatorInstance(ModelGenerator.class);
	}

	@Test
	public void callBuildMethodWithModelClassArgument() {
		gen.testString(TEST_STRING);
		Model m = gen.build(Model.class);

		assertThat(m.getTestString()).isEqualTo(TEST_STRING);
	}

	@Test
	public void callBuildMethodWithJsonNodeArgument() {
		gen.testString(TEST_STRING);
		JsonNode n = gen.build(JsonNode.class);

		assertThat(n.get("testString").asText()).isEqualTo(TEST_STRING);
	}

}
