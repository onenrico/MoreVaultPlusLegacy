package me.onenrico.morevaultplus.hook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.object.EVault;

public class NewPlaceholderAPIHook extends PlaceholderExpansion {

	/**
	 * This method should always return true unless we have a dependency we need to
	 * make sure is on the server for our placeholders to work!
	 *
	 * @return always true since we do not have any dependencies.
	 */
	@Override
	public boolean canRegister() {
		return true;
	}

	/**
	 * The name of the person who created this expansion should go here.
	 *
	 * @return The name of the author as a String.
	 */
	@Override
	public String getAuthor() {
		return "Onenrico";
	}

	/**
	 * The placeholder identifier should go here. <br>
	 * This is what tells PlaceholderAPI to call our onRequest method to obtain a
	 * value if a placeholder starts with our identifier. <br>
	 * This must be unique and can not contain % or _
	 *
	 * @return The identifier in {@code %<identifier>_<value>%} as String.
	 */
	@Override
	public String getIdentifier() {
		return "mvault";
	}

	/**
	 * This is the version of this expansion. <br>
	 * You don't have to use numbers, since it is set as a String.
	 *
	 * @return The version as a String.
	 */
	@Override
	public String getVersion() {
		return "1.0.0";
	}

	/**
	 * This is the method called when a placeholder with our identifier is found and
	 * needs a value. <br>
	 * We specify the value identifier in this method. <br>
	 * Since version 2.9.1 can you use OfflinePlayers in your requests.
	 *
	 * @param player     A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
	 * @param identifier A String containing the identifier/value.
	 *
	 * @return Possibly-null String of the requested identifier.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public String onRequest(OfflinePlayer player, String identifier) {

		String prefix = identifier;
		OfflinePlayer ofc = player;
		if (identifier.contains(":")) {
			prefix = identifier.split(":")[0];
			ofc = Bukkit.getOfflinePlayer(identifier.split(":")[1]);
		}
		final World world = Bukkit.getWorlds().get(0);
		final CustomPermission cp = MoreVaultAPI.getCustomPermission(ofc, world);
		switch (prefix) {
		case "minowned": {
			return new StringBuilder().append(MoreVaultAPI.getMinVault(ofc, cp)).toString();
		}
		case "totalitems": {
			String total = "";
			int temp2 = 0;
			for (final EVault ev : Datamanager.getOwnedVault(player.getUniqueId())) {
				temp2 += ev.getItemsAmount();
			}
			total = new StringBuilder().append(temp2).toString();
			return total;
		}
		case "maxexpdeposit": {
			return new StringBuilder().append(MoreVaultAPI.getMaxExpDeposit(cp)).toString();
		}
		case "cost": {
			final double cost = MoreVaultAPI.getRealCost(ofc, cp);
			return (cost == -99.0) ? "Max" : new StringBuilder().append(round(cost, 2)).toString();
		}
		case "increase": {
			return new StringBuilder().append(MoreVaultAPI.getIncrease(cp)).toString();
		}
		case "items": {
			String amount = "";
			int temp3 = 0;
			for (final EVault ev2 : Datamanager.getOwnedVault(player.getUniqueId())) {
				temp3 += ev2.getUsed();
			}
			amount = new StringBuilder().append(temp3).toString();
			return amount;
		}
		case "owned": {
			return new StringBuilder().append(MoreVaultAPI.getOwnedVault(ofc, cp)).toString();
		}
		case "space": {
			return new StringBuilder().append(MoreVaultAPI.getSpace(cp)).toString();
		}
		case "maxowned": {
			return new StringBuilder().append(MoreVaultAPI.getMaxVault(ofc, cp)).toString();
		}
		case "devicecost": {
			return new StringBuilder().append(MoreVaultAPI.getDeviceCost(cp)).toString();
		}
		case "maxdeposit": {
			return new StringBuilder().append(MoreVaultAPI.getMaxDeposit(cp)).toString();
		}
		default:
			break;
		}
		return "";
	}

	public static double round(double value, final int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		final long factor = (long) Math.pow(10.0, places);
		value *= factor;
		final long tmp = Math.round(value);
		return tmp / factor;
	}
}
