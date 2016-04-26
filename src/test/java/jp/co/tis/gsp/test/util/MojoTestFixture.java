package jp.co.tis.gsp.test.util;

/**
 * 各テストメソッドでMojoを生成するためのテストケース・DBパターン情報を保持するクラス.
 */
public class MojoTestFixture {
	public MojoTestFixture(Class<?> testClass, String caseName, TestDB testDb) {
		this.testClass = testClass;
		this.caseName = caseName;
		this.testDb = testDb;
	}

	/** 実行時のテストクラスの情報 */
	public Class<?> testClass;

	/** ケース名 */
	public String caseName;

	/** テスト対象DBの情報 */
	public TestDB testDb;
}
