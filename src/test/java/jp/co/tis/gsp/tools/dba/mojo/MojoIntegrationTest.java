package jp.co.tis.gsp.tools.dba.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author kawasima
 */
public class MojoIntegrationTest {

    @Test
    public void testExport() throws Exception {
        ExportSchemaMojo mojo = new ExportSchemaMojo();
        mojo.adminUser = "sa";
        mojo.user = "sa";
        mojo.schema = "db1";
        mojo.outputDirectory = new File("target/dump");
        mojo.driver = org.h2.Driver.class.getName();
        mojo.url = "jdbc:h2:file:./target/db1";

        mojo.jarArchiver = new JarArchiver();

        DriverManagerUtil.registerDriver(mojo.driver);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(mojo.url, mojo.user, null);
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS test (id integer, name varchar(100))");
            stmt.execute("INSERT INTO test VALUES(1, 'abc')");
            conn.commit();
        } catch (Exception ex) {
            Assert.fail(ex.toString());
        } finally {
            ConnectionUtil.close(conn);
        }
        mojo.execute();
    }

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    @Ignore("Oracleでしか動かないので")
    public void testGenerateEntity() throws MojoFailureException, MojoExecutionException, IOException {
        GenerateEntity mojo = new GenerateEntity();
        mojo.adminUser = "system";
        mojo.adminPassword = "oracle";
        mojo.user = "system";
        mojo.password = "oracle";
        mojo.diconDir = new File("target/classes");
        mojo.schema = "ARCHETYPE_DATABASE_SCHEMA";
        mojo.driver = "oracle.jdbc.driver.OracleDriver";
        mojo.url = "jdbc:oracle:thin:@localhost:1521/xe";
        mojo.rootPackage = "com.example";
        mojo.useAccessor = true;
        mojo.ignoreTableNamePattern = "(SCHEMA_INFO|.*\\$.*)";
        mojo.javaFileDestDir = temp.newFolder();
        mojo.execute();

        File pkg  = new File(mojo.javaFileDestDir, "com/example/");
        assertThat(pkg.exists(), is(true));


    }
}
