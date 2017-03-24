package jp.co.tis.gsp.tools.dba.mojo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import jp.co.tis.gsp.test.util.DirUtil;
import jp.co.tis.gsp.test.util.Entry;
import jp.co.tis.gsp.test.util.MojoTestFixture;
import jp.co.tis.gsp.test.util.TestDB;
import jp.co.tis.gsp.test.util.TestDBPattern;
import org.apache.maven.plugin.MojoExecutionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GenerateEntityTest extends AbstractDdlMojoTest<GenerateEntity> {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	/**
	 * テーブルのデータ型を網羅するEntity生成テスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>各DBごとにサポートするデータ型を網羅したテーブルを元にEntityを生成するテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
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

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}

    /**
     * テーブルのデータ型を網羅するDoma用のEntity生成テスト。
     *
     * <h4>検証内容</h4>
     * <ul>
     * <li>各DBごとにサポートするデータ型を網羅したテーブルを元にEntityを生成するテスト。</li>
     * </ul>
     * <h4>検証結果</h4>
     * <ul>
     * <li>期待値Entityフォルダと同一であること。</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test
    @TestDBPattern(testCase = "typeWithDoma", testDb = { TestDB.h2 })
    public void testTypeWithDoma() throws Exception {

        // 指定されたケース及びテスト対象のDBだけループ
        for (MojoTestFixture mf : mojoTestFixtureList) {

            // テストケース対象プロジェクトのpom.xmlを取得
            File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

            // 先にインプットになるテーブル定義を作成するためexecute-ddl
            ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
            ddlTest.setUp();
            ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
            ddlMojo.execute();

            // pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
            GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
            mojo.execute();

            // 検証
            String actualPath = mojo.javaFileDestDir.getAbsolutePath();
            Entry actualFiles = DirUtil.collectEntry(actualPath);
            Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
            assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

        }
    }

	/**
	 * ＤＢの基本機能を生成するＤＤＬの実行テスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>下記のＤＢ基本機能を元にEntityを生成するテスト。</li>
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
	 * <li>期待値Entityフォルダと同一であること。</li>
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

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}

    /**
     * ＤＢの基本機能を生成するＤＤＬの実行テスト。
     *
     * <h4>検証内容</h4>
     * <ul>
     * <li>下記のＤＢ基本機能を元にDomaのEntityを生成するテスト。</li>
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
     * <li>期待値Entityフォルダと同一であること。</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test
    @TestDBPattern(testCase = "basicWithDoma", testDb = { TestDB.h2 })
    public void testBasicWithDoma() throws Exception {

        // 指定されたケース及びテスト対象のDBだけループ
        for (MojoTestFixture mf : mojoTestFixtureList) {

            // テストケース対象プロジェクトのpom.xmlを取得
            File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

            // 先にインプットになるテーブル定義を作成するためexecute-ddl
            ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
            ddlTest.setUp();
            ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
            ddlMojo.execute();

            // pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
            GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
            mojo.execute();

            // 検証
            String actualPath = mojo.javaFileDestDir.getAbsolutePath();
            Entry actualFiles = DirUtil.collectEntry(actualPath);
            Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
            assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
        }
    }

	/**
	 * ユーザ名≠スキーマ名のテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>スキーマ名とユーザ名が異なる時のEntity生成テスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
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

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}

	/**
	 * パラメータ：ignoreTableNamePatternのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>ignoreTableNamePatternで指定したパターンに一致したEntityが生成されていないことをテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "ignoreTableNamePattern", testDb = { TestDB.h2 })
	public void testIgnoreTableNamePattern() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}

	/**
	 * パラメータ：useAccessorのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>useAccessorをfalseにしてテスト。getter,setterが生成されないこと。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "useAccessor", testDb = { TestDB.h2 })
	public void testUseAccessor() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}

	/**
	 * パラメータ：entityTemplateのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>entityTemplateで指定したテンプレートがEntity生成で仕様されていること。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "entityTemplate", testDb = { TestDB.h2 })
	public void testEntityTemplate() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}

	/**
	 * パラメータ：dialectClassName のテスト
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>dialectClassNameにgspのPostgresqlDialectで操作していないorg.seasar.extension.jdbc.dialect.Postgre81Dialectを指定してテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "dialectClassName", testDb = { TestDB.postgresql })
	public void testDialectClassName() throws Exception {

		for (MojoTestFixture mf : mojoTestFixtureList) {
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}

	/**
	 * パラメータ：genDialectClassName のテスト
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>genDialectClassNameで指定したクラスが利用されていることをテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "genDialectClassName", testDb = { TestDB.h2 })
	public void testGenDialectClassName() throws Exception {

		for (MojoTestFixture mf : mojoTestFixtureList) {
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));
		}
	}

	/**
	 * パラメータ：diconDirのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>diconDirのパスを指定してテストを実行。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "diconDir", testDb = { TestDB.h2 })
	public void testDiconDir() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}

	/**
	 * パラメータ：diconDirの異常系テスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>diconDirに無効なパスを指定する。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>エラーとなる。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "diconDir_error", testDb = { TestDB.h2 })
	public void testDiconDirError() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			expected.expect(MojoExecutionException.class);
			expected.expectMessage("Can't generate dicon file.");

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();
		}
	}

	/**
	 * パラメータ：templateFilePrimaryDirのテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>templateFilePrimaryDirを指定してテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "templateFilePrimaryDir", testDb = { TestDB.h2 })
	public void testＴemplateFilePrimaryDir() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}

	/**
	 * Viewのエンティティ生成のテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>複雑なView定義を元にEntityを生成するテスト。</li>
	 * <li>テーブル結合やWHERE、GROUP BY、ORDER BYを使う。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "view", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.sqlserver,
	        TestDB.mysql })
	public void testView() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}

	/**
	 * シナリオ外テストケース。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>日本語名テーブルのテスト。</li>
	 * </ul>
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityフォルダと同一であること。</li>
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

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}

	/**
	 * allocationSizeを指定した場合のテスト。
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>oracleのみでテスト。</li>
	 * <li>allocationSizeに100を指定。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "allocationSize", testDb = { TestDB.oracle })
	public void testAllocationSize() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}
	

	/**
	 * @Versionアノテーションの付与テスト
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>VersionColumnNamePatternが未指定の場合。カラム名「VERSION_NO」に@Versionアノテーションが付与されるかを検証。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "nonVersionColumnNamePattern", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.h2,
	        TestDB.sqlserver, TestDB.mysql })
	public void testNonVersionColumnNamePattern() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();
			
			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}
	

	/**
	 * @Versionアノテーションの付与テスト
	 * 
	 * <h4>検証内容</h4>
	 * <ul>
	 * <li>versionColumnNamePatternに「HOG.」を指定した場合</li>
	 * <li>カラム名「HOGE」に@Versionアノテーションが付与されるかを検証。</li>
	 * </ul>
	 * 
	 * <h4>検証結果</h4>
	 * <ul>
	 * <li>期待値Entityファイルと同一であること。</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestDBPattern(testCase = "customVersionColumnNamePattern", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.h2,
	        TestDB.sqlserver, TestDB.mysql })
	public void testCustomVersionColumnNamePattern() throws Exception {

		// 指定されたケース及びテスト対象のDBだけループ
		for (MojoTestFixture mf : mojoTestFixtureList) {

			// テストケース対象プロジェクトのpom.xmlを取得
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

			// 先にインプットになるテーブル定義を作成するためexecute-ddl
			ExecuteDdlMojoTest ddlTest = new ExecuteDdlMojoTest();
			ddlTest.setUp();
			ExecuteDdlMojo ddlMojo = ddlTest.lookupConfiguredMojo(pom, EXECUTE_DDL, mf.testDb);
			ddlMojo.execute();

			// pom.xmlより指定ゴールのMojoを取得し実行。Mavenプロファイルを指定する(DB)
			GenerateEntity mojo = this.lookupConfiguredMojo(pom, GENERATE_ENTITY, mf.testDb);
			mojo.execute();
			
			assertTrue(true);

			// 検証
			String actualPath = mojo.javaFileDestDir.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "output");
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}
	}
}
