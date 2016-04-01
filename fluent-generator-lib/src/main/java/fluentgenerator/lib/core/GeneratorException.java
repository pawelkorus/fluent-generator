package fluentgenerator.lib.core;

/**
 *
 * @author pkorus
 */
public class GeneratorException extends RuntimeException  {

	private final String _msg;
	private final Class<?> _generatorType;
	
	public GeneratorException(Class<?> generatorType, String msg) {
		super();
		_generatorType = generatorType;
		
		StringBuilder b = new StringBuilder();
		b.append("Error when working with generator: ");
		b.append(generatorType.getSimpleName());
		b.append(". ");
		b.append(msg);
		_msg = b.toString();
	}
	
	public GeneratorException(Class<?> generatorType, String msg, Throwable cause) {
		this(generatorType, msg);
		initCause(cause);
	}

	@Override
	public String getMessage() {
		return _msg;
	}
	
}
