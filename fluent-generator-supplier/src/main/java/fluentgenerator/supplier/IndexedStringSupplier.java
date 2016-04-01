package fluentgenerator.supplier;

import java.util.function.Supplier;

/**
 *
 * @author pkorus
 */
public class IndexedStringSupplier implements Supplier<String> {

	private int _currentIndex = 1;
	private String _prefix = "";
	
	public IndexedStringSupplier() {}
	
	public IndexedStringSupplier startFrom(int v) {
		_currentIndex = v;
		return this;
	}
	
	public IndexedStringSupplier prefix(String prefix) {
		_prefix = prefix;
		return this;
	}
	
	@Override
	public String get() {
		return _prefix + _currentIndex++;
	}

}
