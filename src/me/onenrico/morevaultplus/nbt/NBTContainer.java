//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

public class NBTContainer extends NBTCompound {
	private Object nbt;

	public NBTContainer() {
		super(null, null);
		nbt = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
	}

	protected NBTContainer(final Object nbt) {
		super(null, null);
		this.nbt = nbt;
	}

	public NBTContainer(final String nbtString) throws IllegalArgumentException {
		super(null, null);
		try {
			nbt = ReflectionMethod.PARSE_NBT.run(null, nbtString);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException("Malformed Json: " + ex.getMessage());
		}
	}

	@Override
	protected Object getCompound() {
		return nbt;
	}

	@Override
	protected void setCompound(final Object tag) {
		nbt = tag;
	}
}
