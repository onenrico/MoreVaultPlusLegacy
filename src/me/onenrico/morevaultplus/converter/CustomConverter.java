//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;

public class CustomConverter implements Runnable {
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void run() {
		final String datafolder = Core.getThis().getDataFolder().toString();
		final File pvdir = new File(datafolder, "customvaults");
		if (pvdir.exists()) {
			final File[] fl = pvdir.listFiles();
			MessageUT.cmsg("CustomVault Data Exist, Starting Converter. Total Data: " + fl.length);
			int index = 1;
			List<String> total = new ArrayList<>();
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
					MessageUT.cmsg("CustomVault Converting: " + index++ + " out of " + fl.length);
					final OfflinePlayer op = Bukkit.getOfflinePlayer(f.getName().replace(".yml", ""));
					if (op == null) {
						MessageUT.cmsg("WARNING ! Player " + f.getName() + " is Not found !!");
					} else {
						final UUID uuid = op.getUniqueId();
						final FileConfiguration config = YamlConfiguration.loadConfiguration(f);
						final List<ItemStack> items = (List<ItemStack>) config.getList("maxichest");
						if (items != null && !items.isEmpty()) {
							final Inventory inv = Bukkit.createInventory((InventoryHolder) null, 54);
							if (items.size() > 54) {
								int in = 0;
								int times = 1;
								do {
									inv.clear();
									final int size = new ArrayList<>(items).size();
									for (final ItemStack j : new ArrayList<>(items)) {
										items.remove(0);
										if (j != null) {
											inv.setItem(in++, j);
											if (in >= (int) MathUT.clamp(size, 0L, 54L)) {
												in = 0;
												final EVault ev = Datamanager.getVaultByID(uuid, times++);
												ev.setRow(6);
												if (ev.getInv() == null) {
													ev.build();
												}
												ev.getInv().setContents(inv.getContents());
												total = Datamanager.addQueue(total, ev);
												break;
											}
											continue;
										} else {
											inv.setItem(in++, j);
											if (in >= (int) MathUT.clamp(size, 0L, 54L)) {
												in = 0;
												final EVault ev = Datamanager.getVaultByID(uuid, times++);
												ev.setRow(6);
												if (ev.getInv() == null) {
													ev.build();
												}
												ev.getInv().setContents(inv.getContents());
												total = Datamanager.addQueue(total, ev);
												break;
											}
											continue;
										}
									}
								} while (!items.isEmpty());
							} else {
								final EVault ev2 = Datamanager.getVaultByID(uuid, 1);
								ev2.setRow(6);
								if (ev2.getInv() == null) {
									ev2.build();
								}
								ev2.getInv().setContents((ItemStack[]) items.toArray());
								total = Datamanager.addQueue(total, ev2);
							}
						}
						f.delete();
					}
				}
			}
			Datamanager.executeQueue(total);
			pvdir.delete();
			MessageUT.cmsg("CustomVault Data Convert Completed");
			MessageUT.cmsg("The Server Will Restarting...");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}
	}
}
