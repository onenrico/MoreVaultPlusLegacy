//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.onenrico.morevaultplus.gui.EditDescriptionMenu;
import me.onenrico.morevaultplus.gui.ManageMenu;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.menu.Action;
import me.onenrico.morevaultplus.menu.VaultMenu;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.EconomyUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;
import me.onenrico.morevaultplus.utils.PlayerUT;
import me.onenrico.morevaultplus.utils.StringUT;

public class PlayerListener implements Listener {
	public static HashMap<UUID, EVault> cacheexp;
	public static HashMap<UUID, EVault> cachebalance;
	public static HashMap<UUID, EVault> cachename;
	public static HashMap<UUID, EVault> cachewexp;
	public static HashMap<UUID, EVault> cachewbalance;
	public static HashMap<UUID, EVault> cachedescription;
	public static HashMap<UUID, Integer> cacheline;
	private static String mode;

	static {
		PlayerListener.cacheexp = new HashMap<>();
		PlayerListener.cachebalance = new HashMap<>();
		PlayerListener.cachename = new HashMap<>();
		PlayerListener.cachewexp = new HashMap<>();
		PlayerListener.cachewbalance = new HashMap<>();
		PlayerListener.cachedescription = new HashMap<>();
		PlayerListener.cacheline = new HashMap<>();
	}

	public static void clearCache(final UUID p) {
		if (PlayerListener.cacheexp.containsKey(p)) {
			PlayerListener.cacheexp.remove(p);
		}
		if (PlayerListener.cachebalance.containsKey(p)) {
			PlayerListener.cachebalance.remove(p);
		}
		if (PlayerListener.cachename.containsKey(p)) {
			PlayerListener.cachename.remove(p);
		}
		if (PlayerListener.cachewexp.containsKey(p)) {
			PlayerListener.cachewexp.remove(p);
		}
		if (PlayerListener.cachewbalance.containsKey(p)) {
			PlayerListener.cachewbalance.remove(p);
		}
		if (PlayerListener.cachedescription.containsKey(p)) {
			PlayerListener.cachedescription.remove(p);
		}
	}

	public static EVault getCacheVault(final UUID p) {
		EVault result = null;
		if (PlayerListener.cacheexp.containsKey(p)) {
			result = PlayerListener.cacheexp.get(p);
			PlayerListener.mode = "exp";
		} else if (PlayerListener.cachebalance.containsKey(p)) {
			result = PlayerListener.cachebalance.get(p);
			PlayerListener.mode = "balance";
		} else if (PlayerListener.cachename.containsKey(p)) {
			result = PlayerListener.cachename.get(p);
			PlayerListener.mode = "name";
		} else if (PlayerListener.cachewexp.containsKey(p)) {
			result = PlayerListener.cachewexp.get(p);
			PlayerListener.mode = "wexp";
		} else if (PlayerListener.cachewbalance.containsKey(p)) {
			result = PlayerListener.cachewbalance.get(p);
			PlayerListener.mode = "wbalance";
		} else if (PlayerListener.cachedescription.containsKey(p)) {
			result = PlayerListener.cachedescription.get(p);
			PlayerListener.mode = "description";
		}
		if (result != null) {
			clearCache(p);
		}
		return result;
	}

