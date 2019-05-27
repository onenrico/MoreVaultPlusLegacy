//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.sorter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class Shakersort {
	static List<ItemStack> items;
	static ItemStack k;

	static {
		Shakersort.items = new ArrayList<>();
	}

	public static List<ItemStack> sort(final List<ItemStack> is) {
		Shakersort.items = is;
		for (int i = 0, l = Shakersort.items.size(); i < l; ++i) {
			shaker1(i, l);
			--l;
			shaker2(i, l);
		}
		return Shakersort.items;
	}

	private static void shaker1(final int i, final int l) {
		for (int j = i; j < l - 1; ++j) {
			if (Shakersort.items.get(j).getData().getItemType().getId() > Shakersort.items.get(j + 1).getData()
					.getItemType().getId()) {
				Shakersort.k = Shakersort.items.get(j);
				Shakersort.items.set(j, Shakersort.items.get(j + 1));
				Shakersort.items.set(j + 1, Shakersort.k);
			}
		}
	}

	private static void shaker2(final int i, final int l) {
		for (int j = l - 1; j >= i; --j) {
			if (Shakersort.items.get(j).getData().getItemType().getId() > Shakersort.items.get(j + 1).getData()
					.getItemType().getId()) {
				Shakersort.k = Shakersort.items.get(j);
				Shakersort.items.set(j, Shakersort.items.get(j + 1));
				Shakersort.items.set(j, Shakersort.items.get(j + 1));
				Shakersort.items.set(j + 1, Shakersort.k);
			}
		}
	}

	public static List<ItemStack> sortDurability(final List<ItemStack> is) {
		Shakersort.items = is;
		for (int i = 0, l = Shakersort.items.size(); i < l; ++i) {
			shakerdurability1(i, l);
			--l;
			shakerdurability2(i, l);
		}
		return Shakersort.items;
	}

	private static void shakerdurability1(final int i, final int l) {
		for (int j = i; j < l - 1; ++j) {
			if (Shakersort.items.get(j).getDurability() > Shakersort.items.get(j + 1).getDurability()) {
				Shakersort.k = Shakersort.items.get(j);
				Shakersort.items.set(j, Shakersort.items.get(j + 1));
				Shakersort.items.set(j + 1, Shakersort.k);
			}
		}
	}

	private static void shakerdurability2(final int i, final int l) {
		for (int j = l - 1; j >= i; --j) {
			if (Shakersort.items.get(j).getDurability() > Shakersort.items.get(j + 1).getDurability()) {
				Shakersort.k = Shakersort.items.get(j);
				Shakersort.items.set(j, Shakersort.items.get(j + 1));
				Shakersort.items.set(j, Shakersort.items.get(j + 1));
				Shakersort.items.set(j + 1, Shakersort.k);
			}
		}
	}
}
