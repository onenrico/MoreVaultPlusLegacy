//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import org.bukkit.inventory.ItemStack;

public class NBTItem extends NBTCompound {
	private ItemStack bukkitItem;

	public NBTItem(final ItemStack item) {
		super(null, null);
		if (item == null) {
			throw new NullPointerException("ItemStack can't be null!");
		}
		bukkitItem = item.clone();
	}

	@Override
	protected Object getCompound() {
		return NBTReflectionUtil.getItemRootNBTTagCompound(ReflectionMethod.ITEMSTACK_NMSCOPY.run(null, bukkitItem));
	}

	@Override
	protected void setCompound(final Object compound) {
		final Object stack = ReflectionMethod.ITEMSTACK_NMSCOPY.run(null, bukkitItem);
		ReflectionMethod.ITEMSTACK_SET_TAG.run(stack, compound);
		bukkitItem = (ItemStack) ReflectionMethod.ITEMSTACK_BUKKITMIRROR.run(null, stack);
	}

	public ItemStack getItem() {
		return bukkitItem;
	}

	protected void setItem(final ItemStack item) {
		bukkitItem = item;
	}

	public boolean hasNBTData() {
		return this.getCompound() != null;
	}

	public static NBTContainer convertItemtoNBT(final ItemStack item) {
		return NBTReflectionUtil.convertNMSItemtoNBTCompound(ReflectionMethod.ITEMSTACK_NMSCOPY.run(null, item));
	}

	public static ItemStack convertNBTtoItem(final NBTCompound comp) {
		return (ItemStack) ReflectionMethod.ITEMSTACK_BUKKITMIRROR.run(null,
				NBTReflectionUtil.convertNBTCompoundtoNMSItem(comp));
	}
}
