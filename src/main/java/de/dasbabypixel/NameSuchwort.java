package de.dasbabypixel;

public class NameSuchwort implements DATENELEMENT {
	private String name;

	public NameSuchwort(String name) {
		this.name = name;
	}

	@Override
	public void ausgeben() {
		System.out.println(name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int compareTo(DATENELEMENT o) {
		return Integer.compare(hashCode(), o.hashCode());
	}
}
