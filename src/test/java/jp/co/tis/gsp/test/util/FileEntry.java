package jp.co.tis.gsp.test.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class FileEntry extends Entry {

	public FileEntry(String path) {
		this.path = path;
	}

	@Override
	public void debugPrint() {
		System.out.println(this.getFullPath());
//		System.out.println(root);
	}

	@Override
	public boolean equals(Object obj) {

		// ファイル判定
		if (!(obj instanceof FileEntry)) {
			return false;
		}

		// ファイル名判定
		if (!super.equals(obj)) {
			return false;
		}

		// ファイルコンテンツ比較
		try {
			return FileUtils.contentEquals(new File(this.getFullPath()), new File(((Entry) obj).getFullPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;

	}

}
