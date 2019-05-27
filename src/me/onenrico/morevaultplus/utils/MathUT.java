//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import me.onenrico.morevaultplus.main.ConfigManager;

public class MathUT {
	private static final NavigableMap<Long, String> suffixes;
	private static int[] bases;
	private static HashMap<Integer, String> map;
	private static Boolean setup;

	static {
		(suffixes = new TreeMap<>()).put(1000L, "k");
		MathUT.suffixes.put(1000000L, "M");
		MathUT.suffixes.put(1000000000L, "B");
		MathUT.suffixes.put(1000000000000L, "T");
		MathUT.suffixes.put(1000000000000000L, "P");
		MathUT.suffixes.put(1000000000000000000L, "E");
		MathUT.bases = new int[] { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
		MathUT.map = new HashMap<>();
		MathUT.setup = false;
	}

	public static double getPersentase(final double value, final double maxvalue) {
		final double persentase = 100.0 / maxvalue * value;
		return persentase;
	}

	public static double getRealvalue(final double value, final double persentase) {
		final double realvalue = value * persentase / 100.0;
		return realvalue;
	}

	public static final Block getTarget(final Player player, final Integer range) {
		final BlockIterator bi = new BlockIterator(player, range);
		Block lastBlock = bi.next();
		while (bi.hasNext()) {
			lastBlock = bi.next();
			if (lastBlock.getType() == Material.AIR) {
				continue;
			}
			break;
		}
		return lastBlock;
	}

	public static String format(final double value) {
		if (!ConfigManager.minimalformat) {
			final DecimalFormat df = new DecimalFormat("###,###.##", new DecimalFormatSymbols(Locale.US));
			return df.format(value);
		}
		final long lvalue = (long) value;
		if (lvalue == Long.MIN_VALUE) {
			return format(-9.223372036854776E18);
		}
		if (lvalue < 0L) {
			return "-" + format(-lvalue);
		}
		if (lvalue < 1000L) {
			return Long.toString(lvalue);
		}
		final Map.Entry<Long, String> e = MathUT.suffixes.floorEntry(lvalue);
		final Long divideBy = e.getKey();
		final String suffix = e.getValue();
		final long truncated = lvalue / (divideBy / 10L);
		final boolean hasDecimal = truncated < 100L && truncated / 10.0 != truncated / 10L;
		return hasDecimal ? (String.valueOf(truncated / 10.0) + suffix) : (String.valueOf(truncated / 10L) + suffix);
	}

	public static int strInt(String str) {
		if (str == null || str.length() == 0 || str.isEmpty()) {
			return 0;
		}
		str = str.trim();
		double result = 0.0;
		int flag = 0;
		int i = 0;
		if (str.charAt(0) == '-') {
			flag = 1;
			++i;
		} else if (str.charAt(0) == '+') {
			++i;
		}
		while (i < str.length() && str.charAt(i) >= '0' && str.charAt(i) <= '9') {
			result = result * 10.0 + (str.charAt(i) - '0');
			++i;
		}
		if (flag == 1) {
			result = -result;
		}
		result = clamp((long) result, Long.MIN_VALUE, Long.MAX_VALUE);
		return (int) result;
	}

	public static long strLong(String str) {
		if (str == null || str.length() == 0 || str.isEmpty()) {
			return 0L;
		}
		str = str.trim();
		double result = 0.0;
		int flag = 0;
		int i = 0;
		if (str.charAt(0) == '-') {
			flag = 1;
			++i;
		} else if (str.charAt(0) == '+') {
			++i;
		}
		while (i < str.length() && str.charAt(i) >= '0' && str.charAt(i) <= '9') {
			result = result * 10.0 + (str.charAt(i) - '0');
			++i;
		}
		if (flag == 1) {
			result = -result;
		}
		result = clamp((long) result, Long.MIN_VALUE, Long.MAX_VALUE);
		return (long) result;
	}

	public static long clamp(final long value, final long min, final long max) {
		if (value > max) {
			return max;
		}
		if (value < min) {
			return min;
		}
		return value;
	}

	public static long clamp(final long value, final long min) {
		return clamp(value, min, 2147483647L);
	}

	public static long clamp(final long value, final float max) {
		final long maxx = (long) max;
		return clamp(value, -2147483648L, maxx);
	}

	private static int c2n(final char c) {
		switch (c) {
		case 'I': {
			return 1;
		}
		case 'V': {
			return 5;
		}
		case 'X': {
			return 10;
		}
		case 'L': {
			return 50;
		}
		case 'C': {
			return 100;
		}
		case 'D': {
			return 500;
		}
		case 'M': {
			return 1000;
		}
		default: {
			return 0;
		}
		}
	}

	public static int romanToInt(final String a) {
		int result = 0;
		for (int i = 0; i < a.length(); ++i) {
			if (i > 0 && c2n(a.charAt(i)) > c2n(a.charAt(i - 1))) {
				result += c2n(a.charAt(i)) - 2 * c2n(a.charAt(i - 1));
			} else {
				result += c2n(a.charAt(i));
			}
		}
		return result;
	}

	public static String intToRoman(int a) {
		if (!MathUT.setup) {
			setup();
			MathUT.setup = true;
		}
		String result = "";
		int[] bases;
		for (int length = (bases = MathUT.bases).length, j = 0; j < length; ++j) {
			for (int i = bases[j]; a >= i; a -= i) {
				result = String.valueOf(result) + MathUT.map.get(i);
			}
		}
		return result;
	}

	private static void setup() {
		MathUT.map.put(1, "I");
		MathUT.map.put(4, "IV");
		MathUT.map.put(5, "V");
		MathUT.map.put(9, "IX");
		MathUT.map.put(10, "X");
		MathUT.map.put(40, "XL");
		MathUT.map.put(50, "L");
		MathUT.map.put(90, "XC");
		MathUT.map.put(100, "C");
		MathUT.map.put(400, "CD");
		MathUT.map.put(500, "D");
		MathUT.map.put(900, "CM");
		MathUT.map.put(1000, "M");
	}

	public static double safe(final String str) {
		if (str == null || str.isEmpty()) {
			return 0.0;
		}
		try {
			final double d = Double.parseDouble(str);
			return d;
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}
}
