//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.menu.Action;
import me.onenrico.morevaultplus.menu.GUIMenu;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.EconomyUT;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;

public class IconMenu extends GUIMenu {
	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack IconItem;
	private static ItemStack CancelItem;
	public static boolean first;
	private static String title;
	private static HashMap<UUID, IconMenu> cache;

	static {
		IconMenu.first = true;
		IconMenu.cache = new HashMap<>();
	}

	public static IconMenu setup(final UUID uuid) {
		final IconMenu mm = IconMenu.cache.getOrDefault(uuid, new IconMenu());
		if (mm.getName() == null) {
			mm.setName("IconMenu");
			IconMenu.cache.put(uuid, mm);
		}
		if (IconMenu.first) {
			IconMenu.PrevPageItem = GUIMenu.setupItem(mm, "PrevPageItem");
			IconMenu.NextPageItem = GUIMenu.setupItem(mm, "NextPageItem");
			IconMenu.IconItem = GUIMenu.setupItem(mm, "IconItem");
			IconMenu.CancelItem = GUIMenu.setupItem(mm, "CancelItem");
			IconMenu.title = Core.guiconfig.getStr(String.valueOf(mm.getName()) + ".Title", "&cNot Defined");
			IconMenu.first = false;
		}
		return mm;
	}

	public static void open(final Player p, final EVault ev, final int page, final int mainpage) {
		final IconMenu im = setup(p.getUniqueId());
		if (im.inv != null) {
			im.menuitems.clear();
			im.inv.clear();
		}
		int current = ConfigManager.icons.keySet().size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = (int) MathUT.clamp(maxpage, 1L);
		final PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("page", new StringBuilder().append(page).toString());
		pu.add("nextpage", new StringBuilder().append(page + 1).toString());
		pu.add("prevpage", new StringBuilder().append(page - 1).toString());
		pu.add("maxpage", new StringBuilder().append(maxpage).toString());
		pu.add("player", new StringBuilder().append(p.getName()).toString());
		pu.add("owner", new StringBuilder().append(ev.getOwnerName()).toString());
		im.setTitle(pu.t(IconMenu.title));
		im.setRow(6);
		im.build();
		final ItemStack tempPrevPageItem = pu.t(IconMenu.PrevPageItem.clone());
		final ItemStack tempNextPageItem = pu.t(IconMenu.NextPageItem.clone());
		final ItemStack tempCancelItem = pu.t(IconMenu.CancelItem.clone());
		final int multiplier = 45 * (page - 1);
		current -= multiplier;
		if (maxpage > 1 && page + 1 <= maxpage) {
			im.setItem(53, tempNextPageItem).addAction(new Action() {
				@Override
				public void act() {
					IconMenu.open(p, ev, page + 1, mainpage);
				}
			});
		}
		if (page > 1) {
			im.setItem(45, tempPrevPageItem).addAction(new Action() {
				@Override
				public void act() {
					IconMenu.open(p, ev, page - 1, mainpage);
				}
			});
		}
		final List<ItemStack> keys = new ArrayList<>(ConfigManager.icons.keySet());
		for (int x = 0; x < MathUT.clamp(current, 0L, 45L); ++x) {
			final int newx = x + multiplier;
			final ItemStack ite = keys.get(newx);
			final double cost = ConfigManager.icons.get(ite);
			if (ItemUT.getName(ite) == null) {
				pu.add("material", ite.getType().toString().replace("LEGACY_", "").replace("_", " "));
			} else {
				pu.add("material", ItemUT.getName(ite));
			}
			pu.add("cost", MathUT.format(cost));
			ItemStack icon = IconMenu.IconItem.clone();
			icon.setType(ite.getType());
			icon = ItemUT.changeData(icon, ite.getDurability());
			icon = pu.t(icon);
			im.setItem(x, icon).addAction(new Action() {
				@Override
				public void act() {
					if (!EconomyUT.has(p, cost)) {
						pu.add("money", new StringBuilder().append(cost).toString());
						MessageUT.plmessage(p, Locales.getValue("insufficient_money"), pu);
						return;
					}
					EconomyUT.subtractBal(p, cost);
					String icon = "";
					if (ItemUT.getName(ite) == null) {
						icon = ite.getType().toString().replace("LEGACY_", "").replace("_", " ");
						pu.add("icon", icon);
					} else {
						icon = ItemUT.getName(ite);
						pu.add("icon", icon);
					}
					if (ite.getDurability() > 0) {
						icon = String.valueOf(icon) + ":" + ite.getDurability();
					}
					String type = ite.getType().toString().replace("LEGACY_", "");
					if (ite.getDurability() > 0) {
						type = String.valueOf(type) + ":" + ite.getDurability();
					}
					ev.setIcon(type);
					ev.save();
					MessageUT.plmessage(p, Locales.getValue("edit_icon"), pu);
					MainMenu.open(p, Bukkit.getOfflinePlayer(ev.getOwner()), mainpage);
				}
			});
		}
		for (int x = 0; x < 5; ++x) {
			im.setItem(x + 47, tempCancelItem).addAction(new Action() {
				@Override
				public void act() {
					ManageMenu.open(p, ev, mainpage);
				}
			});
		}
		im.open(p);
	}
}
