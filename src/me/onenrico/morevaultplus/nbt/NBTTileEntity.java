//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import org.bukkit.block.BlockState;

public class NBTTileEntity extends NBTCompound {
	private final BlockState tile;

	public NBTTileEntity(final BlockState tile) {
		super(null, null);
		this.tile = tile;
	}

	@Override
	protected Object getCompound() {
		return NBTReflectionUtil.getTileEntityNBTTagCompound(tile);
	}

	@Override
	protected void setCompound(final Object compound) {
		NBTReflectionUtil.setTileEntityNBTTagCompound(tile, compound);
	}
}
