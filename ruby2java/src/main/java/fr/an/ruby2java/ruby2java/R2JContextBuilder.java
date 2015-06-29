package fr.an.ruby2java.ruby2java;

import java.util.HashMap;
import java.util.Map;

import org.jruby.Ruby;

public class R2JContextBuilder {

	public Ruby ruby;
	
	private Map<String,R2JContext> forFiles = new HashMap<String,R2JContext>();
	
	private CountMap unsupportedCountMap = new CountMap ();
	
	
	public R2JContextBuilder(Ruby ruby) {
		this.ruby = ruby;
	}
	
	public Ruby getRuby() {
		return ruby;
	}

	public R2JContext forFile(String path) {
		R2JContext res = forFiles.get(path);
		if (res == null) {
			res = new R2JContext(this);
			forFiles.put(path, res);
		}
		return res;
	}

	public CountMap getUnsupportedCountMap() {
		return unsupportedCountMap;
	}

	
	
}
