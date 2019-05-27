//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.menu.Action;
import me.onenrico.morevaultplus.menu.GUIMenu;
import me.onenrico.morevaultplus.menu.VaultMenu;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.EconomyUT;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;
import me.onenrico.morevaultplus.utils.PlayerUT;

public class MainMenu extends GUIMenu {
	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	public static ItemStack AvailableVault;
	private static ItemStack LockedVault;
	private static ItemStack BorderItem;
	private static String title;
	public static Boolean first;
	private static HashMap<UUID, MainMenu> cache;

	static {
		MainMenu.first = true;
		MainMenu.cache = new HashMap<>();
	}

	public static MainMenu setup(final UUID uuid) {
		final MainMenu mm = MainMenu.cache.getOrDefault(uuid, new MainMenu());
		if (mm.getName() == null) {
			mm.setName("MainMenu");
			MainMenu.cache.put(uuid, mm);
		}
		if (MainMenu.first) {
			MainMenu.PrevPageItem = GUIMenu.setupItem(mm, "PrevPageItem");
			MainMenu.NextPageItem = GUIMenu.setupItem(mm, "NextPageItem");
			MainMenu.AvailableVault = GUIMenu.setupItem(mm, "AvailableVault");
			MainMenu.LockedVault = GUIMenu.setupItem(mm, "LockedVault");
			MainMenu.BorderItem = GUIMenu.setupItem(mm, "BorderItem");
			MainMenu.title = Core.guiconfig.getStr(String.valueOf(mm.getName()) + ".Title", "&cNot Defined");
			MainMenu.first = false;
		}
		return mm;
	}

