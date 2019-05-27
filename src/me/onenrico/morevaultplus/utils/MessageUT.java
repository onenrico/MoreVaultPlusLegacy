//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.nms.actionbar.ActionBar;
import me.onenrico.morevaultplus.nms.sound.SoundManager;
import me.onenrico.morevaultplus.nms.titlebar.TitleBar;

public class MessageUT {
	static boolean debug;

	static {
		MessageUT.debug = false;
	}

	public static void plmessage(final Player p, final List<String> msg) {
		plmessage(p, msg, null);
	}

	public static void plmessage(final Player p, final List<String> msg, final PlaceholderUT pu) {
		for (final String m : msg) {
			plmessage(p, m, pu);
		}
	}

	public static void plmessage(final Player p, final String msg) {
		plmessage(p, msg, null);
	}

	public static void plmessage(final Player p, String msg, PlaceholderUT pu) {
		String prefix = Locales.jsonpluginPrefix;
		if (pu == null) {
			pu = new PlaceholderUT(Locales.getPlaceholder());
		}
		pu.add("player", p.getName());
		msg = pu.t(msg);
		String main = "<np>";
		if (msg.contains(main)) {
			prefix = "";
			msg = StringUT.remove(msg, main);
		}
		main = "<console>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			cmsg(msg);
			return;
		}
		main = "<sound>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			SoundManager.playSound(p, msg.toUpperCase());
			return;
		}
		main = "<title>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			main = "<subtitle>";
			if (msg.contains(main)) {
				TitleBar.sendFullTitle(p, 0, 60, 20, msg.split(main)[0], msg.split(main)[1]);
				msg = StringUT.remove(msg, main);
			} else {
				TitleBar.sendTitle(p, 0, 60, 20, msg);
			}
			return;
		}
		main = "<subtitle>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			TitleBar.sendSubtitle(p, 0, 60, 20, msg);
			return;
		}
		main = "<actionbar>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			ActionBar.sendActionBar(p, msg);
			return;
		}
		main = "<center>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			msg = String.valueOf(prefix) + msg;
			if (msg.contains("<json>")) {
				pmessage(p, String.valueOf(main) + msg);
				return;
			}
			msg = StringUT.centered(msg);
			pmessage(p, msg);
		} else {
			pmessage(p, String.valueOf(prefix) + msg);
		}
	}

	public static void cmsg(final String msg) {
		Bukkit.getConsoleSender().sendMessage(StringUT.t("|" + Core.getThis().getName() + "> " + msg));
	}

	public static void cmsg(final List<String> msgs, final PlaceholderUT pu) {
		for (final String msg : msgs) {
			Bukkit.getConsoleSender().sendMessage(StringUT.t("|" + Core.getThis().getName() + "> " + pu.t(msg)));
		}
	}

	public static void debug(final String msg) {
		if (MessageUT.debug) {
			Bukkit.getConsoleSender()
					.sendMessage(StringUT.t("&b&l|&dD]&f" + Core.getThis().getName() + "&7&l> &r" + msg));
		}
	}

	public static void pmessage(final Player p, final String msg) {
		if (msg.contains("<json>")) {
			JsonUT.send(p, StringUT.t(msg));
		} else {
			p.sendMessage(StringUT.t(msg));
		}
	}

	public static void jmessage(final Player p, final String msg) {
		JsonUT.send(p, StringUT.t(msg));
	}
}
