//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.utils.Pair;
import me.onenrico.morevaultplus.utils.SqlUT;

public class ETable {
	private String name;
	private HashMap<String, String> columns;
	private HashMap<String, HashMap<String, String>> loadedValue;
	private Set<EObject> loadedData;
	private HashMap<UUID, Set<EObject>> playerOwnedData;
	private String primary;

	public ETable(final String name) {
		columns = new HashMap<>();
		loadedValue = new HashMap<>();
		loadedData = new HashSet<>();
		playerOwnedData = new HashMap<>();
		primary = "";
		this.name = name;
	}

	public void setPrimaryKey(final String data) {
		primary = data;
	}

	public String getPrimaryKey() {
		return primary;
	}

	public void addColumn(final String data, final String type) {
		columns.put(data, type);
	}

	public void removeColumn(final String data, final String type) {
		columns.remove(data);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public HashMap<String, String> getColumns() {
		return columns;
	}

	public void setColumns(final HashMap<String, String> columns) {
		this.columns = columns;
	}

	public Set<EObject> getLoadedData() {
		return loadedData;
	}

	public void setLoadedData(final Set<EObject> loadedData) {
		this.loadedData = loadedData;
	}

	public HashMap<UUID, Set<EObject>> getPlayerOwnedData() {
		return playerOwnedData;
	}

	public void setPlayerOwnedData(final HashMap<UUID, Set<EObject>> playerOwnedData) {
		this.playerOwnedData = playerOwnedData;
	}

	public HashMap<String, HashMap<String, String>> getLoadedValue() {
		return loadedValue;
	}

	public void setLoadedValue(final HashMap<String, HashMap<String, String>> loadedValue) {
		this.loadedValue = loadedValue;
	}

	public String getValue(final String identifier, final String column) {
		final HashMap<String, String> value = loadedValue.getOrDefault(identifier, null);
		if (value != null) {
			return value.getOrDefault(column, "");
		}
		return "";
	}

	public String getPrimary() {
		return primary;
	}

	public void setPrimary(final String primary) {
		this.primary = primary;
	}

	@Override
	public String toString() {
		return getName();
	}

	public void create(final BukkitRunnable callback, final Boolean mysql) {
		if (Datamanager.getDB() != null) {
			final String tablesql = SqlUT.createTable(getName(), getColumns(), getPrimaryKey());
			if (mysql) {
				SqlUT.executeUpdate(
						String.valueOf(tablesql.substring(0, tablesql.length() - 1)) + " CHARACTER SET = utf8;");
			} else {
				SqlUT.executeUpdate(tablesql);
			}
			for (final String column : getColumns().keySet()) {
				if (!SqlUT.columnExist(getName(), column)) {
					SqlUT.addColumn(getName(), column, getColumns().get(column));
				}
			}
			new BukkitRunnable() {
				PreparedStatement ps = null;
				ResultSet rs = null;

				@Override
				public void run() {
					try {
						final String sql = SqlUT.select(ETable.this.getName(), "*");
						final Pair<PreparedStatement, ResultSet> pair = SqlUT.executeQuery(sql);
						ps = pair.getLeft();
						rs = pair.getRight();
						while (rs.next()) {
							final HashMap<String, String> value = new HashMap<>();
							for (final String column : columns.keySet()) {
								value.put(column, rs.getString(column));
							}
							loadedValue.put(value.remove(primary), value);
						}
						if (callback != null) {
							callback.run();
						}
					} catch (SQLException ex) {
						Bukkit.getLogger().info(ex.getMessage());
						return;
					} finally {
						ETable.this.close(ps, rs);
					}
					ETable.this.close(ps, rs);
				}
			}.runTask(Core.getThis());
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					final FileConfiguration cp = Core.databaseconfig.getConfig();
					ConfigurationSection cs = cp.getConfigurationSection(name);
					if (cs == null) {
						cs = cp.createSection(name);
					}
					for (final String identifier : cs.getKeys(false)) {
						final String pref = String.valueOf(identifier) + ".";
						final HashMap<String, String> value = new HashMap<>();
						for (final String column : columns.keySet()) {
							value.put(column, cs.getString(String.valueOf(pref) + column));
						}
						value.put(primary, identifier);
						loadedValue.put(value.remove(primary), value);
					}
					if (callback != null) {
						callback.run();
					}
				}
			}.runTask(Core.getThis());
		}
	}

	public void refresh(final UUID player, final BukkitRunnable callback, final Boolean mysql) {
		if (Datamanager.getDB() != null) {
			new BukkitRunnable() {
				PreparedStatement ps = null;
				ResultSet rs = null;

				@Override
				public void run() {
					try {
						final HashMap<String, Object> condition = new HashMap<>();
						condition.put("Owner", player.toString());
						final String sql = SqlUT.select(ETable.this.getName(), "*", condition);
						final Pair<PreparedStatement, ResultSet> pair = SqlUT.executeQuery(sql);
						ps = pair.getLeft();
						rs = pair.getRight();
						while (rs.next()) {
							final HashMap<String, String> value = new HashMap<>();
							for (final String column : columns.keySet()) {
								value.put(column, rs.getString(column));
							}
							loadedValue.put(value.remove(primary), value);
						}
						if (callback != null) {
							callback.run();
						}
					} catch (SQLException ex) {
						Bukkit.getLogger().info(ex.getMessage());
						return;
					} finally {
						ETable.this.close(ps, rs);
					}
					ETable.this.close(ps, rs);
				}
			}.runTask(Core.getThis());
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					final FileConfiguration cp = Core.databaseconfig.getConfig();
					ConfigurationSection cs = cp.getConfigurationSection(name);
					if (cs == null) {
						cs = cp.createSection(name);
					}
					for (final String identifier : cs.getKeys(false)) {
						if (!identifier.contains(player.toString())) {
							continue;
						}
						final String pref = String.valueOf(identifier) + ".";
						final HashMap<String, String> value = new HashMap<>();
						for (final String column : columns.keySet()) {
							value.put(column, cs.getString(String.valueOf(pref) + column));
						}
						value.put(primary, identifier);
						loadedValue.put(value.remove(primary), value);
					}
					if (callback != null) {
						callback.run();
					}
				}
			}.runTask(Core.getThis());
		}
	}

	protected void close(PreparedStatement ps, ResultSet rs) {
		Label_0032: {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
					break Label_0032;
				} finally {
					ps = null;
				}
				ps = null;
			}
		}
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			} finally {
				rs = null;
			}
			rs = null;
		}
	}
}
