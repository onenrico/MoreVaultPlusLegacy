//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.hook;

import org.bukkit.plugin.RegisteredServiceProvider;

import me.onenrico.morevaultplus.main.Core;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultHook {
	private static Core instance;
	public static Economy v_economy;
	public static Permission v_permission;
	public static Chat v_chat;

	static {
		VaultHook.v_economy = null;
		VaultHook.v_permission = null;
		VaultHook.v_chat = null;
	}

	public static void setup() {
		VaultHook.instance = Core.getThis();
		if (setupEconomy()) {
			setupPermissions();
		}
	}

	public static boolean setupEconomy() {
		if (VaultHook.instance.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		final RegisteredServiceProvider<Economy> rsp = VaultHook.instance.getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		VaultHook.v_economy = rsp.getProvider();
		return VaultHook.v_economy != null;
	}

	public static boolean setupPermissions() {
		final RegisteredServiceProvider<Permission> rsp = VaultHook.instance.getServer().getServicesManager()
				.getRegistration(Permission.class);
		VaultHook.v_permission = rsp.getProvider();
		return VaultHook.v_permission != null;
	}
}
