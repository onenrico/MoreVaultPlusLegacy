//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.onenrico.morevaultplus.nms.particle.ParticleManager;

public class ParticleUT {
	public static Location newloc;

	public static void hat(Player player) {
		Location lochat = player.getEyeLocation().add(0.0D, 0.25D, 0.0D);
		for (int i = 0; i < 12; i++) {
			white(lochat.clone().add(0.35D * Math.cos(Math.toRadians(30.0D * i)), 0.0D,
					0.35D * Math.sin(Math.toRadians(30.0D * i))));
			red(lochat.clone().add(0.25D * Math.cos(Math.toRadians(30.0D * i)), 0.1D,
					0.25D * Math.sin(Math.toRadians(30.0D * i))));
			red(lochat.clone().add(0.16D * Math.cos(Math.toRadians(30.0D * i)), 0.2D,
					0.16D * Math.sin(Math.toRadians(30.0D * i))));
			red(lochat.clone().add(0.07D * Math.cos(Math.toRadians(30.0D * i)), 0.3D,
					0.07D * Math.sin(Math.toRadians(30.0D * i))));
			red(lochat.clone().add(0.07D * Math.cos(Math.toRadians(30.0D * i)), 0.4D,
					0.07D * Math.sin(Math.toRadians(30.0D * i))));
		}
		for (int i = 0; i < 5; i++) {
			white(lochat.clone().add((Math.random() - 0.5D) / 10.0D, 0.46D, (Math.random() - 0.5D) / 10.0D));
		}
	}

	static Particle cache;

	static void white(Location loc) {
		if (ReflectionUT.VERSION.startsWith("v1_13")) {
			if (cache == null) {
				cache = Particle.valueOf("REDSTONE");
			}
			org.bukkit.Particle.DustOptions dustOptions = new org.bukkit.Particle.DustOptions(
					Color.fromRGB(255, 255, 255), 1);
			loc.getWorld().spawnParticle(cache, loc, 50, dustOptions);
		} else {
			send((Player) null, "REDSTONE", loc, 1f, 1f, 1f, 1f, 0, true);
		}
	}

	static void red(Location loc) {
		if (ReflectionUT.VERSION.startsWith("v1_13")) {
			if (cache == null) {
				cache = Particle.valueOf("REDSTONE");
			}
			org.bukkit.Particle.DustOptions dustOptions = new org.bukkit.Particle.DustOptions(Color.fromRGB(255, 0, 0),
					1);
			loc.getWorld().spawnParticle(cache, loc, 50, dustOptions);
		} else {
			send((Player) null, "REDSTONE", loc, 1f, 0f, 0f, 1f, 0, true);
		}
	}

//	  static void white(Location loc)
//	  {
//		    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, 1.0D, 1.0D, 1.0D, 1.0D);
//		  }
//
//		  static void red(Location loc)
//		  {
//		    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, 1.0D, 0.0D, 0.0D, 1.0D);
//		  }
	public static void send(final Player player, final String effect, final Location loc, final float xOffset,
			final float yOffset, final float zOffset, final float speed, final int amount, final Boolean all) {
		boolean oldmethod = false;
		if (ReflectionUT.VERSION.startsWith("v1_8")) {
			oldmethod = true;
		}
		if (all) {
			if (oldmethod) {
				for (final Player p : Bukkit.getOnlinePlayers()) {
					ParticleManager.sendParticles(p, effect, loc, xOffset, yOffset, zOffset, speed, amount);
				}
			} else {
				loc.getWorld().spawnParticle(Particle.valueOf(effect.toUpperCase()), loc, amount, xOffset, yOffset,
						zOffset, speed);
			}
		} else if (oldmethod) {
			ParticleManager.sendParticles(player, effect, loc, xOffset, yOffset, zOffset, speed, amount);
		} else {
			player.spawnParticle(Particle.valueOf(effect.toUpperCase()), loc, amount, xOffset, yOffset, zOffset, speed);
		}
	}

	public static void send(final Player player, final String effect, final Location loc, final float speed,
			final int amount, final Boolean all) {
		send(player, effect, loc, 0.0f, 0.0f, 0.0f, speed, amount, all);
	}

	public static void send(final Player player, final String effect, final Location loc, final float speed,
			final Boolean all) {
		send(player, effect, loc, speed, 1, all);
	}

	public static void send(final Player player, final String effect, final Location loc, final int amount,
			final Boolean all) {
		send(player, effect, loc, 0.0f, amount, all);
	}

	public static void send(final Player player, final String effect, final Location loc, final Boolean all) {
		send(player, effect, loc, 0.0f, 0.0f, 0.0f, 0.0f, 1, all);
	}

	public static void send(final Player player, final String effect, final Boolean all) {
		send(player, effect, player.getLocation(), all);
	}

	public static Vector rotateAroundAxisX(final Vector v, final double cos, final double sin) {
		final double y = v.getY() * cos - v.getZ() * sin;
		final double z = v.getY() * sin + v.getZ() * cos;
		return v.setY(y).setZ(z);
	}

	public static Vector rotateAroundAxisY(final Vector v, final double cos, final double sin) {
		final double x = v.getX() * cos + v.getZ() * sin;
		final double z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	public static Vector rotateAroundAxisZ(final Vector v, final double cos, final double sin) {
		final double x = v.getX() * cos - v.getY() * sin;
		final double y = v.getX() * sin + v.getY() * cos;
		return v.setX(x).setY(y);
	}

	public static List<Location> circle(final Location loc, final double r, final double h, final boolean hollow,
			final boolean sphere, final double plus_y) {
		final List<Location> circleblocks = new ArrayList<>();
		final double cx = loc.getX();
		final double cy = loc.getY();
		final double cz = loc.getZ();
		for (double x = cx - r; x <= cx + r; x += 0.3) {
			for (double z = cz - r; z <= cz + r; z += 0.3) {
				for (double y = sphere ? (cy - r) : cy; y < (sphere ? (cy + r) : (cy + h)); y += 0.3) {
					final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z)
							+ (sphere ? ((cy - y) * (cy - y)) : 0.0);
					if (dist < r * r && (!hollow || dist >= (r - 1.0) * (r - 1.0))) {
						final Location l = new Location(loc.getWorld(), x, y + plus_y, z);
						circleblocks.add(l);
					}
				}
			}
		}
		return circleblocks;
	}
}
