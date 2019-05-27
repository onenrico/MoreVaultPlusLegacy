//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import java.util.Set;

public class NBTCompound {
	private String compundName;
	private NBTCompound parent;

	protected NBTCompound(final NBTCompound owner, final String name) {
		compundName = name;
		parent = owner;
	}

	public String getName() {
		return compundName;
	}

	protected Object getCompound() {
		return parent.getCompound();
	}

	protected void setCompound(final Object compound) {
		parent.setCompound(compound);
	}

	public NBTCompound getParent() {
		return parent;
	}

	public void mergeCompound(final NBTCompound comp) {
		NBTReflectionUtil.addOtherNBTCompound(this, comp);
	}

	public void setString(final String key, final String value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_STRING, key, value);
	}

	public String getString(final String key) {
		return (String) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_STRING, key);
	}

	protected String getContent(final String key) {
		return NBTReflectionUtil.getContent(this, key);
	}

	public void setInteger(final String key, final Integer value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_INT, key, value);
	}

	public Integer getInteger(final String key) {
		return (Integer) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_INT, key);
	}

	public void setDouble(final String key, final Double value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_DOUBLE, key, value);
	}

	public Double getDouble(final String key) {
		return (Double) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_DOUBLE, key);
	}

	public void setByte(final String key, final Byte value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_BYTE, key, value);
	}

	public Byte getByte(final String key) {
		return (Byte) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_BYTE, key);
	}

	public void setShort(final String key, final Short value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_SHORT, key, value);
	}

	public Short getShort(final String key) {
		return (Short) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_SHORT, key);
	}

	public void setLong(final String key, final Long value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_LONG, key, value);
	}

	public Long getLong(final String key) {
		return (Long) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_LONG, key);
	}

	public void setFloat(final String key, final Float value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_FLOAT, key, value);
	}

	public Float getFloat(final String key) {
		return (Float) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_FLOAT, key);
	}

	public void setByteArray(final String key, final byte[] value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_BYTEARRAY, key, value);
	}

	public byte[] getByteArray(final String key) {
		return (byte[]) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_BYTEARRAY, key);
	}

	public void setIntArray(final String key, final int[] value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_INTARRAY, key, value);
	}

	public int[] getIntArray(final String key) {
		return (int[]) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_INTARRAY, key);
	}

	public void setBoolean(final String key, final Boolean value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_BOOLEAN, key, value);
	}

	protected void set(final String key, final Object val) {
		NBTReflectionUtil.set(this, key, val);
	}

	public Boolean getBoolean(final String key) {
		final Boolean bool = (Boolean) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_BOOLEAN, key);
		return bool;
	}

	public void setObject(final String key, final Object value) {
		NBTReflectionUtil.setObject(this, key, value);
	}

	public <T> T getObject(final String key, final Class<T> type) {
		return NBTReflectionUtil.getObject(this, key, type);
	}

	public Boolean hasKey(final String key) {
		final Boolean bool = (Boolean) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_HAS_KEY, key);
		if (bool == null) {
			return false;
		}
		return bool;
	}

	public void removeKey(final String key) {
		NBTReflectionUtil.remove(this, key);
	}

	public Set<String> getKeys() {
		return NBTReflectionUtil.getKeys(this);
	}

	public NBTCompound addCompound(final String name) {
		NBTReflectionUtil.addNBTTagCompound(this, name);
		return this.getCompound(name);
	}

	public NBTCompound getCompound(final String name) {
		final NBTCompound next = new NBTCompound(this, name);
		if (NBTReflectionUtil.valideCompound(next)) {
			return next;
		}
		return null;
	}

	public NBTList getList(final String name, final NBTType type) {
		return NBTReflectionUtil.getList(this, name, type);
	}

	public NBTType getType(final String name) {
		if (MinecraftVersion.getVersion() == MinecraftVersion.MC1_7_R4) {
			return NBTType.NBTTagEnd;
		}
		return NBTType.valueOf((byte) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_TYPE, name));
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		for (final String key : getKeys()) {
			result.append(this.toString(key));
		}
		return result.toString();
	}

	public String toString(final String key) {
		final StringBuilder result = new StringBuilder();
		for (NBTCompound compound = this; compound.getParent() != null; compound = compound.getParent()) {
			result.append("   ");
		}
		if (getType(key) == NBTType.NBTTagCompound) {
			return this.getCompound(key).toString();
		}
		return result + "-" + key + ": " + getContent(key) + System.lineSeparator();
	}

	public String asNBTString() {
		return NBTReflectionUtil.gettoCompount(this.getCompound(), this).toString();
	}
}
