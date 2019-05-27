package me.onenrico.morevaultplus.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import me.onenrico.morevaultplus.locale.Locales;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.PlaceholderUT;
import me.onenrico.morevaultplus.utils.StringUT;

public class Help {
	private static List<String> msg;
	private static List<String> msgprocessed = new ArrayList<>();
	private static HashMap<Integer, List<String>> data = new HashMap<>();
	private static List<String> header;
	private static List<String> footer;
	private static int max;
	private static int maxpage;


	public static void setup(final List<String> msg, final int max) {
		Help.msg = msg;
		Help.max = max;
		build();
	}

	private static void build() {
		msgprocessed.clear();
		if (header == null) {
			header = Locales.getValue("help_header");
			footer = Locales.getValue("help_footer");
		}
		if (Help.msg.size() == Help.max) {
			Help.maxpage = 1;
		} else {
			Help.maxpage = (int) Math.ceil((double) Help.msg.size() / (double) Help.max);
		}
		final PlaceholderUT pm = new PlaceholderUT();
		pm.add("maxpage", Help.maxpage);
		for (int i = 0; i < Help.maxpage; ++i) {
			pm.add("page", new StringBuilder().append(i + 1).toString());
			final int index = Help.max * i;
			final List<String> result = new ArrayList<>();
			result.addAll(Help.header);
			result.add("<np>");
			for (int x = 0; x < Help.max && index + x + 1 <= Help.msg.size(); ++x) {
				String m = Help.msg.get(index + x);
				if (m.contains("<hover:")) {
					final String desc = m.split("<hover:")[1];
					m = m.split("<hover:")[0];
					m = "<json>" + m +"&7- "+desc+ "@CS:" + StringUT.d(m) + "@H:" + desc + "</json>";
				}
				result.add(m);
				msgprocessed.add(m);
			}
			result.add("<np>");
			result.addAll(Help.footer);
			Help.data.put(i, pm.t(result));
		}
	}

	public static void send(final Player p, int page) {
		if (Help.maxpage == 0) {
			return;
		}
		if (page > Help.maxpage) {
			page = Help.maxpage;
		}
		if (page < 1) {
			page = 1;
		}
		boolean next = false;
		if (page < Help.maxpage) {
			next = true;
		}
		PlaceholderUT pm = new PlaceholderUT();
		final List<String> m = Help.data.get(page - 1);
		if (Help.maxpage != 1) {
			if (next) {
				m.addAll(Locales.getValue("help_next"));
				pm.add("nextpage", page + 1);
			} else {
				m.addAll(Locales.getValue("help_prev"));
				pm.add("prevpage", page - 1);
			}
		}
		pm.add("page", page);
		pm.add("prefix", Locales.jsonpluginPrefix);
		pm.add("nojsonprefix", Locales.rawpluginPrefix);
		pm.add("maxpage", Help.maxpage);
		MessageUT.plmessage(p, Help.data.get(page - 1), pm);
		MessageUT.plmessage(p, Locales.getValue("help_decoration"), pm);
	}

	public static void send(final Player p, final String help) {
		if (Help.maxpage == 0) {
			return;
		}
		final List<String> m = new ArrayList<>();
		m.addAll(Help.header);
		m.add("<np>");
		for (final String mm : msgprocessed) {
			if (mm.contains(help)) {
				m.add(mm);
			}
		}
		m.add("<np>");
		m.addAll(Locales.getValue("unknown_command"));
		PlaceholderUT pm = new PlaceholderUT();
		pm.add("prefix", Locales.jsonpluginPrefix);
		pm.add("nojsonprefix", Locales.rawpluginPrefix);
		MessageUT.plmessage(p, m, pm);
	}
}
