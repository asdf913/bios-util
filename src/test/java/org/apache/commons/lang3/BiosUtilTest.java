package org.apache.commons.lang3;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.reflect.Reflection;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;

import io.github.toolfactory.narcissus.Narcissus;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

class BiosUtilTest {

	private static Method METHOD_GET_NAME, METHOD_GET_CLASS, METHOD_HEX_TO_BYTES, METHOD_GROUP, METHOD_GROUP_COUNT,
			METHOD_MATCHER, METHOD_MATCHES, METHOD_AND = null;

	@BeforeSuite
	static void beforeSuite() throws NoSuchMethodException {
		//
		final Class<?> clz = BiosUtil.class;
		//
		(METHOD_GET_NAME = clz.getDeclaredMethod("getName", Member.class)).setAccessible(true);
		//
		(METHOD_GET_CLASS = clz.getDeclaredMethod("getClass", Object.class)).setAccessible(true);
		//
		(METHOD_HEX_TO_BYTES = clz.getDeclaredMethod("hexToBytes", String.class)).setAccessible(true);
		//
		(METHOD_GROUP = clz.getDeclaredMethod("group", Matcher.class, Integer.TYPE)).setAccessible(true);
		//
		(METHOD_GROUP_COUNT = clz.getDeclaredMethod("groupCount", Matcher.class)).setAccessible(true);
		//
		(METHOD_MATCHER = clz.getDeclaredMethod("matcher", Pattern.class, CharSequence.class)).setAccessible(true);
		//
		(METHOD_MATCHES = clz.getDeclaredMethod("matches", Matcher.class)).setAccessible(true);
		//
		(METHOD_AND = clz.getDeclaredMethod("and", Object.class, Predicate.class, Predicate.class)).setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private Boolean test;

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			//
			final String name = getName(method);
			//
			if (proxy instanceof Member && Objects.equals(name, "getName")) {
				//
				return null;
				//
			} // if
				//
			if (proxy instanceof Predicate && Objects.equals(name, "test")) {
				//
				return test;
				//
			} else if (proxy instanceof Function && Objects.equals(name, "apply")) {
				//
				return null;
				//
			} else if (proxy instanceof Collection && Objects.equals(name, "stream")) {
				//
				return null;
				//
			} else if (proxy instanceof Stream) {
				//
				if (Objects.equals(name, "toList")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "filter")) {
					//
					return proxy;
					//
				} // if
					//
			} // if
				//
			throw new Throwable(name);
			//
		}

	}

	private static class MH implements MethodHandler {

		@Override
		public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args)
				throws Throwable {
			//
			if (Objects.equals(getReturnType(thisMethod), Void.TYPE)) {
				//
				return null;
				//
			} // if
				//
			throw new Throwable(getName(thisMethod));
			//
		}

	}

	private static Class<?> getReturnType(final Method instance) {
		return instance != null ? instance.getReturnType() : null;
	}

	@Test
	void testNull() throws Throwable {
		//
		final Method[] ms = BiosUtil.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Object result = null;
		//
		String toString = null;
		// s
		Class<?>[] parameterTypes = null;
		//
		Collection<Object> collection = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else {
					//
					add(collection, null);
					//
				} // if
					//
			} // for
				//
			toString = Objects.toString(m);
			//
			result = Narcissus.invokeStaticMethod(m, toArray(collection));
			//
			if (contains(Arrays.asList(Boolean.TYPE, Integer.TYPE), getReturnType(m)) || Boolean.logicalAnd(
					Objects.equals(getName(m), "getVendor"), Arrays.equals(parameterTypes, new Class<?>[] {}))) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	private static boolean contains(final Collection<?> instance, final Object item) {
		return instance != null && instance.contains(item);
	}

	@Test
	void testNotNull() throws Throwable {
		//
		final Method[] ms = BiosUtil.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		Object result = null;
		//
		String name, toString = null;
		//
		Collection<Object> collection = null;
		//
		final IH ih = new IH();
		//
		ih.test = Boolean.FALSE;
		//
		ProxyFactory proxyFactory = null;
		//
		Object object = null;
		//
		MH mh = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Class.class)) {
					//
					add(collection, Object.class);
					//
				} else if (Objects.equals(parameterType, Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (parameterType != null && parameterType.isInterface()) {
					//
					add(collection, Reflection.newProxy(parameterType, ih));
					//
				} else if (parameterType != null && Modifier.isAbstract(parameterType.getModifiers())) {
					//
					(proxyFactory = new ProxyFactory()).setSuperclass(parameterType);
					//
					object = null;
					//
					if (Objects.equals(parameterType, WmiResult.class)) {
						//
						object = newInstance(getDeclaredConstructor(proxyFactory.createClass(), Class.class));
						//
					} else if (Objects.equals(parameterType, Enum.class)) {
						//
						object = newInstance(
								getDeclaredConstructor(proxyFactory.createClass(), String.class, Integer.TYPE), null,
								Integer.valueOf(0));
						//
					} // if
						//
					if (object instanceof ProxyObject) {
						//
						((ProxyObject) object).setHandler(mh = ObjectUtils.getIfNull(mh, MH::new));
						//
					} // if
						//
					add(collection, object);
					//
				} else {
					//
					add(collection, Narcissus.allocateInstance(parameterType));
					//
				} // if
					//
			} // for
				//
			toString = Objects.toString(m);
			//
			result = Narcissus.invokeStaticMethod(m, toArray(collection));
			//
			if (contains(Arrays.asList(Boolean.TYPE, Integer.TYPE), getReturnType(m))
					|| Boolean.logicalAnd(Objects.equals(name = getName(m), "getVendor"),
							Arrays.equals(parameterTypes, new Class<?>[] {}))
					|| Boolean.logicalAnd(Objects.equals(name, "getName"),
							Arrays.equals(parameterTypes, new Class<?>[] { Class.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getClass"),
							Arrays.equals(parameterTypes, new Class<?>[] { Object.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "filter"),
							Arrays.equals(parameterTypes, new Class<?>[] { Stream.class, Predicate.class }))) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	private static Object invoke(final Method method, final Object instance, final Object... args)
			throws IllegalAccessException, InvocationTargetException {
		return method != null && method.getDeclaringClass() != null ? method.invoke(instance, args) : null;
	}

	private static <T> T newInstance(final Constructor<T> instance, final Object... args)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return instance != null ? instance.newInstance(args) : null;
	}

	private static <T> Constructor<T> getDeclaredConstructor(final Class<T> instance, final Class<?>... parameterTypes)
			throws NoSuchMethodException {
		return instance != null ? instance.getDeclaredConstructor(parameterTypes) : null;
	}

	private static String getName(final Member instance) throws Throwable {
		try {
			Object obj = invoke(METHOD_GET_NAME, null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(getName(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static String getName(final Class<?> instance) throws Throwable {
		try {
			Object obj = invoke(METHOD_GET_NAME, null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(getName(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static Class<?> getClass(final Object instance) throws Throwable {
		try {
			Object obj = invoke(METHOD_GET_CLASS, null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (Class<?>) obj;
			}
			throw new Throwable(getName(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static <E> void add(final Collection<E> instance, final E item) {
		if (instance != null) {
			instance.add(item);
		}
	}

	private static void clear(final Collection<?> instance) {
		if (instance != null) {
			instance.clear();
		}
	}

	private static Object[] toArray(final Collection<?> instance) {
		return instance != null ? instance.toArray() : null;
	}

	@Test
	public void testHexToString() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNotNull(invoke(METHOD_HEX_TO_BYTES, null, "4100700070006c0065000000"));
		//
	}

	@Test
	public void testGroup() throws IllegalAccessException, InvocationTargetException {
		//
		final Pattern pattern = Pattern.compile("\\d+");
		//
		final String string = "123";
		//
		final Object matcher = invoke(METHOD_MATCHER, null, pattern, string);
		//
		Assert.assertNotNull(matcher);
		//
		Assert.assertEquals(invoke(METHOD_MATCHES, null, matcher), Boolean.TRUE);
		//
		Assert.assertEquals(invoke(METHOD_GROUP_COUNT, null, matcher), Integer.valueOf(0));
		//
		Assert.assertEquals(invoke(METHOD_GROUP, null, matcher, Integer.valueOf(0)), string);
		//
	}

	@Test
	public void testAnd() throws IllegalAccessException, InvocationTargetException {
		//
		final Predicate<?> alwaysTrue = Predicates.alwaysTrue();
		//
		Assert.assertEquals(invoke(METHOD_AND, null, null, alwaysTrue, alwaysTrue), Boolean.TRUE);
		//
		Assert.assertEquals(invoke(METHOD_AND, null, null, alwaysTrue, Predicates.alwaysFalse()), Boolean.FALSE);
		//
	}

}