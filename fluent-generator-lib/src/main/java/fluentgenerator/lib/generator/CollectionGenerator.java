package fluentgenerator.lib.generator;

import fluentgenerator.core.Generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @param <T> type of generated instances
 */
public class CollectionGenerator<T> implements Generator<Collection<T>> {
	
	private final List<Supplier<? extends T>> _suppliers = new ArrayList<>();
	
	private CollectionGenerator() {}
	
	public static <E> CollectionGenerator<E> create() {
		return new CollectionGenerator<>();
	}
	
	public CollectionGenerator<T> add(Supplier<? extends T> gen) {
		return add(gen, 1);
	}
	
	public CollectionGenerator<T> add(Supplier<? extends T> gen, int num) {
		for(int i = num; i > 0; i--) {
			_suppliers.add(gen);
		}
		return this;
	}
	
	public CollectionGenerator<T> randomize() {
		Collections.shuffle(_suppliers);
		Collections.shuffle(_suppliers);
		return this;
	}

	@Override
	public Collection<T> build() {
		List<T> _items = new ArrayList<>();
		fill(_items);
		return _items;
	}
	
	public void fill(Collection<? super T> collection) {
		for(Supplier<? extends T> supplier : _suppliers) {
			collection.add(supplier.get());
		}
	}

}
