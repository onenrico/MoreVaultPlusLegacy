//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.EMaterial;
import me.onenrico.morevaultplus.utils.EconomyUT;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.ParticleUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;

@SuppressWarnings("deprecation")
public class PickupListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPick(final PlayerPickupItemEvent e) {
		if (ConfigManager.link) {
			final Player p = e.getPlayer();
			final int id = MoreVaultAPI.getLinkedVault(p);
			if (id > 0) {
				final EVault ev = Datamanager.getVaultByID(p.getUniqueId(), id);
				final Inventory inv = ev.getInv();
				ItemStack fill;
				if (e.getItem().getItemStack().getType().equals(Material.DIRT)) {
					fill = ItemUT.createItem(Material.COBBLESTONE, "morevaultpluspropertyitem");
				} else {
					fill = ItemUT.createItem(Material.DIRT, "morevaultpluspropertyitem");
				}
				if (ConfigManager.nobottom) {
					for (int i = inv.getSize() - 9; i < inv.getSize(); ++i) {
						inv.setItem(i, fill);
					}
				}
				if (ConfigManager.vaultn) {
					inv.setItem(inv.getSize() - 9, fill);
					inv.setItem(inv.getSize() - 1, fill);
				}
				if (ConfigManager.vaulted) {
					inv.setItem(inv.getSize() - 4, fill);
				}
				if (ConfigManager.vaultd) {
					inv.setItem(inv.getSize() - 6, fill);
				}
				final int firstempty = inv.firstEmpty();
				if (firstempty != -1) {
					e.setCancelled(true);
					inv.addItem(new ItemStack[] { e.getItem().getItemStack() });
					Datamanager.queue(ev);
					if (ConfigManager.linkparticle) {
						ParticleUT.send(p, "SNOWBALL", e.getItem().getLocation(), 10, Boolean.valueOf(false));
						ParticleUT.send(p, "CLOUD", e.getItem().getLocation().add(0.0, 1.4, 0.0), 0,
								Boolean.valueOf(false));
					}
					e.getItem().remove();
				} else {
					if (ConfigManager.autosell && MoreVaultAPI.getAutoSell(p)) {
						double total = 0;
						for (int i = 0; i < inv.getSize(); ++i) {
							ItemStack item = inv.getItem(i);
							if (item != null && (ItemUT.getName(item) == null || ItemUT.getName(item).isEmpty())) {
								EMaterial current = EMaterial.fromMaterial(item.getType());
								if (current.isUnique(current)) {
									String search = current.m + ":" + item.getDurability();
									current = EMaterial.fromString(search);
								}
								if (ConfigManager.price_data.containsKey(current)) {
									double price = ConfigManager.price_data.getOrDefault(current, 0d)
											* item.getAmount();
									inv.setItem(i, new ItemStack(Material.AIR));
									total += price;

								}
							}
						}
						EconomyUT.addBal(p, total);
						final PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
						pu.add("total", total);
						MessageUT.plmessage(p, Locales.getValue("auto_sell"), pu);
						return;
					}
				}
			}
		}
	}
}
