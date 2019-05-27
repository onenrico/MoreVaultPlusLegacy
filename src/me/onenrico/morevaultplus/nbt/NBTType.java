//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

public enum NBTType {
	NBTTagEnd("NBTTagEnd", 0, 0), NBTTagByte("NBTTagByte", 1, 1), NBTTagShort("NBTTagShort", 2, 2),
	NBTTagInt("NBTTagInt", 3, 3), NBTTagLong("NBTTagLong", 4, 4), NBTTagFloat("NBTTagFloat", 5, 5),
	NBTTagDouble("NBTTagDouble", 6, 6), NBTTagByteArray("NBTTagByteArray", 7, 7),
	NBTTagIntArray("NBTTagIntArray", 8, 11), NBTTagString("NBTTagString", 9, 8), NBTTagList("NBTTagList", 10, 9),
	NBTTagCompound("NBTTagCompound", 11, 10);

	private final int id;

	private NBTType(final String s, final int n, final int i) {
		id = i;
	}

	public int getId() {
		return id;
	}

	public static NBTType valueOf(final int id) {
		NBTType[] values;
		for (int length = (values = values()).length, i = 0; i < length; ++i) {
			final NBTType t = values[i];
			if (t.getId() == id) {
				return t;
			}
		}
		return NBTType.NBTTagEnd;
	}
}
