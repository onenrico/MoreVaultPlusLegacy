//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.sorter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Sorting {
	public static void sortInventory(final Inventory inv) {
		shakerSortInventory(inv);
	}

	public static List<ItemStack> getInventory(final Inventory inv) {
		final List<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < inv.getSize(); ++i) {
			if (inv.getItem(i) != null) {
				items.add(inv.getItem(i).clone());
			}
		}
		return items;
	}

	public static void setInventory(final Inventory inv, final List<ItemStack> items) {
		for (int i = 0; i < items.size(); ++i) {
			inv.setItem(i, items.get(i));
		}
	}

	public static void shakerSortInventory(final Inventory inv) {
		List<ItemStack> items = new ArrayList<>();
		items = getInventory(inv);
		if (items.size() > 0) {
			if (items.size() == 1) {
				inv.clear();
				setInventory(inv, items);
				return;
			}
			items = getFullStackList(items);
			items = Shakersort.sort(items);
			if (items.size() > 1) {
				items = sortDurability(items);
			}
			inv.clear();
			setInventory(inv, items);
		}
	}

	public static List<ItemStack> getFullStackList(final List<ItemStack> is) {
		List<ItemStack> copy = new ArrayList<>(is);
		final List<ItemStack> items = new ArrayList<>();
		if (is.size() > 1) {
			int types = 1;
			for (int x = 0; x < copy.size(); x = 0, ++x) {
				final ItemStack stack = copy.get(x).clone();
				final List<ItemStack> remove = new ArrayList<>();
				for (int y = 0; y < copy.size(); ++y) {
					if (stack.getType().equals(copy.get(y).getType())
							&& stack.getItemMeta().equals(copy.get(y).getItemMeta())
							&& stack.getDurability() == copy.get(y).getDurability() && x != y) {
						remove.add(copy.get(y));
					}
				}
				remove.add(stack.clone());
				for (int i = 0; i < remove.size(); ++i) {
					copy.remove(remove.get(i));
				}
				++types;
			}
			copy = new ArrayList<>(is);
			for (int a = 0; a < types && copy.size() > 0; ++a) {
				int amount = copy.get(0).getAmount();
				final ItemStack stack2 = copy.get(0).clone();
				final List<ItemStack> remove2 = new ArrayList<>();
				for (int y2 = 0; y2 < copy.size(); ++y2) {
					if (stack2.getType().equals(copy.get(y2).getType())
							&& stack2.getItemMeta().equals(copy.get(y2).getItemMeta())
							&& stack2.getDurability() == copy.get(y2).getDurability() && y2 != 0) {
						amount += copy.get(y2).getAmount();
						remove2.add(copy.get(y2));
					}
				}
				remove2.add(stack2.clone());
				for (int j = 0; j < remove2.size(); ++j) {
					copy.remove(remove2.get(j));
				}
				while (amount > stack2.getMaxStackSize()) {
					final ItemStack item = new ItemStack(stack2.getType(), stack2.getMaxStackSize());
					item.setDurability(stack2.getDurability());
					item.setItemMeta(stack2.getItemMeta());
					items.add(item);
					amount -= stack2.getMaxStackSize();
				}
				final ItemStack item = new ItemStack(stack2.getType(), amount);
				item.setDurability(stack2.getDurability());
				item.setItemMeta(stack2.getItemMeta());
				items.add(item);
			}
		}
		return items;
	}

	public static List<ItemStack> sortDurability(final List<ItemStack> items) {
		final List<List<ItemStack>> itemlist = getMonoTypeStacks(items);
		if (itemlist.size() > 1) {
			final List<ItemStack> returnlist = new ArrayList<>();
			for (int i = 0; i < itemlist.size(); ++i) {
				final List<ItemStack> templist = Shakersort.sortDurability(itemlist.get(i));
				for (int j = 0; j < templist.size(); ++j) {
					returnlist.add(templist.get(j));
				}
			}
			return returnlist;
		}
		return items;
	}

	@SuppressWarnings("deprecation")
	public static List<List<ItemStack>> getMonoTypeStacks(final List<ItemStack> items) {
		final List<List<ItemStack>> newitems = new ArrayList<>();
		int beginn = 0;
		int currentID = items.get(0).getData().getItemType().getId();
		for (int i = 1; i < items.size(); ++i) {
			if (currentID != items.get(i).getData().getItemType().getId()) {
				final List<ItemStack> element = new ArrayList<>();
				for (int j = beginn; j < i; ++j) {
					element.add(items.get(j));
				}
				newitems.add(element);
				beginn = i;
				currentID = items.get(i).getData().getItemType().getId();
			}
		}
		final List<ItemStack> element2 = new ArrayList<>();
		for (int k = beginn; k < items.size(); ++k) {
			element2.add(items.get(k));
		}
		newitems.add(element2);
		return newitems;
	}
}
