//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.menu.VaultMenu;

public class MoveListener implements Listener {
	@EventHandler
	public void onTeleport(final PlayerTeleportEvent e) {
		if (e.getPlayer().getOpenInventory().getTopInventory() != null
				&& e.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof VaultMenu) {
			e.getPlayer().closeInventory();
		}
	}

	@EventHandler
	public void onMove(final PlayerMoveEvent e) {
		if (ConfigManager.cancelon && e.getPlayer().getOpenInventory().getTopInventory() != null) {
			final double x = e.getFrom().getX();
			final double y = e.getFrom().getY();
			final double z = e.getFrom().getZ();
			final double x2 = e.getTo().getX();
			final double y2 = e.getTo().getY();
			final double z2 = e.getTo().getZ();
			if ((x != x2 || y != y2 || z != z2)
					&& e.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof VaultMenu) {
				e.getPlayer().closeInventory();
			}
		}
	}
}
