//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

public class MethodNames {
	private static final MinecraftVersion MINECRAFT_VERSION;

	static {
		MINECRAFT_VERSION = MinecraftVersion.getVersion();
	}

	public static String getTileDataMethodName() {
		if (MethodNames.MINECRAFT_VERSION == MinecraftVersion.MC1_8_R3) {
			return "b";
		}
		return "save";
	}

	public static String getEntityNbtGetterMethodName() {
		return "b";
	}

	public static String getEntityNbtSetterMethodName() {
		return "a";
	}
}
