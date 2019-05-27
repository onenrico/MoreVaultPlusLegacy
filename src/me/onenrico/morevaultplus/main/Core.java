//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.morevaultplus.commands.MoreVaultPlus;
import me.onenrico.morevaultplus.config.EConfig;
import me.onenrico.morevaultplus.converter.CustomConverter;
import me.onenrico.morevaultplus.converter.PlayerVaultBase64Converter;
import me.onenrico.morevaultplus.converter.PlayerVaultJsonConverter;
import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.database.sql.MySQL;
import me.onenrico.morevaultplus.gui.EditDescriptionMenu;
import me.onenrico.morevaultplus.gui.IconMenu;
import me.onenrico.morevaultplus.gui.MainMenu;
import me.onenrico.morevaultplus.gui.ManageMenu;
import me.onenrico.morevaultplus.gui.MoveDescriptionMenu;
import me.onenrico.morevaultplus.hook.NewPlaceholderAPIHook;
import me.onenrico.morevaultplus.hook.VaultHook;
import me.onenrico.morevaultplus.hook.WorldGuardHook;
import me.onenrico.morevaultplus.listener.InteractListener;
import me.onenrico.morevaultplus.listener.JoinQuitListener;
import me.onenrico.morevaultplus.listener.MoveListener;
import me.onenrico.morevaultplus.listener.PickupListener;
import me.onenrico.morevaultplus.listener.PlayerListener;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.menu.MenuListener;
import me.onenrico.morevaultplus.menu.MenuLiveUpdate;
import me.onenrico.morevaultplus.nbt.MinecraftVersion;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.EMaterial;
import me.onenrico.morevaultplus.utils.EconomyUT;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.ReflectionUT;
import me.onenrico.morevaultplus.utils.SqlUT;
import me.onenrico.morevaultplus.utils.StringUT;

public class Core extends JavaPlugin {
	private static Core instance;
	public static EConfig configplugin;
	public static EConfig guiconfig;
	public static EConfig databaseconfig;
	public static EConfig materialid;
	public static EConfig worthconfig;
	public static boolean thereWorldGuard;
	public static Boolean ultra;
	public static Boolean luck;

	static {
		Core.thereWorldGuard = false;
		Core.ultra = false;
		Core.luck = false;
	}

	public static Core getThis() {
		return Core.instance;
	}

