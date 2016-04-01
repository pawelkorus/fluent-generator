package fluentgenerator.supplier;

import java.util.function.Supplier;

/**
 *
 * @author pkorus
 */
public class StaticValueSupplier<T> implements Supplier<T> {

	private final T _value;
	
	private StaticValueSupplier(T value) {
		_value = value;
	}
	
	@Override
	public T get() {
		return _value;
	}

	public static StaticValueSupplier<Integer> build(int v) {
		return new StaticValueSupplier<>(v);
	}
	
	public static StaticValueSupplier<Long> build(long v) {
		return new StaticValueSupplier<>(v);
	}
	
	public static StaticValueSupplier<String> build(String v) {
		return new StaticValueSupplier<>(v);
	}
	
	public static StaticValueSupplier<Object> build(Object v) {
		return new StaticValueSupplier<>(v);
	}
}
