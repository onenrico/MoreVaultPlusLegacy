//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.onenrico.morevaultplus.database.Datamanager;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.menu.Action;
import me.onenrico.morevaultplus.menu.MenuAnimation;
import me.onenrico.morevaultplus.nms.particle.ParticleManager;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.PermissionUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;

public class MoreVaultAPI {
	public static Core instance;
	public static List<CustomPermission> lcp;

	static {
		MoreVaultAPI.lcp = new ArrayList<>();
	}

	public MoreVaultAPI() {
		MoreVaultAPI.instance = Core.getThis();
		ParticleManager.setup();
		MenuAnimation.startTimer();
	}

	public static void openVault(final Player player, final String identifier) {
		final EVault ev = Datamanager.getVaultByID(UUID.fromString(identifier.split("<@>")[0]),
				MathUT.strInt(identifier.split("<@>")[1]));
		if (!Bukkit.getOfflinePlayer(ev.getOwner()).isOnline()) {
			final PlaceholderUT pu = new PlaceholderUT();
			pu.add("target", ev.getOwnerName());
			MessageUT.plmessage(player, pu.t(Locales.getValue("no_owner")));
			return;
		}
		ev.openVault(null, player);
		ev.setCloseaction(new Action() {
			@Override
			public void act() {
				ev.save();
			}
		});
	}

	public static CustomPermission getCustomPermission(final OfflinePlayer ofp, final World w) {
		for (final CustomPermission cp : MoreVaultAPI.lcp) {
			if (cp.is(ofp, w)) {
				return cp;
			}
		}
		return null;
	}

	public static List<ItemStack> getBlackList(final OfflinePlayer ofp, final CustomPermission cp) {
		if (ofp.isOnline()) {
			final Player p = ofp.getPlayer();
			if (PermissionUT.has(p, "morevaultplus.bypass.blacklist")) {
				return new ArrayList<>();
			}
		}
		if (cp == null) {
			return MoreVaultAPI.lcp.get(MoreVaultAPI.lcp.size() - 1).getBlacklistitem();
		}
		return cp.getBlacklistitem();
	}

	public static boolean isAllowChangeDescription(final OfflinePlayer ofp, final CustomPermission cp) {
		if (ofp.isOnline()) {
			final Player p = ofp.getPlayer();
			if (PermissionUT.has(p, "morevaultplus.change.description")) {
				return true;
			}
		}
		return cp != null && cp.isChangeDescription();
	}

	public static boolean isAllowChangeName(final OfflinePlayer ofp, final CustomPermission cp) {
		if (ofp.isOnline()) {
			final Player p = ofp.getPlayer();
			if (PermissionUT.has(p, "morevaultplus.change.name")) {
				return true;
			}
		}
		return cp != null && cp.isChangeName();
	}

	public static boolean isAllowChangeIcon(final OfflinePlayer ofp, final CustomPermission cp) {
		if (ofp.isOnline()) {
			final Player p = ofp.getPlayer();
			if (PermissionUT.has(p, "morevaultplus.change.icon")) {
				return true;
			}
		}
		return cp != null && cp.isChangeIcon();
	}

	public static int getMaxVault(final OfflinePlayer ofp, final CustomPermission cp) {
		int highest = 0;
		if (ofp.isOnline()) {
			final Player p = ofp.getPlayer();
			for (final PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
				String perm = pai.getPermission();
				if (perm.startsWith("morevaultplus.maxvault.")) {
					perm = perm.replace("morevaultplus.maxvault.", "");
					if (perm.equalsIgnoreCase("*")) {
						return 1000;
					}
					final int amount = MathUT.strInt(perm);
					if (amount <= highest) {
						continue;
					}
					highest = amount;
				}
			}
			if (highest > 0) {
				return highest;
			}
		}
		if (cp == null) {
			return 0;
		}
		return cp.getMaxvault();
	}

