/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.serialization.ConfigurationSerializable
 *  org.bukkit.configuration.serialization.ConfigurationSerialization
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.json.simple.JSONArray
 *  org.json.simple.JSONObject
 *  org.json.simple.JSONValue
 */
package me.onenrico.morevaultplus.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JsonSerializer {
	@Deprecated
	public static Map<String, Object> toMap(JSONObject object) {
		HashMap<String, Object> map = new HashMap<>();
		if (object == null) {
			return map;
		}
		for (Object key : object.keySet()) {
			map.put(key.toString(), JsonSerializer.fromJson(object.get(key)));
		}
		return map;
	}

	@Deprecated
	private static Object fromJson(Object json) {
		if (json == null) {
			return null;
		}
		if (json instanceof JSONObject) {
			return JsonSerializer.toMap((JSONObject) json);
		}
		if (json instanceof JSONArray) {
			return JsonSerializer.toList((JSONArray) json);
		}
		return json;
	}

	@Deprecated
	public static List<Object> toList(JSONArray array) {
		ArrayList<Object> list = new ArrayList<>();
		for (Object value : array) {
			list.add(JsonSerializer.fromJson(value));
		}
		return list;
	}

	@Deprecated
	public static List<String> toString(Inventory inv) {
		ArrayList<String> result = new ArrayList<>();
		ArrayList<ItemStack> items = new ArrayList<>();
		Collections.addAll(items, inv.getContents());
		for (ConfigurationSerializable cs : items) {
			if (cs == null) {
				result.add("null");
				continue;
			}
			result.add(new JSONObject(JsonSerializer.serialize(cs)).toString());
		}
		return result;
	}

	public static Inventory toInventory(List<String> stringItems) {
		Inventory inv = Bukkit.createInventory(null, 54);
		ArrayList<ItemStack> contents = new ArrayList<>();
		for (String piece : stringItems) {
			if (piece.equalsIgnoreCase("null")) {
				contents.add(null);
				continue;
			}
			ItemStack item = (ItemStack) JsonSerializer
					.deserialize(JsonSerializer.toMap((JSONObject) JSONValue.parse(piece)));
			contents.add(item);
		}
		ItemStack[] items = new ItemStack[contents.size()];
		int x = 0;
		while (x < contents.size()) {
			items[x] = contents.get(x);
			++x;
		}
		inv.setContents(items);
		return inv;
	}

	@Deprecated
	public static Map<String, Object> serialize(ConfigurationSerializable cs) {
		Map<String, Object> returnVal = JsonSerializer.handleSerialization(cs.serialize());
		returnVal.put("==", ConfigurationSerialization.getAlias(cs.getClass()));
		return returnVal;
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	private static Map<String, Object> handleSerialization(Map<String, Object> map) {
		Map<String, Object> serialized = JsonSerializer.recreateMap(map);
		for (Map.Entry<String, Object> entry : serialized.entrySet()) {
			if (entry.getValue() instanceof ConfigurationSerializable) {
				entry.setValue(JsonSerializer.serialize((ConfigurationSerializable) entry.getValue()));
				continue;
			}
			if (entry.getValue() instanceof Iterable) {
				List<Object> newList = new ArrayList<>();
				for (Object object : (Iterable<?>) entry.getValue()) {
					if (object instanceof ConfigurationSerializable) {
						object = JsonSerializer.serialize((ConfigurationSerializable) object);
					}
					newList.add(object);
				}
				entry.setValue(newList);
				continue;
			}
			if (!(entry.getValue() instanceof Map)) {
				continue;
			}
			entry.setValue(JsonSerializer.handleSerialization((Map<String, Object>) entry.getValue()));
		}
		return serialized;
	}

	@Deprecated
	public static Map<String, Object> recreateMap(Map<String, Object> original) {
		return new HashMap<>(original);
	}

	@SuppressWarnings("unchecked")
	public static Object deserialize(Map<String, Object> map) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() instanceof Map) {
				entry.setValue(JsonSerializer.deserialize((Map<String, Object>) entry.getValue()));
				continue;
			}
			if (entry.getValue() instanceof Iterable) {
				entry.setValue(JsonSerializer.convertIterable((Iterable<?>) entry.getValue()));
				continue;
			}
			if (!(entry.getValue() instanceof Number)) {
				continue;
			}
			entry.setValue(JsonSerializer.convertNumber((Number) entry.getValue()));
		}
		return map.containsKey("==") ? ConfigurationSerialization.deserializeObject(map) : map;
	}

	@SuppressWarnings("unchecked")
	private static List<?> convertIterable(Iterable<?> iterable) {
		ArrayList<Object> newList = new ArrayList<>();
		for (Object object : iterable) {
			if (object instanceof Map) {
				object = JsonSerializer.deserialize((Map<String, Object>) object);
			} else if (object instanceof List) {
				object = JsonSerializer.convertIterable((Iterable<?>) object);
			} else if (object instanceof Number) {
				object = JsonSerializer.convertNumber((Number) object);
			}
			newList.add(object);
		}
		return newList;
	}

	private static Number convertNumber(Number number) {
		Long longObj;
		if (number instanceof Long && (longObj = (Long) number) == longObj.intValue()) {
			return longObj.intValue();
		}
		return number;
	}
}
