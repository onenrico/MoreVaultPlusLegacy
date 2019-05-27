//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.StringUT;

public class EConfig {
	public FileConfiguration config;
	public FileConfiguration defaultconfig;
	public File defaultfile;
	public File currentfile;
	public String filename;

	public EConfig(final Core plugin, final String filename) {
		config = null;
		defaultconfig = null;
		defaultfile = null;
		currentfile = null;
		this.filename = "";
		this.filename = filename;
		currentfile = new File(plugin.getDataFolder(), filename);
		if (!currentfile.getParentFile().exists()) {
			currentfile.getParentFile().mkdir();
		}
		if (!currentfile.exists()) {
			try {
				plugin.saveResource(filename, false);
			} catch (Exception ex) {
			}
		}
		final InputStream is = plugin.getResource(filename);
		defaultfile = new File(Core.getThis().getDataFolder(), String.valueOf(filename) + ".temp");
		try {
			if (defaultfile.exists()) {
				defaultfile.delete();
				defaultfile.createNewFile();
			}
			Files.copy(is, defaultfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//			FileUtils.copyInputStreamToFile(is, defaultfile);
			defaultconfig = YamlConfiguration.loadConfiguration(defaultfile);
			defaultfile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		reload();
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void reload() {
		try {
			currentfile = new File(Core.getThis().getDataFolder(), filename);
			config = YamlConfiguration.loadConfiguration(currentfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		MessageUT.debug("Saving " + filename);
		try {
			config.save(currentfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getStr(final String path, String def) {
		def = StringUT.t(def);
		if (config.get(path) == null) {
			if (defaultconfig.get(path) == null) {
				config.set(path, def);
			} else {
				def = new StringBuilder().append(defaultconfig.get(path)).toString();
				config.set(path, def);
			}
			try {
				config.save(currentfile);
				MessageUT.debug("Path: " + path);
				MessageUT.debug("Config Saving From Str");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
		return config.getString(path, def);
	}

	public List<String> getStrList(final String path) {
		return config.getStringList(path);
	}

	public List<String> getStrList(final String path, List<String> def) {
		if (!config.getStringList(path).isEmpty()) {
			return config.getStringList(path);
		}
		if (defaultconfig.getStringList(path).isEmpty()) {
			config.set(path, new ArrayList<>());
			try {
				config.save(currentfile);
				MessageUT.debug("Path: " + path);
				MessageUT.debug("Config Saving From StrList");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
		def = defaultconfig.getStringList(path);
		config.set(path, def);
		return def;
	}

	public int getInt(final String path, final int def) {
		if (config.get(path) == null) {
			config.set(path, def);
			MessageUT.debug("Tembus " + path);
			try {
				config.save(currentfile);
				MessageUT.debug("Path: " + path);
				MessageUT.debug("Config Saving From Int");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
		return config.getInt(path, def);
	}

	public String getStr(final String path) {
		return StringUT.t(config.getString(path));
	}

	public int getInt(final String path) {
		return config.getInt(path);
	}

	public double getDouble(final String path, final double def) {
		if (config.get(path) == null) {
			config.set(path, def);
			MessageUT.debug("Tembus " + path);
			try {
				config.save(currentfile);
				MessageUT.debug("Path: " + path);
				MessageUT.debug("Config Saving From Double");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
		return config.getDouble(path, def);
	}

	public Boolean getBool(final String path) {
		return config.getBoolean(path);
	}

	public Boolean getBool(final String path, final Boolean def) {
		return this.getBool(path, def, null);
	}

	public Boolean getBool(final String path, Boolean def, final Boolean seconddef) {
		if (config.get(path) != null) {
			if (def == null) {
				def = false;
			}
			return config.getBoolean(path, def);
		}
		if (seconddef != null) {
			config.set(path, seconddef);
			try {
				config.save(currentfile);
				MessageUT.debug("Path: " + path);
				MessageUT.debug("Config Saving From Bool");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return seconddef;
		}
		if (def == null) {
			def = false;
		}
		config.set(path, def);
		MessageUT.debug("Tembus " + path);
		try {
			config.save(currentfile);
			MessageUT.debug("Path: " + path);
			MessageUT.debug("Config Saving From Bool");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return def;
	}
}
