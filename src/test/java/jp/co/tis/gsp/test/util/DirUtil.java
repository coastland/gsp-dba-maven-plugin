package jp.co.tis.gsp.test.util;

import java.io.File;

public class DirUtil {

	/**
	 * 指定ディレクトリ配下のファイル・ディレクトリを集めます。
	 * 
	 * @param root
	 *            - 基点になるディレクトリパス
	 * @return - ディレクトリ集合
	 */
	public static Entry collectEntry(String root) {
		return collectEntry(root, root);
	}

	/**
	 * 指定ディレクトリ配下のファイル・ディレクトリを集めます。
	 * 
	 * ディレクトリやファイルにルートとなるパスを設定します。
	 * 
	 * @param path
	 *            - ディレクトリパス
	 * @param rootPath
	 *            - 基点ディレクトリパス
	 * @return - ディレクトリ集合
	 */
	 private static Entry collectEntry(String path, String rootPath) {

		File rootDir = new File(path);
		File[] list = rootDir.listFiles();

		if (list == null)
			return null;

		Entry dir = new Directory(path, rootPath);

		try {
			for (File f : list) {
				if (f.isDirectory()) {
					Entry subdir = collectEntry(f.getAbsolutePath(), rootPath);
					dir.add(subdir);
				} else {
					dir.add(new FileEntry(f.getAbsolutePath()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dir;
	}
}
