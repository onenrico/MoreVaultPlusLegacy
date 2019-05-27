//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import java.util.HashSet;
import java.util.Set;

public class NBTListCompound {
	private NBTList owner;
	private Object compound;

	protected NBTListCompound(final NBTList parent, final Object obj) {
		owner = parent;
		compound = obj;
	}

	public void setString(final String key, final String value) {
		if (value == null) {
			remove(key);
			return;
		}
		try {
			compound.getClass().getMethod("setString", String.class, String.class).invoke(compound, key, value);
			owner.save();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setInteger(final String key, final int value) {
		try {
			compound.getClass().getMethod("setInt", String.class, Integer.TYPE).invoke(compound, key, value);
			owner.save();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getInteger(final String value) {
		try {
			return (int) compound.getClass().getMethod("getInt", String.class).invoke(compound, value);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public void setDouble(final String key, final double value) {
		try {
			compound.getClass().getMethod("setDouble", String.class, Double.TYPE).invoke(compound, key, value);
			owner.save();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public double getDouble(final String key) {
		try {
			return (double) compound.getClass().getMethod("getDouble", String.class).invoke(compound, key);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0.0;
		}
	}

	public String getString(final String key) {
		try {
			return (String) compound.getClass().getMethod("getString", String.class).invoke(compound, key);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public boolean hasKey(final String key) {
		try {
			return (boolean) compound.getClass().getMethod("hasKey", String.class).invoke(compound, key);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings({ "unchecked" })
	public Set<String> getKeys() {
		try {
			return (Set<String>) ReflectionMethod.LISTCOMPOUND_GET_KEYS.run(compound, new Object[0]);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new HashSet<>();
		}
	}

	public void remove(final String key) {
		try {
			compound.getClass().getMethod("remove", String.class).invoke(compound, key);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
