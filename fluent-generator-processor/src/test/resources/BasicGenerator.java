package model;

import fluentgenerator.lib.core.Generator;

import java.util.function.Supplier;

public interface BasicGenerator extends Generator<Basic> {

	BasicGenerator firstName(Supplier<java.lang.String> supplier);
	BasicGenerator lastName(Supplier<java.lang.String> supplier);

	Basic build();

}