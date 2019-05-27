//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.PermissionUT;

public class CustomPermission {
	private int minvault;
	private int maxvault;
	private int space;
	private String path;
	private double cost;
	private boolean increasecost;
	private String increaseamount;
	private double devicecost;
	private double linkcost;
	private String permission;
	private boolean changeIcon;
	private boolean changeName;
	private boolean changeDescription;
	private List<ItemStack> blacklistitem;
	private double maxdeposit;
	private double maxexpdeposit;
	private int stacksize;
	public static int lowest;

	static {
		CustomPermission.lowest = 54;
	}

	public CustomPermission(final String path) {
		changeIcon = false;
		changeName = false;
		changeDescription = false;
		blacklistitem = new ArrayList<>();
		stacksize = 64;
		this.path = path;
		final String pref = "custom-permission." + path + ".";
		increasecost = Core.configplugin.getBool(String.valueOf(pref) + "increasing-cost", false);
		increaseamount = Core.configplugin.getStr(String.valueOf(pref) + "increase-amount", "0");
		cost = Core.configplugin.getDouble(String.valueOf(pref) + "unlock-cost", -100.0);
		devicecost = Core.configplugin.getDouble(String.valueOf(pref) + "device-cost", -100.0);
		linkcost = Core.configplugin.getDouble(String.valueOf(pref) + "link-cost", -100.0);
		minvault = Core.configplugin.getInt(String.valueOf(pref) + "min-vault", -100);
		space = Core.configplugin.getInt(String.valueOf(pref) + "space", 0);
		if (space < CustomPermission.lowest) {
			CustomPermission.lowest = space;
		}
		maxvault = Core.configplugin.getInt(String.valueOf(pref) + "max-vault", -100);
		stacksize = Core.configplugin.getInt(String.valueOf(pref) + "stack-size", -100);
		permission = Core.configplugin.getStr(String.valueOf(pref) + "permission", "not.set");
		changeName = Core.configplugin.getBool(String.valueOf(pref) + "change-name", false);
		changeIcon = Core.configplugin.getBool(String.valueOf(pref) + "change-icon", false);
		changeDescription = Core.configplugin.getBool(String.valueOf(pref) + "change-description", null);
		maxdeposit = Core.configplugin.getDouble(String.valueOf(pref) + "max-deposit", -100.0);
		maxexpdeposit = Core.configplugin.getDouble(String.valueOf(pref) + "max-exp-deposit", -100.0);
		final List<String> dbli = new ArrayList<>();
		dbli.add("BEDROCK");
		final List<String> blitem = Core.configplugin.getStrList(String.valueOf(pref) + "item-blacklist", dbli);
		for (final String i : blitem) {
			final ItemStack bl = ItemUT.getItem(i);
			blacklistitem.add(bl);
		}
	}

	public int getMinvault() {
		return minvault;
	}

	public int getMaxvault() {
		return maxvault;
	}

	public String getPermission() {
		return permission;
	}

	public Boolean is(final OfflinePlayer ofc, final World world) {
		if (path.equalsIgnoreCase("default")) {
			return true;
		}
		if (ofc.isOnline()) {
			final Player p = ofc.getPlayer();
			if (PermissionUT.has(p, permission)) {
				return true;
			}
			return false;
		} else {
			if (PermissionUT.has(ofc, permission, world)) {
				return true;
			}
			return false;
		}
	}

	public String getPath() {
		return path;
	}

	public double getCost() {
		return cost;
	}

	public int getStackSize() {
		return stacksize;
	}

	public double getDeviceCost() {
		return devicecost;
	}

	public int getSpace() {
		return space;
	}

	public boolean isChangeIcon() {
		return changeIcon;
	}

	public boolean isChangeName() {
		return changeName;
	}

	public List<ItemStack> getBlacklistitem() {
		return blacklistitem;
	}

	public boolean isIncreasecost() {
		return increasecost;
	}

	public String getIncreaseamount() {
		return increaseamount;
	}

	public double getMaxdeposit() {
		return maxdeposit;
	}

	public double getMaxExpdeposit() {
		return maxexpdeposit;
	}

	public void setMaxdeposit(final double maxdeposit) {
		this.maxdeposit = maxdeposit;
	}

	public boolean isChangeDescription() {
		return changeDescription;
	}

	public double getLinkcost() {
		return linkcost;
	}
}
