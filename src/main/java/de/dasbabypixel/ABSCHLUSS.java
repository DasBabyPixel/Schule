package de.dasbabypixel;

public class ABSCHLUSS implements BAUMELEMENT {
	public static final ABSCHLUSS instance = new ABSCHLUSS();
	private ABSCHLUSS(){
	}

	@Override
	public void ausgeben() {

	}

	@Override
	public BAUMELEMENT vorneEinfuegen(BAUMELEMENT element) {
		return element;
	}

	@Override
	public int anzahlKnoten() {
		return 0;
	}

	@Override
	public DATENELEMENT suchen(DATENELEMENT suchwort) {
		return null;
	}

	@Override
	public DATENELEMENT entfernen(DATENELEMENT suchwort) {
		return null;
	}

	@Override
	public BAUMELEMENT einfuegen(DATENELEMENT daten) {
		return new KNOTEN(daten);
	}
}
