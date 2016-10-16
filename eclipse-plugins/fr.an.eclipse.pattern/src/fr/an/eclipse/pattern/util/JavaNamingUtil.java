package fr.an.eclipse.pattern.util;

public class JavaNamingUtil {

	public static String capitalize(String name) {
		if (name == null || name.isEmpty()) {
			return name;
		}
		return name.substring(0, 1).toUpperCase() + (name.length() > 1? name.substring(1) : "");
	}
}
