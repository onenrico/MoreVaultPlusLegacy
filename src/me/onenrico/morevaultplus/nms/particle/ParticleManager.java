//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nms.particle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.ReflectionUT;

public class ParticleManager {
	private static Class<?> packetClass;
	private static Class<?> newEnum;
	private static Class<?> playerConnection;
	private static Class<?> Packet;
	private static Method getEnum;
	private static Method sendPacket;
	private static Constructor<?> packetConstructor;
	public static boolean first;

	static {
		ParticleManager.first = true;
	}

	public static void setup() {
		ParticleManager.first = false;
		try {
			ParticleManager.packetClass = ReflectionUT.getNMSClass("PacketPlayOutWorldParticles");
			ParticleManager.newEnum = ReflectionUT.getNMSClass("EnumParticle");
			ParticleManager.playerConnection = ReflectionUT.getNMSClass("PlayerConnection");
			ParticleManager.Packet = ReflectionUT.getNMSClass("Packet");
			ParticleManager.getEnum = ParticleManager.newEnum.getMethod("valueOf", String.class);
			ParticleManager.sendPacket = ParticleManager.playerConnection.getMethod("sendPacket",
					ParticleManager.Packet);
			ParticleManager.packetConstructor = ParticleManager.packetClass.getConstructor(ParticleManager.newEnum,
					Boolean.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE,
					Integer.TYPE, int[].class);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex2) {
			MessageUT.cmsg("Using new Particle Protocol");
		}
	}

	public static void sendParticles(final Player player, final String particle, final Location loc,
			final float xOffset, final float yOffset, final float zOffset, final float data, final int amount) {
		if (ParticleManager.first) {
			setup();
		}
		final float x = (float) loc.getX();
		final float y = (float) loc.getY();
		final float z = (float) loc.getZ();
		try {
			final Object enumParticle = ParticleManager.getEnum.invoke(ParticleManager.newEnum, particle);
			final Object packet = ParticleManager.packetConstructor.newInstance(enumParticle, true, x, y, z, xOffset,
					yOffset, zOffset, data, amount, null);
			ParticleManager.sendPacket.invoke(ReflectionUT.getConnection(player), packet);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {

			e.printStackTrace();
		}
	}
}
