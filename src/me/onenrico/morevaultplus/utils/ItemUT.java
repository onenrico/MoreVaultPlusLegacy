//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUT {
	public static Material getMaterial(String data) {
		data = data.replace(" ", "_");
		final EMaterial em = EMaterial.fromString(data);
		final Material material = (em == null) ? Material.TORCH : em.parseMaterial();
		return material;
	}

	public static ItemStack getItem(String data) {
		data = data.replace(" ", "_");
		final EMaterial em = EMaterial.fromString(data);
		ItemStack result = null;
		if (em == null) {
			MessageUT.cmsg("Unknown material: " + data + " Contact dev if you think you type correct material.");
			result = createItem(Material.TORCH, "&4&lUnknown Material: " + data,
					createLore("&7Contact dev if you think you type correct material"));
		} else {
			result = em.parseItem();
		}
		return result;
	}

	public static ItemStack createItem(final Material material) {
		return createItem(material, 1);
	}

	public static ItemStack createItem(final Material material, final int amount) {
		return createItem(material, amount, (short) 0);
	}

	public static ItemStack createItem(final Material material, final short data) {
		return createItem(material, 1, data);
	}

	public static ItemStack createItem(final Material material, final int amount, final short data) {
		return createItem(material, null, null, amount, data);
	}

	public static ItemStack createItem(final Material material, final List<String> lore) {
		final ItemStack item = createItem(material);
		return changeLore(item, lore);
	}

	public static ItemStack createItem(final Material material, final String displayname) {
		final ItemStack item = createItem(material);
		return changeDisplayName(item, displayname);
	}

	public static ItemStack createItem(final Material material, final String displayname, final short data) {
		return createItem(material, displayname, null, 1, data);
	}

	public static ItemStack createItem(final Material material, final String displayname, final List<String> lore,
			final short data) {
		return createItem(material, displayname, lore, 1, data);
	}

	public static ItemStack createItem(final Material material, final String displayname, final List<String> lore,
			final int amount) {
		return createItem(material, displayname, lore, amount, (short) 0);
	}

	public static ItemStack createItem(final Material material, final String displayname, final List<String> lore) {
		return createItem(material, displayname, lore, 1);
	}

	public static ItemStack createItem(final Material material, final String displayname, final List<String> lore,
			final int amount, final short data) {
		ItemStack item = new ItemStack(material, amount);
		if (data >= 0) {
			item = changeData(item, data);
		}
		if (displayname != null) {
			item = changeDisplayName(item, displayname);
		}
		if (lore != null) {
			item = changeLore(item, lore);
		}
		return item;
	}

	public static List<String> getLore(final ItemStack item) {
		if (item.hasItemMeta()) {
			final List<String> lore = item.getItemMeta().getLore();
			return lore;
		}
		return null;
	}

	public static String getName(final ItemStack item) {
		if (item.hasItemMeta()) {
			return item.getItemMeta().getDisplayName();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack changeData(final ItemStack item, final short data) {
		item.setDurability(data);
		return item;
	}

	public static ItemStack changeDisplayName(final ItemStack item, final String displayname) {
		if (displayname == null || item == null || item.getType().equals(Material.AIR)) {
			return item;
		}
		ItemMeta meta = item.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(item.getType());
		}
		meta.setDisplayName(StringUT.t(displayname));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack changeLore(final ItemStack item, final List<String> lore) {
		if (lore == null || item == null || item.getType().equals(Material.AIR)) {
			return item;
		}
		ItemMeta meta = item.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(item.getType());
		}
		meta.setLore(StringUT.t(lore));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack changeLore(final ItemStack item, final String lore, final int line) {
		if (lore == null || item == null || item.getType().equals(Material.AIR)) {
			return item;
		}
		ItemMeta meta = item.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(item.getType());
		}
		List<String> theLore;
		if (meta.hasLore()) {
			theLore = getLore(item);
			theLore.set(line - 1, StringUT.t(lore));
		} else {
			theLore = new ArrayList<>();
			theLore.set(line - 1, StringUT.t(lore));
		}
		meta.setLore(theLore);
		item.setItemMeta(meta);
		return item;
	}

	public static List<String> createLore(final String lores, final String splitter) {
		return createLore(lores, splitter, "", "");
	}

	public static List<String> createLore(final String lores, final String splitter, final String prefix) {
		return createLore(lores, splitter, prefix, "");
	}

	public static List<String> createLore(final String lores) {
		return createLore(lores, "%n%");
	}

	public static List<String> createLore(final String lores, final String splitter, String prefix, String suffix) {
		final List<String> Lore = new ArrayList<>();
		prefix = StringUT.t(prefix);
		suffix = StringUT.t(suffix);
		final String[] loreArray = StringUT.t(lores).split(splitter);
		String[] array;
		for (int length = (array = loreArray).length, i = 0; i < length; ++i) {
			final String l = array[i];
			Lore.add(String.valueOf(prefix) + l + suffix);
		}
		return Lore;
	}

	public static boolean cekItem(final Player player, final Material material, final int amount) {
		return removeItem(player, material, amount, Boolean.valueOf(false));
	}

	public static boolean removeItem(final Player player, final ItemStack item, int amount, final Boolean remove) {
		if (amount < 0) {
			return true;
		}
		final ItemStack[] contents = player.getInventory().getContents();
		for (int i = 0; i < contents.length; ++i) {
			if (contents[i] != null) {
				final ItemStack content = contents[i];
				if (content.getType().equals(item.getType()) && getName(item).equals(getName(content))
						&& getLore(item).equals(getLore(content))) {
					int contentamount = content.getAmount();
					if (amount < contentamount) {
						contentamount -= amount;
						if (remove) {
							content.setAmount(contentamount);
						}
						return true;
					}
					amount -= contentamount;
					if (remove) {
						player.getInventory().setItem(i, (ItemStack) null);
					}
				}
			}
		}
		return false;
	}

	public static boolean removeItem(final Player player, final Material material, final int amount,
			final Boolean remove) {
		return removeItem(player.getInventory(), material, amount, remove);
	}

	public static boolean removeItem(final Inventory inv, final Material material, int amount, final Boolean remove) {
		if (amount < 0) {
			return true;
		}
		final ItemStack[] contents = inv.getContents();
		for (int i = 0; i < contents.length; ++i) {
			if (contents[i] != null) {
				final ItemStack content = contents[i];
				if (content.getType().equals(material)) {
					int contentamount = content.getAmount();
					if (amount < contentamount) {
						contentamount -= amount;
						if (remove) {
							content.setAmount(contentamount);
						}
						return true;
					}
					amount -= contentamount;
					if (remove) {
						inv.setItem(i, (ItemStack) null);
					}
				}
			}
		}
		return false;
	}

	public static Boolean hasLore(final ItemStack item, final String check) {
		return hasLore(item, check, false);
	}

	public static Boolean hasLore(final ItemStack item, String check, final Boolean contains) {
		final List<String> lores = getLore(item);
		if (lores == null) {
			return false;
		}
		for (String lore : lores) {
			lore = StringUT.u(lore);
			check = StringUT.u(check);
			if (contains) {
				if (lore.contains(check)) {
					return true;
				}
				continue;
			} else {
				if (lore.equals(check)) {
					return true;
				}
				continue;
			}
		}
		return false;
	}

	public static ItemStack setGlowing(final ItemStack item, final Boolean glow) {
		final ItemMeta meta = item.getItemMeta();
		if (glow) {
			meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
			meta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
		} else {
			meta.removeItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
			meta.removeEnchant(Enchantment.ARROW_INFINITE);
		}
		item.setItemMeta(meta);
		return item;
	}
}
