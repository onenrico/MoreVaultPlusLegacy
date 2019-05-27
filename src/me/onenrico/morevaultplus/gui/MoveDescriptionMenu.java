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

import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.menu.Action;
import me.onenrico.morevaultplus.menu.GUIMenu;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.ArrayUT;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;

public class MoveDescriptionMenu extends GUIMenu {
	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack CurrentPositionItem;
	private static ItemStack ReplacePositionItem;
	private static ItemStack CancelItem;
	public static boolean first;
	private static String title;
	private static HashMap<UUID, MoveDescriptionMenu> cache;

	static {
		MoveDescriptionMenu.first = true;
		MoveDescriptionMenu.cache = new HashMap<>();
	}

	public static MoveDescriptionMenu setup(final UUID uuid) {
		final MoveDescriptionMenu mm = MoveDescriptionMenu.cache.getOrDefault(uuid, new MoveDescriptionMenu());
		if (mm.getName() == null) {
			mm.setName("MoveDescriptionMenu");
			MoveDescriptionMenu.cache.put(uuid, mm);
		}
		if (MoveDescriptionMenu.first) {
			MoveDescriptionMenu.PrevPageItem = GUIMenu.setupItem(mm, "PrevPageItem");
			MoveDescriptionMenu.NextPageItem = GUIMenu.setupItem(mm, "NextPageItem");
			MoveDescriptionMenu.CurrentPositionItem = GUIMenu.setupItem(mm, "CurrentPosition");
			MoveDescriptionMenu.ReplacePositionItem = GUIMenu.setupItem(mm, "ReplacePosition");
			MoveDescriptionMenu.CancelItem = GUIMenu.setupItem(mm, "CancelItem");
			MoveDescriptionMenu.title = Core.guiconfig.getStr(String.valueOf(mm.getName()) + ".Title", "&cNot Defined");
			MoveDescriptionMenu.first = false;
		}
		return mm;
	}

	public static void open(final Player p, final EVault ev, final int cline, final int page) {
		final MoveDescriptionMenu im = setup(p.getUniqueId());
		if (im.inv != null) {
			im.menuitems.clear();
			im.inv.clear();
		}
		if (im.getCloseaction() == null) {
			im.setCloseaction(new Action() {
				@Override
				public void act() {
					EditDescriptionMenu.open(p, ev, page);
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
		pu.add("cline", new StringBuilder().append(cline).toString());
		pu.add("description", ArrayUT.stringFromList(ev.getDescription(), "<nl>"));
		im.setTitle(pu.t(MoveDescriptionMenu.title));
		im.setRow(6);
		im.build();
		final ItemStack tempPrevPageItem = pu.t(MoveDescriptionMenu.PrevPageItem.clone());
		final ItemStack tempNextPageItem = pu.t(MoveDescriptionMenu.NextPageItem.clone());
		final ItemStack tempCancelItem = pu.t(MoveDescriptionMenu.CancelItem.clone());
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
					MoveDescriptionMenu.open(p, ev, cline, page + 1);
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
					MoveDescriptionMenu.open(p, ev, cline, page - 1);
				}
			});
		}
		for (int x = 0; x < MathUT.clamp(current, 0L, 45L); ++x) {
			final int newx = x + multiplier;
			pu.add("content", description.get(newx));
			pu.add("line", new StringBuilder().append(newx + 1).toString());
			if (newx == cline) {
				ItemStack icon = MoveDescriptionMenu.CurrentPositionItem.clone();
				icon = pu.t(icon);
				ItemUT.setGlowing(icon, true);
				im.setItem(x, icon);
			} else {
				ItemStack icon = MoveDescriptionMenu.ReplacePositionItem.clone();
				icon = pu.t(icon);
				final PlaceholderUT cachepu = pu;
				im.setItem(x, icon).addAction(new Action(new ClickType[] { ClickType.LEFT }) {
					@Override
					public void act() {
						if (newx > cline) {
							description.add(newx + 1, description.get(cline));
							description.remove(cline);
						} else {
							final String cache = description.get(cline);
							description.remove(cline);
							description.add(newx, cache);
						}
						ev.save();
						im.setCloseaction(new Action() {
							@Override
							public void act() {
							}
						});
						EditDescriptionMenu.open(p, ev, 1);
						MessageUT.plmessage(p, Locales.getValue("move_line"), cachepu);
					}
				}).addAction(new Action(new ClickType[] { ClickType.RIGHT }) {
					@Override
					public void act() {
						final String cache = description.get(newx);
						description.set(newx, description.get(cline));
						description.set(cline, cache);
						ev.save();
						im.setCloseaction(new Action() {
							@Override
							public void act() {
							}
						});
						EditDescriptionMenu.open(p, ev, 1);
						MessageUT.plmessage(p, Locales.getValue("swap_line"), cachepu);
					}
				});
			}
		}
		for (int x = 0; x < 5; ++x) {
			im.setItem(x + 47, tempCancelItem).addAction(new Action() {
				@Override
				public void act() {
					im.setCloseaction(new Action() {
						@Override
						public void act() {
						}
					});
					EditDescriptionMenu.open(p, ev, 1);
				}
			});
		}
		im.open(p);
	}
}
