package jp.co.tis.gsp.test.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Directory extends Entry {

	protected List<Entry> list = new ArrayList<Entry>();

	public Directory(String path, String rootPath) {
		if (path.equals(rootPath)) {
			this.path = IS_ROOT;
		} else {
			this.path = path;
		}

		this.root = rootPath;
	}

	public List<Entry> getList() {
		return this.list;
	}

	public Entry add(Entry entry) {
		entry.path = entry.path.replaceFirst("^" + Pattern.quote(root + SEP), "");
		entry.root = this.root;
		list.add(entry);
		return this;
	}

	@Override
	public void debugPrint() {
		System.out.println(this.getFullPath());
//		System.out.println(root);
		Iterator<Entry> it = list.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			entry.debugPrint();
		}
	}

	@Override
	public boolean equals(Object obj) {

		// ディレクトリ判定
		if (!(obj instanceof Directory)) {
			return false;
		}

		// ディレクトリ名の比較
		if (!super.equals(obj)) {
			return false;
		}

		// ディレクトリ配下の比較
		List<Entry> testList = ((Directory) obj).list;

		// ディレクトリ配下のエントリ数比較
		if (list.size() != testList.size()) {
			return false;
		}

		// 比較のために名前ソート
		Collections.sort(list);
		Collections.sort(testList);

		Iterator<Entry> ite = list.iterator();
		Iterator<Entry> testIte = testList.iterator();

		// 一応、比較対象も一緒にhasNext
		while (ite.hasNext() && testIte.hasNext()) {
			Entry e = ite.next();
			Entry t = testIte.next();
			if (!e.equals(t)) {
				return false;
			}
		}

		return true;

	}

}
