//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import org.bukkit.Bukkit;

public enum ClassWrapper {
	CRAFT_ITEMSTACK("CRAFT_ITEMSTACK", 0, "org.bukkit.craftbukkit.", ".inventory.CraftItemStack"),
	CRAFT_ENTITY("CRAFT_ENTITY", 1, "org.bukkit.craftbukkit.", ".entity.CraftEntity"),
	CRAFT_WORLD("CRAFT_WORLD", 2, "org.bukkit.craftbukkit.", ".CraftWorld"),
	NMS_NBTBASE("NMS_NBTBASE", 3, "net.minecraft.server.", ".NBTBase"),
	NMS_NBTTAGSTRING("NMS_NBTTAGSTRING", 4, "net.minecraft.server.", ".NBTTagString"),
	NMS_ITEMSTACK("NMS_ITEMSTACK", 5, "net.minecraft.server.", ".ItemStack"),
	NMS_NBTTAGCOMPOUND("NMS_NBTTAGCOMPOUND", 6, "net.minecraft.server.", ".NBTTagCompound"),
	NMS_NBTTAGLIST("NMS_NBTTAGLIST", 7, "net.minecraft.server.", ".NBTTagList"),
	NMS_NBTCOMPRESSEDSTREAMTOOLS("NMS_NBTCOMPRESSEDSTREAMTOOLS", 8, "net.minecraft.server.",
			".NBTCompressedStreamTools"),
	NMS_MOJANGSONPARSER("NMS_MOJANGSONPARSER", 9, "net.minecraft.server.", ".MojangsonParser"),
	NMS_TILEENTITY("NMS_TILEENTITY", 10, "net.minecraft.server.", ".TileEntity"),
	NMS_BLOCKPOSITION("NMS_BLOCKPOSITION", 11, "net.minecraft.server.", ".BlockPosition"),
	NMS_WORLD("NMS_WORLD", 12, "net.minecraft.server.", ".WorldServer"),
	NMS_ENTITY("NMS_ENTITY", 13, "net.minecraft.server.", ".Entity");

	private Class<?> clazz;

	private ClassWrapper(final String s, final int n, final String pre, final String suffix) {
		try {
			final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
			clazz = Class.forName(String.valueOf(pre) + version + suffix);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Class<?> getClazz() {
		return clazz;
	}
}
