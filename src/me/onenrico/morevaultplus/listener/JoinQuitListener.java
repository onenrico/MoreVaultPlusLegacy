//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.listener;

import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.database.sql.MySQL;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.ReflectionUT;
import me.onenrico.morevaultplus.utils.StringUT;

public class JoinQuitListener implements Listener {
	@EventHandler
	public void onLogout(final PlayerQuitEvent e) {
		PlayerListener.clearCache(e.getPlayer().getUniqueId());
		if (Datamanager.waiting.contains(e.getPlayer().getUniqueId())) {
			Datamanager.waiting.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		if (Datamanager.getDB() instanceof MySQL) {
			if (Datamanager.waiting.contains(p.getUniqueId())) {
				return;
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!e.getPlayer().isOnline()) {
						if (Datamanager.waiting.contains(p.getUniqueId())) {
							Datamanager.waiting.remove(p.getUniqueId());
						}
						return;
					}
					if (!Datamanager.waiting.contains(p.getUniqueId())) {
						Datamanager.waiting.add(p.getUniqueId());
					}
				}
			}.runTaskLater(Core.getThis(), 20L);
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (Datamanager.waiting.contains(e.getPlayer().getUniqueId())) {
						Datamanager.waiting.remove(e.getPlayer().getUniqueId());
					}
					final CustomPermission cp = MoreVaultAPI.getCustomPermission(p, p.getWorld());
					Datamanager.waiting.add(e.getPlayer().getUniqueId());
					Datamanager.vault_table.refresh(e.getPlayer().getUniqueId(), new BukkitRunnable() {
						@Override
						public void run() {
							for (final EVault ev : Datamanager.cacheData.getOrDefault(e.getPlayer().getUniqueId(),
									new HashSet<EVault>())) {
								if (StringUT.d(ev.getTitle()).contains("1.13") && ReflectionUT.NUMBER_VERSION < 113) {
									ev.setRow(MoreVaultAPI.getSpace(cp) / 9);
								} else {
									if (ev.getInv() != null) {
										continue;
									}
									ev.setRow(MoreVaultAPI.getSpace(cp) / 9);
									ev.build();
									ev.fromString(Datamanager.vault_table.getValue(ev.getIdentifier(), "Content"));
								}
							}
							Datamanager.waiting.remove(e.getPlayer().getUniqueId());
							if (ConfigManager.loadm) {
								MessageUT.plmessage(e.getPlayer(), Locales.getValue("success_load"));
							}
						}
					}, false);
				}
			}.runTask(Core.getThis());
		}
	}
}
