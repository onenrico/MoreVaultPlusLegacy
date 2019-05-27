//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.converter;

import java.io.StringReader;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.menu.GUIMenu;
import me.onenrico.morevaultplus.utils.EMaterial;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.ReflectionUT;

public class ItemSerializer {
	private static FileConfiguration cache;

	static {
		ItemSerializer.cache = new YamlConfiguration();
	}
	static int tried = 0;

	public static synchronized String serialize(final ItemStack item) {
		ItemSerializer.cache.set("item", item);
		try {
			if (ItemSerializer.cache.getItemStack("item") == null) {
				ItemSerializer.cache.set("item", item);
			}
		} catch (Exception ex) {
			if (tried <= 2) {
				serialize(item);
				tried += 1;
			} else {
				tried = 0;
				return "";
			}
		}
		final String a = ItemSerializer.cache.saveToString();
		final String data = a.replace("\n", "<nl>").replace("'", "<qz>").replace("\"", "<qzz>").replace(";",
				"<semicolon>");
		tried = 0;
		return data;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack deserialize(final String seritem) {
		String seri = seritem.replace("<nl>", "\n").replace("<qz>", "'").replace("<qzz>", "\"").replace("<semicolon>",
				";");
		if (ReflectionUT.NUMBER_VERSION < 113) {
			seri = seri.replace("LEGACY_", "");
		}
		if (!seri.contains("type:")) {
			return new ItemStack(Material.AIR);
		}
		String type = seri.split("type: ")[1].split("\n")[0];
		type = type.trim();
		if (Material.matchMaterial(type) == null) {
			EMaterial em = EMaterial.fromString(type);
			if (em != null) {
				if (em.isUnique(em)) {
					if (seri.contains("damage: ")) {
						final short damage = Short.valueOf(seri.split("damage: ")[1].split("\n")[0]);
						em = EMaterial.fromString(String.valueOf(type) + ":" + damage);
					} else {
						if (ReflectionUT.NUMBER_VERSION < 113) {
							final ItemStack w = em.parseItem();
							seri = seri.replace("type: " + type,
									"type: " + em.parseMaterial().toString() + "\n  damage: " + w.getDurability());
						}
					}
				}
				if (!seri.contains("v: 1628")) {
					seri = seri.replace(".inventory.ItemStack", ".inventory.ItemStack\n  v: 1628");
				}
				seri = seri.replace("type: " + type, "type: " + em.parseMaterial().toString());
			} else {
				seri = seri.replace("type: ", "type: LEGACY_");
				type = seri.split("type: ")[1].split("\n")[0];
				if (Material.matchMaterial(type) == null) {
					seri = seri.replace("type: " + type, "type: STONE");
				}
			}
		}
		FileConfiguration loaded = null;
		try {
			if (seri == null || !seri.contains("==: org.bukkit.inventory.ItemStack") || seri.contains("*id0")
					|| seri.contains("&id0") || seri.contains("==:\n")) {
				return new ItemStack(Material.AIR);
			}
			loaded = YamlConfiguration.loadConfiguration(new StringReader(seri));
		} catch (Exception ex) {
			MessageUT.cmsg("&c&lFAIL ERROR");
			return new ItemStack(Material.AIR);
		}
		return loaded.getItemStack("item");
	}

	public static synchronized String serialize(final Inventory tempinv) {
		if (tempinv == null) {
			return "empty";
		}
		final Inventory inv = Bukkit.createInventory((InventoryHolder) null, tempinv.getSize());
		inv.setContents(tempinv.getContents().clone());
		tempinv.setContents(inv.getContents());
		final StringBuilder result = new StringBuilder();
		for (int slot = 0; slot < inv.getSize(); ++slot) {
			final ItemStack item = inv.getItem(slot);
			if (item != null) {
				if (!item.getType().equals(Material.AIR)) {
					if (!GUIMenu.isSecured(item)) {
						result.append("<#" + slot + "$:" + serialize(item));
					}
				}
			}
		}
		if (result.toString() == null) {
			return "empty";
		}
		return result.toString();
	}

	public static void deserialize(final String arg, final Inventory inv) {
		inv.clear();
		if (arg == null) {
			return;
		}
		if (arg.isEmpty()) {
			return;
		}
		if (arg.equals("empty")) {
			return;
		}
		final String[] list = arg.split("<#");
		if (list == null || list.length < 1) {
			return;
		}
		String[] array;
		for (int length = (array = list).length, i = 0; i < length; ++i) {
			final String l = array[i];
			try {
				final int slot = MathUT.strInt(l);
				final String rawitem = l.replace(String.valueOf(slot) + "$:", "");
				if (slot + 1 > inv.getSize()) {
					break;
				}
				if (rawitem.equals("air")) {
					inv.setItem(slot, ItemUT.createItem(Material.AIR));
				} else {
					final ItemStack item = deserialize(rawitem);
					inv.setItem(slot, item);
				}
			} catch (Exception ex) {
			}
		}
		for (final HumanEntity p : inv.getViewers()) {
			((Player) p).updateInventory();
		}
	}
}
