package fluentgenerator.lib.core.reflect;

import java.util.function.Supplier;

public interface GenerationVisitor {

	void acceptConstructorStrategy(ConstructorStrategy constructorStrategy);

	void acceptPropertySupplier(Property property, Supplier<Object> supplier);

	Object finishObject();

}