	public static void process(final Player p, final OfflinePlayer op, final EVault ev, final String msg) {
		final PlaceholderUT pu = VaultMenu.getPlaceholder(p, op, ev);
		final CustomPermission cp = MoreVaultAPI.getCustomPermission(op, p.getWorld());
		double amount = 0.0;
		pu.add("id", new StringBuilder().append(ev.getId()).toString());
		pu.add("cancel", ConfigManager.cancelword);
		switch (PlayerListener.mode) {
		case "description": {
			if (msg.equalsIgnoreCase(ConfigManager.cancelword)) {
				MessageUT.plmessage(p, Locales.getValue("edit_canceled"), pu);
				break;
			}
			final int line = PlayerListener.cacheline.get(p.getUniqueId());
			pu.add("line", new StringBuilder().append(line).toString());
			pu.add("content", new StringBuilder().append(msg).toString());
			PlayerListener.cacheline.remove(p.getUniqueId());
			if (ev.getDescription().size() <= line) {
				ev.getDescription().add(StringUT.t(msg));
			} else {
				ev.getDescription().set(line, StringUT.t(msg));
			}
			ev.save();
			MessageUT.plmessage(p, Locales.getValue("edited_line"), pu);
			EditDescriptionMenu.open(p, ev, 1);
			return;
		}
		case "balance": {
			if (msg.equalsIgnoreCase(ConfigManager.cancelword)) {
				MessageUT.plmessage(p, Locales.getValue("deposit_canceled"), pu);
				break;
			}
			try {
				amount = Double.parseDouble(msg);
				amount = Math.abs(amount);
			} catch (NumberFormatException ex) {
				MessageUT.plmessage(p, Locales.getValue("must_number"), pu);
				break;
			}
			if (!EconomyUT.has(p, amount)) {
				MessageUT.plmessage(p, Locales.getValue("deposit_notenough"), pu);
				break;
			}
			if (MoreVaultAPI.getMaxDeposit(cp) < ev.getBalance() + amount) {
				MessageUT.plmessage(p, Locales.getValue("deposit_exceed"), pu);
				break;
			}
			EconomyUT.subtractBal(p, amount);
			ev.addBalance(amount);
			ev.save();
			pu.add("money", new StringBuilder().append(amount).toString());
			pu.add("amount", new StringBuilder().append(ev.getBalance()).toString());
			MessageUT.plmessage(p, Locales.getValue("deposited_vault"), pu);
			break;
		}
		case "exp": {
			if (msg.equalsIgnoreCase(ConfigManager.cancelword)) {
				MessageUT.plmessage(p, Locales.getValue("deposit_canceled"), pu);
				break;
			}
			try {
				amount = Double.parseDouble(msg);
				amount = Math.abs(amount);
			} catch (NumberFormatException ex) {
				MessageUT.plmessage(p, Locales.getValue("must_number"), pu);
				break;
			}
			if (PlayerUT.getTotalExperience(p) < amount) {
				MessageUT.plmessage(p, Locales.getValue("exp_deposit_notenough"), pu);
				break;
			}
			if (MoreVaultAPI.getMaxExpDeposit(cp) < ev.getExp() + amount) {
				MessageUT.plmessage(p, Locales.getValue("exp_deposit_exceed"), pu);
				break;
			}
			PlayerUT.setTotalExperience(p, PlayerUT.getTotalExperience(p) - (int) amount);
			ev.addExp(amount);
			ev.save();
			pu.add("exp", new StringBuilder().append(amount).toString());
			pu.add("amount", new StringBuilder().append(ev.getExp()).toString());
			MessageUT.plmessage(p, Locales.getValue("exp_deposited_vault"), pu);
			break;
		}
		case "name": {
			if (msg.equalsIgnoreCase(ConfigManager.cancelword)) {
				MessageUT.plmessage(p, Locales.getValue("rename_canceled"), pu);
				break;
			}
			String name = msg;
			if (Bukkit.getServer().getPluginManager().getPlugin("DeluxeChat") != null) {
				name = name.replace("%", "&");
			}
			ev.setTitle(name);
			ev.build();
			ev.save();
			pu.add("name", new StringBuilder().append(ev.getTitle()).toString());
			MessageUT.plmessage(p, Locales.getValue("rename_vault"), pu);
			ManageMenu.open(p, ev, 1);
			return;
		}
		case "wexp": {
			if (msg.equalsIgnoreCase(ConfigManager.cancelword)) {
				MessageUT.plmessage(p, Locales.getValue("deposit_canceled"), pu);
				break;
			}
			try {
				amount = Double.parseDouble(msg);
				amount = Math.abs(amount);
			} catch (NumberFormatException ex) {
				MessageUT.plmessage(p, Locales.getValue("must_number"), pu);
				break;
			}
			if (ev.getExp() < amount) {
				MessageUT.plmessage(p, Locales.getValue("exp_withdraw_notenough"), pu);
				break;
			}
			PlayerUT.setTotalExperience(p, PlayerUT.getTotalExperience(p) + (int) amount);
			ev.addExp(-amount);
			ev.save();
			pu.add("exp", new StringBuilder().append(amount).toString());
			pu.add("amount", new StringBuilder().append(ev.getExp()).toString());
			MessageUT.plmessage(p, Locales.getValue("exp_withdrawed_vault"), pu);
			break;
		}
		case "wbalance": {
			if (msg.equalsIgnoreCase(ConfigManager.cancelword)) {
				MessageUT.plmessage(p, Locales.getValue("withdraw_canceled"), pu);
				break;
			}
			try {
				amount = Double.parseDouble(msg);
				amount = Math.abs(amount);
			} catch (NumberFormatException ex) {
				MessageUT.plmessage(p, Locales.getValue("must_number"), pu);
				break;
			}
			if (ev.getBalance() < amount) {
				MessageUT.plmessage(p, Locales.getValue("withdraw_notenough"), pu);
				break;
			}
			EconomyUT.addBal(p, amount);
			ev.addBalance(-amount);
			ev.save();
			pu.add("money", new StringBuilder().append(amount).toString());
			pu.add("amount", new StringBuilder().append(ev.getBalance()).toString());
			MessageUT.plmessage(p, Locales.getValue("withdrawed_vault"), pu);
			break;
		}
		default:
			break;
		}
		ev.openVault(null, p);
		ev.setCloseaction(new Action() {
			@Override
			public void act() {
				ev.save();
			}
		});
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void chat(final AsyncPlayerChatEvent e) {
		final Player p = e.getPlayer();
		final EVault ev = getCacheVault(p.getUniqueId());
		if (ev != null) {
			final OfflinePlayer op = Bukkit.getOfflinePlayer(ev.getOwner());
			e.setCancelled(true);
			process(p, op, ev, e.getMessage());
		}
	}
}
