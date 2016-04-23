package fr.an.ruby2java.ruby2java;

import java.util.HashMap;
import java.util.Map;

public class CountMap {

	public static class Count {
		int value;

		@Override
		public String toString() {
			return Integer.toString(value);
		}
		
	}
	
	private Map<String,Count> countsMap = new HashMap<String,Count>();

	// ------------------------------------------------------------------------

	public CountMap() {
	}

	// ------------------------------------------------------------------------

	public Count getCount(String key) {
		Count res = countsMap.get(key);
		if (res == null) {
			res = new Count();
			countsMap.put(key,  res);
		}
		return res;
	}

	@Override
	public String toString() {
		return countsMap.toString();
	}
	
}
