package jp.co.tis.gsp.tools.dba.mojo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import jp.co.tis.gsp.test.util.DirUtil;
import jp.co.tis.gsp.test.util.Directory;
import jp.co.tis.gsp.test.util.Entry;
import jp.co.tis.gsp.test.util.MojoTestFixture;
import jp.co.tis.gsp.test.util.TestDB;
import jp.co.tis.gsp.test.util.TestDBPattern;

public class GenerateDdlMojoTest extends AbstractDdlMojoTest<GenerateDdlMojo> {

	/**
	 * テーブルのデータ型を網羅するDDL実行テスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>各DBごとにサポートするデータ型を網羅したテーブル定義のあるedmファイルをインプットにしてテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値DDLファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "type", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.h2,
			TestDB.sqlserver, TestDB.mysql })
	public void testType() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行
			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "ddl");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}

	/**
	 * ＤＢの基本機能を生成するＤＤＬの実行テスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>下記のＤＢ基本機能を有するedmファイルをインプットにしたテスト。</li>
	 * </ul>
	 * <ul>
	 * <li>主キー</li>
	 * <li>関連・外部キー</li>
	 * <li>制約</li>
	 * <li>シーケンス</li>
	 * <li>ビュー</li>
	 * </ul>
	 * 
	 * <sub>h2はビュー未対応なので除く</sub>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値DDLファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "basic", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.h2,
			TestDB.sqlserver, TestDB.mysql })
	public void testBasic() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "ddl");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}

	/**
	 * パラメータ：ddlTemplateFileDirのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>：ddlTemplateFileDirで指定されたDDLテンプレートファイルが参照されていることをテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値DDLファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "ddlTemplateFileDir", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.h2,
			TestDB.sqlserver, TestDB.mysql })
	public void testDdlTemplateFileDir() throws Exception {
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "ddl");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}

	/**
	 * パラメータ：lengthSemanticsのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>Oracleのみ。</li>
	 * <li>{@code LengthSemantics.BYTE}を指定した時のテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値DDLファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "lengthSemantics", testDb = { TestDB.oracle })
	public void testLengthSemantics() throws Exception {
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "ddl");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}

	/**
	 * ユーザ名≠スキーマ名のテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>スキーマ名とユーザ名が異なる時のDDL生成テスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値DDLファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "another_schema", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.sqlserver,
			TestDB.db2 })
	public void testAnotherSchema() throws Exception {
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "ddl");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}

	/**
	 * 自動採番属性の判定テスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>各ＤＢ毎に条件にあったものだけカラムに自動採番属性が付与されることをテスト。</li>
	 * <li>Oracleは自動採番属性がないのでシーケンスの確認をする。</li>
	 * <li>Postgresqlとh2は、Serial型などデータ型と自動採番属性がセットになっているのでこのテストは対象外。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値DDLファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "auto_increment", testDb = { TestDB.oracle, TestDB.db2, TestDB.sqlserver, TestDB.mysql })
	public void testAutoIncrement() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行
			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "ddl");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}

	/**
	 * シナリオ外テストケース。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>edmファイル上のリレーションシップの「論理のみ」「物理のみ」のテスト</li>
	 * <li>1つのテーブルに複数の外部キーがあるパターン</li>
	 * <li>スキーマ名と同じモデル名のモデルを追加しておく</li>
	 * <li>メインモデルにはないが、スキーマ（サブモデル）ではリレーションが存在する場合のテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値DDLファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "extra", testDb = { TestDB.oracle })
	public void testExtra() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行
			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "ddl");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}

	/**
	 * パラメータ：outputDirectoryのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>outputDirectoryに指定されたディレクトリが存在するパターンをテスト。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値DDLファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "outputDirectoryIsExist", testDb = { TestDB.h2 })
	public void testOutputDirectoryIsExist() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "ddl");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}

	/**
	 * DDL出力フラグ(print*)をオフにした場合のテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>h2のみでテスト。</li>
	 * <li>printフラグをオフにしたDialectを用意してテスト。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値DDLファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "no_print", testDb = { TestDB.h2 })
	public void testNoPrint() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Directory actualFiles = (Directory)DirUtil.collectEntry(actualPath);
			assertThat("TestDb:" + mf.testDb, actualFiles.getList().size(), is(0));
		}
	}
}
