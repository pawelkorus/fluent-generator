package fluentgenerator.lib.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

/**
 *
 * @author pkorus
 */
public class MethodInvocation {

	private final Method _method;
	private final Collection<Entry> _args;

	private MethodInvocation(Method method) {
		_method = method;
		_args = new ArrayList<>();
	}
	
	public void addArg(Class<?> argType, Supplier<?> argValueProvider) {
		_args.add(Entry.build(argType, argValueProvider));
	}
	
	public String getMethodName() {
		return _method.getName();
	}
	
	public void invoke(Object target)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Collection<Object> args = new ArrayList<>();
		for(Entry e : _args) {
			args.add(e.valueProvider.get());
		}
		_method.invoke(target, args.toArray());
	}
	
	public static MethodInvocation build(
		Method setter, Class<?> argType, Supplier<?> argValue)
	{
		MethodInvocation mi = new MethodInvocation(setter);
		mi.addArg(argType, argValue);
		return mi;
	}
	
	private static class Entry {
		Class<?> type;
		Supplier<?> valueProvider;
		
		public static Entry build(Class<?> type, Supplier<?> provider) {
			Entry e = new Entry();
			e.type = type;
			e.valueProvider = provider;
			return e;
		}
	}
}
