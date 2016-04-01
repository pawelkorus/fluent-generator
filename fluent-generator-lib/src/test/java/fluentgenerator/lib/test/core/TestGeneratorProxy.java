package fluentgenerator.lib.test.core;

import fluentgenerator.lib.core.Generator;
import static org.mockito.Mockito.*;

import fluentgenerator.lib.core.GeneratorFactory;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author pkorus
 */
public class TestGeneratorProxy {
	
	private GeneratorFactory genFactory = new GeneratorFactory();
		
	private ModelGenerator gen;
	
	private static String TEST_VALUE_STRING = "alamakota";
	private static int TEST_VALUE_INT = 5;
	
	public interface Model {
		void setStringValue(String v);
		void setIntValue(int v);
		
		void setSomeValue(String v);

		void setValueX(String v);
	}
	
	public static class ModelImpl implements Model {
		public static Model e = mock(Model.class);

		public ModelImpl() {}
		
		@Override public void setStringValue(String v) { e.setStringValue(v); }
		@Override public void setIntValue(int v) { e.setIntValue(v); }
		@Override public void setSomeValue(String v) { e.setSomeValue(v); }
		@Override public void setValueX(String v) { e.setValueX(v); }
	}
	
	public interface ModelGenerator extends Generator<ModelImpl> {
		ModelGenerator constructor(Supplier<Object> v);
		ModelGenerator stringValue(String v);
		ModelGenerator stringValue(Supplier<String> v);
		ModelGenerator intValue(int v);
		ModelGenerator someValue(Supplier<String> v);
		void valueX(Supplier<String> v);
		@Override ModelImpl build();
	}
	
	@Before
	public void beforeEach() {
		reset(ModelImpl.e);
		gen = genFactory.generatorInstance(ModelGenerator.class);
	}
	
	@Test
	public void invoke_setter_with_static_value() {		
		gen.stringValue(TEST_VALUE_STRING);
		gen.intValue(TEST_VALUE_INT);
		gen.build();
		
		verify(ModelImpl.e).setStringValue(eq(TEST_VALUE_STRING));
		verify(ModelImpl.e).setIntValue(eq(TEST_VALUE_INT));
	}
	
	@Test
	public void build_many_object_with_static_values() {
		ModelGenerator gen = genFactory.generatorInstance(ModelGenerator.class);
		gen.stringValue(TEST_VALUE_STRING);
		gen.intValue(TEST_VALUE_INT);
		
		gen.build();
		gen.build();
		gen.build();
		
		verify(ModelImpl.e, times(3)).setStringValue(eq(TEST_VALUE_STRING));
		verify(ModelImpl.e, times(3)).setIntValue(eq(TEST_VALUE_INT));
	}
	
	@Test
	public void invoke_setter_with_value_provider() {
		int invocationsNumber = 2;
		Supplier<String> valueProviderMock = mock(Supplier.class);
		
		gen.stringValue(valueProviderMock);
		invokeTimes(invocationsNumber, gen);
		
		verify(ModelImpl.e, times(invocationsNumber)).setStringValue(any());
		verify(valueProviderMock, times(invocationsNumber)).get();
	}
	
	@Test
	public void invoke_constructor() {
		int invocationsNumber = 2;
		Supplier<Object> supplier = mock(Supplier.class);
		when(supplier.get()).thenReturn(new Object());
		
		gen.constructor(supplier);
		invokeTimes(invocationsNumber, gen);
		
		verify(supplier, times(invocationsNumber)).get();
	}
	
	@Test
	public void invoke_setter_with_value_provider_no_const_setter_defined() {
		int invocationsNumber = 2;
		ModelGenerator gen = genFactory.generatorInstance(ModelGenerator.class);
		Supplier<String> sup = mock(Supplier.class);
		when(sup.get()).thenReturn(TEST_VALUE_STRING);
		
		gen.someValue(sup);
		invokeTimes(invocationsNumber, gen);
		
		verify(sup, times(invocationsNumber)).get();
	}

	@Test
	public void invoke_generator_method_which_doesnt_return_generator_instance_should_work() {
		ModelGenerator gen = genFactory.generatorInstance(ModelGenerator.class);
		Supplier<String> sup = mock(Supplier.class);
		when(sup.get()).thenReturn(TEST_VALUE_STRING);

		gen.valueX(sup);
		ModelImpl m = gen.build();

		verify(sup, times(1)).get();
		verify(m.e).setValueX(eq(TEST_VALUE_STRING));
	}
	
	private void invokeTimes(int times, Generator<?> gen) {
		for(int i = times - 1; i >= 0; i--) {
			gen.build();
		}
	}
}
