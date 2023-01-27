package de.dasbabypixel;

public interface BAUMELEMENT {
	void ausgeben();
	BAUMELEMENT einfuegen(DATENELEMENT daten);
	int anzahlKnoten();

	DATENELEMENT suchen(DATENELEMENT suchwort);

	DATENELEMENT entfernen(DATENELEMENT suchwort);

	BAUMELEMENT vorneEinfuegen(BAUMELEMENT element);
}
