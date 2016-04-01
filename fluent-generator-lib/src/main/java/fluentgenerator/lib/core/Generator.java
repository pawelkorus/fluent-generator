/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fluentgenerator.lib.core;

import java.util.function.Supplier;

/**
 *
 * @author pkorus
 */
public interface Generator<T> extends Supplier<T> {
	
	T build();
	
	@Override
	default T get() { return this.build(); }
}
