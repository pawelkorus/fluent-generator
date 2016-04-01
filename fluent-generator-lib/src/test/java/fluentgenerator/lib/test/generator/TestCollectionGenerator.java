package fluentgenerator.lib.test.generator;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import fluentgenerator.lib.generator.CollectionGenerator;
import java.util.Collection;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author pkorus
 */
public class TestCollectionGenerator {
	
	CollectionGenerator<Object> gen = CollectionGenerator.create();
	Object[] objects = { 
		new Object(), 
		new Object(), 
		new Object() };
	
	@Before
	public void beforeAll() {
		gen = CollectionGenerator.create();
	}
	
	@Test
	public void generate_collection_order_as_added() {
		Supplier<Object> objectSupplier = mock(Supplier.class);
		when(objectSupplier.get()).thenAnswer(new ObjectArrayAnswer(objects));
	
		gen.add(objectSupplier);
		gen.add(objectSupplier);
		gen.add(objectSupplier);
		
		Collection<Object> col = gen.build();
		
		verify(objectSupplier, times(3)).get();
		assertEquals(3, col.size());
		assertArrayEquals(objects, col.toArray());
	}
	
	@Test
	public void generate_collection_order_as_added_add_at_once() {
		Supplier<Object> objectSupplier = mock(Supplier.class);
		when(objectSupplier.get()).thenAnswer(new ObjectArrayAnswer(objects));
	
		gen.add(objectSupplier, 3);
		
		Collection<Object> col = gen.build();
		
		verify(objectSupplier, times(3)).get();
		assertEquals(3, col.size());
		assertArrayEquals(objects, col.toArray());
	}
	
	private class ObjectArrayAnswer implements Answer<Object> {

		private final Object[] _objects;
		private int _currentIndex = 0;
		
		ObjectArrayAnswer(Object[] objects) {
			_objects = objects;
		}
		
		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			return _objects[_currentIndex++];
		}
		
	}
}
