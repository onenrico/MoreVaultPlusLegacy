//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import org.bukkit.entity.Entity;

public class NBTEntity extends NBTCompound {
	private final Entity ent;

	public NBTEntity(final Entity entity) {
		super(null, null);
		ent = entity;
	}

	@Override
	protected Object getCompound() {
		return NBTReflectionUtil.getEntityNBTTagCompound(NBTReflectionUtil.getNMSEntity(ent));
	}

	@Override
	protected void setCompound(final Object compound) {
		NBTReflectionUtil.setEntityNBTTag(compound, NBTReflectionUtil.getNMSEntity(ent));
	}
}