	public static void place(final MainMenu mm, final Player p, final OfflinePlayer target, final EVault ev,
			final int slot, final int page) {
		final PlaceholderUT pu = VaultMenu.getPlaceholder(p, target, ev);
		pu.add("nextpage", new StringBuilder().append(page + 1).toString());
		pu.add("prevpage", new StringBuilder().append(page - 1).toString());
		pu.add("stack", "64");
		pu.add("money", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return MathUT.format(EconomyUT.getRawBal(p));
			}
		});
		pu.add("exp", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return new StringBuilder().append(PlayerUT.getTotalExperience(p)).toString();
			}
		});
		final ItemStack icon = ItemUT.getItem(ev.getIcon());
		ItemStack av = MainMenu.AvailableVault.clone();
		if (ConfigManager.numbered && ev.getId() > 0) {
			int modded = ev.getId() % 28;
			if (modded == 0) {
				modded = 28;
			}
			av.setAmount(modded);
		}
		if (ConfigManager.link && ev.getId() == MoreVaultAPI.getLinkedVault(p)) {
			av = ItemUT.setGlowing(av, true);
		}
		av.setType(icon.getType());
		if (ev.getDescription() == null || ev.getDescription().isEmpty()) {
			ev.setDescription(new ArrayList<>(ItemUT.getLore(av)));
		} else if (ev.getDescription() != null) {
			ItemUT.changeLore(av, ev.getDescription());
		}
		ItemUT.changeData(av, icon.getDurability());
		mm.setItem(slot, pu.t(av)).addAction(new Action(new ClickType[] { ClickType.LEFT, ClickType.SHIFT_LEFT }) {
			@Override
			public void act() {
				ev.openVault(pu, p);
				ev.setCloseaction(new Action() {
					@Override
					public void act() {
						ev.save();
						if (ConfigManager.autoback) {
							MainMenu.open(p, target, page);
						}
					}
				});
			}
		}).addAction(new Action(new ClickType[] { ClickType.RIGHT, ClickType.SHIFT_RIGHT }) {
			@Override
			public void act() {
				ManageMenu.open(p, ev, 1);
			}
		});
	}

	public static void open(final Player p, final OfflinePlayer target, final int page) {
		MessageUT.plmessage(p, Locales.getValue("main_menu_open"));
		final MainMenu mm = setup(p.getUniqueId());
		final PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		final CustomPermission cp = MoreVaultAPI.getCustomPermission(target, p.getWorld());
		final int maxvault = MoreVaultAPI.getMaxVault(target, cp);
		final int ownedvault = MoreVaultAPI.getOwnedVault(target, cp);
		int reserved = maxvault;
		if (ownedvault > maxvault) {
			reserved = ownedvault;
		}
		final int[] border = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36, 45, 46, 47, 48, 49, 50, 51, 52, 53, 44, 35, 26,
				17 };
		final Integer[] temp = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
				38, 39, 40, 41, 42, 43 };
		List<Integer> vaultslot = Arrays.asList(temp);
		int maxpage;
		if (reserved == 0) {
			maxpage = 1;
		} else if (reserved % vaultslot.size() == 0) {
			maxpage = reserved / vaultslot.size();
		} else {
			maxpage = reserved / vaultslot.size() + 1;
		}
		pu.add("page", new StringBuilder().append(page).toString());
		pu.add("maxpage", new StringBuilder().append(maxpage).toString());
		pu.add("nextpage", new StringBuilder().append(page + 1).toString());
		pu.add("prevpage", new StringBuilder().append(page - 1).toString());
		mm.setTitle(pu.t(MainMenu.title));
		mm.setRow(6);
		mm.build();
		int[] array;
		for (int length = (array = border).length, k = 0; k < length; ++k) {
			final int slot = array[k];
			mm.setItem(slot, MainMenu.BorderItem);
		}
		final Iterator<Integer> iterator = vaultslot.iterator();
		while (iterator.hasNext()) {
			final int slot = iterator.next();
			mm.setItem(slot, new ItemStack(Material.AIR));
		}
		if (page + 1 <= maxpage) {
			mm.setItem(53, pu.t(MainMenu.NextPageItem.clone())).addAction(new Action() {
				@Override
				public void act() {
					MainMenu.open(p, target, page + 1);
				}
			});
		}
		if (page - 1 > 0) {
			mm.setItem(45, pu.t(MainMenu.PrevPageItem.clone())).addAction(new Action() {
				@Override
				public void act() {
					MainMenu.open(p, target, page - 1);
				}
			});
		}
		final int offset = vaultslot.size() * (page - 1);
		final int available = ownedvault - offset;
		int locked = maxvault - offset - available;
		if (locked < 0) {
			locked = 0;
		}
		final int total = available + locked;
		if (total < 7) {
			vaultslot = Arrays.asList(21, 22, 23, 30, 31, 32);
		} else if (total < 11) {
			vaultslot = Arrays.asList(20, 21, 22, 23, 24, 29, 30, 31, 32, 33);
		} else if (total < 21) {
			vaultslot = Arrays.asList(11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42);
		}
		int index = 0;
		for (int i = 1; i <= available && i <= vaultslot.size(); ++i) {
			final EVault ev = Datamanager.getVaultByID(target.getUniqueId(), i + offset);
			place(mm, p, target, ev, vaultslot.get(index++), page);
		}
		final List<Integer> cachevaultslot = vaultslot;
		if (locked > 0 && index + 1 < vaultslot.size()) {
			final double cost = MoreVaultAPI.getCost(cp);
			final String inc = MoreVaultAPI.getIncrease(cp);
			double increase = 0.0;
			double mult = 0.0;
			try {
				increase = Double.parseDouble(inc);
			} catch (Exception ex) {
				mult = Double.parseDouble(inc.replace("x", ""));
			}
			for (int j = 1; j <= locked; ++j) {
				if (index == vaultslot.size()) {
					break;
				}
				double ucost = 0.0;
				if (mult > 0.0) {
					double tcost = cost;
					for (int y = 1; y <= index + 1 + offset; ++y) {
						tcost *= mult;
					}
					ucost = tcost;
				} else {
					ucost = cost + increase * (index + offset - 1);
				}
				pu.add("cost", MathUT.format(ucost));
				final ItemStack deflitem = pu.t(MainMenu.LockedVault.clone());
				final int cacheindex = index;
				final double cachecost = ucost;
				mm.setItem(vaultslot.get(index++), deflitem).addAction(new Action() {
					@Override
					public void act() {
						if (cacheindex + offset > MoreVaultAPI.getOwnedVault(target, cp)) {
							MessageUT.plmessage(p, Locales.getValue("disorder_unlock"));
							return;
						}
						if (!EconomyUT.has(p, cachecost)) {
							pu.add("money", new StringBuilder().append(cachecost).toString());
							MessageUT.plmessage(p, Locales.getValue("insufficient_money"), pu);
							return;
						}
						EconomyUT.subtractBal(p, cachecost);
						pu.add("id", new StringBuilder().append(cacheindex + 1 + offset).toString());
						MessageUT.plmessage(p, Locales.getValue("success_unlock"), pu);
						Datamanager.addCurrentOwned(target.getUniqueId(), 1);
						MainMenu.place(mm, p, target,
								Datamanager.getVaultByID(target.getUniqueId(), cacheindex + 1 + offset),
								cachevaultslot.get(cacheindex), page);
					}
				});
			}
		}
		mm.open(p);
	}
}
