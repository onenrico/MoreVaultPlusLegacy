package me.onenrico.morevaultplus.gui;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.listener.PlayerListener;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.menu.Action;
import me.onenrico.morevaultplus.menu.GUIMenu;
import me.onenrico.morevaultplus.nbt.NBTItem;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.ArrayUT;
import me.onenrico.morevaultplus.utils.EconomyUT;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.PermissionUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;

public class ManageMenu extends GUIMenu {
	private static ItemStack RenameItem;
	private static ItemStack DescriptionItem;
	private static ItemStack IconItem;
	private static ItemStack DeviceItem;
	private static ItemStack CancelItem;
	private static ItemStack LinkItem;
	private static ItemStack UnlinkItem;
	private static ItemStack OnAutoSellItem;
	private static ItemStack OffAutoSellItem;
	public static boolean first;
	private static String title;
	private static HashMap<UUID, ManageMenu> cache;

	static {
		ManageMenu.first = true;
		ManageMenu.cache = new HashMap<>();
	}

	public static ManageMenu setup(final UUID uuid) {
		final ManageMenu mm = ManageMenu.cache.getOrDefault(uuid, new ManageMenu());
		if (mm.getName() == null) {
			mm.setName("ManageMenu");
			ManageMenu.cache.put(uuid, mm);
		}
		if (ManageMenu.first) {
			ManageMenu.RenameItem = GUIMenu.setupItem(mm, "RenameItem");
			ManageMenu.DescriptionItem = GUIMenu.setupItem(mm, "DescriptionItem");
			ManageMenu.DeviceItem = GUIMenu.setupItem(mm, "DeviceItem");
			ManageMenu.IconItem = GUIMenu.setupItem(mm, "IconItem");
			ManageMenu.CancelItem = GUIMenu.setupItem(mm, "CancelItem");
			ManageMenu.LinkItem = GUIMenu.setupItem(mm, "LinkItem");
			ManageMenu.UnlinkItem = GUIMenu.setupItem(mm, "UnlinkItem");
			ManageMenu.OnAutoSellItem = GUIMenu.setupItem(mm, "OnAutoSellItem");
			ManageMenu.OffAutoSellItem = GUIMenu.setupItem(mm, "OffAutoSellItem");
			ManageMenu.title = Core.guiconfig.getStr(String.valueOf(mm.getName()) + ".Title", "&cNot Defined");
			ManageMenu.first = false;
		}
		if (mm.getInv() != null) {
			mm.getInv().clear();
			mm.menuitems.clear();
		}
		return mm;
	}

	// 00 01 02 03 04 05 06 07 08
	// 09 10 11 12 13 14 15 16 17
	// 18 19 20 21 22 23 24 25 26
	// 27 28 29 30 31 32 33 34 35
	// 36 37 38 39 40 41 42 43 44
	// 45 46 47 48 49 50 51 52 53

