//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class JsonUT {
	private static String jsonsplit;

	static {
		JsonUT.jsonsplit = "<cmd>";
	}

	public static void send(final Player player, final String json) {
		send(player, json.split("<br>"));
	}

	public static String parseSimple(final String json) {
		String result = "";
		int index = 0;
		String[] split;
		for (int length = (split = json.split("<json>")).length, i = 0; i < length; ++i) {
			final String raw = split[i];
			String inside = raw.split("</json>")[0];
			Boolean d = false;
			String p = "@CS:";
			if (inside.contains(p)) {
				inside = inside.replace(p, "<cmd>#C:$SUGGEST:");
				d = true;
			}
			if (!d) {
				p = "@CR:";
				if (inside.contains(p)) {
					inside = inside.replace(p, "<cmd>#C:$RUN:");
					d = true;
				}
				if (!d) {
					p = "@CU:";
					if (inside.contains(p)) {
						inside = inside.replace(p, "<cmd>#C:$URL:");
						d = true;
					}
				}
			}
			p = "@H:";
			if (inside.contains(p)) {
				String t = "";
				if (d) {
					t = "<and>";
				} else {
					t = "<cmd>";
				}
				inside = inside.replace(p, String.valueOf(t) + "#H:$TEXT:");
			}
			final String addition = (raw.split("</json>").length > 1) ? raw.split("</json>")[1] : "";
			if (!inside.isEmpty()) {
				result = String.valueOf(result) + inside;
				if (!addition.isEmpty()) {
					result = String.valueOf(result) + "<br>" + addition;
				}
				if (index + 1 != json.split("<json>").length) {
					result = String.valueOf(result) + "<br>";
				}
			}
			++index;
		}
		if (result.endsWith("<br>")) {
			result = result.substring(0, result.length() - "<br>".length());
		}
		return result;
	}

	public static void send(final Player player, final String[] json) {
		final TextComponent rs = new TextComponent("");
		String raw = "";
		for (String component : json) {
			if (component.contains("<json>")) {
				send(player, parseSimple(component));
				return;
			}
			component = component.replace("\n", "");
			final String text = component.split(JsonUT.jsonsplit)[0];
			final TextComponent single = new TextComponent(text);
			if (component.split(JsonUT.jsonsplit).length > 1) {
				final String command = component.split(JsonUT.jsonsplit)[1];
				final String[] commands = command.split("<and>");
				String[] array;
				for (int length2 = (array = commands).length, j = 0; j < length2; ++j) {
					String eventstr = array[j];
					eventstr = eventstr.replace("{text}", text);
					eventstr = eventstr.replace("{player}", player.getName());
					if (eventstr.contains("#C:")) {
						eventstr = eventstr.replace("#C:", "");
						if (eventstr.contains("$RUN:")) {
							eventstr = eventstr.replace("$RUN:", "");
							final String cmd = StringUT.u(eventstr);
							single.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
						}
						if (eventstr.contains("$SUGGEST:")) {
							eventstr = eventstr.replace("$SUGGEST:", "");
							final String cmd = StringUT.u(eventstr);
							single.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
						}
						if (eventstr.contains("$URL:")) {
							final String cmd;
							eventstr = (cmd = eventstr.replace("$URL:", ""));
							single.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, cmd));
						}
					}
					if (eventstr.contains("#H:")) {
						eventstr = eventstr.replace("#H:", "");
						if (eventstr.contains("$TEXT:")) {
							eventstr = eventstr.replace("$TEXT:", "");
							final String msg = eventstr.replace("<nl>", "\n").replace("<n>", "\n");
							single.setHoverEvent(
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(msg).create()));
						}
					}
				}
			}
			raw = String.valueOf(raw) + single.getText();
			rs.addExtra(single);
		}
		String space = "";
		if (raw.contains("<center>")) {
			raw = raw.replace("<center>", "");
			raw = StringUT.centered(raw);
			space = StringUT.getLeadingSpaces(raw);
		}
		for (final BaseComponent tc : rs.getExtra()) {
			if (((TextComponent) tc).getText().contains("<center>")) {
				((TextComponent) tc).setText(StringUT.remove(((TextComponent) tc).getText(), "<center>"));
			}
		}
		rs.setText(space);
		player.spigot().sendMessage(rs);
	}
}
