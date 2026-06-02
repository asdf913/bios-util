package org.apache.commons.lang3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;

import io.github.toolfactory.narcissus.Narcissus;

public class BiosUtil {

	private BiosUtil() {
		//
	}

	private static enum BiosProperty {
		Manufacturer
	}

	public static String getVendor() throws IOException {
		//
		final String name = getName(getClass(FileSystems.getDefault()));
		//
		if (Objects.equals(name, "sun.nio.fs.LinuxFileSystem")) {
			//
			final File file = new File("/sys/devices/virtual/dmi/id/bios_vendor");
			//
			if (!file.exists() || !file.isFile() || !file.canRead()) {
				//
				return null;
				//
			} // if
				//
			return StringUtils.trim(stream(Files.readAllLines(file.toPath())).collect(Collectors.joining()));
			//
		} else if (Objects.equals(name, "sun.nio.fs.WindowsFileSystem")) {
			//
			final Ole32 ole32 = Ole32.INSTANCE;
			//
			if (ole32 != null) {
				//
				ole32.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
				//
			} // if
				//
			String manufacturer = null;
			//
			try {
				//
				final WmiResult<BiosProperty> result = new WmiQuery<>("Win32_ComputerSystem", BiosProperty.class)
						.execute();
				//
				for (int i = 0; i < getResultCount(result); i++) {
					//
					if (manufacturer != null) {
						//
						throw new IllegalStateException();
						//
					} // if
						//
					manufacturer = Objects.toString(getValue(result, BiosProperty.Manufacturer, 0));
					//
				} // for
					//
				return manufacturer;
				//
			} finally {
				//
				if (ole32 != null) {
					//
					ole32.CoUninitialize();
					//
				} // if
					//
			} // try
				//
		} else if (Objects.equals(name, "sun.nio.fs.MacOSXFileSystem")) {
			//
			String firmwareVendor = null;
			//
			try (final InputStream is = getInputStream(
					new ProcessBuilder("ioreg", "-l", "-p", "IODeviceTree").start())) {
				//
				final Collection<String> lines = is != null ? IOUtils.readLines(is, StandardCharsets.UTF_8) : null;
				//
				if (lines != null) {
					//
					Pattern pattern = null;
					//
					Matcher matcher = null;
					//
					for (final String line : lines) {
						//
						if (matches(
								matcher = matcher(
										pattern = ObjectUtils.getIfNull(pattern,
												() -> Pattern.compile(
														"^.+\\\"firmware\\-vendor\"\\s+\\=\\s+\\<([0-9a-f]+)\\>$")),
										line))
								&& groupCount(matcher) > 0) {
							//
							if (firmwareVendor != null) {
								//
								throw new IllegalStateException();
								//
							} // if
								//
							firmwareVendor = new String(hexToBytes(group(matcher, 1)));
							//
						} // if
							//
					} // for
						//
				} // if
					//
				return StringUtils.trim(firmwareVendor);
				//
			} // try
				//
		} // if
			//
		return null;
		//
	}

	private static String group(final Matcher instance, final int index) {
		//
		if (instance == null) {
			//
			return null;
			//
		} // if
			//
		final Field parentPattern = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(stream(FieldUtils.getAllFieldsList(getClass(instance))),
						f -> Objects.equals(getName(f), "parentPattern"))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (parentPattern == null || Narcissus.getField(instance, parentPattern) == null) {
			//
			return null;
			//
		} // if
			//
		return instance.group(index);
		//
	}

	private static int groupCount(final Matcher instance) {
		//
		if (instance == null) {
			//
			return 0;
			//
		} // if
			//
		final Field parentPattern = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(stream(FieldUtils.getAllFieldsList(getClass(instance))),
						f -> Objects.equals(getName(f), "parentPattern"))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (parentPattern == null || Narcissus.getField(instance, parentPattern) == null) {
			//
			return 0;
			//
		} // if
			//
		return instance.groupCount();
		//
	}

	private static boolean matches(final Matcher instance) {
		//
		if (instance == null) {
			//
			return false;
			//
		} // if
			//
		final Field groups = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(stream(FieldUtils.getAllFieldsList(getClass(instance))),
						f -> Objects.equals(getName(f), "groups"))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (groups == null || Narcissus.getField(instance, groups) == null) {
			//
			return false;
			//
		} // if
			//
		return instance.matches();
		//
	}

	private static Matcher matcher(final Pattern instance, final CharSequence cs) {
		//
		if (instance == null) {
			//
			return null;
			//
		} // if
			//
		final Field normalizedPattern = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(stream(FieldUtils.getAllFieldsList(getClass(instance))),
						f -> Objects.equals(getName(f), "normalizedPattern"))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (normalizedPattern == null || Narcissus.getField(instance, normalizedPattern) == null) {
			//
			return null;
			//
		} // if
			//
		return instance.matcher(cs);
		//
	}

	private static InputStream getInputStream(final Process instance) {
		return instance != null ? instance.getInputStream() : null;
	}

	private static <T extends Enum<T>> Object getValue(final WmiResult<T> instance, final T property, final int index) {
		//
		if (instance == null) {
			//
			return null;
			//
		} // if
			//
		final Field propertyMap = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(stream(FieldUtils.getAllFieldsList(getClass(instance))),
						f -> Objects.equals(getName(f), "value"))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (propertyMap == null || Narcissus.getField(instance, propertyMap) == null) {
			//
			return null;
			//
		} // if
			//
		return instance.getValue(property, index);
		//
	}

	private static int getResultCount(final WmiResult<?> instance) {
		return instance != null ? instance.getResultCount() : 0;
	}

	private static <T> Stream<T> stream(final Collection<T> instance) {
		return instance != null ? instance.stream() : null;
	}

	private static byte[] hexToBytes(final String hexString) {
		//
		if (hexString == null) {
			//
			return null;
			//
		} // if
			//
		final Field value = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(stream(FieldUtils.getAllFieldsList(getClass(hexString))),
						f -> Objects.equals(getName(f), "value"))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (value == null || Narcissus.getField(hexString, value) == null) {
			//
			return null;
			//
		} // if
			//
		final int len = StringUtils.length(hexString);
		//
		final byte[] data = new byte[len / 2];
		//
		for (int i = 0; i < len; i += 2) {
			//
			data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
					+ Character.digit(hexString.charAt(i + 1), 16));
			//
		} // for
			//
		return data;
		//
	}

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static <T, R> R testAndApply(final Predicate<T> predicate, final T value, final Function<T, R> functionTrue,
			final Function<T, R> functionFalse) {
		return test(predicate, value) ? apply(functionTrue, value) : apply(functionFalse, value);
	}

	private static <T> boolean test(final Predicate<T> instance, final T value) {
		return instance != null && instance.test(value);
	}

	private static <T, R> R apply(final Function<T, R> instance, final T value) {
		return instance != null ? instance.apply(value) : null;
	}

	private static <T> List<T> toList(final Stream<T> instance) {
		return instance != null ? instance.toList() : null;
	}

	private static <T> Stream<T> filter(final Stream<T> instance, final Predicate<? super T> predicate) {
		return instance != null ? instance.filter(predicate) : instance;
	}

	private static String getName(final Class<?> instance) {
		return instance != null ? instance.getName() : null;
	}

	private static Class<?> getClass(final Object instance) {
		return instance != null ? instance.getClass() : null;
	}

}