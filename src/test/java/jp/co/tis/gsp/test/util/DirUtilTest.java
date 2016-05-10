package jp.co.tis.gsp.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

public class DirUtilTest {

	private static final String SEP = System.getProperty("file.separator");

	@Test
	public void testEqualCase() {
		String path = this.getClass().getResource("DirUtil/Equal").getPath();

	
		File rootDir = new File(path);
		File[] list = rootDir.listFiles();

		for (File f : list) {
			Entry a = DirUtil.collectEntry(f.getAbsolutePath() + SEP + "A");
			Entry b = DirUtil.collectEntry(f.getAbsolutePath() + SEP + "B");

			assertThat("Test : " + f.getName(), a.equals(b), is(true));
		}
	}

	@Test
	public void testDifferentCase() {
		String path = this.getClass().getResource("DirUtil/Different").getPath();

		File rootDir = new File(path);
		File[] list = rootDir.listFiles();

		for (File f : list) {
			Entry a = DirUtil.collectEntry(f.getAbsolutePath() + SEP + "A");
			Entry b = DirUtil.collectEntry(f.getAbsolutePath() + SEP + "B");

			assertThat("Test : " + f.getName(), a.equals(b), is(false));
		}
	}
}
