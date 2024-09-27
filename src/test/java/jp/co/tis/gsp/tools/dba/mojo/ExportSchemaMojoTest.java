package jp.co.tis.gsp.tools.dba.mojo;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
	@TestDBPattern(testCase = "basic", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.h2, TestDB.mysql, TestDB.db2, TestDB.sqlserver })
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

			// db2とsqlserverは汎用モードのため、バイナリデータの検証は無し
			if(!mf.testDb.equals(TestDB.db2) && !mf.testDb.equals(TestDB.sqlserver)) {
    			// 画像は手動でInsertする
    			File file = new File(getTestCaseDBPath(mf) + "/data/neko.jpg");
    
    			for (long i = 1; i <= 20; i++) {
    				DBTestUtil.insertFileTbl(i, file, loadMojo.url, loadMojo.user, loadMojo.password, null);
    			}
			}

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			mojo.execute();

		}
	}

	/**
	 * ユーザ名≠スキーマ名の時のエクスポートテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>スキーマ名とユーザー名が異なる状況でエクスポートが出来ることをテスト。</li>
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
	@TestDBPattern(testCase = "another_schema", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.sqlserver })
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

			// db2とsqlserverは汎用モードのため、バイナリデータの検証は無し
			if(!mf.testDb.equals(TestDB.db2) && !mf.testDb.equals(TestDB.sqlserver)) {
    			// 画像は手動でInsertする
    			File file = new File(getTestCaseDBPath(mf) + "/data/neko.jpg");
    
    			for (long i = 1; i <= 20; i++) {
    				DBTestUtil.insertFileTbl(i, file, loadMojo.url, loadMojo.user, loadMojo.password,
    						"GSPANOTHER.FILE_TBL");
    			}
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
			ExportSchemaMojo mojo = this.lookupConfiguredMojo(pom, EXPORT_SCHEMA, mf.testDb);
			mojo.execute();

		}
	}

	/**
	 * 汎用モードでのパラメータ：ddlDirectoryの指定誤りによる例外テスト
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>パラメータ：ddlDirectoryに存在しないパスを指定する。</li>
	 * <li>db2</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>エラーメッセージが表示され処理が中断されること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "ddl_dir_not_exist", testDb = { TestDB.h2 })
	public void testDdlDirectoryNotExist() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			Mojo mojo = this.lookupConfiguredMojo(pom, EXPORT_SCHEMA, mf.testDb);
			
			try {
			    mojo.execute();
			    fail("期待した例外が発生しませんでした。");
	         } catch (Exception e) {
	             assertThat(e.getCause().getClass().equals(MojoExecutionException.class), is(true));
	             assertThat(e.getCause().getMessage().equals("DDLディレクトリの指定が誤っています"), is(true));
	         }

		}
	}

	/**
	 * 汎用モードでのパラメータ：extraDdlDirectoryの指定誤りによる例外テスト
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>パラメータ：extraDdlDirectoryに存在しないパスを指定する。</li>
	 * <li>db2</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>エラーメッセージが表示され処理が中断されること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "extraDdl_dir_not_exist", testDb = { TestDB.h2 })
	public void testExtraDdlDirectoryNotExist() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			Mojo mojo = this.lookupConfiguredMojo(pom, EXPORT_SCHEMA, mf.testDb);
			
			try {
			    mojo.execute();
			    fail("期待した例外が発生しませんでした。");
	         } catch (Exception e) {
	             assertThat(e.getCause().getClass().equals(MojoExecutionException.class), is(true));
	             assertThat(e.getCause().getMessage().equals("extraDDLディレクトリの指定が誤っています"), is(true));
	         }

		}
	}
}
