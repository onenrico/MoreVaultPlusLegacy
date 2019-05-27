//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.listener;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.gui.MainMenu;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.menu.VaultMenu;
import me.onenrico.morevaultplus.nbt.NBTItem;
import me.onenrico.morevaultplus.utils.PermissionUT;
import me.onenrico.morevaultplus.utils.PlayerUT;
import me.onenrico.morevaultplus.utils.ReflectionUT;
import me.onenrico.morevaultplus.utils.StringUT;

public class InteractListener implements Listener {
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteractEntity(final PlayerInteractEntityEvent e) {
		final Player p = e.getPlayer();
		if (p.getOpenInventory().getTopInventory() == null) {
			return;
		}
		final EntityType localEntityType = e.getRightClicked().getType();
		if ((localEntityType == EntityType.VILLAGER || localEntityType == EntityType.MINECART)
				&& p.getOpenInventory().getTopInventory().getHolder() instanceof VaultMenu) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void interaction(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		final ItemStack item = PlayerUT.getHand(p);
		final boolean oldmethod = ReflectionUT.NUMBER_VERSION < 19;
		if (!oldmethod) {
			try {
				if (!event.getHand().equals(EquipmentSlot.HAND)) {
					return;
				}
			} catch (NullPointerException ex) {
			}
		}
		final ItemStack device = ConfigManager.deviceitem;
		if (item.getType().equals(device.getType())) {
			if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
					|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				final NBTItem ni = new NBTItem(item);
				if (ni.hasKey("vault-id") == null) {
					return;
				}
				if (ni.hasKey("vault-id")) {
					event.setCancelled(true);
					if (!Core.configplugin.getBool("device-enabled", false)) {
						return;
					}
					if (!PermissionUT.check(p, "morevaultplus.use.device")) {
						return;
					}
					final String data = ni.getString("vault-id");
					MoreVaultAPI.openVault(p, data);
				}
			}
		} else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (event.isCancelled()) {
				return;
			}
			if (event.getClickedBlock().getType().equals(Material.ENDER_CHEST) && ConfigManager.endern) {
				if (!PermissionUT.has(p, "morevaultplus.use.enderchest")) {
					return;
				}
				event.setCancelled(true);
				MainMenu.open(p, p, 1);
			}
			final Boolean enable = Core.configplugin.getBool("sign-enabled", true);
			if (!enable) {
				return;
			}
			if (blockisSign(event)) {
				final Sign sign = (Sign) event.getClickedBlock().getState();
				final String first = StringUT.t(sign.getLine(0));
				if (first.equals(StringUT.t(ConfigManager.signline))
						&& PermissionUT.check(p, "morevaultplus.use.sign")) {
					MainMenu.open(event.getPlayer(), event.getPlayer(), 1);
				}
			}
		}
	}

	@EventHandler
	public void signPlace(final SignChangeEvent event) {
		final String first = event.getLine(0);
		if (first.equalsIgnoreCase("[mvp]") && PermissionUT.check(event.getPlayer(), "morevaultplus.create.sign")) {
			final String sign = ConfigManager.signline;
			event.setLine(0, StringUT.t(sign));
		}
	}

	private Boolean blockisSign(final PlayerInteractEvent event) {
		return event.getClickedBlock().getType().toString().contains("SIGN");
	}
}
