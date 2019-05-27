//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.locale;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.onenrico.morevaultplus.callable.PlaceholderCall;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.utils.PlaceholderUT;

public class Locales {
	public static Boolean premium;
	private static HashMap<String, List<String>> map;
	private static HashMap<String, Object> map2;
	public static HashMap<String, List<String>> map3;
	public static PlaceholderUT pub;
	private static FileConfiguration config;
	private static Random rand;
	public static String rawpluginPrefix;
	public static String jsonpluginPrefix;
	private static File file;

	static {
		Locales.premium = true;
		Locales.map = new HashMap<>();
		Locales.map2 = new HashMap<>();
		Locales.map3 = new HashMap<>();
		Locales.pub = null;
		Locales.config = null;
		Locales.rand = new Random();
	}

	public static void setup() {
		final Set<String> nkeys = Locales.config.getConfigurationSection("messages").getKeys(false);
		if (nkeys != null) {
			for (final String key : nkeys) {
				Locales.map.put(key, Locales.config.getStringList("messages." + key));
			}
		}
		final Set<String> nkeys2 = Locales.config.getConfigurationSection("custom-placeholder").getKeys(false);
		if (nkeys2 != null) {
			for (final String key2 : nkeys2) {
				Locales.map2.put(key2, Locales.config.getString("custom-placeholder." + key2));
			}
		}
		ConfigurationSection cs = Locales.config.getConfigurationSection("random-placeholder");
		if (cs == null) {
			final List<String> values = new ArrayList<>();
			values.add("&c");
			values.add("&4");
			Locales.config.set("random-placeholder.red", new ArrayList<>(values));
			values.clear();
			values.add("&a");
			values.add("&2");
			Locales.config.set("random-placeholder.green", new ArrayList<>(values));
			values.clear();
			values.add("&b");
			values.add("&3");
			Locales.config.set("random-placeholder.blue", new ArrayList<>(values));
			values.clear();
			values.add("&f");
			values.add("&7");
			Locales.config.set("random-placeholder.white", new ArrayList<>(values));
			values.clear();
			values.add("&e");
			values.add("&6");
			Locales.config.set("random-placeholder.yellow", new ArrayList<>(values));
			try {
				Locales.config.save(Locales.file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			cs = Locales.config.getConfigurationSection("random-placeholder");
		}
		final Set<String> nkeys3 = cs.getKeys(false);
		if (nkeys3 != null) {
			for (final String key3 : nkeys3) {
				Locales.map3.put(key3, Locales.config.getStringList("random-placeholder." + key3));
				final PlaceholderCall pc = new PlaceholderCall();
				pc.values = Locales.map3.get(key3);
				Locales.map2.put(key3, pc);
			}
		}
		Locales.pub = new PlaceholderUT(getPlaceholder());
		Locales.rawpluginPrefix = Core.configplugin.getStr("pluginPrefix", "&cNot Configured");
		if (ConfigManager.spigotpage) {
			Locales.jsonpluginPrefix = "<json>" + Locales.rawpluginPrefix
					+ "@CU:https://www.spigotmc.org/resources/43920/" + "@H:&7Click To Go to Plugin Page" + "</json>";
		} else {
			Locales.jsonpluginPrefix = Locales.rawpluginPrefix;
		}
	}

	public static List<String> getValue(final String msg) {
		if (Locales.map.get(msg) == null) {
			final InputStream is = Core.getThis().getResource("lang_EN.yml");
			final File tfile = new File(Core.getThis().getDataFolder(), "lang.temp");
			try {
				Files.copy(is, tfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//				FileUtils.copyInputStreamToFile(is, tfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			final FileConfiguration defaultc = YamlConfiguration.loadConfiguration(tfile);
			final List<String> mmsg = defaultc.getStringList("messages." + msg);
			Locales.config.set("messages." + msg, mmsg);
			Locales.map.put(msg, mmsg);
			try {
				Locales.config.save(Locales.file);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			tfile.delete();
		}
		return Locales.pub.t(Locales.map.get(msg));
	}

	public static HashMap<String, Object> getPlaceholder() {
		return Locales.map2;
	}

	public static String getRandom(final String value) {
		final List<String> values = Locales.map3.getOrDefault(value, new ArrayList<String>());
		final String hasil = values.get(Locales.rand.nextInt(values.size()));
		return Locales.pub.t(hasil);
	}

	public static void reload(final String locale) {
		try {
			Locales.file = new File(Core.getThis().getDataFolder(), "lang_" + locale + ".yml");
			if (!Locales.file.getParentFile().exists()) {
				Locales.file.getParentFile().mkdir();
			}
			if (!Locales.file.exists()) {
				Core.getThis().saveResource("lang_EN.yml", false);
			}
			Locales.config = YamlConfiguration.loadConfiguration(Locales.file);
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			Locales.config.save(Locales.file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
