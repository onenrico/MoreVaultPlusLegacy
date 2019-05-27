//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import org.bukkit.Bukkit;

import me.onenrico.morevaultplus.utils.MessageUT;

public enum MinecraftVersion {
	Unknown("Unknown", 0, Integer.MAX_VALUE), MC1_7_R4("MC1_7_R4", 1, 174), MC1_8_R3("MC1_8_R3", 2, 183),
	MC1_9_R1("MC1_9_R1", 3, 191), MC1_9_R2("MC1_9_R2", 4, 192), MC1_10_R1("MC1_10_R1", 5, 1101),
	MC1_11_R1("MC1_11_R1", 6, 1111), MC1_12_R1("MC1_12_R1", 7, 1121), MC1_13_R1("MC1_13_R1", 8, 1131),
	MC1_13_R2("MC1_13_R2", 9, 1132), MC1_14_R1("MC1_14_R1", 9, 1141);

	private static MinecraftVersion version;
	private static Boolean hasGsonSupport;
	private final int versionId;

	private MinecraftVersion(final String s, final int n, final int versionId) {
		this.versionId = versionId;
	}

	public int getVersionId() {
		return versionId;
	}

	public static MinecraftVersion getVersion() {
		if (MinecraftVersion.version != null) {
			return MinecraftVersion.version;
		}
		final String ver = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		MessageUT.cmsg("&aYou are running on &7'&b" + ver + "&7' &aversion");
		try {
			MinecraftVersion.version = valueOf(ver.replace("v", "MC"));
		} catch (IllegalArgumentException ex) {
			MinecraftVersion.version = MinecraftVersion.Unknown;
		}
		if (MinecraftVersion.version != MinecraftVersion.Unknown) {
			MessageUT.cmsg("&aNMS support &7'&b" + MinecraftVersion.version.name() + "&7' &aloaded!");
		} else {
			MessageUT.cmsg("&b" + MinecraftVersion.version.name() + " &ccurrently not supported please tell Dev");
		}
		return MinecraftVersion.version;
	}

	public static boolean hasGsonSupport() {
		if (MinecraftVersion.hasGsonSupport != null) {
			return MinecraftVersion.hasGsonSupport;
		}
		try {
			MinecraftVersion.hasGsonSupport = true;
		} catch (Exception ex) {
			MinecraftVersion.hasGsonSupport = false;
		}
		return MinecraftVersion.hasGsonSupport;
	}
}
