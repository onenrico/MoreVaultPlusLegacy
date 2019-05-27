//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.onenrico.morevaultplus.database.sql.Database;
import me.onenrico.morevaultplus.database.sql.MySQL;
import me.onenrico.morevaultplus.database.sql.SQLite;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.SqlUT;

public class Datamanager {
	public static Set<EVault> LoadedData;
	public static HashMap<UUID, Set<EVault>> cacheData;
	public static HashMap<UUID, Integer> cachePlayer;
	public static HashMap<UUID, Integer> cacheLinked;
	public static HashMap<UUID, Boolean> cacheAutoSell = new HashMap<>();
	static Core instance;
	public static Database db;
	public static ETable vault_table;
	public static ETable player_table;
	public static List<UUID> waiting;
	public static BukkitTask mysqlsync;
	private static boolean done;
	public static List<EVault> savecache;
	public static boolean yml;
	private static long last;

	static {
		Datamanager.LoadedData = new HashSet<>();
		Datamanager.cacheData = new HashMap<>();
		Datamanager.cachePlayer = new HashMap<>();
		Datamanager.cacheLinked = new HashMap<>();
		Datamanager.waiting = new ArrayList<>();
		Datamanager.done = false;
		Datamanager.savecache = new ArrayList<>();
		Datamanager.yml = false;
	}

	public Datamanager() {
		Datamanager.instance = Core.getThis();
	}

	public void reloadData() {
		setup();
	}

	public static Database getDB() {
		return Datamanager.db;
	}

	public static void setup() {
		MoreVaultAPI.lcp.clear();
		final Set<String> keys = Core.configplugin.getConfig().getConfigurationSection("custom-permission")
				.getKeys(false);
		for (final String key : keys) {
			MoreVaultAPI.lcp.add(new CustomPermission(key));
		}
		(Datamanager.vault_table = new ETable("MoreVault_VaultData")).setPrimaryKey("Identifier");
		Datamanager.vault_table.addColumn("Identifier", "varchar(255)");
		Datamanager.vault_table.addColumn("Id", "bigint");
		Datamanager.vault_table.addColumn("Owner", "varchar(36)");
		Datamanager.vault_table.addColumn("Space", "bigint");
		Datamanager.vault_table.addColumn("Name", "text");
		Datamanager.vault_table.addColumn("Icon", "varchar(50)");
		Datamanager.vault_table.addColumn("Content", "text");
		Datamanager.vault_table.addColumn("Balance", "float(2)");
		Datamanager.vault_table.addColumn("Exp", "float(2)");
		Datamanager.vault_table.addColumn("Description", "text");
		(Datamanager.player_table = new ETable("MoreVault_PlayerData")).setPrimaryKey("Owner");
		Datamanager.player_table.addColumn("Owner", "varchar(37)");
		Datamanager.player_table.addColumn("Maxvault", "bigint");
		Datamanager.player_table.addColumn("Linkedvault", "bigint");
		Datamanager.player_table.addColumn("Autosell", "tinyint");
		if (Datamanager.LoadedData != null) {
			Datamanager.LoadedData.clear();
		}
		if (Core.databaseconfig.getStr("database.type", "sqlite").equalsIgnoreCase("mysql")) {
			Datamanager.db = getMySQL();
			MessageUT.cmsg("Using MySQL");
		} else if (Core.databaseconfig.getStr("database.type", "sqlite").equalsIgnoreCase("sqlite")) {
			Datamanager.db = new SQLite();
			MessageUT.cmsg("Using SQLite Database");
		} else {
			if (!Core.databaseconfig.getStr("database.type", "sqlite").equalsIgnoreCase("yml")) {
				Bukkit.getServer().getLogger().warning(
						"Database config error ! wrong type: " + Core.databaseconfig.getStr("database.type", "sqlite"));
				return;
			}
			Datamanager.db = null;
			Datamanager.yml = true;
			MessageUT.cmsg("Using YML Database");
		}
		if (Datamanager.db != null) {
			Datamanager.db.load();
			Datamanager.done = true;
			if (Datamanager.db instanceof MySQL) {
				Datamanager.vault_table.create(new BukkitRunnable() {
					@Override
					public void run() {
						Datamanager.loadObject();
					}
				}, true);
				Datamanager.player_table.create(null, true);
				new BukkitRunnable() {
					@Override
					public void run() {
						reCheck();
					}
				}.runTask(Core.getThis());
			} else {
				Datamanager.vault_table.create(new BukkitRunnable() {
					@Override
					public void run() {
						Datamanager.loadObject();
					}
				}, false);
				Datamanager.player_table.create(null, false);
			}
		}
		reSave();
	}

