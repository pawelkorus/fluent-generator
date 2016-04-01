package fluentgenerator.lib.core;

/**
 * Created by plks on 2016-03-31.
 */
public interface GeneratorFactory {
	<T> T generatorInstance(Class<T> generatorClass);
}
