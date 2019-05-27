//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.object;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class Seriloc {
	private Location loc;

	public Seriloc(final Location realloc) {
		loc = realloc;
	}

	public Seriloc(final String serializedloc) {
		final Location realloc = Deserialize(serializedloc);
		loc = realloc;
	}

	public static Location centered(final Location loc) {
		return loc.clone().add(0.5, 0.0, 0.5);
	}

	public static String Serialize(final Location loc) {
		final Map<String, Object> rawLoc = loc.serialize();
		String serializedLoc = "";
		int index = 0;
		for (final String key : rawLoc.keySet()) {
			if (++index < rawLoc.size()) {
				serializedLoc = String.valueOf(serializedLoc) + rawLoc.get(key) + "<>";
			} else {
				serializedLoc = String.valueOf(serializedLoc) + rawLoc.get(key);
			}
		}
		return serializedLoc;
	}

	public static Location Deserialize(String rawLoc) {
		rawLoc = rawLoc.replace("<r>", "<>");
		final String[] deserializedLoc = rawLoc.split("<>");
		World world = null;
		for (final World w : Bukkit.getWorlds()) {
			if (w.getName().equalsIgnoreCase(deserializedLoc[0])) {
				world = w;
			}
		}
		if (world == null) {
			return null;
		}
		final double x = Double.valueOf(deserializedLoc[1]);
		final double y = Double.valueOf(deserializedLoc[2]);
		final double z = Double.valueOf(deserializedLoc[3]);
		final float yaw = Float.valueOf(deserializedLoc[5]);
		final float pitch = Float.valueOf(deserializedLoc[4]);
		return new Location(world, x, y, z, yaw, pitch);
	}

	public String Serialize() {
		return Serialize(loc);
	}

	public Location Deserialize() {
		return Deserialize(Serialize(loc));
	}

	public void setWorld(final World world) {
		loc.setWorld(world);
	}

	public World getWorld() {
		return loc.getWorld();
	}

	public Chunk getChunk() {
		return loc.getChunk();
	}

	public Block getBlock() {
		return loc.getBlock();
	}

	public void setX(final double x) {
		loc.setX(x);
	}

	public double getX() {
		return loc.getX();
	}

	public int getBlockX() {
		return loc.getBlockX();
	}

	public void setY(final double y) {
		loc.setY(y);
	}

	public double getY() {
		return loc.getY();
	}

	public int getBlockY() {
		return loc.getBlockY();
	}

	public void setZ(final double z) {
		loc.setZ(z);
	}

	public double getZ() {
		return loc.getZ();
	}

	public int getBlockZ() {
		return loc.getBlockZ();
	}

	public void setYaw(final float yaw) {
		loc.setYaw(yaw);
	}

	public float getYaw() {
		return loc.getYaw();
	}

	public void setPitch(final float pitch) {
		loc.setPitch(pitch);
	}

	public float getPitch() {
		return loc.getPitch();
	}

	public Vector getDirection() {
		return loc.getDirection();
	}

	public Location setDirection(final Vector vector) {
		return loc.setDirection(vector);
	}

	public Location add(final Location vec) {
		return loc.add(vec);
	}

	public Location add(final Vector vec) {
		return loc.add(vec);
	}

	public Location add(final double x, final double y, final double z) {
		return loc.add(x, y, z);
	}

	public Location subtract(final Location vec) {
		return loc.subtract(vec);
	}

	public Location subtract(final Vector vec) {
		return loc.subtract(vec);
	}

	public Location subtract(final double x, final double y, final double z) {
		return loc.subtract(x, y, z);
	}

	public double length() {
		return loc.length();
	}

	public double lengthSquared() {
		return loc.lengthSquared();
	}

	public double distance(final Location o) {
		return loc.distance(o);
	}

	public double distanceSquared(final Location o) {
		return loc.distanceSquared(o);
	}

	public Location multiply(final double m) {
		return loc.multiply(m);
	}

	public Location zero() {
		return loc.zero();
	}

	@Override
	public boolean equals(final Object obj) {
		return loc.equals(obj);
	}

	@Override
	public int hashCode() {
		return loc.hashCode();
	}

	@Override
	public String toString() {
		return loc.toString();
	}

	public Vector toVector() {
		return loc.toVector();
	}

	public Location toLoc() {
		return loc;
	}
}
