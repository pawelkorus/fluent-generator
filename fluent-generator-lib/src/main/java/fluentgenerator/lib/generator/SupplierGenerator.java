package fluentgenerator.lib.generator;

import fluentgenerator.core.Generator;

import java.util.function.Supplier;

/**
 *
 * @author pkorus
 */
public class SupplierGenerator<T> implements Generator<T> {

	private final Supplier<T> _supplier;
	
	private SupplierGenerator(Supplier<T> supplier) {
		_supplier = supplier;
	}
	
	@Override
	public T build() {
		return _supplier.get();
	}

	public static class Builder<T> {
		private Supplier<T> supplier;

		public static <T> Builder create(Supplier<T> supplier) {
			Builder<T> b = new Builder<>();
			b.supplier = supplier;
			return b;
		}

		public SupplierGenerator<T> build() {
			return new SupplierGenerator<>(supplier);
		}
	}
}
