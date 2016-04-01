package fluentgenerator.supplier.test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import fluentgenerator.supplier.Suppliers;
import fluentgenerator.lib.test.util.Utils;
import org.junit.Test;

/**
 *
 * @author pkorus
 */
public class TestSuppliers {
	
	private enum TestEnum {
		VALUE_1, VALUE_2, VALUE_3
	}
	
	@Test
	public void test_oneOf() {
		Object[] objects = 
			{ new Object(), new Object(), new Object(), new Object() };
		
		Supplier<Object> sup = Suppliers.oneOf(Arrays.asList(objects));
		for(int i = 40; i > 0; i--) {
			assertThat(sup.get(), isIn(objects));
		}
	}
	
	@Test
	public void test_oneOf_enum() {
		Supplier<TestEnum> sup = Suppliers.oneOf(TestEnum.class);
		for(int i = 40; i > 0; i--) {
			assertThat(sup.get(), isIn(TestEnum.class.getEnumConstants()));
		}
	}
	
	@Test
	public void test_indexedString() {
		Supplier<String> sup = Suppliers.indexedString();
		
		for(int i = 1; i <= 40; i++) {
			assertThat(sup.get(), equalTo(String.valueOf(i)));
		}
	}
	
	@Test
	public void test_indexedString_with_prefix_and_start() {
		String PREFIX = "Test";
		int START_FROM = 10;
		
		Supplier<String> sup = Suppliers.indexedString().prefix(PREFIX).startFrom(10);
		
		for(int i = 0; i < 40; i++) {
			assertThat(sup.get(), equalTo(PREFIX + String.valueOf(i + START_FROM)));
		}
	}
	
	@Test
	public void test_loremIpsum() {
		Supplier<String> sup = Suppliers.loremIpsum().numberOfWords(10);
		String text = sup.get();
		assertThat(text.split(" ").length, equalTo(10));
		
		sup = Suppliers.loremIpsum().randomNumberOfWords(10, 20);
		text = sup.get();
		assertThat(text.split(" ").length, 
			allOf(greaterThanOrEqualTo(10), lessThanOrEqualTo(20)));
	}
	
	@Test
	public void test_subsetOf() {
		int COLLECTION_SIZE = 10;
		List<Object> objs = new ArrayList<>();
		for(int i = 0; i < 10; i++) objs.add(new Object());
		
		Supplier<List<Object>> supplier0_5Size = Suppliers.subsetOf(objs, 0, 5);
		Utils.repeat(() -> {
			List<Object> subset = supplier0_5Size.get();
			assertThat(subset.size(), greaterThanOrEqualTo(0));
			assertThat(subset.size(), lessThanOrEqualTo(5));
		}).times(20);
		
		Supplier<List<Object>> supplier3_5Size = Suppliers.subsetOf(objs, 3, 5);
		Utils.repeat(() -> {
			List<Object> subset = supplier3_5Size.get();
			assertThat(subset.size(), greaterThanOrEqualTo(3));
			assertThat(subset.size(), lessThanOrEqualTo(5));
		}).times(20);
		
		Supplier<List<Object>> supplierMinSize = Suppliers.subsetOf(objs, 0, 0);
		Utils.repeat(() -> {
			assertThat(supplierMinSize.get().size(), equalTo(0));
		}).times(20);
		
		Supplier<List<Object>> supplier3Size = Suppliers.subsetOf(objs, 3, 3);
		Utils.repeat(() -> {
			assertThat(supplier3Size.get().size(), equalTo(3));
		}).times(20);
		
		Supplier<List<Object>> supplierMaxSize = 
			Suppliers.subsetOf(objs, COLLECTION_SIZE, COLLECTION_SIZE);
		Utils.repeat(() -> {
			assertThat(supplierMaxSize.get().size(), equalTo(COLLECTION_SIZE));
		}).times(20);
	}
	
}
