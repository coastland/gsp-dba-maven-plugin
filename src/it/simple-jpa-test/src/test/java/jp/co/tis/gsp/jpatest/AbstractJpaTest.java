package jp.co.tis.gsp.jpatest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractJpaTest {

	private static EntityManagerFactory entityManagerFactory;
	protected static EntityManager entityManager;
	protected static EntityTransaction tran;

	@BeforeClass
	public static void initEntityManager() {

		// invokerプラグインでキック
		// surefireプラグインで「GSP_DB」をセット
		String db = System.getProperty("GSP_DB");
		
		entityManagerFactory = Persistence.createEntityManagerFactory("persistence-" + db);
		entityManager = entityManagerFactory.createEntityManager();
	}

	@AfterClass
	public static void closeEntityManager() {
		entityManager.close();
		entityManagerFactory.close();
	}

	@Before
	public void setUp() {
		tran = entityManager.getTransaction();
		tran.begin();
	}

	@After
	public void after() {
		tran.rollback();
	}
}
