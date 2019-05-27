//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.gui;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.listener.PlayerListener;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.menu.Action;
import me.onenrico.morevaultplus.menu.GUIMenu;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.ArrayUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;

public class EditDescriptionMenu extends GUIMenu {
	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack LoreItem;
	private static ItemStack AddLoreItem;
	public static boolean first;
	private static String title;
	private static HashMap<UUID, EditDescriptionMenu> cache;

	static {
		EditDescriptionMenu.first = true;
		EditDescriptionMenu.cache = new HashMap<>();
	}

	public static EditDescriptionMenu setup(final UUID uuid) {
		final EditDescriptionMenu mm = EditDescriptionMenu.cache.getOrDefault(uuid, new EditDescriptionMenu());
		if (mm.getName() == null) {
			mm.setName("EditDescriptionMenu");
			EditDescriptionMenu.cache.put(uuid, mm);
		}
		if (EditDescriptionMenu.first) {
			EditDescriptionMenu.PrevPageItem = GUIMenu.setupItem(mm, "PrevPageItem");
			EditDescriptionMenu.NextPageItem = GUIMenu.setupItem(mm, "NextPageItem");
			EditDescriptionMenu.LoreItem = GUIMenu.setupItem(mm, "LoreItem");
			EditDescriptionMenu.AddLoreItem = GUIMenu.setupItem(mm, "AddLoreItem");
			EditDescriptionMenu.title = Core.guiconfig.getStr(String.valueOf(mm.getName()) + ".Title", "&cNot Defined");
			EditDescriptionMenu.first = false;
		}
		return mm;
	}

	public static void open(final Player p, final EVault ev, final int page) {
		final EditDescriptionMenu im = setup(p.getUniqueId());
		if (im.inv != null) {
			im.menuitems.clear();
			im.inv.clear();
		}
		if (im.getCloseaction() == null) {
			im.setCloseaction(new Action() {
				@Override
				public void act() {
					ManageMenu.open(p, ev, 1);
				}
			});
		}
		final List<String> description = ev.getDescription();
		int current = description.size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = (int) MathUT.clamp(maxpage, 1L);
		final PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("page", new StringBuilder().append(page).toString());
		pu.add("nextpage", new StringBuilder().append(page + 1).toString());
		pu.add("prevpage", new StringBuilder().append(page - 1).toString());
		pu.add("maxpage", new StringBuilder().append(maxpage).toString());
		pu.add("player", new StringBuilder().append(p.getName()).toString());
		pu.add("owner", new StringBuilder().append(ev.getOwnerName()).toString());
		pu.add("lines", new StringBuilder().append(description.size()).toString());
		pu.add("description", ArrayUT.stringFromList(ev.getDescription(), "<nl>"));
		im.setTitle(pu.t(EditDescriptionMenu.title));
		im.setRow(6);
		im.build();
		final ItemStack tempPrevPageItem = pu.t(EditDescriptionMenu.PrevPageItem.clone());
		final ItemStack tempNextPageItem = pu.t(EditDescriptionMenu.NextPageItem.clone());
		final ItemStack tempAddLoreItem = pu.t(EditDescriptionMenu.AddLoreItem.clone());
		final int multiplier = 45 * (page - 1);
		current -= multiplier;
		if (maxpage > 1 && page + 1 <= maxpage) {
			im.setItem(53, tempNextPageItem).addAction(new Action() {
				@Override
				public void act() {
					im.setCloseaction(new Action() {
						@Override
						public void act() {
						}
					});
					EditDescriptionMenu.open(p, ev, page + 1);
				}
			});
		}
		if (page > 1) {
			im.setItem(45, tempPrevPageItem).addAction(new Action() {
				@Override
				public void act() {
					im.setCloseaction(new Action() {
						@Override
						public void act() {
						}
					});
					EditDescriptionMenu.open(p, ev, page - 1);
				}
			});
		}
		for (int x = 0; x < MathUT.clamp(current, 0L, 45L); ++x) {
			final int newx = x + multiplier;
			pu.add("content", description.get(newx));
			pu.add("line", new StringBuilder().append(newx + 1).toString());
			ItemStack icon = EditDescriptionMenu.LoreItem.clone();
			icon = pu.t(icon);
			if (ConfigManager.numbered) {
				icon.setAmount(x + 1);
			}
			final PlaceholderUT cachepu = pu;
			im.setItem(x, icon).addAction(new Action(new ClickType[] { ClickType.LEFT }) {
				@Override
				public void act() {
					PlayerListener.cachedescription.put(p.getUniqueId(), ev);
					PlayerListener.cacheline.put(p.getUniqueId(), newx);
					im.setCloseaction(null);
					p.closeInventory();
					MessageUT.plmessage(p, Locales.getValue("edit_line"), cachepu);
				}
			}).addAction(new Action(new ClickType[] { ClickType.RIGHT }) {
				@Override
				public void act() {
					im.setCloseaction(null);
					MoveDescriptionMenu.open(p, ev, newx, 1);
				}
			}).addAction(new Action(new ClickType[] { ClickType.MIDDLE }) {
				@Override
				public void act() {
					im.setCloseaction(new Action() {
						@Override
						public void act() {
						}
					});
					ev.getDescription().add(newx, "");
					ev.save();
					EditDescriptionMenu.open(p, ev, page);
				}
			}).addAction(new Action(new ClickType[] { ClickType.SHIFT_RIGHT }) {
				@Override
				public void act() {
					im.setCloseaction(new Action() {
						@Override
						public void act() {
						}
					});
					ev.getDescription().remove(newx);
					ev.save();
					EditDescriptionMenu.open(p, ev, 1);
				}
			});
		}
		pu.add("line", new StringBuilder().append(current + 1).toString());
		final PlaceholderUT cachepu2 = pu;
		for (int x2 = 0; x2 < 5; ++x2) {
			im.setItem(x2 + 47, tempAddLoreItem).addAction(new Action() {
				@Override
				public void act() {
					PlayerListener.cachedescription.put(p.getUniqueId(), ev);
					PlayerListener.cacheline.put(p.getUniqueId(), ev.getDescription().size());
					im.setCloseaction(null);
					p.closeInventory();
					MessageUT.plmessage(p, Locales.getValue("edit_line"), cachepu2);
				}
			});
		}
		im.open(p);
	}
}
