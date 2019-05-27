//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUT {
	public static Collection<? extends Player> getOnlinePlayers() {
		return Bukkit.getServer().getOnlinePlayers();
	}

	public static int getTotalExperience(final Player player) {
		int experience = 0;
		final int level = player.getLevel();
		if (level >= 0 && level <= 15) {
			experience = (int) Math.ceil(Math.pow(level, 2.0) + 6 * level);
			final int requiredExperience = 2 * level + 7;
			final double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += (int) Math.ceil(currentExp * requiredExperience);
			return experience;
		}
		if (level > 15 && level <= 30) {
			experience = (int) Math.ceil(2.5 * Math.pow(level, 2.0) - 40.5 * level + 360.0);
			final int requiredExperience = 5 * level - 38;
			final double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += (int) Math.ceil(currentExp * requiredExperience);
			return experience;
		}
		experience = (int) Math.ceil(4.5 * Math.pow(level, 2.0) - 162.5 * level + 2220.0);
		final int requiredExperience = 9 * level - 158;
		final double currentExp = Double.parseDouble(Float.toString(player.getExp()));
		experience += (int) Math.ceil(currentExp * requiredExperience);
		return experience;
	}

	public static void setTotalExperience(final Player player, final int xp) {
		if (xp >= 0 && xp < 351) {
			final int a = 1;
			final int b = 6;
			final int c = -xp;
			final int level = (int) (-b + Math.sqrt(Math.pow(b, 2.0) - 4 * a * c)) / (2 * a);
			final int xpForLevel = (int) (Math.pow(level, 2.0) + 6 * level);
			final int remainder = xp - xpForLevel;
			final int experienceNeeded = 2 * level + 7;
			float experience = remainder / experienceNeeded;
			experience = round(experience, 2);
			player.setLevel(level);
			player.setExp(experience);
		} else if (xp >= 352 && xp < 1507) {
			final double a2 = 2.5;
			final double b2 = -40.5;
			final int c2 = -xp + 360;
			final double dLevel = (-b2 + Math.sqrt(Math.pow(b2, 2.0) - 4.0 * a2 * c2)) / (2.0 * a2);
			final int level2 = (int) Math.floor(dLevel);
			final int xpForLevel2 = (int) (2.5 * Math.pow(level2, 2.0) - 40.5 * level2 + 360.0);
			final int remainder2 = xp - xpForLevel2;
			final int experienceNeeded2 = 5 * level2 - 38;
			float experience2 = remainder2 / experienceNeeded2;
			experience2 = round(experience2, 2);
			player.setLevel(level2);
			player.setExp(experience2);
		} else {
			final double a2 = 4.5;
			final double b2 = -162.5;
			final int c2 = -xp + 2220;
			final double dLevel = (-b2 + Math.sqrt(Math.pow(b2, 2.0) - 4.0 * a2 * c2)) / (2.0 * a2);
			final int level2 = (int) Math.floor(dLevel);
			final int xpForLevel2 = (int) (4.5 * Math.pow(level2, 2.0) - 162.5 * level2 + 2220.0);
			final int remainder2 = xp - xpForLevel2;
			final int experienceNeeded2 = 9 * level2 - 158;
			float experience2 = remainder2 / experienceNeeded2;
			experience2 = round(experience2, 2);
			player.setLevel(level2);
			if (experience2 < 0.0f) {
				experience2 = 0.0f;
			}
			player.setExp(experience2);
		}
	}

	private static float round(final float d, final int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, 5);
		return bd.floatValue();
	}

	public static Player getPlayer(final String name) {
		return getPlayer(name, false);
	}

	public static Player getPlayer(final String name, final Boolean exact) {
		if (exact) {
			return Bukkit.getPlayerExact(name);
		}
		return Bukkit.getPlayer(name);
	}

	public static Player getPlayer(final UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}

	public static Player getPlayer(final Object object) {
		final Player player = (Player) object;
		return player;
	}

	public static Boolean isOnline(final Player player) {
		return isOnline(player.getName());
	}

	public static Boolean isOnline(final String name) {
		if (Bukkit.getPlayer(name) == null) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public static void setHand(final Player player, final ItemStack item) {
		boolean oldmethod = false;
		for (int x = 1; x < 4; ++x) {
			if (ReflectionUT.VERSION.equalsIgnoreCase("v1_8_R" + x)) {
				oldmethod = true;
			}
		}
		if (oldmethod) {
			player.getInventory().setItemInHand(item);
			return;
		}
		player.getInventory().setItemInMainHand(item);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getHand(final Player player) {
		boolean oldmethod = false;
		for (int x = 1; x < 4; ++x) {
			if (ReflectionUT.VERSION.equalsIgnoreCase("v1_8_R" + x)) {
				oldmethod = true;
			}
		}
		if (oldmethod) {
			return player.getItemInHand();
		}
		return player.getInventory().getItemInMainHand();
	}
//
//	public enum Skull {
//		ARROW_LEFT("ARROW_LEFT", 0, "MHF_ArrowLeft"), ARROW_RIGHT("ARROW_RIGHT", 1, "MHF_ArrowRight"),
//		ARROW_UP("ARROW_UP", 2, "MHF_ArrowUp"), ARROW_DOWN("ARROW_DOWN", 3, "MHF_ArrowDown"),
//		QUESTION("QUESTION", 4, "MHF_Question"), EXCLAMATION("EXCLAMATION", 5, "MHF_Exclamation"),
//		CAMERA("CAMERA", 6, "FHG_Cam"), ZOMBIE_PIGMAN("ZOMBIE_PIGMAN", 7, "MHF_PigZombie"), PIG("PIG", 8, "MHF_Pig"),
//		SHEEP("SHEEP", 9, "MHF_Sheep"), BLAZE("BLAZE", 10, "MHF_Blaze"), CHICKEN("CHICKEN", 11, "MHF_Chicken"),
//		COW("COW", 12, "MHF_Cow"), SLIME("SLIME", 13, "MHF_Slime"), SPIDER("SPIDER", 14, "MHF_Spider"),
//		SQUID("SQUID", 15, "MHF_Squid"), VILLAGER("VILLAGER", 16, "MHF_Villager"), OCELOT("OCELOT", 17, "MHF_Ocelot"),
//		HEROBRINE("HEROBRINE", 18, "MHF_Herobrine"), LAVA_SLIME("LAVA_SLIME", 19, "MHF_LavaSlime"),
//		MOOSHROOM("MOOSHROOM", 20, "MHF_MushroomCow"), GOLEM("GOLEM", 21, "MHF_Golem"), GHAST("GHAST", 22, "MHF_Ghast"),
//		ENDERMAN("ENDERMAN", 23, "MHF_Enderman"), CAVE_SPIDER("CAVE_SPIDER", 24, "MHF_CaveSpider"),
//		CACTUS("CACTUS", 25, "MHF_Cactus"), CAKE("CAKE", 26, "MHF_Cake"), CHEST("CHEST", 27, "MHF_Chest"),
//		MELON("MELON", 28, "MHF_Melon"), LOG("LOG", 29, "MHF_OakLog"), PUMPKIN("PUMPKIN", 30, "MHF_Pumpkin"),
//		TNT("TNT", 31, "MHF_TNT"), DYNAMITE("DYNAMITE", 32, "MHF_TNT2");
//
//		private static final Base64 base64;
//		private String id;
//
//		static {
//			base64 = new Base64();
//		}
//
//		private Skull(final String s, final int n, final String id) {
//			this.id = id;
//		}
//
//		public static GameProfile getProfile(final String url, final Boolean encoded) {
//			final GameProfile profile = new GameProfile(UUID.randomUUID(), (String) null);
//			final PropertyMap propertyMap = profile.getProperties();
//			if (propertyMap == null) {
//				throw new IllegalStateException("Profile doesn't contain a property map");
//			}
//			if (!encoded) {
//				final byte[] encodedData = Skull.base64
//						.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
//				propertyMap.put("textures", new Property("textures", new String(encodedData)));
//			} else {
//				propertyMap.put("textures", new Property("textures", url));
//			}
//			return profile;
//		}
//
//		public String getId() {
//			return id;
//		}
//	}
}
