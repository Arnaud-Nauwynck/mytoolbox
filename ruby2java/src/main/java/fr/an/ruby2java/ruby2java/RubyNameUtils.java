package fr.an.ruby2java.ruby2java;

public class RubyNameUtils {

	public static String safe_name(String name) {
		String res = "";
		if (name.equals("new")) res = "$new";
		else if (name.equals("class")) res = "$class";
		else {
			res = name;
			res = res.replaceAll("+", "$plus");
			res = res.replaceAll("-", "$minus");
			res = res.replaceAll("*", "$times");
			res = res.replaceAll("/", "$div");
			res = res.replaceAll("<", "$less");
			res = res.replaceAll(">", "$greater");
			res = res.replaceAll("=", "$equal");
			res = res.replaceAll("&", "$tilde");
			res = res.replaceAll("!", "$bang");
			res = res.replaceAll("%", "$percent");
			res = res.replaceAll("^", "$up");
			res = res.replaceAll("?", "$qmark");
			res = res.replaceAll("|", "$bar");
			res = res.replaceAll("[", "$lbrack");
			res = res.replaceAll("]", "$rbrack");
		}
		return res;
	}


	public static String proper_class(String name) {
		if (name.equals("String")) return "RString";
		if (name.equals("Array")) return "RArray";
		if (name.equals("Fixnum")) return "RFixnum";
		if (name.equals("Boolean")) return "RBoolean";
		if (name.equals("Float")) return "RFloat";
		if (name.equals("Time")) return "RTime";
		if (name.equals("Object")) return "RObject";
		return name;
	}

	
}
