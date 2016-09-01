package jp.co.tis.gsp.tools.dba.mojo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jp.co.tis.gsp.test.util.DBTestUtil;
import jp.co.tis.gsp.test.util.MojoTestFixture;
import jp.co.tis.gsp.test.util.TestDB;
import jp.co.tis.gsp.test.util.TestDBPattern;
import junit.framework.AssertionFailedError;

public class ExecuteDdlMojoTest extends AbstractDdlMojoTest<ExecuteDdlMojo> {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	/**
	 * SQLファイルの実行確認テスト。
	 *
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>指定されたディレクトリにあるSQLファイルが実行されていることを検証する。</li>
	 * <li>事前にスキーマをドロップする。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>DDLの実行により生成されたテーブルをSELECTした時に例外が発生しなければＯＫとする。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "sql_file", testDb = { TestDB.h2 })
	public void testExecSqlFile() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			ExecuteDdlMojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);

			// スキーマのドロップ
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			mojo.execute();

			// 検証
			String sql = "SELECT COUNT(*) FROM INDEX_TEST1";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);

			sql = "SELECT COUNT(*) FROM INDEX_TEST2";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);

			sql = "SELECT COUNT(*) FROM INDEX_TEST3";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);

			sql = "SELECT COUNT(*) FROM TEST_TBL1";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);

			sql = "SELECT COUNT(*) FROM TEST_TBL2";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);

		}
	}

	/**
	 * テーブルのデータ型を網羅するDDL実行テスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>各DBごとにサポートするデータ型を網羅したテーブルのDDLを実行。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>DDLの実行でエラーが発生しなければＯＫ</li>
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

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			Mojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();

		}
	}

	/**
	 * ＤＢの基本機能を生成するＤＤＬの実行テスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>下記のＤＢ基本機能を生成するＤＤＬを実行する。</li>
	 * <li>実行時にユーザが存在する場合としない場合をテストする。DB2はユーザ削除が出来ないので対象外。</li>
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
	 * <li>DDLの実行でエラーが発生しなければＯＫ</li>
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

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			ExecuteDdlMojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);

			// ユーザを削除
			if (!(mf.testDb.equals(TestDB.db2))) {
				DBTestUtil.dropUser(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
						mojo.driver, mf.testDb);
			}

			// ユーザが存在しない場合
			mojo.execute();

			// ユーザが存在する場合
			mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();
		}
	}

	/**
	 * 実行時にスキーマが存在する/しない場合のテスト（ユーザ名＝スキーマ名）。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>Goal実行時にスキーマが存在しない時の実行テスト。</li>
	 * <li>Goal実行時にスキーマが存在する時の実行テスト。</li>
	 * <li>ユーザを削除しておく</li>
	 * <li>スキーマ名とユーザ名は同じにしておく。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>DDLの実行でエラーが発生しなければＯＫ</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "exist_schema", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.h2,
			TestDB.sqlserver })
	public void testExistSchema() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			ExecuteDdlMojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);

			// スキーマのドロップ
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			// ユーザのドロップ
			if (!(mf.testDb == TestDB.db2)) {
				DBTestUtil.dropUser(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
						mojo.driver, mf.testDb);
			}

			// スキーマが存在しない、ユーザが存在しない場合
			mojo.execute();

			// スキーマが存在する場合、ユーザが存在する場合
			mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();

		}
	}

	/**
	 * 実行時にスキーマが存在する/しない場合のテスト（ユーザ名≠スキーマ名）。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>Goal実行時にスキーマが存在しない時の実行テスト。</li>
	 * <li>Goal実行時にスキーマが存在する時の実行テスト。</li>
	 * <li>スキーマ名とユーザ名が異なるパターン。</li>
	 * <li>MySQLはスキーマの生成/削除に対応していないため除外</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>DDLの実行でエラーが発生しなければＯＫ</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "exist_another_schema", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2,
			TestDB.sqlserver })
	public void testExistAnotherSchema() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			ExecuteDdlMojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);

			// スキーマのドロップ
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			// ユーザのドロップ
			if (!(mf.testDb == TestDB.db2)) {
				DBTestUtil.dropUser(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
						mojo.driver, mf.testDb);
			}

			// スキーマが存在しない、ユーザが存在しない場合
			mojo.execute();

			// スキーマが存在する、ユーザが存在する場合
			mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();

		}
	}

	/**
	 * MySQLの時にスキーマ名が指定されていたらエラーとするテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>MySQLの時にパラメータ：schemaが指定されていたらエラーとする。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>例外が発生、指定出来ない旨のメッセージが表示されればＯＫ</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "specific_schema_error", testDb = { TestDB.mysql })
	public void testSpecificSchemaError() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			expected.expect(MojoExecutionException.class);
			expected.expectMessage("MySQLではスキーマを指定することは出来ません。");

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			ExecuteDdlMojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);

			mojo.execute();
		}
	}

	/**
	 * MySQLの時にスキーマ名をJDBCのURLから取得するテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>MySQLの時にパラメータ：schemaを指定させずにjdbcのURLよりデータベース名（スキーマ名）を取得して設定する。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>jdbcのURLのデータベース名がスキーマにセットされていればＯＫ</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "set_schema_from_url", testDb = { TestDB.mysql })
	public void testSetSchemaNameForMySQL() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			ExecuteDdlMojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);

			mojo.execute();

			assertThat(mf.testDb + ": ", mojo.schema, is("gsptest"));
		}
	}

	/**
	 * パラメータ：extraDdlDirectoryのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>extraDdlDirectoryで指定されたSQLファイルが実行されていることをテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>extraDdlDirectoryにあるSQLファイル実行により生成されたテーブルをSELECT出来ればＯＫ。Ｋ</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "extraDdlDirectory", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.h2,
			TestDB.sqlserver, TestDB.mysql })
	public void testExtraDdlDirectory() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			ExecuteDdlMojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);

			mojo.execute();

			// 検証
			// extraDdlDirectoryのDDLが流れたことを確認する。
			String sql = "SELECT COUNT(*) FROM TYPETEST_EXT";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
		}
	}

	/**
	 * パラメータ：extraDdlDirectoryにファイルのパスを指定する。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>extraDdlDirectoryにファイルのパスを指定した時の挙動確認。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * // *
	 * <li>エラーにならず、ddlフォルダのSQLのみ実行されていること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "extraDdlDirectoryIsFile", testDb = { TestDB.h2 })
	public void testExtraDdlDirectoryIsFile() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			ExecuteDdlMojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);

			mojo.execute();

			// 検証
			String sql = "SELECT COUNT(*) FROM TYPETEST";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
		}
	}

	/**
	 * ユーザ名≠スキーマ名の時のオブジェクト操作権限のテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>スキーマ名とユーザ名が異なる状況でGoal実行。</li>
	 * <li>接続ユーザが自分とは異なる名前のスキーマのオブジェクトを操作できるか、権限の付与をテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>接続ユーザが別名スキーマのテーブルをINSERT,UPDATE,SELECT,DELETE出来ればＯＫ。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "another_schema_crud", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2,
			TestDB.sqlserver })
	public void testAnotherSchemaCrud() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			ExecuteDdlMojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);

			// スキーマのドロップ
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			// スキーマが存在しない状態
			mojo.execute();

			// スキーマが存在する状態
			mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();

			Properties props = new Properties();
			props.put("user", mojo.user);
			props.put("password", mojo.password);
			Connection conn = DriverManager.getConnection(mojo.url, props);
			Statement stmt = conn.createStatement();

			// CRUDの検証
			try {
				String selectSql = "SELECT * FROM " + mojo.schema + ".TEST_TBL1";
				String insertSql = "INSERT INTO " + mojo.schema + ".TEST_TBL1(TEST_NAME) VALUES('TEST_INSERT')";
				String updateSql = "UPDATE " + mojo.schema + ".TEST_TBL1 SET TEST_NAME = 'TEST_UPDATE'";
				String deleteSql = "DELETE FROM " + mojo.schema + ".TEST_TBL1";
				String selectViewSql = "SELECT * FROM " + mojo.schema + ".VIEW1";

				switch (mf.testDb) {
				// シーケンスのnextval()を追加で検証
				case oracle:
					insertSql = "INSERT INTO " + mojo.schema + ".TEST_TBL1(TEST_TBL1_ID, TEST_NAME) VALUES("
							+ mojo.schema + ".TEST_TBL1_ID_SEQ.nextval, 'TEST_INSERT')";
					break;
				}

				stmt.executeQuery(selectSql);
				stmt.executeUpdate(insertSql);
				stmt.executeUpdate(updateSql);
				stmt.executeUpdate(deleteSql);
				stmt.executeQuery(selectViewSql);

			} catch (Exception e) {
				throw new AssertionFailedError(e.getMessage());
			} finally {
				stmt.close();
				conn.close();
			}
		}
	}

	/**
	 * パラメータ：optionalDialectsのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>optionalDialectsによりDialectクラスを差し替えることが出来るかをテスト。</li>
	 * <li>h2だけでテスト。</li>
	 * <li>同じjdbcURLのＤＢプロトコルでも、optionalDialectsで指定したDialectが優先されることをテスト。</li>
	 * <li>このテストで差し込むoptionalDialectsはオブジェクト生成時に例外を投げるようにし、これを期待値とする。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>正しく差し替えられていれば、例外がスローされるのでそれを期待値とする。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "optionalDialects_1", testDb = { TestDB.h2 })
	public void testOptionalDialects1() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// 差し替えたDialectはただ例外を投げる動きをするので、それを期待値とする。
			expected.expect(MojoExecutionException.class);
			expected.expectMessage("OptionalDialectsTestDialect");

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			Mojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();
		}
	}

	/**
	 * パラメータ：optionalDialectsのテスト.
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>optionalDialectsによりDialectクラスを差し替えることが出来るかをテスト。</li>
	 * <li>h2だけでテスト。</li>
	 * <li>gspに標準装備されていないjdbcURLのＤＢプロトコルで、optionalDialectsを差し込むことが出来ることをテスト。
	 * </li>
	 * <li>このテストで差し込むoptionalDialectsはオブジェクト生成時に例外を投げるようにし、これを期待値とする。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>正しく差し替えられていれば、例外がスローされるのでそれを期待値とする。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "optionalDialects_2", testDb = { TestDB.h2 })
	public void testOptionalDialects2() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			expected.expect(MojoExecutionException.class);
			expected.expectMessage("OptionalDialectsTestDialect");

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			Mojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();

		}
	}

	/**
	 * パラメータ：onErrorの検証
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>h2だけでテスト。</li>
	 * <li>不正なDDLを実行することでＤＤＬ実行エラーを引き起こす。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>abortを指定した場合は例外がスローされるのでそれを期待値とする。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "on_error_abort", testDb = { TestDB.h2 })
	public void testOnErrorAbort() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			expected.expect(MojoExecutionException.class);
			expected.expectMessage("DDLの実行に失敗しました:");

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			Mojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();

		}
	}

	/**
	 * パラメータ：onErrorの検証
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>h2だけでテスト。</li>
	 * <li>不正なDDLを実行することでＤＤＬ実行エラーを引き起こす。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>例外が発生しなければＯＫ。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "on_error_continue", testDb = { TestDB.h2 })
	public void testOnErrorContinue() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			Mojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();

			// 例外がスローされてこなければＯＫ！

		}
	}

	/**
	 * DBの組み込みスキーマ(一般的に使われることがある)を指定した場合のテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>Postgresqlは「PUBLIC」、SQLServerは「dbo」を指定して実行。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>DDLの実行でエラーが発生しなければＯＫ</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "builtin_schema", testDb = { TestDB.postgresql, TestDB.sqlserver })
	public void testBuiltInSchema() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			ExecuteDdlMojo mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);

			DBTestUtil.dropUser(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();
		}
	}
	
	/**
	 * DB2の場合にOSユーザが存在しない場合のテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>存在しないOSユーザを指定して実行する。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>エラーとなる。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "not_exist_os_user", testDb = { TestDB.db2})
	public void testNotExistOsUser() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {
			
			expected.expect(MojoExecutionException.class);
			expected.expectMessage("指定されたユーザがOSに存在しない、またはパスワードが間違っている可能性があります。");

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			mojo = this.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			mojo.execute();
		}
	}
}
