package de.dasbabypixel;

public class KNOTEN implements BAUMELEMENT {
	private BAUMELEMENT prev = ABSCHLUSS.instance, next = ABSCHLUSS.instance;
	private DATENELEMENT daten;

	public KNOTEN(DATENELEMENT daten) {
		this.daten = daten;
	}

	@Override
	public BAUMELEMENT einfuegen(DATENELEMENT daten) {
		if (this.daten == null) {
			this.daten = daten;
		} else if (daten.compareTo(this.daten) < 0) {
			prev = prev.einfuegen(daten);
		} else {
			next = next.einfuegen(daten);
		}
		return this;
	}

	@Override
	public void ausgeben() {
		if (daten == null)
			return;
		prev.ausgeben();
		daten.ausgeben();
		next.ausgeben();
	}

	@Override
	public DATENELEMENT suchen(DATENELEMENT suchwort) {
		if (daten == null) {
			return null;
		}
		int comp = daten.compareTo(suchwort);
		if (comp == 0) {
			return daten;
		} else if (comp > 0) {
			return prev.suchen(suchwort);
		} else {
			return next.suchen(suchwort);
		}
	}

	@Override
	public DATENELEMENT entfernen(DATENELEMENT suchwort) {
		int comp = daten.compareTo(suchwort);
		if (comp == 0) {
			DATENELEMENT o = daten;
			next = next.vorneEinfuegen(prev);
			daten = null;
			return o;
		} else if (comp > 0) {
			return prev.entfernen(suchwort);
		} else {
			return next.entfernen(suchwort);
		}
	}

	@Override
	public BAUMELEMENT vorneEinfuegen(BAUMELEMENT element) {
		prev = prev.vorneEinfuegen(element);
		return this;
	}

	public int anzahlKnoten() {
		return (daten == null ? 0 : 1) + prev.anzahlKnoten() + next.anzahlKnoten();
	}

}
