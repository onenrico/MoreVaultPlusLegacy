//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReflectionUT {
	private static String OBC_PREFIX;
	private static String NMS_PREFIX;
	public static String VERSION;
	private static Pattern MATCH_VARIABLE;
	public static int NUMBER_VERSION;

	static {
		ReflectionUT.OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
		ReflectionUT.NMS_PREFIX = ReflectionUT.OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
		ReflectionUT.VERSION = ReflectionUT.OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
		ReflectionUT.MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");
	}

	public static Class<?> getNMSClass(final String nmsClassString) throws ClassNotFoundException {
		final String version = ReflectionUT.VERSION;
		final String name = "net.minecraft.server." + version + "." + nmsClassString;
		final Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}

	public static Object getConnection(final Player player) throws SecurityException, NoSuchMethodException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		final Method getHandle = player.getClass().getMethod("getHandle", new Class[0]);
		final Object nmsPlayer = getHandle.invoke(player, new Object[0]);
		final Field conField = nmsPlayer.getClass().getField("playerConnection");
		final Object con = conField.get(nmsPlayer);
		return con;
	}

	private static String expandVariables(final String name) {
		final StringBuffer output = new StringBuffer();
		final Matcher matcher = ReflectionUT.MATCH_VARIABLE.matcher(name);
		while (matcher.find()) {
			final String variable = matcher.group(1);
			String replacement;
			if ("nms".equalsIgnoreCase(variable)) {
				replacement = ReflectionUT.NMS_PREFIX;
			} else if ("obc".equalsIgnoreCase(variable)) {
				replacement = ReflectionUT.OBC_PREFIX;
			} else {
				if (!"version".equalsIgnoreCase(variable)) {
					throw new IllegalArgumentException("Unknown variable: " + variable);
				}
				replacement = ReflectionUT.VERSION;
			}
			if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.') {
				replacement = String.valueOf(replacement) + ".";
			}
			matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(output);
		return output.toString();
	}

	private static Class<?> getCanonicalClass(final String canonicalName) {
		try {
			return Class.forName(canonicalName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Cannot find " + canonicalName, e);
		}
	}

	public static Class<?> getClass(final String lookupName) {
		return getCanonicalClass(expandVariables(lookupName));
	}

	public static ConstructorInvoker getConstructor(final String className, final Class<?>... params) {
		return getConstructor(getClass(className), params);
	}

	public static ConstructorInvoker getConstructor(final Class<?> clazz, final Class<?>... params) {
		Constructor<?>[] declaredConstructors;
		for (int length = (declaredConstructors = clazz.getDeclaredConstructors()).length, i = 0; i < length; ++i) {
			final Constructor<?> constructor = declaredConstructors[i];
			if (Arrays.equals(constructor.getParameterTypes(), params)) {
				constructor.setAccessible(true);
				return new ConstructorInvoker() {
					@Override
					public Object invoke(final Object... arguments) {
						try {
							return constructor.newInstance(arguments);
						} catch (Exception e) {
							throw new RuntimeException("Cannot invoke constructor " + constructor, e);
						}
					}
				};
			}
		}
		throw new IllegalStateException(
				String.format("Unable to find constructor for %s (%s).", clazz, Arrays.asList(params)));
	}

	public static Class<?> getCraftBukkitClass(final String name) {
		return getCanonicalClass(String.valueOf(ReflectionUT.OBC_PREFIX) + "." + name);
	}

	public static <T> FieldAccessor<T> getField(final Class<?> target, final String name, final Class<T> fieldType) {
		return getField(target, name, fieldType, 0);
	}

	public static <T> FieldAccessor<T> getField(final String className, final String name, final Class<T> fieldType) {
		return getField(getClass(className), name, fieldType, 0);
	}

	public static <T> FieldAccessor<T> getField(final Class<?> target, final Class<T> fieldType, final int index) {
		return getField(target, null, fieldType, index);
	}

	public static <T> FieldAccessor<T> getField(final String className, final Class<T> fieldType, final int index) {
		return getField(getClass(className), fieldType, index);
	}

	@SuppressWarnings("unchecked")
	private static <T> FieldAccessor<T> getField(final Class<?> target, final String name, final Class<T> fieldType,
			int index) {
		Field[] declaredFields;
		for (int length = (declaredFields = target.getDeclaredFields()).length, i = 0; i < length; ++i) {
			final Field field = declaredFields[i];
			if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType())
					&& index-- <= 0) {
				field.setAccessible(true);
				return new FieldAccessor<T>() {
					@Override
					public T get(final Object target) {
						try {
							return (T) field.get(target);
						} catch (IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}

					@Override
					public void set(final Object target, final Object value) {
						try {
							field.set(target, value);
						} catch (IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}

					@Override
					public boolean hasField(final Object target) {
						return field.getDeclaringClass().isAssignableFrom(target.getClass());
					}
				};
			}
		}
		if (target.getSuperclass() != null) {
			return (FieldAccessor<T>) getField(target.getSuperclass(), name, (Class<Object>) fieldType, index);
		}
		throw new IllegalArgumentException("Cannot find field with type " + fieldType);
	}

	public static MethodInvoker getMethod(final String className, final String methodName, final Class<?>... params) {
		return getTypedMethod(getClass(className), methodName, null, params);
	}

	public static MethodInvoker getMethod(final Class<?> clazz, final String methodName, final Class<?>... params) {
		return getTypedMethod(clazz, methodName, null, params);
	}

	public static Method getMethodSimply(final Class<?> clazz, final String method) {
		Method[] methods;
		for (int length = (methods = clazz.getMethods()).length, i = 0; i < length; ++i) {
			final Method m = methods[i];
			if (m.getName().equals(method)) {
				return m;
			}
		}
		return null;
	}

	public static Class<?> getMinecraftClass(final String name) {
		return getCanonicalClass(String.valueOf(ReflectionUT.NMS_PREFIX) + "." + name);
	}

	public static MethodInvoker getTypedMethod(final Class<?> clazz, final String methodName, final Class<?> returnType,
			final Class<?>... params) {
		Method[] declaredMethods;
		for (int length = (declaredMethods = clazz.getDeclaredMethods()).length, i = 0; i < length; ++i) {
			final Method method = declaredMethods[i];
			if (((methodName == null || method.getName().equals(methodName)) && returnType == null)
					|| (method.getReturnType().equals(returnType)
							&& Arrays.equals(method.getParameterTypes(), params))) {
				method.setAccessible(true);
				return new MethodInvoker() {
					@Override
					public Object invoke(final Object target, final Object... arguments) {
						try {
							return method.invoke(target, arguments);
						} catch (Exception e) {
							throw new RuntimeException("Cannot invoke method " + method, e);
						}
					}
				};
			}
		}
		if (clazz.getSuperclass() != null) {
			return getMethod(clazz.getSuperclass(), methodName, params);
		}
		throw new IllegalStateException(
				String.format("Unable to find method %s (%s).", methodName, Arrays.asList(params)));
	}

	@SuppressWarnings("unchecked")
	public static Class<Object> getUntypedClass(final String lookupName) {
		final Class<Object> clazz = (Class<Object>) getClass(lookupName);
		return clazz;
	}

	public static <T> T newInstance(final Class<T> type) {
		try {
			return type.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public interface ConstructorInvoker {
		Object invoke(final Object... p0);
	}

	public interface FieldAccessor<T> {
		T get(final Object p0);

		void set(final Object p0, final Object p1);

		boolean hasField(final Object p0);
	}

	public interface MethodInvoker {
		Object invoke(final Object p0, final Object... p1);
	}
}
