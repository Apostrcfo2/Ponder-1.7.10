package net.createmod.metanip.data;
// Pure Java - no changes needed for 1.7.10

import java.util.function.Function;

public class FunctionalHelper {

	public static <U> Function<Object, U> filterAndCast(Class<? extends U> clazz) {
		return t -> clazz.isInstance(t) ? clazz.cast(t) : null;
	}

}
