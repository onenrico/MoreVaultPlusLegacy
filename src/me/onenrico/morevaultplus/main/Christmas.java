package me.onenrico.morevaultplus.main;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class Christmas {
	public static Set<Player> opening = new HashSet<>();
	static boolean started = false;

	public static void startParty() {
//		if(!started) {
//			new BukkitRunnable() {
//				@Override
//				public void run() {
//					if(!ConfigManager.christmas) {
//						this.cancel();
//						started = false;
//						return;
//					}
//					for(Player p : opening) {
//						if(p == null || !p.isOnline()) {
//							opening.remove(p);
//							continue;
//						}
//						ParticleUT.hat(p);
//					}
//				}
//			}.runTaskTimer(Core.getThis(), 2, 2);
//			started = true;
//		}
	}

}
