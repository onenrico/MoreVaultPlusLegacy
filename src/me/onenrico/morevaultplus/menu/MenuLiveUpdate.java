//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.menu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;

public class MenuLiveUpdate {
	private static Set<GUIMenu> animated;

	static {
		MenuLiveUpdate.animated = new HashSet<>();
	}

	public static void addAnimated(final GUIMenu gm) {
		MenuLiveUpdate.animated.add(gm);
	}

	public static void removeAnimated(final GUIMenu gm) {
		if (MenuLiveUpdate.animated.contains(gm)) {
			MenuLiveUpdate.animated.remove(gm);
		}
	}

	public static void refresh(final GUIMenu gm) {
		if (gm.getInv().getViewers().isEmpty()) {
			MenuLiveUpdate.animated.remove(gm);
			return;
		}
		for (final MenuItem mi : gm.getMenuitems()) {
			refresh(gm, mi);
		}
	}

	public static void refresh(final GUIMenu gm, final MenuItem mi) {
		final PlaceholderUT mpu = mi.getPu();
		if (mpu != null) {
			final ItemStack it = gm.getInv().getItem(mi.getSlot());
			if (it != null && !it.getType().equals(Material.AIR)) {
				ItemUT.changeDisplayName(it, mpu.t(ItemUT.getName(mi.getItem())));
				ItemUT.changeLore(it, mpu.t(ItemUT.getLore(mi.getItem().clone())));
			}
		}
	}

	public static void startTimer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (final GUIMenu gm : new ArrayList<>(MenuLiveUpdate.animated)) {
					MenuLiveUpdate.refresh(gm);
				}
			}
		}.runTaskTimer(Core.getThis(), 20L, 20L);
	}
}
