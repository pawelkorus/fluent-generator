/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fluentgenerator.lib.core;

import java.util.function.Supplier;

/**
 * Basic interface for Generators.
 * <p>
 * Invoking its {@link #build()} method should always return new class instance.
 * <p>
 * Assuming model class:
 * <p>
 * <pre><code>
 *     class Person {
 *         String lastName;
 *         int age;
 *
 *         public setLastName(String v) { ... }
 *         public setAge(int v) { ... }
 *
 *         ...
 *     }
 * }
 * </code></pre>
 * <p>
 * Following generator could be defined:
 * <pre><code>
 *     interface PersonGenerator extends Generator<Person>{
 *         PersonGenerator lastName(Supplier<String> v);
 *         PersonGenerator age(Supplier<Integer> v);
 *         Person build();
 *     }
 * </code></pre>
 *
 * @author pkorus
 * @see GeneratorFactory
 * @see GeneratorProxy
 */
public interface Generator<T> extends Supplier<T> {

	T build();

	@Override
	default T get() {
		return this.build();
	}
}
