package fluentgenerator.core;

public interface GeneratorFactory {

	<T> T generatorInstance(Class<T> generatorClass);

}
