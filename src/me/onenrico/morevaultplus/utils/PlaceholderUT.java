//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.onenrico.morevaultplus.callable.PlaceholderCall;

public class PlaceholderUT extends StrSubstitutor {
	private HashMap<String, Object> acuan;

	@SuppressWarnings("unchecked")
	public PlaceholderUT(final HashMap<String, Object> acuan) {
		this.acuan = (HashMap<String, Object>) acuan.clone();
	}

	public PlaceholderUT() {
		acuan = new HashMap<>();
	}

	public HashMap<String, Object> getAcuan() {
		return acuan;
	}

	public void setAcuan(final HashMap<String, Object> acuan) {
		this.acuan = acuan;
	}

	public void remove(final String data) {
		if (acuan.containsKey(data)) {
			acuan.remove(data);
		}
	}

	public void add(final String placeholder, final Object data) {
		acuan.put(placeholder, data);
	}

	public Object get(final String placeholder) {
		return acuan.getOrDefault(placeholder, "");
	}

	public List<String> t(final List<String> data) {
		final List<String> result = new ArrayList<>();
		if (data == null) {
			return null;
		}
		for (final String b : data) {
			final String tb = this.t(b);
			String[] split;
			for (int length = (split = tb.split("<nl>")).length, i = 0; i < length; ++i) {
				final String tbb = split[i];
				result.add(tbb);
			}
		}
		return result;
	}

	public String t(final String data) {
		return this.t(null, data);
	}

	@SuppressWarnings("unchecked")
	public String t(final Player player, String data) {
		if (data == null) {
			return null;
		}
		final String rawdata = data;
		for (final String a : acuan.keySet()) {
			if (data.contains("{" + a + "}")) {
				Object geta = acuan.get(a);
				if (geta instanceof PlaceholderCall) {
					try {
						final PlaceholderCall pc = (PlaceholderCall) geta;
						data = data.replace("{" + a + "}",
								new StringBuilder().append(pc.callFrom(String.valueOf(a) + rawdata)).toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (geta instanceof Callable<?>) {
					try {
						data = data.replace("{" + a + "}", ((Callable<String>) geta).call());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
//					data = newpu.replace(data);
//					for (final String b : acuan.keySet()) {
//						if (geta.toString().contains("{" + b + "}")) {
//							geta = geta.toString().replace("{" + b + "}",
//									acuan.get(b).toString());
//						}
//					}
					StrSubstitutor newpu = new StrSubstitutor(acuan, "{", "}");
					geta = newpu.replace(geta);
					if (geta.toString().contains("<bar>")) {
						geta = LoadingbarUT.getBarFromString(geta.toString());
					}
					data = data.replace("{" + a + "}", geta.toString());
				}
			}
		}
		return StringUT.t(data);
	}

	public ItemStack t(final ItemStack item) {
		ItemUT.changeDisplayName(item, this.t(ItemUT.getName(item)));
		ItemUT.changeLore(item, this.t(ItemUT.getLore(item)));
		return item;
	}
}
