//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import me.onenrico.morevaultplus.hook.VaultHook;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.Core;

public class PermissionUT {
	public static boolean check(final Player player, final String perm) {
		if (has(player, perm)) {
			return true;
		}
		final PlaceholderUT pu = new PlaceholderUT();
		pu.add("perm", perm);
		MessageUT.plmessage(player, Locales.getValue("no_permission"), pu);
		return false;
	}

	public static boolean has(final Player player, String cperm) {
		for (final Permission perm : Core.getThis().getDescription().getPermissions()) {
			if (perm.getChildren().containsKey(cperm) && has(player, perm.getName())) {
				return perm.getChildren().get(cperm);
			}
		}
		if (!player.hasPermission(cperm) && Core.ultra) {
			cperm = cperm.replace(".", " ");
		}
		return player.hasPermission(cperm);
	}

	public static boolean has(final OfflinePlayer offlineplayer, final String perm, World world) {
		if (offlineplayer.getName() == null) {
			return false;
		}
		if (offlineplayer.isOnline()) {
			return has(offlineplayer.getPlayer(), perm);
		}
		if (VaultHook.v_permission == null) {
			return true;
		}
		if (Core.luck) {
			return true;
		}
		final World w = Bukkit.getWorld("world");
		if (w != null) {
			world = w;
		}
		return VaultHook.v_permission.playerHas(world.getName(), offlineplayer, perm);
	}
}
