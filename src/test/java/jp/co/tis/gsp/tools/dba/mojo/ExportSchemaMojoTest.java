package jp.co.tis.gsp.tools.dba.mojo;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import jp.co.tis.gsp.test.util.DBTestUtil;
import jp.co.tis.gsp.test.util.MojoTestFixture;
import jp.co.tis.gsp.test.util.TestDB;
import jp.co.tis.gsp.test.util.TestDBPattern;

public class ExportSchemaMojoTest extends AbstractDdlMojoTest<ExportSchemaMojo> {

	/**
	 * ＤＢの基本機能を有するエクスポートのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>下記のＤＢ機能を有するＤＢをエクスポートするテスト。</li>
	 * <li>DB2とSQLServerは除外。</li>
	 * </ul>
	 * <ul>
	 * <li>主キー</li>
	 * <li>関連・外部キー</li>
	 * <li>制約</li>
	 * <li>シーケンス</li>
	 * <li>ビュー</li>
	 * <li>シノニム</li>
	 * <li>プロシージャ</li>
	 * <li>ファンクション</li>
	 * <li>トリガ</li>
	 * </ul>
	 * 
	 * <sub>h2はビュー未対応なので除く</sub>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>例外が発生しないこと。</li>
	 * <li>ダンプデータの妥当性はImportSchemaMojoTestで実施。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "basic", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.h2, TestDB.mysql })
	public void testBasic() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");
			
			ExportSchemaMojo mojo = this.lookupConfiguredMojo(pom, EXPORT_SCHEMA, mf.testDb);
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			LoadDataMojoTest loadTest = new LoadDataMojoTest();
			loadTest.setUp();
			LoadDataMojo loadMojo = loadTest.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			loadMojo.execute();

			// 画像は手動でInsertする
			File file = new File(getTestCaseDBPath(mf) + "/data/neko.jpg");

			for (long i = 1; i <= 20; i++) {
				DBTestUtil.insertFileTbl(i, file, loadMojo.url, loadMojo.user, loadMojo.password, null);
			}

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			
			mojo.execute();

		}
	}

	/**
	 * DB2とSQLServerのexport-schema未対応テスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>DB2とSQLServerの場合、export-schemaを実行すると例外が発生することをテスト。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>例外が発生し、未対応であることを知らせるメッセージが取得出来ること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "no_support_db", testDb = { TestDB.db2, TestDB.sqlserver })
	public void testNoSupportDb() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			try {

				// テストケース対象プロジェクトのpom.xmlを取得
				File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

				// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
				Mojo mojo = this.lookupConfiguredMojo(pom, EXPORT_SCHEMA, mf.testDb);
				mojo.execute();

			} catch (MojoExecutionException e) {

				String msg = e.getCause().getMessage();

				switch (mf.testDb) {
				case db2:
					if (!msg.equals("db2を用いたexport-schemaはサポートしていません。"))
						fail();
					break;
				case sqlserver:
					if (!msg.equals("Sqlserverを用いたexport-schemaはサポートしていません。"))
						fail();
					break;
				}

			} catch (Throwable t) {
				fail("期待した例外が発生しませんでした。");
			}
		}
	}

	/**
	 * ユーザ名≠スキーマ名の時のエクスポートテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>スキーマ名とユーザー名が異なる状況でエクスポートが出来ることをテスト。</li>
	 * <li>DB2とSQLServerは除外。</li>
	 * </ul>
	 * 
	 * <ul>
	 * <li>主キー</li>
	 * <li>関連・外部キー</li>
	 * <li>制約</li>
	 * <li>シーケンス</li>
	 * <li>ビュー</li>
	 * <li>シノニム</li>
	 * <li>プロシージャ</li>
	 * <li>ファンクション</li>
	 * <li>トリガ</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>例外が発生しないこと。</li>
	 * <li>ダンプデータの妥当性はImportSchemaMojoTestで実施。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "another_schema", testDb = { TestDB.oracle, TestDB.postgresql })
	public void testAnotherSchema() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");
			ExportSchemaMojo mojo = this.lookupConfiguredMojo(pom, EXPORT_SCHEMA, mf.testDb);
			
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			LoadDataMojoTest loadTest = new LoadDataMojoTest();
			loadTest.setUp();
			LoadDataMojo loadMojo = loadTest.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			loadMojo.execute();

			// 画像は手動でInsertする
			File file = new File(getTestCaseDBPath(mf) + "/data/neko.jpg");

			for (long i = 1; i <= 20; i++) {
				DBTestUtil.insertFileTbl(i, file, loadMojo.url, loadMojo.user, loadMojo.password,
						"GSPANOTHER.FILE_TBL");
			}

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			mojo.execute();

		}
	}

	/**
	 * パラメータ：outputDirectoryのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>outputDirectoryに指定されたディレクトリが存在しないパターンをテスト。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>ダンプデータの妥当性はImportSchemaMojoTestで実施。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "outputDirectoryIsNotExist", testDb = { TestDB.h2 })
	public void testOutputDirectoryIsNotExist() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			LoadDataMojoTest loadTest = new LoadDataMojoTest();
			loadTest.setUp();
			LoadDataMojo loadMojo = loadTest.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			loadMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			Mojo mojo = this.lookupConfiguredMojo(pom, EXPORT_SCHEMA, mf.testDb);
			mojo.execute();

		}
	}

}