	public static void queue(final EVault ev) {
		for (int i = 0; i < Datamanager.savecache.size(); ++i) {
			if (Datamanager.savecache.get(i).hashCode() == ev.hashCode()) {
				Datamanager.savecache.remove(i);
				break;
			}
		}
		Datamanager.savecache.add(ev);
	}

	private static void reSave() {
		if (!Datamanager.savecache.isEmpty()) {
			final EVault next = Datamanager.savecache.get(0);
			Datamanager.savecache.remove(0);
			new BukkitRunnable() {
				@Override
				public void run() {
					next.save();
					new BukkitRunnable() {
						@Override
						public void run() {
							reSave();
						}
					}.runTaskLater(Core.getThis(), 20L);
				}
			}.runTask(Core.getThis());
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					reSave();
				}
			}.runTaskLater(Core.getThis(), 20L);
		}
	}

	private static void reCheck() {
		if (!(Datamanager.db instanceof MySQL)) {
			return;
		}
		if (Datamanager.done) {
			try {
				if (Datamanager.db.connection.isClosed()) {
					Datamanager.db.load();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (!Datamanager.waiting.isEmpty()) {
			final UUID next = Datamanager.waiting.get(0);
			Datamanager.waiting.remove(0);
			for (final EVault ev : getOwnedVault(next)) {
				Datamanager.LoadedData.remove(ev);
			}
			Datamanager.cacheData.put(next, new HashSet<EVault>());
			Datamanager.cachePlayer.put(next, -1);
			final HashMap<String, Object> condition = new HashMap<>();
			condition.put("Owner", next.toString());
			Datamanager.vault_table.refresh(next, new BukkitRunnable() {
				PreparedStatement ps = null;
				ResultSet rs = null;

				@Override
				public void run() {
					try {
						final String sql = SqlUT.select(Datamanager.vault_table.getName(), "Identifier", condition);
						ps = Datamanager.db.connection.prepareStatement(sql);
						rs = ps.executeQuery();
						while (rs.next()) {
							final String key = rs.getString("Identifier");
							final EVault ev = new EVault(key);
							Datamanager.LoadedData.add(ev);
							Datamanager.addOwnedVault(ev);
						}
						if (ConfigManager.loadm) {
							MessageUT.plmessage(Bukkit.getPlayer(next), Locales.getValue("success_load"));
						}
					} catch (SQLException ex) {
						MessageUT.cmsg("A: " + ex);
						new BukkitRunnable() {
							@Override
							public void run() {
								reCheck();
							}
						}.runTaskLater(Core.getThis(), 5L);
						if (ps != null) {
							try {
								ps.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						if (rs != null) {
							try {
								rs.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						return;
					} finally {
						new BukkitRunnable() {
							@Override
							public void run() {
								reCheck();
							}
						}.runTaskLater(Core.getThis(), 5L);
						if (ps != null) {
							try {
								ps.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						if (rs != null) {
							try {
								rs.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							reCheck();
						}
					}.runTaskLater(Core.getThis(), 5L);
					if (ps != null) {
						try {
							ps.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}, true);
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					reCheck();
				}
			}.runTaskLater(Core.getThis(), 5L);
		}
	}

	public static MySQL getMySQL() {
		final String hostname = Core.databaseconfig.getStr("database.hostname", "unknown");
		final String port = Core.databaseconfig.getStr("database.port", "3306");
		final String database = Core.databaseconfig.getStr("database.database", "database");
		final String user = Core.databaseconfig.getStr("database.user", "localhost");
		final String password = Core.databaseconfig.getStr("database.password", "localhost");
		if (hostname.equalsIgnoreCase("unknown")) {
			return null;
		}
		return new MySQL(hostname, port, database, user, password);
	}

	public static int getCurrentOwned(final UUID uuid) {
		int result = Datamanager.cachePlayer.getOrDefault(uuid, -1);
		if (result != -1) {
			return result;
		}
		if (!Datamanager.yml) {
			final HashMap<String, Object> condition = new HashMap<>();
			condition.put("Owner", new StringBuilder().append(uuid).toString());
			final String sql = SqlUT.select(Datamanager.player_table.getName(), "*", condition);
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = Datamanager.db.connection.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					result = rs.getInt("Maxvault");
					Datamanager.cachePlayer.put(uuid, result);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				return result;
			} finally {
				if (Datamanager.db != null) {
					Datamanager.db.close(ps, rs);
				}
			}
			if (Datamanager.db != null) {
				Datamanager.db.close(ps, rs);
			}
		} else {
			result = Core.databaseconfig
					.getInt(String.valueOf(Datamanager.player_table.getName()) + "." + uuid + "." + "Maxvault", -1);
			Datamanager.cachePlayer.put(uuid, result);
		}
		return result;
	}

	public static Set<EVault> getOwnedVault(final UUID player) {
		final Set<EVault> result = Datamanager.cacheData.getOrDefault(player, new HashSet<EVault>());
		return result;
	}

	public static EVault getVaultByID(final UUID player, int id) {
		if (id <= 0) {
			id = 1;
		}
		for (final EVault ev : Datamanager.cacheData.getOrDefault(player, new HashSet<EVault>())) {
			if (ev.getId() == id) {
				if (ev.getInv() == null) {
					if (Bukkit.getPlayer(player) != null) {
						ev.setRow(MoreVaultAPI
								.getSpace(MoreVaultAPI.getCustomPermission(Bukkit.getOfflinePlayer(player).getPlayer(),
										Bukkit.getOfflinePlayer(player).getPlayer().getWorld())));
					} else {
						ev.setRow(6);
					}
					ev.build();
					ev.fromString(Datamanager.vault_table.getValue(ev.getIdentifier(), "Content"));
				}
				return ev;
			}
		}
		EVault ev = new EVault(player, id);
		return ev;
	}

	public static void addOwnedVault(final EVault ev) {
		final Set<EVault> result = Datamanager.cacheData.getOrDefault(ev.getOwner(), new HashSet<EVault>());
		if (result.isEmpty()) {
			result.add(ev);
			Datamanager.cacheData.put(ev.getOwner(), result);
		} else {
			result.add(ev);
		}
	}

	public static List<EVault> getLoaded() {
		return new ArrayList<>(Datamanager.LoadedData);
	}

	public static void addData(final EVault newdata) {
		Datamanager.LoadedData.add(newdata);
		addOwnedVault(newdata);
		save(newdata);
	}

	public static void setLinked(final UUID uuid, final int id) {
		Datamanager.cacheLinked.put(uuid, id);
		addCurrentOwned(uuid, 0);
	}

	public static int getLinked(final UUID uuid) {
		int result = Datamanager.cacheLinked.getOrDefault(uuid, -1);
		if (result != -1) {
			return result;
		}
		if (!Datamanager.yml) {
			final HashMap<String, Object> condition = new HashMap<>();
			condition.put("Owner", new StringBuilder().append(uuid).toString());
			final String sql = SqlUT.select(Datamanager.player_table.getName(), "Linkedvault", condition);
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = Datamanager.db.connection.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					result = rs.getInt("Linkedvault");
					Datamanager.cacheLinked.put(uuid, result);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				return result;
			} finally {
				Datamanager.db.close(ps, rs);
			}
			Datamanager.db.close(ps, rs);
		} else {
			result = Core.databaseconfig.getInt(Datamanager.player_table + "." + uuid + "." + "Linkedvault", -1);
			Datamanager.cacheLinked.put(uuid, result);
		}
		return result;
	}

	public static void setAutoSell(final UUID uuid, final boolean state) {
		Datamanager.cacheAutoSell.put(uuid, state);
		addCurrentOwned(uuid, 0);
	}

	public static Boolean getAutoSell(final UUID uuid) {
		Boolean result = Datamanager.cacheAutoSell.getOrDefault(uuid, null);
		if (result != null) {
			return result;
		}
		if (!Datamanager.yml) {
			final HashMap<String, Object> condition = new HashMap<>();
			condition.put("Owner", new StringBuilder().append(uuid).toString());
			final String sql = SqlUT.select(Datamanager.player_table.getName(), "Autosell", condition);
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = Datamanager.db.connection.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					result = rs.getInt("Autosell") == 1 ? true : false;
					Datamanager.cacheAutoSell.put(uuid, result);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				return result;
			} finally {
				Datamanager.db.close(ps, rs);
			}
			Datamanager.db.close(ps, rs);
		} else {
			result = Core.databaseconfig.getBool(Datamanager.player_table + "." + uuid + "." + "Autosell", false);
			Datamanager.cacheAutoSell.put(uuid, result);
		}
		if (result == null) {
			return false;
		}
		return result;
	}

	public static void addCurrentOwned(final UUID uuid, final int amount) {
		final CustomPermission cp = MoreVaultAPI.getCustomPermission(Bukkit.getOfflinePlayer(uuid),
				Bukkit.getWorlds().get(0));
		final int currentowned = MoreVaultAPI.getOwnedVault(Bukkit.getOfflinePlayer(uuid), cp);
		setCurrentOwned(uuid, amount + currentowned);
	}

	public static void setCurrentOwned(final UUID uuid, final int amount) {
		if (!Datamanager.yml) {
			Datamanager.cachePlayer.put(uuid, amount);
			new BukkitRunnable() {
				@Override
				public void run() {
					final HashMap<String, Object> columns = new HashMap<>();
					columns.put("Owner", uuid.toString());
					columns.put("Maxvault", amount);
					columns.put("Linkedvault", Datamanager.getLinked(uuid));
					columns.put("Autosell", Datamanager.getAutoSell(uuid) ? 1 : 0);
					SqlUT.executeUpdate(SqlUT.insert(Datamanager.player_table.getName(), columns));
				}
			}.runTask(Core.getThis());
		} else {
			final FileConfiguration fc = Core.databaseconfig.getConfig();
			final String p = Datamanager.player_table + "." + uuid + ".";
			final String key = "Maxvault";
			fc.set(String.valueOf(p) + key, amount);
			Core.databaseconfig.save();
			Datamanager.cachePlayer.put(uuid, amount);
		}
	}

	public static List<String> addQueue(final EVault data) {
		return addQueue(null, data);
	}

	public static List<String> addQueue(List<String> queue, final EVault data) {
		if (queue == null || queue.isEmpty()) {
			queue = new ArrayList<>();
		}
		final HashMap<String, Object> columns = data.getValues();
		if (!Datamanager.yml) {
			queue.add(SqlUT.insert(Datamanager.vault_table.getName(), columns));
		} else {
			final FileConfiguration fc = Core.databaseconfig.getConfig();
			final String name = data.getIdentifier();
			final String p = Datamanager.vault_table + "." + name + ".";
			for (final String key : Datamanager.vault_table.getColumns().keySet()) {
				fc.set(String.valueOf(p) + key, columns.get(key));
			}
		}
		return queue;
	}

	public static synchronized void executeQueue(final List<String> queue) {
		if (!Datamanager.yml) {
			new BukkitRunnable() {
				@Override
				public void run() {
					SqlUT.executeBatch(queue);
				}
			}.runTask(Core.getThis());
		} else {
			Core.databaseconfig.save();
		}
	}

	public static void executeQueue(final List<String> queue, final Connection con) {
		if (!Datamanager.yml) {
			SqlUT.executeBatch(queue, con);
		} else {
			Core.databaseconfig.save();
		}
	}

	public static void save(final EVault newdata) {
		addOwnedVault(newdata);
		executeQueue(addQueue(newdata));
	}

	public static void deleteData(final EVault data, final BukkitRunnable callback) {
		if (Datamanager.LoadedData.contains(data)) {
			Datamanager.LoadedData.remove(data);
		}
		final HashMap<String, Object> condition = new HashMap<>();
		condition.put("Identifier", data.getIdentifier());
		if (!Datamanager.yml) {
			SqlUT.executeUpdate(SqlUT.delete(Datamanager.vault_table.getName(), condition));
		} else {
			Core.databaseconfig.config.set(Datamanager.vault_table + "." + data.getIdentifier(), (Object) null);
			Core.databaseconfig.save();
		}
	}

	public static void hit() {
		final long hasil = System.currentTimeMillis() - Datamanager.last;
		final double r = hasil / 1000.0;
		for (final EVault ee : Datamanager.LoadedData) {
			addOwnedVault(ee);
		}
		final int j = Datamanager.LoadedData.size();
		MessageUT.cmsg(String.valueOf(j) + " &eData Loaded");
		MessageUT.cmsg("Load Database Completed in &a" + r + "s");
		if (Datamanager.yml) {
			Core.databaseconfig.save();
		}
	}

	public static void loadObject() {
		Datamanager.last = System.currentTimeMillis();
		MessageUT.cmsg("Start Load Database");
		new BukkitRunnable() {
			@Override
			public void run() {
				final HashMap<String, HashMap<String, String>> databaseKey = Datamanager.vault_table.getLoadedValue();
				final int count = databaseKey.size();
				if (count == 0) {
					MessageUT.cmsg("No Data Found");
					return;
				}
				MessageUT.cmsg("Loading " + count + " Vault Data...");
				MessageUT.cmsg("Please wait...");
				for (final String key : databaseKey.keySet()) {
					final EVault ee = new EVault(key);
					Datamanager.LoadedData.add(ee);
				}
				Datamanager.hit();
			}
		}.runTask(Core.getThis());
	}
}
