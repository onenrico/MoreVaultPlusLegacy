//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;

public class PlayerVaultJsonConverter implements Runnable {
	@Override
	public void run() {
		MessageUT.cmsg("Checking for PlayerVault Data");
		final String datafolder = Core.getThis().getDataFolder().toString();
		final File pvdir = new File(datafolder, "uuidvaults");
		if (pvdir.exists()) {
			final File[] fl = pvdir.listFiles();
			MessageUT.cmsg("PlayerVault Data Exist, Starting Converter. Total Data: " + (fl.length - 1));
			int index = 1;
			File[] array;
			for (int length = (array = fl).length, l = 0; l < length; ++l) {
				final File f = array[l];
				if (f.isDirectory()) {
					File[] listFiles;
					for (int length2 = (listFiles = f.listFiles()).length, n = 0; n < length2; ++n) {
						final File i = listFiles[n];
						i.delete();
					}
					f.delete();
				} else {
					MessageUT.cmsg("PlayerVault Converting: " + index++ + " out of " + (fl.length - 1));
					final UUID uuid = UUID.fromString(f.getName().replace(".yml", ""));
					final FileConfiguration config = YamlConfiguration.loadConfiguration(f);
					final ConfigurationSection cs = config.getConfigurationSection("");
					if (cs != null) {
						for (final String v : cs.getKeys(false)) {
							final ConfigurationSection cs2 = config.getConfigurationSection(v);
							final List<String> items = new ArrayList<>();
							for (final String j : cs2.getKeys(false)) {
								items.add(cs2.getString(j));
							}
							final Inventory inv = JsonSerializer.toInventory(items);
							boolean empty = true;
							ItemStack[] contents;
							for (int length3 = (contents = inv.getContents()).length, n2 = 0; n2 < length3; ++n2) {
								final ItemStack k = contents[n2];
								if (k != null && !k.getType().equals(Material.AIR)) {
									empty = false;
									break;
								}
							}
							if (empty) {
								continue;
							}
							final int id = MathUT.strInt(v.replace("vault", ""));
							final EVault ev = Datamanager.getVaultByID(uuid, id);
							ev.setRow(6);
							ev.build();
							ev.getInv().setContents(inv.getContents());
							Datamanager.addData(ev);
						}
					}
					f.delete();
				}
			}
			pvdir.delete();
			MessageUT.cmsg("PlayerVault Data Convert Completed");
			MessageUT.cmsg("The Server Will Restarting...");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}
	}
}
