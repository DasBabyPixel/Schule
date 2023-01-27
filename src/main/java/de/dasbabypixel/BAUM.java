package de.dasbabypixel;

public class BAUM {
	private KNOTEN wurzel = new KNOTEN(null);

	public void einfuegen(DATENELEMENT daten) {
		wurzel.einfuegen(daten);
	}

	public void ausgeben() {
		wurzel.ausgeben();
	}

	public DATENELEMENT entfernen(DATENELEMENT suchwort){
		return wurzel.entfernen(suchwort);
	}

	public DATENELEMENT suchen(DATENELEMENT suchwort){
		return wurzel.suchen(suchwort);
	}

	public int anzahlKnoten() {
		return wurzel.anzahlKnoten();
	}
}
