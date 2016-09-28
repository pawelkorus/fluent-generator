package fluentgenerator.lib.test.generator;

import fluentgenerator.core.Generator;
import fluentgenerator.lib.generator.SupplierGenerator;
import org.junit.Test;

import java.util.function.Supplier;

import static org.mockito.Mockito.*;

public class TestSupplierGenerator {

	@Test
	public void supplier_generator_build_method_should_call_supplier_get() {
		Supplier<Integer> intSupplier = mock(Supplier.class);
		when(intSupplier.get()).thenReturn(5);

		Generator<Integer> gen = SupplierGenerator.Builder.create(intSupplier).build();

		gen.build();
		gen.build();

		verify(intSupplier, times(2)).get();
	}

}
