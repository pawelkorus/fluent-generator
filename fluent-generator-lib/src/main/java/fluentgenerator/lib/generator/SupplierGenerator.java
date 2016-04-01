package fluentgenerator.lib.generator;

import fluentgenerator.lib.core.Generator;
import java.util.function.Supplier;

/**
 *
 * @author pkorus
 */
public class SupplierGenerator<T> implements Generator<T> {

	private final Supplier<T> _supplier;
	
	public SupplierGenerator(Supplier<T> supplier) {
		_supplier = supplier;
	}
	
	@Override
	public T build() {
		return _supplier.get();
	}

}
