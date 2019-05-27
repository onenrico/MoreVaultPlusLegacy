//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.menu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.utils.PlaceholderUT;

public class MenuItem {
	private int slot;
	private ItemStack item;
	private Set<Action> actions;
	private PlaceholderUT pu;

	public MenuItem(final int slot, final ItemStack item) {
		actions = new HashSet<>();
		this.slot = slot;
		this.item = item;
	}

	public MenuItem(final int slot, final ItemStack item, final PlaceholderUT pu) {
		actions = new HashSet<>();
		this.slot = slot;
		this.item = item;
		this.pu = pu;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(final int slot) {
		this.slot = slot;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(final ItemStack item) {
		this.item = item;
	}

	public MenuItem addAction(final Action action) {
		for (final Action act : new ArrayList<>(actions)) {
			if (act.valid(action.getClickType())) {
				actions.remove(act);
				actions.add(action);
				return this;
			}
		}
		actions.add(action);
		return this;
	}

	@Override
	public MenuItem clone() {
		final MenuItem result = new MenuItem(slot, item.clone(), pu);
		result.actions = new HashSet<>(actions);
		return result;
	}

	public void setActions(final Set<Action> actions) {
		this.actions = actions;
	}

	public Set<Action> getActions() {
		return actions;
	}

	public PlaceholderUT getPu() {
		return pu;
	}

	public void setPu(final PlaceholderUT pu) {
		this.pu = pu;
	}
}
