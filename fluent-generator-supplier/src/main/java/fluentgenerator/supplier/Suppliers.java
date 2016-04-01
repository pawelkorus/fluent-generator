package fluentgenerator.supplier;

import fluentgenerator.lib.generator.SupplierGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 *
 * @author pkorus
 */
public abstract class Suppliers {
	public static <T> Supplier<T> oneOf(Collection<T> values) {
		List<T> items = new ArrayList<>();
		items.addAll(values);
		return oneOf(items);
	}
	
	public static <T> Supplier<T> oneOf(List<T> items) {
		return new SupplierGenerator<>(() -> {
			ThreadLocalRandom random = ThreadLocalRandom.current();
			int randInt = random.nextInt(items.size());
			return items.get(randInt);
		});
	}	
	
	public static <T extends Enum<T>> Supplier<T> oneOf(Class<T> enumType) {
		T[] values = enumType.getEnumConstants();
		
		return new SupplierGenerator(() -> { 
			ThreadLocalRandom random = ThreadLocalRandom.current();
			int randInt = random.nextInt(values.length);
			return values[randInt];
		});
	}
	
	public static <T> Supplier<List<T>> subsetOf(List<T> items, int min, int max) {
		assert min >= 0 : "min parameter should be >= 0, but " + min + " was passed";
		assert max >= 0 : "max parameter should be >= 0, but " + max + " was passed";
		assert max <= items.size() : "max parameters should be <= number of elements in collections, but " + max + " is not <= " + items.size();
		assert max >= min : "max parameter should be >= min, but " + max + " is not >= " + min;
		
		List<T> randomizedItems = new ArrayList<>(items);
		Collections.shuffle(randomizedItems);
		
		return () -> {
			Collections.shuffle(randomizedItems);
			
			ThreadLocalRandom random = ThreadLocalRandom.current();
			int numberOfElements = random.nextInt(min, max+1);
			return randomizedItems.subList(0, numberOfElements);
		};
	}
	
	public static <T> Supplier<List<T>> subsetOf(List<T> items) {
		return subsetOf(items, 0, items.size());
	}
	
	public static IndexedStringSupplier indexedString() {
		return new IndexedStringSupplier();
	}
	
	public static IndexedStringSupplier indexedString(String prefix) {
		IndexedStringSupplier sup = new IndexedStringSupplier();
		sup.prefix(prefix);
		return sup;
	}
	
	public static LoremIpsumSupplier loremIpsum() {
		return new LoremIpsumSupplier();
	}
	
	public static Supplier<String> email(String domain) {
		return RandomStringSupplier.Builder.create().length(10)
			.suffix("@" + domain).build();
	}
	
	public static Supplier<String> randomNumbers(int length) {
		return RandomStringSupplier.Builder.create().numeric().length(length)
			.build();
	}
	
	public static Supplier<Integer> randomInt(int min, int max) {
		return () -> ThreadLocalRandom.current().nextInt(min, max+1);
	}
}
