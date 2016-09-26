package fluentgenerator.lib.core.reflect;

public class Property {
	private final String name;
	private final Class<?> type;

	public Property(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "Property{" +
			"name='" + name + '\'' +
			", type=" + type +
			'}';
	}

	public static Property build(String name, Class<?> type) {
		return new Property(name, type);
	}
}
