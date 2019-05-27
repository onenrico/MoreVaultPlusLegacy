//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.menu;

import java.util.HashSet;
import java.util.Set;

public class MenuAnimation {
	private static Set<GUIMenu> animated;

	static {
		MenuAnimation.animated = new HashSet<>();
	}

	public static void addAnimated(final GUIMenu gm) {
		MenuAnimation.animated.add(gm);
	}

	public static void removeAnimated(final GUIMenu gm) {
		if (MenuAnimation.animated.contains(gm)) {
			MenuAnimation.animated.remove(gm);
		}
	}

	public static void startTimer() {
	}
}
