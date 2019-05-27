package me.onenrico.morevaultplus.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.nbt.NBTItem;

public class SwapListener implements Listener {
	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent e) {
		final ItemStack device = ConfigManager.deviceitem;
		ItemStack item = e.getOffHandItem();
		if (item.getType().equals(device.getType())) {
			final NBTItem ni = new NBTItem(item);
			if (ni.hasKey("vault-id") == null) {
				e.setCancelled(true);
				return;
			}
		}
	}
}
