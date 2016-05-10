package jp.co.tis.gsp.test.util;

import java.util.regex.Pattern;

public abstract class Entry implements Comparable<Entry> {

	protected String root;

	protected String path;

	protected static final String SEP = System.getProperty("file.separator");

	protected final String IS_ROOT = "";

	public String getPath() {
		return path;
	}

	public String getFullPath() {
		if (isRoot()) {
			return this.root;
		} else {
			return root + SEP + path;
		}
	}

	public boolean isRoot() {
		return this.path.equals(IS_ROOT);
	}

	public Entry add(Entry entry) throws Exception {
		throw new Exception();
	}

	public String toString() {
		return getPath();
	}

	public abstract void debugPrint();

	@Override
	public boolean equals(Object obj) {
		Entry entry = (Entry) obj;

		if (!this.path.equals(entry.path)) {
			return false;
		}

		return true;
	}

	@Override
	public int compareTo(Entry o) {
		return this.path.compareTo(o.path);
	}

}
