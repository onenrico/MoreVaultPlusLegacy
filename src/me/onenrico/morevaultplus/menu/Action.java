//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.menu;

import java.util.Arrays;

import org.bukkit.event.inventory.ClickType;

public abstract class Action {
	private ClickType[] clicktype;

	public Action() {
		this(new ClickType[0]);
	}

	public Action(final ClickType... clicktype) {
		this.clicktype = clicktype;
	}

	public ClickType[] getClickType() {
		return clicktype;
	}

	public boolean valid(final ClickType[] clicktype) {
		return this.clicktype.length < 1 || Arrays.equals(this.clicktype, clicktype);
	}

	public boolean valid(final ClickType clicktype) {
		if (this.clicktype.length < 1) {
			return true;
		}
		ClickType[] clicktype2;
		for (int length = (clicktype2 = this.clicktype).length, i = 0; i < length; ++i) {
			final ClickType ct = clicktype2[i];
			if (ct.equals(clicktype)) {
				return true;
			}
		}
		return false;
	}

	public abstract void act();

	public void run() {
		act();
	}
}