	@Override
	public void onEnable() {

		Core.instance = this;
		ReflectionUT.NUMBER_VERSION = MathUT.strInt(ReflectionUT.VERSION.substring(1).replace("_", ""));
		Core.configplugin = new EConfig(this, "config.yml");
		Core.databaseconfig = new EConfig(this, "database.yml");
		Core.guiconfig = new EConfig(this, "gui.yml");
		Core.materialid = new EConfig(this, "materialid.yml");
		Core.worthconfig = new EConfig(this, "worth.yml");
		reloadSetting();
		MinecraftVersion.getVersion();
		Datamanager.setup();
		VaultHook.setup();
		if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
			Core.thereWorldGuard = true;
			WorldGuardHook.getWorldGuard();
			WorldGuardHook.setup();
		}
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new NewPlaceholderAPIHook().register();
//			new PlaceholderAPIHook(this).hook();
		}
		EconomyUT.setupEconomy();
		getCommand("morevaultplus").setExecutor(new MoreVaultPlus());
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new InteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
		Bukkit.getPluginManager().registerEvents(new MoveListener(), this);
		Bukkit.getPluginManager().registerEvents(new PickupListener(), this);
		Bukkit.getPluginManager().registerEvents(new MoreVaultPlus(), this);
		Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
		if (!ReflectionUT.VERSION.contains("8")) {
			Bukkit.getPluginManager().registerEvents(new me.onenrico.morevaultplus.listener.SwapListener(), this);
		}
		new MoreVaultAPI();
		getServer().getScheduler().runTask(this, new PlayerVaultBase64Converter());
		getServer().getScheduler().runTask(this, new PlayerVaultJsonConverter());
		getServer().getScheduler().runTask(this, new CustomConverter());
		MessageUT.cmsg("Starting Metrics...");
		final Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("custom_permission_size", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return new StringBuilder(String.valueOf(MoreVaultAPI.lcp.size())).toString();
			}
		}));
		metrics.addCustomChart(new Metrics.SimplePie("vault_data_size", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return new StringBuilder(String.valueOf(Datamanager.LoadedData.size())).toString();
			}
		}));
		MenuLiveUpdate.startTimer();
		for (final Player p : Bukkit.getOnlinePlayers()) {
			final Inventory top = p.getOpenInventory().getTopInventory();
			if (top != null && top.getHolder() != null) {
				final String holder = top.getHolder().toString().toLowerCase();
				if (!holder.contains("me.onenrico") && !holder.contains("<nl>")) {
					continue;
				}
				p.closeInventory();
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!(Datamanager.db instanceof MySQL)) {
					cancel();
					return;
				}
				SqlUT.execute("SELECT 1;");
			}
		}.runTaskTimerAsynchronously(this, 20, 1200);
	}

	@Override
	public void onDisable() {
		for (final Player p : Bukkit.getOnlinePlayers()) {
			final Inventory top = p.getOpenInventory().getTopInventory();
			if (top != null && top.getHolder() != null) {
				final String holder = top.getHolder().toString().toLowerCase();
				if (!holder.contains("me.onenrico") && !holder.contains("<nl>")) {
					continue;
				}
				p.closeInventory();

			}
		}
		if (Datamanager.getDB() != null) {
			try {
				if (Datamanager.getDB() instanceof MySQL && !Datamanager.getDB().connection.isClosed()) {
					Datamanager.getDB().connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void reloadSetting() {
		Core.configplugin.reload();
		Core.guiconfig.reload();
		Core.databaseconfig.reload();
		Core.materialid.reload();
		ConfigManager.spigotpage = Core.configplugin.getBool("prefix-hover-page", true);
		Locales.reload(Core.configplugin.getStr("locales", "none"));
		MainMenu.first = true;
		ManageMenu.first = true;
		IconMenu.first = true;
		EditDescriptionMenu.first = true;
		MoveDescriptionMenu.first = true;
		EVault.first = true;
		EVault.defaulticon = Core.configplugin.getStr("default-icon", "STORAGE_MINECART");
		EVault.defaulttitle = Core.configplugin.getStr("default-name", "STORAGE_MINECART");
		EVault.defaultspace = Core.configplugin.getInt("default-space", 45);
		ConfigManager.vaultsort = Core.configplugin.getBool("vault-sort", false);
		ConfigManager.vaultn = Core.configplugin.getBool("vault-navigation", true);
		ConfigManager.vaultd = Core.configplugin.getBool("deposit-enabled", true);
		ConfigManager.vaulted = Core.configplugin.getBool("deposit-exp-enabled", true);
		ConfigManager.endern = Core.configplugin.getBool("ender-open-vault", false);
		ConfigManager.forced = Core.configplugin.getBool("force-update-slot", false);
		ConfigManager.cancelon = Core.configplugin.getBool("close-on-move", true);
		ConfigManager.device = Core.configplugin.getBool("device-enabled", true);
		ConfigManager.loadm = Core.configplugin.getBool("load_message", true);
		ConfigManager.sign = Core.configplugin.getBool("sign-enabled", true);
		ConfigManager.link = Core.configplugin.getBool("vault-link", true);
		ConfigManager.linkparticle = Core.configplugin.getBool("vault-link-particle", true);
		ConfigManager.autosell = Core.configplugin.getBool("vault-autosell", true);
		ConfigManager.autoback = Core.configplugin.getBool("vault-autoback", true);
		ConfigManager.numbered = Core.configplugin.getBool("numbered-vault-item", true, false);
		ConfigManager.nobottom = Core.configplugin.getBool("no-bottom", true, false);
		ConfigManager.signline = Core.configplugin.getStr("sign-title", "null");
		ConfigManager.cancelword = Core.configplugin.getStr("cancel-word", "null");
		ConfigManager.minimalformat = Core.configplugin.getBool("minimal-price-format", false);
		ConfigManager.worldperm = Core.configplugin.getBool("world-permission", false);
		ConfigManager.uncolor = Core.configplugin.getBool("uncolor-vault-title", false);
		ConfigManager.onlytake = Core.configplugin.getBool("only-take", false);
		ConfigManager.christmas = Core.configplugin.getBool("christmas", false);

		ConfigManager.icons.clear();
		for (final String icon : new ArrayList<>(Core.configplugin.getConfig().getStringList("custom-icon"))) {
			final String left = icon.split(">")[0];
			final String veryleft = left.split("<")[0];
			ItemStack item = ItemUT.getItem(veryleft);
			if (item == null) {
				continue;
			}
			if (ItemUT.getName(item) != null
					&& ItemUT.getName(item).equalsIgnoreCase(StringUT.t("&4&lError &cConfig!"))) {
				MessageUT.cmsg("UNKNOWN MATERIAL: " + veryleft);
			}
			final int price = MathUT.strInt(icon.split(">")[1]);
			if (left.split("<").length > 1) {
				final String display = StringUT.t(left.split("<")[1]);
				item = ItemUT.changeDisplayName(item, display);
			}
			ConfigManager.icons.put(item, price);
		}
		Core.ultra = (Bukkit.getPluginManager().getPlugin("UltraPermissions") != null);
		Core.luck = (Bukkit.getPluginManager().getPlugin("LuckPerms") != null);
		MoreVaultAPI.lcp.clear();
		final Set<String> keys = Core.configplugin.getConfig().getConfigurationSection("custom-permission")
				.getKeys(false);
		for (final String key : keys) {
			MoreVaultAPI.lcp.add(new CustomPermission(key));
		}
		ConfigManager.deviceitem = MoreVaultAPI.getDevice();
		Christmas.startParty();

		ConfigurationSection cs = Core.worthconfig.getConfig().getConfigurationSection("worth");
		if (cs != null) {
			for (String key : cs.getKeys(false)) {
				ConfigurationSection cs2 = cs.getConfigurationSection(key);
				if (cs2 != null) {
					for (String key2 : cs2.getKeys(false)) {
						if (key2.equalsIgnoreCase("*")) {
							for (int i = 0; i < 16; i++) {
								EMaterial em = EMaterial.fromString(key + ":" + i);
								if (em == null) {
									break;
								}
								double price = cs2.getDouble(key2);
								ConfigManager.price_data.put(em, price);

							}
						} else {
							EMaterial em = EMaterial.fromString(key + ":" + key2);
							if (em == null) {
								MessageUT.cmsg("Warning! " + key + " Material is not found, please tell the Developer");
								continue;
							}
							double price = cs2.getDouble(key2);
							ConfigManager.price_data.put(em, price);
						}
					}
				} else {
					if (key.equals("*")) {
						for (EMaterial em : EMaterial.values()) {
							ConfigManager.price_data.put(em, cs.getDouble(key));
						}
					} else {
						EMaterial em = EMaterial.fromString(key);
						if (em == null) {
							MessageUT.cmsg("Warning! " + key + " Material is not found, please tell the Developer");
							continue;
						}
						if (em.isUnique(em) && !em.fromnew) {
							for (int i = 0; i < 16; i++) {
								EMaterial em2 = EMaterial.fromString(key + ":" + i);
								if (em2 == null) {
									break;
								}
								double price = cs.getDouble(key);
								ConfigManager.price_data.put(em2, price);
							}
						} else {
							em.fromnew = false;
							double price = cs.getDouble(key);
							ConfigManager.price_data.put(em, price);
						}
					}
				}
			}
		}
	}
}
