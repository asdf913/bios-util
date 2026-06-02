package org.apache.commons.lang3;

import java.io.IOException;
import java.io.PrintStream;

import io.github.toolfactory.narcissus.Narcissus;

public class BiosUtilMain {

	public static void main(final String[] args) throws IOException, NoSuchFieldException {
		//
		println(cast(PrintStream.class, Narcissus.getStaticField(System.class.getDeclaredField("out"))),
				BiosUtil.getVendor());
		//
	}

	private static void println(final PrintStream instance, final String string) {
		if (instance != null) {
			instance.print(string);
		}
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

}