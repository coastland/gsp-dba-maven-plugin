package jp.co.tis.gsp.tools.dba.mojo;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.repository.RepositorySystem;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jp.co.tis.gsp.test.util.DBTestUtil;
import jp.co.tis.gsp.test.util.DirUtil;
import jp.co.tis.gsp.test.util.Entry;
import jp.co.tis.gsp.test.util.MojoTestFixture;
import jp.co.tis.gsp.test.util.TestDB;
import jp.co.tis.gsp.test.util.TestDBPattern;

public class ImportSchemaMojoTest extends AbstractDdlMojoTest<ImportSchemaMojo> {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	/**
	 * ＤＢの基本機能を有するダンプデータのインポートテスト（ユーザとスキーマ存在しないパターン）。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>下記のＤＢ機能を有するダンプデータのインポートをテスト。</li>
	 * <li>MySQLとDB2、SQLServerは除外。</li>
	 * <li>実行時にスキーマが存在しないパターン。</li>
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
	 * <li>各テーブルのレコード件数をテスト</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "basic", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.h2 })
	public void testBasicUserSchemaNonExist() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
			ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);

			// テスト用ダンプjarをインストール
			RepositorySystem rs = this.lookup(RepositorySystem.class);
			Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
			artifact.setFile(new File(getTestCaseDBPath(mf) + "/gsp-test-testdata-1.0.0.jar"));
			installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

			// スキーマのドロップ
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);
			
			// ユーザのドロップ
			DBTestUtil.dropUser(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			// スキーマが存在しない状態で。
			mojo.execute();

			// 検証
			String sql = "SELECT COUNT(*) FROM TEST_TBL1";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM TEST_TBL2";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM TEST_TBL3";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM FILE_TBL";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(20));

			// H2はプロシージャがないので対象外
			if(mf.testDb.equals(TestDB.h2)) continue;
			
			Connection conn = null;
			CallableStatement cs = null;
			try {
				conn = DriverManager.getConnection(mojo.url, mojo.user, mojo.password);
				cs = conn.prepareCall("{CALL PROC_SELECT_TEST_TBL1(?, ?)}");
				cs.setInt(1, 100);
				cs.registerOutParameter(2, java.sql.Types.INTEGER);
				cs.execute();

				int rtn = cs.getInt(2);
				assertThat(mf.testDb + ": ", rtn, is(100));
			} finally {
				cs.close();
				conn.close();
			}

		}
	}
	
	/**
	 * ＤＢの基本機能を有するダンプデータのインポートテスト（スキーマ存在するパターン）。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>下記のＤＢ機能を有するダンプデータのインポートをテスト。</li>
	 * <li>MySQLとDB2、SQLServerは除外。</li>
	 * <li>実行時にスキーマが存在パターン。</li>
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
	 * <li>各テーブルのレコード件数をテスト</li>
	 * </ul>
	 * 
	 */
	@Test
	@TestDBPattern(testCase = "basic", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.h2, TestDB.mysql })
	public void testBasicSchemaExist() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
			ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);
			
			// 一旦データベースをきれいにする。
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			// テスト用ダンプjarをインストール
			RepositorySystem rs = this.lookup(RepositorySystem.class);
			Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
			artifact.setFile(new File(getTestCaseDBPath(mf) + "/gsp-test-testdata-1.0.0.jar"));
			installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

			// スキーマを事前に作成
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// スキーマが既に存在する状態で。
			mojo.execute();

			// 検証
			String sql = "SELECT COUNT(*) FROM TEST_TBL1";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM TEST_TBL2";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM TEST_TBL3";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM FILE_TBL";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(20));
			
			// H2はプロシージャがないので対象外
			if(mf.testDb.equals(TestDB.h2)) continue;
			
			Connection conn = null;
			CallableStatement cs = null;
			try {
				conn = DriverManager.getConnection(mojo.url, mojo.user, mojo.password);
				cs = conn.prepareCall("{CALL PROC_SELECT_TEST_TBL1(?, ?)}");
				cs.setInt(1, 100);
				cs.registerOutParameter(2, java.sql.Types.INTEGER);
				cs.execute();

				int rtn = cs.getInt(2);
				assertThat(mf.testDb + ": ", rtn, is(100));
			} finally {
				cs.close();
				conn.close();
			}

		}
	}

	/**
	 * ＤＢの基本機能を有するダンプデータのインポートテスト（スキーマ存在、ユーザ名≠スキーマ名）。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>下記のＤＢ機能を有するダンプデータのインポートをテスト。</li>
	 * <li>MySQLとDB2、SQLServerは除外。</li>
	 * <li>実行時にスキーマが存在パターン。</li>
	 * <li>ユーザ名とスキーマ名が異なる。</li>
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
	 * <li>各テーブルのレコード件数をテスト</li>
	 * </ul>
	 * 
	 */
	@Test
	@TestDBPattern(testCase = "another_schema", testDb = { TestDB.oracle, TestDB.postgresql })
	public void testBasicAnotherSchemaExist() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
			ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);
			
			// 一旦データベースをきれいにする。
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			// テスト用ダンプjarをインストール
			RepositorySystem rs = this.lookup(RepositorySystem.class);
			Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
			artifact.setFile(new File(getTestCaseDBPath(mf) + "/gsp-test-testdata-1.0.0.jar"));
			installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

			// スキーマを事前に作成
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// スキーマが既に存在する状態で。
			mojo.execute();

			// 検証
			String sql = "SELECT COUNT(*) FROM GSPANOTHER.TEST_TBL1";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM GSPANOTHER.TEST_TBL2";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM GSPANOTHER.TEST_TBL3";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM GSPANOTHER.FILE_TBL";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(20));
			
			
			Connection conn = null;
			CallableStatement cs = null;
			try {
				conn = DriverManager.getConnection(mojo.url, mojo.user, mojo.password);
				cs = conn.prepareCall("{CALL GSPANOTHER.PROC_SELECT_TEST_TBL1(?, ?)}");
				cs.setInt(1, 100);
				cs.registerOutParameter(2, java.sql.Types.INTEGER);
				cs.execute();

				int rtn = cs.getInt(2);
				assertThat(mf.testDb + ": ", rtn, is(100));
			} finally {
				cs.close();
				conn.close();
			}
		}
	}

	/**
	 * ＤＢの基本機能を有するダンプデータのインポートテスト（ユーザーとスキーマ存在しない、ユーザ名≠スキーマ名）。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>下記のＤＢ機能を有するダンプデータのインポートをテスト。</li>
	 * <li>MySQLとDB2、SQLServerは除外。</li>
	 * <li>実行時にスキーマが存在しないパターン。</li>
	 * <li>ユーザ名とスキーマ名が異なる。</li>
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
	 * <li>各テーブルのレコード件数をテスト</li>
	 * </ul>
	 * 
	 */
	@Test
	@TestDBPattern(testCase = "another_schema", testDb = { TestDB.oracle, TestDB.postgresql })
	public void testBasicUserAnotherSchemaNonExist() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
			ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);

			// テスト用ダンプjarをインストール
			RepositorySystem rs = this.lookup(RepositorySystem.class);
			Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
			artifact.setFile(new File(getTestCaseDBPath(mf) + "/gsp-test-testdata-1.0.0.jar"));
			installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

			// スキーマのドロップ
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);
			
			// ユーザのドロップ
			DBTestUtil.dropUser(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			// スキーマが存在しない状態で。
			mojo.execute();

			// 検証
			String sql = "SELECT COUNT(*) FROM GSPANOTHER.TEST_TBL1";
			int cnt = 0;
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM GSPANOTHER.TEST_TBL2";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM GSPANOTHER.TEST_TBL3";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(1000));

			sql = "SELECT COUNT(*) FROM GSPANOTHER.FILE_TBL";
			cnt = DBTestUtil.getCount(sql, mojo.url, mojo.user, mojo.password);
			assertThat(mf.testDb + ": ", cnt, is(20));
			
			
			Connection conn = null;
			CallableStatement cs = null;
			try {
				conn = DriverManager.getConnection(mojo.url, mojo.user, mojo.password);
				cs = conn.prepareCall("{CALL GSPANOTHER.PROC_SELECT_TEST_TBL1(?, ?)}");
				cs.setInt(1, 100);
				cs.registerOutParameter(2, java.sql.Types.INTEGER);
				cs.execute();

				int rtn = cs.getInt(2);
				assertThat(mf.testDb + ": ", rtn, is(100));
			} finally {
				cs.close();
				conn.close();
			}

		}
	}

	/**
	 * ダンプjarから期待したファイル名のダンプが取得出来ない場合のテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>スキーマ名.dmpとは異なるファイル名のダンプを格納したダンプjarからimportする。</li>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>エラーとなる。</li>
	 * </ul>
	 * 
	 */
	@Test
	@TestDBPattern(testCase = "notFoundDumpJar", testDb = {TestDB.oracle, TestDB.postgresql, TestDB.h2, TestDB.mysql })
	public void testNotFoundDumpJar() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
			ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);

			// テスト用ダンプjarをインストール
			RepositorySystem rs = this.lookup(RepositorySystem.class);
			Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
			artifact.setFile(new File(getTestCaseDBPath(mf) + "/gsp-test-testdata-1.0.0.jar"));
			installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

			// スキーマを事前に作成
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// スキーマが既に存在する状態で。
			try {
			    mojo.execute();
			    fail("期待した例外が発生しませんでした。");
			} catch(Exception e) {
			    
			    assertThat(e.getCause().getClass().equals(MojoExecutionException.class), is(true));
			    
			    if(mf.testDb.equals(TestDB.h2)) {
			        assertThat(e.getCause().getMessage().equals("PUBLIC.dmp is not found?"), is(true));
			    } else if(mf.testDb.equals(TestDB.postgresql) || mf.testDb.equals(TestDB.mysql)) {
			        assertThat(e.getCause().getMessage().equals("gsptest.dmp is not found?"), is(true));
			    } else {
			        assertThat(e.getCause().getMessage().equals("GSPTEST.dmp is not found?"), is(true));
			    }
			}
		}
	}

	/**
	 * パラメータ：inputDirectoryに無効なパスを指定したテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>inputDirectoryに無効なパスを指定して処理を実行する。</li>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>エラーとなる。</li>
	 * </ul>
	 * 
	 */
	@Test
	@TestDBPattern(testCase = "error_inputDirectory", testDb = { TestDB.h2 })
	public void testErrorInputDicretory() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			expected.expect(MojoExecutionException.class);

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
			ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);

			// テスト用ダンプjarをインストール
			RepositorySystem rs = this.lookup(RepositorySystem.class);
			Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
			artifact.setFile(new File(getTestCaseDBPath(mf) + "/gsp-test-testdata-1.0.0.jar"));
			installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

			// スキーマを事前に作成
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// スキーマが既に存在する状態で。
			mojo.execute();
		}
	}

   /**
     *  汎用モード(DB2とSQLSserver）のテスト。
     * 
     * <h4>検証内容</h4>
     * <ul>
     * <li>実行時にスキーマが存在しないパターン。</li>
     * </ul>
     * 
     * <h4>検証結果</h4>
     * <ul>
     * <li>各テーブルのレコード件数をテスト</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    @TestDBPattern(testCase = "basic_general", testDb = { TestDB.db2, TestDB.sqlserver })
    public void testBasicGeneralUserSchemaNonExist() throws Exception {

        // 指定されたケース及びテスト対象のDBだけループ
        for (MojoTestFixture mf : mojoTestFixtureList) {

            // テストケース対象プロジェクトのpom.xmlを取得
            File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

            // currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
            ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);

            // テスト用ダンプjarをインストール
            RepositorySystem rs = this.lookup(RepositorySystem.class);
            Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
            artifact.setFile(new File(getTestCaseDBPath(mf) + "/gsp-test-testdata-1.0.0.jar"));
            installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

            // スキーマのドロップ
            DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
                    mojo.driver, mf.testDb);
            
            // ユーザのドロップ
            if(!mf.testDb.equals(TestDB.db2)) {
                DBTestUtil.dropUser(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
                        mojo.driver, mf.testDb);
            }

            // スキーマが存在しない状態で。
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
     *  汎用モード(DB2とSQLSserver）のテスト。
     * 
     * <h4>検証内容</h4>
     * <ul>
     * <li>実行時にスキーマが存在するパターン。</li>
     * </ul>
     * 
     * <h4>検証結果</h4>
     * <ul>
     * <li>各テーブルのレコード件数をテスト</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    @TestDBPattern(testCase = "basic_general", testDb = { TestDB.db2, TestDB.sqlserver })
    public void testBasicGeneralUserSchemaExist() throws Exception {

        // 指定されたケース及びテスト対象のDBだけループ
        for (MojoTestFixture mf : mojoTestFixtureList) {

            // テストケース対象プロジェクトのpom.xmlを取得
            File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

            // currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
            ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);

			// 一旦データベースをきれいにする。
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

            // テスト用ダンプjarをインストール
            RepositorySystem rs = this.lookup(RepositorySystem.class);
            Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
            artifact.setFile(new File(getTestCaseDBPath(mf) + "/gsp-test-testdata-1.0.0.jar"));
            installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

			// スキーマを事前に作成
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

            // スキーマが存在する状態で。
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
	 * ＤＢの基本機能を有するダンプデータのインポートテスト（スキーマ存在、ユーザ名≠スキーマ名）、汎用モード。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>実行時にスキーマが存在パターン。</li>
	 * <li>ユーザ名とスキーマ名が異なる。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>各テーブルのレコード件数をテスト</li>
	 * </ul>
	 * 
	 */
	@Test
	@TestDBPattern(testCase = "another_schema_general", testDb = { TestDB.db2, TestDB.sqlserver })
	public void testBasicGeneralAnotherSchemaExist() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
			ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);
			
			// 一旦データベースをきれいにする。
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);

			// テスト用ダンプjarをインストール
			RepositorySystem rs = this.lookup(RepositorySystem.class);
			Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
			artifact.setFile(new File(getTestCaseDBPath(mf) + "/gsp-test-testdata-1.0.0.jar"));
			installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

			// スキーマを事前に作成
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// スキーマが既に存在する状態で。
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
	 * ＤＢの基本機能を有するダンプデータのインポートテスト（ユーザーとスキーマ存在しない、ユーザ名≠スキーマ名）、汎用モード。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>実行時にスキーマが存在しないパターン。</li>
	 * <li>ユーザ名とスキーマ名が異なる。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>各テーブルのレコード件数をテスト</li>
	 * </ul>
	 */
	@Test
	@TestDBPattern(testCase = "another_schema_general", testDb = { TestDB.db2, TestDB.sqlserver })
	public void testBasicGeneralUserAnotherSchemaNonExist() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
			ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);

			// テスト用ダンプjarをインストール
			RepositorySystem rs = this.lookup(RepositorySystem.class);
			Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
			artifact.setFile(new File(getTestCaseDBPath(mf) + "/gsp-test-testdata-1.0.0.jar"));
			installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

			// スキーマのドロップ
			DBTestUtil.dropSchema(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
					mojo.driver, mf.testDb);
			
			// ユーザのドロップ
            if(!mf.testDb.equals(TestDB.db2)) {
    			DBTestUtil.dropUser(mojo.schema, mojo.user, mojo.password, mojo.adminUser, mojo.adminPassword, mojo.url,
    					mojo.driver, mf.testDb);
            }

			// スキーマが存在しない状態で。
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
	 * 汎用モードにおけるデータ型の入出力のテスト。 
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>DDLの実行とデータのロードを行う。（テスト用インプット準備）</li>
	 * <li>export-schema(汎用)を実行する。</li>
	 * <li>import-schema（汎用）を実行する。</li>
	 * <li>export-schema(汎用)を実行し、出力されたCSVデータファイルがインプットのデータファイルと同一であることを検証する。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>export-schema(汎用)を実行し、出力されたCSVデータファイルがインプットのデータファイルと同一である。</li>
	 * </ul>
	 */
	@Test
	@TestDBPattern(testCase = "type", testDb = { TestDB.h2, TestDB.mysql, TestDB.postgresql, TestDB.oracle, TestDB.db2, TestDB.sqlserver })
	public void testTypeGeneral() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// execute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// load-data
			LoadDataMojoTest loadTest = new LoadDataMojoTest();
			loadTest.setUp();
			LoadDataMojo loadMojo = loadTest.lookupConfiguredMojo(pom, LOAD_DATA, mf.testDb);
			loadMojo.execute();
			
			// export-schema
			ExportSchemaMojoTest exportTest = new ExportSchemaMojoTest();
			exportTest.setUp();
			ExportSchemaMojo exportMojo = exportTest.lookupConfiguredMojo(pom, EXPORT_SCHEMA, mf.testDb);

			exportMojo.execute();
			
			File exportDir = exportMojo.outputDirectoryTemp;

			// currentProjectのセットアップ。 ImportSchemaMojoのセットアップ。
			ImportSchemaMojo mojo = this.lookupConfiguredMojo(pom, IMPORT_SCHEMA, mf.testDb);

			// テスト用ダンプjarをインストール
			RepositorySystem rs = this.lookup(RepositorySystem.class);
			Artifact artifact = rs.createArtifact("jp.co.tis.gsp", "gsp-testdata", "1.0.0", "jar");
			artifact.setFile(new File(getTestCaseDBPath(mf) + "/dump/gsp-test-testdata-1.0.0.jar"));
			installArtifactToTestRepo(artifact, currentProject.getProjectBuildingRequest().getLocalRepository());

			// テスト：import-schema
			mojo.execute();
			
			// 検証フェーズ

			// export-schema
			ExportSchemaMojo exportMojo2 = exportTest.lookupConfiguredMojo(pom, EXPORT_SCHEMA, mf.testDb);
			exportMojo2.execute();

			// 検証
			Entry actualFiles = DirUtil.collectEntry(exportDir.getAbsolutePath() + FS + "dataDirectory");
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "data");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}
}
