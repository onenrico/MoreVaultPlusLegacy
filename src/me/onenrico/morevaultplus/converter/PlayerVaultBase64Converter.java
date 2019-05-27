//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.converter;

import java.io.File;
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

public class PlayerVaultBase64Converter implements Runnable {
	@Override
	public void run() {
		MessageUT.cmsg("Checking for PlayerVaultX Data");
		final String datafolder = Core.getThis().getDataFolder().toString();
		final File pvdir = new File(datafolder, "base64vaults");
		if (pvdir.exists()) {
			final File[] fl = pvdir.listFiles();
			MessageUT.cmsg("PlayerVaultX Data Exist, Starting Converter. Total Data: " + (fl.length - 1));
			int index = 1;
			File[] array;
			for (int length = (array = fl).length, k = 0; k < length; ++k) {
				final File f = array[k];
				if (f.isDirectory()) {
					File[] listFiles;
					for (int length2 = (listFiles = f.listFiles()).length, l = 0; l < length2; ++l) {
						final File i = listFiles[l];
						i.delete();
					}
					f.delete();
				} else {
					MessageUT.cmsg("PlayerVaultX Converting: " + index++ + " out of " + (fl.length - 1));
					final UUID uuid = UUID.fromString(f.getName().replace(".yml", ""));
					final FileConfiguration config = YamlConfiguration.loadConfiguration(f);
					final ConfigurationSection cs = config.getConfigurationSection("");
					if (cs != null) {
						for (final String v : cs.getKeys(false)) {
							final Inventory inv = Base64Serializer.fromBase64(config.getString(v));
							boolean empty = true;
							ItemStack[] contents;
							for (int length3 = (contents = inv.getContents()).length, n = 0; n < length3; ++n) {
								final ItemStack j = contents[n];
								if (j != null && !j.getType().equals(Material.AIR)) {
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
			MessageUT.cmsg("PlayerVaultX Data Convert Completed");
			MessageUT.cmsg("The Server Will Restarting...");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}
	}
}
