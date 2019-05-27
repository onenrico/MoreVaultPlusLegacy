//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Stack;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NBTReflectionUtil {
	public static Object getNMSEntity(final Entity entity) {
		try {
			return ReflectionMethod.CRAFT_ENTITY_GET_HANDLE.run(ClassWrapper.CRAFT_ENTITY.getClazz().cast(entity),
					new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object readNBTFile(final FileInputStream stream) {
		try {
			return ReflectionMethod.NBTFILE_READ.run(null, stream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object saveNBTFile(final Object nbt, final FileOutputStream stream) {
		try {
			return ReflectionMethod.NBTFILE_WRITE.run(null, nbt, stream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getItemRootNBTTagCompound(final Object nmsitem) {
		final Class clazz = nmsitem.getClass();
		try {
			final Method method = clazz.getMethod("getTag", new Class[0]);
			final Object answer = method.invoke(nmsitem, new Object[0]);
			return answer;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object convertNBTCompoundtoNMSItem(final NBTCompound nbtcompound) {
		final Class clazz = ClassWrapper.NMS_ITEMSTACK.getClazz();
		try {
			final Object nmsstack = clazz.getConstructor(ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz())
					.newInstance(nbtcompound.getCompound());
			return nmsstack;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static NBTContainer convertNMSItemtoNBTCompound(final Object nmsitem) {
		final Class clazz = nmsitem.getClass();
		try {
			final Method method = clazz.getMethod("save", ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz());
			final Object answer = method.invoke(nmsitem, ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]));
			return new NBTContainer(answer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getEntityNBTTagCompound(final Object NMSEntity) {
		try {
			final Object nbt = ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz().newInstance();
			Object answer = ReflectionMethod.NMS_ENTITY_GET_NBT.run(NMSEntity, nbt);
			if (answer == null) {
				answer = nbt;
			}
			return answer;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object setEntityNBTTag(final Object NBTTag, final Object NMSEntity) {
		try {
			ReflectionMethod.NMS_ENTITY_SET_NBT.run(NMSEntity, NBTTag);
			return NMSEntity;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Object getTileEntityNBTTagCompound(final BlockState tile) {
		try {
			final Object pos = ObjectCreator.NMS_BLOCKPOSITION.getInstance(tile.getX(), tile.getY(), tile.getZ());
			final Object cworld = ClassWrapper.CRAFT_WORLD.getClazz().cast(tile.getWorld());
			final Object nmsworld = ReflectionMethod.CRAFT_WORLD_GET_HANDLE.run(cworld, new Object[0]);
			final Object o = ReflectionMethod.NMS_WORLD_GET_TILEENTITY.run(nmsworld, pos);
			final Object tag = ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz().newInstance();
			Object answer = ReflectionMethod.TILEENTITY_GET_NBT.run(o, tag);
			if (answer == null) {
				answer = tag;
			}
			return answer;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setTileEntityNBTTagCompound(final BlockState tile, final Object comp) {
		try {
			final Object pos = ObjectCreator.NMS_BLOCKPOSITION.getInstance(tile.getX(), tile.getY(), tile.getZ());
			final Object cworld = ClassWrapper.CRAFT_WORLD.getClazz().cast(tile.getWorld());
			final Object nmsworld = ReflectionMethod.CRAFT_WORLD_GET_HANDLE.run(cworld, new Object[0]);
			final Object o = ReflectionMethod.NMS_WORLD_GET_TILEENTITY.run(nmsworld, pos);
			ReflectionMethod.TILEENTITY_SET_NBT.run(o, comp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object getSubNBTTagCompound(final Object compound, final String name) {
		final Class c = compound.getClass();
		try {
			final Method method = c.getMethod("getCompound", String.class);
			final Object answer = method.invoke(compound, name);
			return answer;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void addNBTTagCompound(final NBTCompound comp, final String name) {
		if (name == null) {
			remove(comp, name);
			return;
		}
		Object nbttag = comp.getCompound();
		if (nbttag == null) {
			nbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return;
		}
		final Object workingtag = gettoCompount(nbttag, comp);
		try {
			final Method method = workingtag.getClass().getMethod("set", String.class,
					ClassWrapper.NMS_NBTBASE.getClazz());
			method.invoke(workingtag, name, ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz().newInstance());
			comp.setCompound(nbttag);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Boolean valideCompound(final NBTCompound comp) {
		Object root = comp.getCompound();
		if (root == null) {
			root = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (gettoCompount(root, comp) != null) {
			return true;
		}
		return false;
	}

	static Object gettoCompount(Object nbttag, NBTCompound comp) {
		final Stack<String> structure = new Stack<>();
		while (comp.getParent() != null) {
			structure.add(comp.getName());
			comp = comp.getParent();
		}
		while (!structure.isEmpty()) {
			nbttag = getSubNBTTagCompound(nbttag, structure.pop());
			if (nbttag == null) {
				return null;
			}
		}
		return nbttag;
	}

	public static void addOtherNBTCompound(final NBTCompound comp, final NBTCompound nbtcompound) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		try {
			ReflectionMethod.COMPOUND_ADD.run(workingtag, nbtcompound.getCompound());
			comp.setCompound(rootnbttag);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getContent(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return null;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		try {
			final Method method = workingtag.getClass().getMethod("get", String.class);
			return method.invoke(workingtag, key).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static void set(final NBTCompound comp, final String key, final Object val) {
		if (val == null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			new Throwable("InvalideCompound").printStackTrace();
			return;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		try {
			final Method method = workingtag.getClass().getMethod("set", String.class,
					ClassWrapper.NMS_NBTBASE.getClazz());
			method.invoke(workingtag, key, val);
			comp.setCompound(rootnbttag);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static NBTList getList(final NBTCompound comp, final String key, final NBTType type) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return null;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		try {
			final Method method = workingtag.getClass().getMethod("getList", String.class, Integer.TYPE);
			return new NBTList(comp, key, type, method.invoke(workingtag, key, type.getId()));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static void setObject(final NBTCompound comp, final String key, final Object value) {
		if (!MinecraftVersion.hasGsonSupport()) {
			return;
		}
		try {
			final String json = GsonWrapper.getString(value);
			setData(comp, ReflectionMethod.COMPOUND_SET_STRING, key, json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static <T> T getObject(final NBTCompound comp, final String key, final Class<T> type) {
		if (!MinecraftVersion.hasGsonSupport()) {
			return null;
		}
		final String json = (String) getData(comp, ReflectionMethod.COMPOUND_GET_STRING, key);
		if (json == null) {
			return null;
		}
		return GsonWrapper.deserializeJson(json, type);
	}

	public static void remove(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		ReflectionMethod.COMPOUND_REMOVE_KEY.run(workingtag, key);
		comp.setCompound(rootnbttag);
	}

	public static Set<String> getKeys(final NBTCompound comp) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return null;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		return (Set<String>) ReflectionMethod.COMPOUND_GET_KEYS.run(workingtag, new Object[0]);
	}

	public static void setData(final NBTCompound comp, final ReflectionMethod type, final String key,
			final Object data) {
		if (data == null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		type.run(workingtag, key, data);
		comp.setCompound(rootnbttag);
	}

	public static Object getData(final NBTCompound comp, final ReflectionMethod type, final String key) {
		final Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			return null;
		}
		if (!valideCompound(comp)) {
			return null;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		return type.run(workingtag, key);
	}
}
