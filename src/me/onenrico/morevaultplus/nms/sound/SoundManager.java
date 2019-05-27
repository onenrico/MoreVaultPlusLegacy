//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nms.sound;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SoundManager {
	public static void playSound(final Player player, String sound, final Location loc, final float volume,
			final float pitch) {
		sound = sound.toUpperCase();
		ESound.match(sound).playSound(player, volume, pitch);
	}

	public static void playSound(final Player player, final String sound) {
		playSound(player, sound, player.getLocation(), 5.0f, 1.0f);
	}

	public static void playSound(final Player player, final String sound, final Location loc) {
		playSound(player, sound, loc, 5.0f, 1.0f);
	}

	public static void playSound(final Player player, final String sound, final float volume, final float pitch) {
		playSound(player, sound, player.getLocation(), volume, pitch);
	}

	public static void playSound(final String sound, final Location loc, final float volume, final float pitch) {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			playSound(player, sound, loc, volume, pitch);
		}
	}
}
