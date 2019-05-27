//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;

public class MiningAnimation extends OpenAnimation {
	@Override
	public void open(final Runnable callback, final GUIMenu gm, final Player p) {
		if (p.isOnline()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					final Inventory inv = gm.getInventory();
					final int row = gm.getInv().getSize() / 9;
					final HashMap<Integer, Set<Integer>> rows = new HashMap<>();
					for (final MenuItem mi : gm.getMenuitems()) {
						if (mi.getItem().getType().equals(Material.AIR)) {
							continue;
						}
						final int crow = mi.getSlot() / 9 + 1;
						final Set<Integer> inside = rows.getOrDefault(crow, new HashSet<Integer>());
						if (inside.isEmpty()) {
							inside.add(mi.getSlot());
							rows.put(crow, inside);
						} else {
							inside.add(mi.getSlot());
						}
						Material material = null;
						switch (crow) {
						case 1: {
							material = Material.DIAMOND_ORE;
							break;
						}
						case 2: {
							material = Material.GOLD_ORE;
							break;
						}
						case 3: {
							material = Material.IRON_ORE;
							break;
						}
						case 4: {
							material = Material.COAL_ORE;
							break;
						}
						case 5: {
							material = Material.REDSTONE_ORE;
							break;
						}
						case 6: {
							material = Material.LAPIS_ORE;
							break;
						}
						default: {
							material = Material.STONE;
							break;
						}
						}
						inv.setItem(mi.getSlot(), gm.secure(ItemUT.createItem(material, "&r"), true));
					}
					p.openInventory(inv);
					new BukkitRunnable() {
						int coffset = -1;
						boolean finish = true;

						@Override
						public void run() {
							if (!inv.getViewers().contains(p)) {
								cancel();
								return;
							}
							finish = true;
							++coffset;
							for (int r = 0; r < row; ++r) {
								final int slot = coffset + 9 * r - r;
								final int prevslot = slot - 1;
								boolean there = false;
								if (prevslot >= 9 * r) {
									if (slot > 9 + 9 * r) {
										continue;
									}
									if (prevslot > 53) {
										continue;
									}
									for (final int gmslot : rows.getOrDefault(r + 1, new HashSet<Integer>())) {
										if (gmslot == prevslot) {
											final MenuItem mi = gm.getMenuItem(gmslot);
											final PlaceholderUT mpu = mi.getPu();
											if (mpu != null) {
												inv.setItem(prevslot, mpu.t(mi.getItem().clone()));
											} else {
												inv.setItem(prevslot, mi.getItem().clone());
											}
											there = true;
										}
									}
									if (!there) {
										inv.setItem(prevslot, ItemUT.createItem(Material.AIR));
									}
								}
								if (slot >= 9 * r) {
									if (slot == 9 + 9 * r && !there) {
										inv.setItem(prevslot, ItemUT.createItem(Material.AIR));
									}
									if (slot <= 8 + 9 * r) {
										finish = false;
										inv.setItem(slot, gm.secure(ItemUT.createItem(Material.DIAMOND_PICKAXE), true));
									}
								}
							}
							if (finish) {
								cancel();
								if (callback != null) {
									callback.run();
								}
							}
						}
					}.runTaskTimer(Core.getThis(), 0L, 1L);
				}
			}.runTask(Core.getThis());
		}
	}
}
