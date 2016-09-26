package fluentgenerator.lib.core.reflect;

import java.lang.reflect.InvocationTargetException;

public interface ConstructorStrategy {
	public Object get() throws
		InstantiationException, InvocationTargetException,
		IllegalAccessException;
}