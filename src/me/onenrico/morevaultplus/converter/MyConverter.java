//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.converter;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.database.sql.Database;
import me.onenrico.morevaultplus.database.sql.MySQL;
import me.onenrico.morevaultplus.database.sql.SQLite;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.SqlUT;

public class MyConverter {
	public static void convert(final Player p, final String to) {
		Database todb = null;
		boolean newyml = false;
		if (to.equalsIgnoreCase("sqlite")) {
			if (!Datamanager.yml && Datamanager.db instanceof SQLite) {
				MessageUT.plmessage(p, "&c&lError! &fDatabase already in &c" + to);
				return;
			}
			todb = new SQLite();
			todb.load();
			MessageUT.plmessage(p, "&a&lConnected! &fSuccessfully connected to database !");
		} else if (to.equalsIgnoreCase("mysql")) {
			if (!Datamanager.yml && Datamanager.db instanceof MySQL) {
				MessageUT.plmessage(p, "&c&lError! &fDatabase already in &c" + to);
				return;
			}
			todb = Datamanager.getMySQL();
			try {
				todb.load();
			} catch (Exception ex) {
				MessageUT.plmessage(p, "&c&lError! &fcannot connect to MySQL database !");
				MessageUT.plmessage(p, "&c&lError! &fplease config right credential on database.yml");
				return;
			}
			MessageUT.plmessage(p, "&a&lConnected! &fSuccessfully connected to database !");
		} else if (to.equalsIgnoreCase("yml")) {
			if (Datamanager.yml) {
				MessageUT.plmessage(p, "&c&lError! &fDatabase already in &c" + to);
				return;
			}
			newyml = true;
			MessageUT.plmessage(p, "&a&lConnected! &fSuccessfully connected to database !");
		}
		final List<EVault> loaded = new ArrayList<>(Datamanager.LoadedData);
		final int total = loaded.size();
		MessageUT.plmessage(p,
				"&c&lPlease Wait! &fStarting database convert to " + to + " &8[&fTotal: &e" + total + " &fdata&8]");
		final boolean cacheyml = newyml;
		Connection cn = null;
		if (!newyml) {
			cn = todb.connection;
		}
		final Connection con = cn;
		final List<UUID> playerloaded = new ArrayList<>(Datamanager.cacheData.keySet());
		new BukkitRunnable() {
			int index = 0;
			List<String> total = new ArrayList<>();
			HashMap<String, Object> columns = new HashMap<>();

			@Override
			public void run() {
				if (index >= playerloaded.size()) {
					if (!cacheyml) {
						SqlUT.executeBatch(total, con);
					} else {
						Core.databaseconfig.save();
					}
					cancel();
					new BukkitRunnable() {
						int index = 0;
						List<String> total = new ArrayList<>();

						@Override
						public void run() {
							if (index >= loaded.size()) {
								if (!cacheyml) {
									SqlUT.executeBatch(total, con);
								} else {
									Core.databaseconfig.save();
								}
								MessageUT.plmessage(p, "&a&lCompleted! &fdatabase convert complete !");
								MessageUT.plmessage(p, "&a&lNext Step:");
								MessageUT.plmessage(p, "&a1. Stop the server&f");
								MessageUT.plmessage(p,
										"&a2. Set database type to &e\"" + to + "\" &fin &6database.yml&f");
								MessageUT.plmessage(p, "&a3. Save database.yml file&f");
								MessageUT.plmessage(p, "&a4. Start the server&f");
								cancel();
								return;
							}
							final EVault ev = loaded.get(index++);
							if (ev.getInv() == null) {
								final Player owner = Bukkit.getPlayer(ev.getOwner());
								if (owner != null) {
									ev.setRow(MoreVaultAPI
											.getSpace(MoreVaultAPI.getCustomPermission(owner, owner.getWorld())));
								} else {
									ev.setRow(6);
								}
								ev.build();
								ev.fromString(Datamanager.vault_table.getValue(ev.getIdentifier(), "Content"));
							}
							if (!cacheyml) {
								total = Datamanager.addQueue(total, ev);
							} else {
								columns = ev.getValues();
								final FileConfiguration fc = Core.databaseconfig.getConfig();
								final String name = ev.getIdentifier();
								final String p = Datamanager.vault_table + "." + name + ".";
								for (final String key : Datamanager.vault_table.getColumns().keySet()) {
									fc.set(String.valueOf(p) + key, columns.get(key));
								}
							}
						}
					}.runTaskTimerAsynchronously(Core.getThis(), 0L, 0L);
					return;
				}
				final UUID uid = playerloaded.get(index++);
				if (!cacheyml) {
					columns.put("Owner", uid.toString());
					columns.put("Maxvault",
							new StringBuilder(String.valueOf(Datamanager.cacheData.get(uid).size())).toString());
					columns.put("Linkedvault", Datamanager.getLinked(uid));
					total.add(SqlUT.insert(Datamanager.player_table.getName(), columns));
				} else {
					final FileConfiguration fc = Core.databaseconfig.getConfig();
					final String pl = String.valueOf(Datamanager.player_table.getName()) + "." + uid + ".";
					String key = "Maxvault";
					fc.set(String.valueOf(pl) + key, Datamanager.cacheData.get(uid).size());
					key = "Linkedvault";
					fc.set(String.valueOf(pl) + key, Datamanager.getLinked(uid));
				}
			}
		}.runTaskTimer(Core.getThis(), 0L, 0L);
	}
}