	public static int getMinVault(final OfflinePlayer ofp, final CustomPermission cp) {
		int highest = 0;
		if (ofp.isOnline()) {
			final Player p = ofp.getPlayer();
			for (final PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
				String perm = pai.getPermission();
				if (perm.startsWith("morevaultplus.minvault.")) {
					perm = perm.replace("morevaultplus.minvault.", "");
					if (perm.equalsIgnoreCase("*")) {
						return 1000;
					}
					final int amount = MathUT.strInt(perm);
					if (amount <= highest) {
						continue;
					}
					highest = amount;
				}
			}
			if (highest > 0) {
				return highest;
			}
		}
		if (cp == null) {
			return 0;
		}
		return cp.getMinvault();
	}

	public static int getOwnedVault(final OfflinePlayer ofp, final CustomPermission cp) {
		int owned = Datamanager.getCurrentOwned(ofp.getUniqueId());
		if (owned < 0) {
			owned = getMinVault(ofp, cp);
			Datamanager.cachePlayer.put(ofp.getUniqueId(), owned);
		}
		return owned;
	}

	public static int getLinkedVault(final OfflinePlayer ofp) {
		if (!ConfigManager.link) {
			return -2;
		}
		int owned = Datamanager.getLinked(ofp.getUniqueId());
		if (owned < 0) {
			owned = -2;
			Datamanager.cacheLinked.put(ofp.getUniqueId(), owned);
		}
		return owned;
	}

	public static Boolean getAutoSell(final OfflinePlayer ofp) {
		if (!ConfigManager.autosell) {
			return false;
		}
		Boolean autosell = Datamanager.getAutoSell(ofp.getUniqueId());
		return autosell;
	}

	public static int getSpace(final CustomPermission cp) {
		if (cp == null) {
			return CustomPermission.lowest;
		}
		return cp.getSpace();
	}

	public static double getCost(final CustomPermission cp) {
		if (cp == null) {
			return MoreVaultAPI.lcp.get(MoreVaultAPI.lcp.size() - 1).getCost();
		}
		return cp.getCost();
	}

	public static double getRealCost(final OfflinePlayer ofp, final CustomPermission cp) {
		final double cost = getCost(cp);
		final String inc = getIncrease(cp);
		double increase = 0.0;
		double mult = 0.0;
		try {
			increase = Double.parseDouble(inc);
		} catch (Exception ex) {
			mult = Double.parseDouble(inc.replace("x", ""));
		}
		final int nextid = getOwnedVault(ofp, cp) + 1;
		if (nextid >= getMaxVault(ofp, cp)) {
			return -99.0;
		}
		double ucost = 0.0;
		if (mult > 0.0) {
			double tcost = cost;
			for (int y = 1; y <= nextid; ++y) {
				tcost *= mult;
			}
			ucost = tcost;
		} else {
			ucost = cost + increase * (nextid - 1);
		}
		return ucost;
	}

	public static String getIncrease(final CustomPermission cp) {
		if (cp == null) {
			return MoreVaultAPI.lcp.get(MoreVaultAPI.lcp.size() - 1).getIncreaseamount();
		}
		if (cp.isIncreasecost()) {
			return cp.getIncreaseamount();
		}
		return "0";
	}

	public static double getMaxDeposit(final CustomPermission cp) {
		if (cp == null) {
			return 0.0;
		}
		return cp.getMaxdeposit();
	}

	public static double getMaxExpDeposit(final CustomPermission cp) {
		if (cp == null) {
			return 0.0;
		}
		return cp.getMaxExpdeposit();
	}

	public static double getDeviceCost(final CustomPermission cp) {
		if (cp == null) {
			return 0.0;
		}
		return cp.getDeviceCost();
	}

	public static double getLinkCost(final CustomPermission cp) {
		if (cp == null) {
			return 0.0;
		}
		return cp.getLinkcost();
	}

	public static ItemStack getDevice() {
		ItemStack device = ItemUT.getItem(Core.configplugin.getStr("vault-device.material", "none"));
		device = ItemUT.changeDisplayName(device, Core.configplugin.getStr("vault-device.displayname", "none"));
		device = ItemUT.changeLore(device, Core.configplugin.getStrList("vault-device.lore", new ArrayList<String>()));
		return device;
	}
}
