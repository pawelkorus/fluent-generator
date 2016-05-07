package fluentgenerator.lib.test.example;

import fluentgenerator.lib.core.Generator;
import fluentgenerator.lib.core.GeneratorFactory;
import fluentgenerator.lib.core.reflect.ReflectGeneratorProxyFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Supplier;

/**
 * Created by plks on 2016-03-09.
 */
public class Example1 {

	public static class Person{
		String lastName;
		int age;

		public void setLastName(String v) { lastName = v; }
		public void setAge(Integer v) { age = v; }

		public int getAge() { return age; }
		public String getLastName() { return lastName; }
	}

	public static interface PersonGenerator extends Generator<Person> {
		void lastName(Supplier<String> v);
		void age(Supplier<Integer> v);

		Person build();
	}

	@Test
	public void create_person_classes_using_generator() {
		// create generator factory
		GeneratorFactory generatorFactory = new ReflectGeneratorProxyFactory();

		// create instance of generator
		PersonGenerator generator = generatorFactory.generatorInstance(PersonGenerator.class);

		// configure generator
		generator.age(() -> {
			return 5;
		});
		generator.lastName(() -> {
			return "lastName";
		});

		// create instance of Person model
		Person p = generator.build();

		// check
		Assert.assertEquals("lastName", p.getLastName());
		Assert.assertEquals(5, p.getAge());
	}

}
