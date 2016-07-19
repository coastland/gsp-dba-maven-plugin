package jp.co.tis.gsp.test.util.dialect;

import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;

import jp.co.tis.gsp.tools.dba.dialect.ExtendedOracleGenDialect;
import jp.co.tis.gsp.tools.dba.dialect.MysqlDialect;
import jp.co.tis.gsp.tools.dba.dialect.param.ExportParams;
import jp.co.tis.gsp.tools.dba.dialect.param.ImportParams;

public class MysqlGeneralTestDialect extends MysqlDialect {

    public MysqlGeneralTestDialect() {
        GenDialectRegistry.deregister(org.seasar.extension.jdbc.dialect.OracleDialect.class);
        GenDialectRegistry.register(org.seasar.extension.jdbc.dialect.OracleDialect.class,
                new ExtendedOracleGenDialect());
    }

    public void exportSchema(ExportParams params) throws MojoExecutionException {
        exportSchemaGeneral(params);
    }

    public void importSchema(ImportParams params) throws MojoExecutionException {
        importSchemaGeneral(params);
    }
}
