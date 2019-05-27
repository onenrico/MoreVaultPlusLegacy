//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class Base64Serializer {
	public static String toBase64(final Inventory inventory) {
		try {
			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeInt(inventory.getSize());
			for (int i = 0; i < inventory.getSize(); ++i) {
				dataOutput.writeObject(inventory.getItem(i));
			}
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Cannot into itemstacksz!", e);
		}
	}

	public static String toBase64(final ItemStack[] is, final int size) {
		final Inventory inventory = Bukkit.createInventory((InventoryHolder) null, size);
		inventory.setContents(is);
		return toBase64(inventory);
	}

	public static Inventory fromBase64(final String data) {
		try {
			final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			final Inventory inventory = Bukkit.getServer().createInventory((InventoryHolder) null, dataInput.readInt());
			for (int i = 0; i < inventory.getSize(); ++i) {
				inventory.setItem(i, (ItemStack) dataInput.readObject());
			}
			dataInput.close();
			return inventory;
		} catch (Exception ex) {
			return null;
		}
	}
}
