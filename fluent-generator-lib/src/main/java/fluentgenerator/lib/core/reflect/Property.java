package fluentgenerator.lib.core.reflect;

import java.util.Objects;

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
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;

		if(obj == null || !(obj instanceof Property)) return false;

		Property right = (Property) obj;

		return Objects.equals(name, right.name);
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
