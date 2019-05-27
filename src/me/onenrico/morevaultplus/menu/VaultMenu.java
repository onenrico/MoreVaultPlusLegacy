//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.menu;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.converter.ItemSerializer;
import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.ConfigManager;
import me.onenrico.morevaultplus.main.MoreVaultAPI;
import me.onenrico.morevaultplus.object.CustomPermission;
import me.onenrico.morevaultplus.object.EVault;
import me.onenrico.morevaultplus.utils.ItemUT;
import me.onenrico.morevaultplus.utils.MathUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;
import me.onenrico.morevaultplus.utils.StringUT;

public class VaultMenu extends GUIMenu {
	protected int id;

	@Override
	public void build() {
		if (title != null && title.length() > 32) {
			title = title.trim();
			if (title.length() > 32) {
				title = title.substring(0, 32);
			}
		}
		String temptitle = title;
		if (ConfigManager.uncolor) {
			temptitle = StringUT.d(temptitle);
		}
		final Inventory tempinv = Bukkit.createInventory(this, row * 9, StringUT.t(temptitle));
		tempinv.setMaxStackSize(stacksize);
		if (inv != null) {
			for (int x = 0; x < tempinv.getSize() && x < inv.getSize(); ++x) {
				tempinv.setItem(x, inv.getItem(x));
			}
		}
		for (final MenuItem mi : new ArrayList<>(menuitems)) {
			if (mi.getSlot() + 1 < tempinv.getSize()) {
				tempinv.setItem(mi.getSlot(), new ItemStack(Material.AIR));
			}
		}
		menuitems.clear();
		inv = tempinv;
	}

	public VaultMenu(final String title, final int row, final UUID owner, final int id) {
		super("vault-icon", title, row, true, true, owner);
		this.id = id;
	}

	public VaultMenu() {
	}

	@Override
	public String toString() {
		for (final MenuItem mi : menuitems) {
			getInventory().setItem(mi.getSlot(), ItemUT.createItem(Material.AIR));
		}
		return ItemSerializer.serialize(getInventory());
	}

	public void fromString(final String str) {
		ItemSerializer.deserialize(str, getInventory());
	}

	public int getId() {
		return id;
	}

	public static PlaceholderUT getPlaceholder(final Player p, final OfflinePlayer target, final EVault ev) {
		final PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		final CustomPermission cp = MoreVaultAPI.getCustomPermission(target, p.getWorld());
		final double moneyinside = ev.getBalance();
		final double maxmoney = MoreVaultAPI.getMaxDeposit(cp);
		final double expinside = ev.getExp();
		final double maxexp = MoreVaultAPI.getMaxExpDeposit(cp);
		int space = MoreVaultAPI.getSpace(cp);
		ev.setRow(space / 9);
		final int used = ev.getUsed();
		final int total = ev.getItemsAmount();
		pu.add("amount", MathUT.format(moneyinside));
		pu.add("max-amount", MathUT.format(maxmoney));
		pu.add("exp-amount", MathUT.format(expinside));
		pu.add("max-exp-amount", MathUT.format(maxexp));
		pu.add("used-space", new StringBuilder().append(used).toString());
		int before = 0;
		if (ConfigManager.vaultsort) {
			--space;
			++before;
		}
		if (ConfigManager.vaultn) {
			space -= 2;
			before += 2;
		}
		if (ConfigManager.vaultd) {
			--space;
			++before;
		}
		if (ConfigManager.vaulted) {
			--space;
			++before;
		}
		if (ConfigManager.nobottom) {
			space -= 9 - before;
		}
		pu.add("max-space", new StringBuilder().append(space).toString());
		pu.add("used-total", new StringBuilder().append(ev.getItemsAmount()).toString());
		pu.add("total-space", new StringBuilder().append(space * 64).toString());
		pu.add("moneyinside", new StringBuilder().append(moneyinside).toString());
		pu.add("maxmoney", new StringBuilder().append(maxmoney).toString());
		pu.add("expinside", new StringBuilder().append(expinside).toString());
		pu.add("maxexp", new StringBuilder().append(maxexp).toString());
		pu.add("used", new StringBuilder().append(used).toString());
		pu.add("space", new StringBuilder().append(space).toString());
		pu.add("total", new StringBuilder().append(total).toString());
		pu.add("space64", new StringBuilder().append(space * 64).toString());
		pu.add("moneybar",
				"<bar>bars=15<>value={moneyinside}<>maxvalue={maxmoney}<>symbol=\u258d<>fill=&6<>empty=&b</bar>");
		pu.add("expbar", "<bar>bars=15<>value={expinside}<>maxvalue={maxexp}<>symbol=\u258d<>fill=&6<>empty=&b</bar>");
		pu.add("spacebar", "<bar>bars=15<>value={used}<>maxvalue={space}<>symbol=\u258d<>fill=&6<>empty=&b</bar>");
		pu.add("totalbar", "<bar>bars=15<>value={total}<>maxvalue={space64}<>symbol=\u258d<>fill=&6<>empty=&b</bar>");
		pu.add("id", new StringBuilder().append(ev.getId()).toString());
		pu.add("name", pu.t(ev.getTitle()));
		return pu;
	}
}
