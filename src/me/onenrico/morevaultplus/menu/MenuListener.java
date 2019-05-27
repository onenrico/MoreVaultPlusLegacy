//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.Christmas;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.utils.EMaterial;
import me.onenrico.morevaultplus.utils.MessageUT;

public class MenuListener implements Listener {
	@EventHandler
	public void onDrag(final InventoryDragEvent event) {
		final Inventory top = event.getView().getTopInventory();
		if (top != null && top.getHolder() instanceof GUIMenu && event.getWhoClicked() instanceof Player) {
			final GUIMenu gm = (GUIMenu) top.getHolder();
			if (gm instanceof VaultMenu) {
				CustomPermission cp = MoreVaultAPI.getCustomPermission((Player) event.getWhoClicked(),
						event.getWhoClicked().getWorld());
				String str = "" + event.getOldCursor().getType();
				str += event.getOldCursor().getDurability() == 0 ? "" : ":" + event.getOldCursor().getDurability();
				EMaterial cursor = EMaterial.fromString(str);
				if (cursor != null) {
					for (ItemStack i : cp.getBlacklistitem()) {
						if (cursor.isSameMaterial(i)) {
							event.setCancelled(true);
							MessageUT.plmessage((Player) event.getWhoClicked(), Locales.getValue("item-blacklisted"));
							return;
						}
					}
				}
			}
			if (gm.isEditable()) {
				return;
			}
			final Inventory inv = event.getInventory();
			for (final int slot : event.getRawSlots()) {
				if (slot < inv.getSize()) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(final InventoryClickEvent event) {
		if (event.getSlotType().equals(InventoryType.SlotType.OUTSIDE) && event.getClickedInventory() == null) {
			return;
		}
		final Inventory top = event.getView().getTopInventory();
		if (top == null || !(top.getHolder() instanceof GUIMenu)) {
			return;
		}
		final GUIMenu gm = (GUIMenu) top.getHolder();
		if (gm instanceof VaultMenu && event.getClickedInventory().equals(event.getView().getBottomInventory())
				&& event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)) {
			CustomPermission cp = MoreVaultAPI.getCustomPermission((Player) event.getWhoClicked(),
					event.getWhoClicked().getWorld());
			String str = "" + event.getCurrentItem().getType();
			str += event.getCurrentItem().getDurability() == 0 ? "" : ":" + event.getCurrentItem().getDurability();
			EMaterial cursor = EMaterial.fromString(str);
			if (cursor != null) {
				for (ItemStack i : cp.getBlacklistitem()) {
					if (cursor.isSameMaterial(i)) {
						event.setCancelled(true);
						MessageUT.plmessage((Player) event.getWhoClicked(), Locales.getValue("item-blacklisted"));
						return;
					}
				}
			}
		} else if (gm instanceof VaultMenu && event.getClick().equals(ClickType.NUMBER_KEY)
				&& event.getClickedInventory().equals(event.getView().getTopInventory())) {
			int num = event.getHotbarButton();
			ItemStack item = event.getWhoClicked().getInventory().getItem(num);
			if (item != null && !item.getType().equals(Material.AIR)) {
				CustomPermission cp = MoreVaultAPI.getCustomPermission((Player) event.getWhoClicked(),
						event.getWhoClicked().getWorld());
				String str = "" + item.getType();
				str += item.getDurability() == 0 ? "" : ":" + item.getDurability();
				EMaterial cursor = EMaterial.fromString(str);
				if (cursor != null) {
					for (ItemStack i : cp.getBlacklistitem()) {
						if (cursor.isSameMaterial(i)) {
							event.setCancelled(true);
							MessageUT.plmessage((Player) event.getWhoClicked(), Locales.getValue("item-blacklisted"));
							return;
						}
					}
				}
			}
		}
		event.setCancelled(true);
		if (event.getClickedInventory().equals(event.getView().getBottomInventory())) {
			if (gm.isEditable()) {
				event.setCancelled(false);
			}
			return;
		}
		final int slot = event.getSlot();
		final MenuItem mi = gm.getMenuItem(slot);
		if (mi == null) {
			if (gm.isStealable()) {
				event.setCancelled(false);
			}
			return;
		}
		MessageUT.plmessage((Player) event.getWhoClicked(), Locales.getValue("gui_click"));
		for (final Action act : mi.getActions()) {
			if (act.valid(event.getClick())) {
				act.run();
			}
		}
	}

	@EventHandler
	public void onDrop(final PlayerDropItemEvent e) {
		if (GUIMenu.isSecured(e.getItemDrop().getItemStack())) {
			e.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onClose(final InventoryCloseEvent e) {
		final Inventory inv = e.getInventory();
		if (inv != null && inv.getHolder() instanceof GUIMenu) {
			final GUIMenu gm = (GUIMenu) inv.getHolder();
			Christmas.opening.remove(e.getPlayer());
			if (gm.getCloseaction() != null) {
				final Action ac = gm.getCloseaction();
				gm.setCloseaction(null);
				ac.run();
			}
			if (gm.getBefore() != null) {
				gm.getBefore().open((Player) e.getPlayer());
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				final Player p = (Player) e.getPlayer();
				if (!p.isOnline()) {
					return;
				}
				final ItemStack[] inven = p.getInventory().getContents();
				if (inven.length < 1) {
					return;
				}
				ItemStack[] array;
				for (int length = (array = inven).length, j = 0; j < length; ++j) {
					final ItemStack i = array[j];
					if (!p.isOnline()) {
						return;
					}
					if (i != null && GUIMenu.isSecured(i)) {
						e.getPlayer().getInventory().remove(i);
					}
				}
			}
		}.runTask(Core.getThis());
	}
}