	public static void open(final Player p, final EVault ev, final int page) {
		final CustomPermission cp = MoreVaultAPI.getCustomPermission(p, p.getWorld());
		final ManageMenu mm = setup(p.getUniqueId());
		final int current = ConfigManager.icons.keySet().size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = (int) MathUT.clamp(maxpage, 1L);
		final PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("player", new StringBuilder().append(p.getName()).toString());
		pu.add("owner", new StringBuilder().append(ev.getOwnerName()).toString());
		pu.add("description", ArrayUT.stringFromList(ev.getDescription(), "<nl>"));
		pu.add("cancel", ConfigManager.cancelword);
		mm.setTitle(pu.t(ManageMenu.title));
		mm.setRow(5);
		mm.build();
		final ItemStack tempCancelItem = pu.t(ManageMenu.CancelItem.clone());
		final ItemStack tempIconItem = pu.t(ManageMenu.IconItem.clone());
		final ItemStack cicon = ItemUT.getItem(ev.getIcon());
		tempIconItem.setType(cicon.getType());
		tempIconItem.setDurability(cicon.getDurability());
		mm.setItem(12, pu.t(ManageMenu.RenameItem.clone())).addAction(new Action() {
			@Override
			public void act() {
				if (!MoreVaultAPI.isAllowChangeName(p, cp)) {
					MessageUT.plmessage(p, Locales.getValue("not_permitted"));
					return;
				}
				PlayerListener.cachename.put(p.getUniqueId(), ev);
				MessageUT.plmessage(p, Locales.getValue("renaming_vault"), pu);
				p.closeInventory();
			}
		});
		mm.setItem(14, pu.t(tempIconItem)).addAction(new Action() {
			@Override
			public void act() {
				if (!MoreVaultAPI.isAllowChangeIcon(p, cp)) {
					MessageUT.plmessage(p, Locales.getValue("not_permitted"));
					return;
				}
				IconMenu.open(p, ev, 1, page);
			}
		});
		if (MoreVaultAPI.isAllowChangeDescription(p, cp)) {
			mm.setItem(22, pu.t(ManageMenu.DescriptionItem.clone())).addAction(new Action() {
				@Override
				public void act() {
					if (!MoreVaultAPI.isAllowChangeDescription(p, cp)) {
						MessageUT.plmessage(p, Locales.getValue("not_permitted"));
						return;
					}
					EditDescriptionMenu.open(p, ev, 1);
				}
			});
		}
		if (ConfigManager.device) {
			final double cost = MoreVaultAPI.getDeviceCost(cp);
			pu.add("cost", MathUT.format(cost));
			mm.setItem(30, pu.t(ManageMenu.DeviceItem.clone())).addAction(new Action() {
				@Override
				public void act() {
					if (!PermissionUT.check(p, "morevaultplus.get.device")) {
						return;
					}
					if (!EconomyUT.has(p, cost)) {
						pu.add("money", new StringBuilder().append(cost).toString());
						MessageUT.plmessage(p, Locales.getValue("insufficient_money"), pu);
						return;
					}
					ItemStack device = ConfigManager.deviceitem.clone();
					pu.add("id", new StringBuilder().append(ev.getId()).toString());
					pu.add("owner", ev.getOwnerName());
					final NBTItem ni = new NBTItem(device);
					ni.setString("vault-id", ev.getIdentifier());
					device = ni.getItem();
					device = pu.t(device);
					p.getInventory().addItem(new ItemStack[] { device });
					MessageUT.plmessage(p, pu.t(Locales.getValue("device_get")));
					EconomyUT.subtractBal(p, cost);
					p.closeInventory();
				}
			});
		}
		if (ConfigManager.link) {
			final double cost = MoreVaultAPI.getLinkCost(cp);
			pu.add("linkcost", MathUT.format(cost));
			boolean islinked = MoreVaultAPI.getLinkedVault(Bukkit.getOfflinePlayer(ev.getOwner())) == ev.getId();
			if (islinked) {
				if (ConfigManager.autosell) {
					boolean autosell = MoreVaultAPI.getAutoSell(Bukkit.getOfflinePlayer(ev.getOwner()));
					if (autosell) {
						mm.setItem(31, pu.t(ManageMenu.OffAutoSellItem.clone())).addAction(new Action() {
							@Override
							public void act() {
								if (!PermissionUT.check(p, "morevaultplus.autosell")) {
									return;
								}
								pu.add("id", new StringBuilder().append(ev.getId()).toString());
								pu.add("owner", ev.getOwnerName());
								Datamanager.setAutoSell(ev.getOwner(), false);
								MessageUT.plmessage(p, pu.t(Locales.getValue("vault_autosell_off")));
								p.closeInventory();
							}
						});
					} else {
						mm.setItem(31, pu.t(ManageMenu.OnAutoSellItem.clone())).addAction(new Action() {
							@Override
							public void act() {
								if (!PermissionUT.check(p, "morevaultplus.autosell")) {
									return;
								}
								pu.add("id", new StringBuilder().append(ev.getId()).toString());
								pu.add("owner", ev.getOwnerName());
								Datamanager.setAutoSell(ev.getOwner(), true);
								MessageUT.plmessage(p, pu.t(Locales.getValue("vault_autosell_on")));
								p.closeInventory();
							}
						});
					}
				}
				mm.setItem(32, pu.t(ManageMenu.UnlinkItem.clone())).addAction(new Action() {
					@Override
					public void act() {
						if (!PermissionUT.check(p, "morevaultplus.link.vault")) {
							return;
						}
						pu.add("id", new StringBuilder().append(ev.getId()).toString());
						pu.add("owner", ev.getOwnerName());
						Datamanager.setLinked(ev.getOwner(), -2);
						MessageUT.plmessage(p, pu.t(Locales.getValue("vault_unlinked")));
						p.closeInventory();
					}
				});
			} else {
				mm.setItem(32, pu.t(ManageMenu.LinkItem.clone())).addAction(new Action() {
					@Override
					public void act() {
						if (!PermissionUT.check(p, "morevaultplus.link.vault")) {
							return;
						}
						if (!EconomyUT.has(p, cost)) {
							pu.add("money", new StringBuilder().append(cost).toString());
							MessageUT.plmessage(p, Locales.getValue("insufficient_money"), pu);
							return;
						}
						EconomyUT.subtractBal(p, cost);
						pu.add("id", new StringBuilder().append(ev.getId()).toString());
						pu.add("owner", ev.getOwnerName());
						Datamanager.setLinked(ev.getOwner(), ev.getId());
						MessageUT.plmessage(p, pu.t(Locales.getValue("vault_linked")));
						p.closeInventory();
					}
				});
			}
		}
		for (int x = 0; x < 5; ++x) {
			mm.setItem(x + 38, tempCancelItem).addAction(new Action() {
				@Override
				public void act() {
					MainMenu.open(p, Bukkit.getOfflinePlayer(ev.getOwner()), page);
				}
			});
		}
		mm.open(p);
	}
}
