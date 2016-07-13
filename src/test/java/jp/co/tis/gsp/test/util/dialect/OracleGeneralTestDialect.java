package jp.co.tis.gsp.test.util.dialect;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;

import jp.co.tis.gsp.tools.dba.dialect.ExtendedOracleGenDialect;
import jp.co.tis.gsp.tools.dba.dialect.OracleDialect;
import jp.co.tis.gsp.tools.dba.dialect.param.ExportParams;
import jp.co.tis.gsp.tools.dba.dialect.param.ImportParams;

public class OracleGeneralTestDialect extends OracleDialect {
    private static final List<String> USABLE_TYPE_NAMES = new ArrayList<String>();

    static {
        USABLE_TYPE_NAMES.add("CHAR");
        USABLE_TYPE_NAMES.add("DATE");
        USABLE_TYPE_NAMES.add("LONG");
        USABLE_TYPE_NAMES.add("FLOAT");
        USABLE_TYPE_NAMES.add("NCHAR");
        USABLE_TYPE_NAMES.add("NUMBER");
        USABLE_TYPE_NAMES.add("NVARCHAR2");
        USABLE_TYPE_NAMES.add("VARCHAR2");
    }

    public OracleGeneralTestDialect() {
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
