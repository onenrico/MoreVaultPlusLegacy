//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.database;

public abstract class EObject {
	protected String identifier;

	public EObject(final String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public abstract void save();

	public abstract void load();

	public abstract void delete();
}
