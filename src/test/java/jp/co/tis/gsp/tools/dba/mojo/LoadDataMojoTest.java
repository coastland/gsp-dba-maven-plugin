package jp.co.tis.gsp.tools.dba.mojo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jp.co.tis.gsp.test.util.DBTestUtil;
import jp.co.tis.gsp.test.util.MojoTestFixture;
import jp.co.tis.gsp.test.util.TestDB;
import jp.co.tis.gsp.test.util.TestDBPattern;
import jp.co.tis.gsp.tools.GspToolsException;

public class LoadDataMojoTest extends AbstractDdlMojoTest<LoadDataMojo> {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	/**
	 * テーブルのデータ型を網羅したデータのロードテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>各DBごとにサポートするデータ型を網羅したテーブルを作成し、各カラムにデータを投入する。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>テーブルの件数をテスト</li>
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

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行
			LoadDataMojo mojo = this.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			mojo.execute();

			// 検証
			String sql = "SELECT COUNT(*) FROM TYPETEST";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1));

		}
	}

	/**
	 * 外部制約を考慮した順にデータがロードされていることのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>複数のテーブルを用意し、外部制約を設定。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>例外が発生しない。</li>
	 * <li>テーブルの件数をテスト。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "depend_data", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.h2,
			TestDB.sqlserver, TestDB.mysql })
	public void testDepend() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");
			
			//generate-ddl テスト準備用コード
//			GenerateDdlMojoTest genTest = new GenerateDdlMojoTest();
//			genTest.setUp();
//			GenerateDdlMojo genMojo = genTest.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
//			genMojo.execute();
//			if(true) return;
			
			
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行
			LoadDataMojo mojo = this.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			mojo.execute();

			// 検証
			String sql = "SELECT COUNT(*) FROM A_TABLE";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(300));

			sql = "SELECT COUNT(*) FROM B_TABLE";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(300));

			sql = "SELECT COUNT(*) FROM C_TABLE";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(300));

			sql = "SELECT COUNT(*) FROM D_TABLE";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(300));

		}
	}

	/**
	 * ユーザ名≠スキーマ名の時のデータロードテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>ユーザ名とスキーマ名が異なる状況でデータがロード出来ることをテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>テーブルの件数をテスト。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "another_schema", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2,
			TestDB.sqlserver })
	public void testAnotherSchema() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行
			LoadDataMojo mojo = this.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			mojo.execute();

			// 検証
			String sql = "SELECT COUNT(*) FROM GSPANOTHER.A_TABLE";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(300));

			sql = "SELECT COUNT(*) FROM GSPANOTHER.B_TABLE";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(300));

			sql = "SELECT COUNT(*) FROM GSPANOTHER.C_TABLE";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(300));

			sql = "SELECT COUNT(*) FROM GSPANOTHER.D_TABLE";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(300));

		}
	}

	/**
	 * パラメータ：specifiedEncodingFilesのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>specifiedEncodingFilesにUTF-8を設定。</li>
	 * <li>csvファイルのエンコーディングをUTF-8にしてデータをロードする。</li>
	 * <li>SQLServerはVARCHARではなく、NVARCHARにしないとUTF-8文字として格納できなかった。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>テーブルの件数をテスト。</li>
	 * <li>テーブルから取得したバイト配列が期待したUTF-8文字であることをテスト</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "specifiedEncodingFiles", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2,
			TestDB.h2, TestDB.sqlserver, TestDB.mysql })
	public void testSpecifiedEncodingFiles() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行
			LoadDataMojo mojo = this.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			mojo.execute();

			// 検証
			String sql = "SELECT COUNT(*) FROM A_TABLE";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1));

			sql = "SELECT COUNT(*) FROM B_TABLE";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(300));

			// UTF-8文字チェック
			sql = "SELECT TEST_NAME FROM A_TABLE";
			String val = DBTestUtil.getValueOfSql(sql, mojo.url, mojo.user, mojo.password);

			assertThat(mf.testDb + ": ", val, is("È"));
		}
	}

	/**
	 * パラメータ：onErrorの検証
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>h2だけでテスト。</li>
	 * <li>不正なデータで実行することで実行エラーを引き起こす。</li>
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

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行
			LoadDataMojo mojo = this.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			mojo.execute();
		}
	}

	/**
	 * パラメータ：onErrorの検証
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>h2だけでテスト。</li>
	 * <li>不正なデータで実行することで実行エラーを引き起こす。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>例外がスローされなければＯＫ</li>
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

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行
			LoadDataMojo mojo = this.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			mojo.execute();

			// 例外がスローされてこなければＯＫ！
		}
	}

	/**
	 * DB接続エラーのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>h2だけでテスト。</li>
	 * <li>不正なURLを指定する。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>エラーとなる。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "connect_error", testDb = { TestDB.h2 })
	public void testConnectError() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			expected.expect(MojoExecutionException.class);

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// pom.xmlより指定ゴールのMojoを取得し実行
			LoadDataMojo mojo = this.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			mojo.execute();
		}
	}

	/**
	 * 循環参照エラーテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>h2だけでテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>エラーになる</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "circular_reference", testDb = { TestDB.h2 })
	public void testCircularReference() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行
			LoadDataMojo mojo = this.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			
	         try {
	                mojo.execute();
	                fail("期待した例外が発生しませんでした。");
	         } catch (Exception e) {
	             assertThat(e.getCause().getClass().equals(GspToolsException.class), is(true));
	             assertThat(e.getCause().getMessage().equals("ルートとなるテーブルが見つかりません。循環参照になっていると思います！"), is(true));
	         }
		}
	}
}
