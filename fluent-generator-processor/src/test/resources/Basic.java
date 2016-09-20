package model;

import fluentgenerator.annotation.FluentGenerator;

@FluentGenerator
public class Basic {
	private String firstName;
	private String lastName;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Basic author = (Basic) o;

		if (firstName != null ? !firstName.equals(author.firstName) : author.firstName != null) return false;
		return lastName != null ? lastName.equals(author.lastName) : author.lastName == null;

	}

	@Override
	public int hashCode() {
		int result = firstName != null ? firstName.hashCode() : 0;
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		return result;
	}
}
