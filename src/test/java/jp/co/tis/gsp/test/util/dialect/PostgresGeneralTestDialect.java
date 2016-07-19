package jp.co.tis.gsp.test.util.dialect;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;

import jp.co.tis.gsp.tools.dba.dialect.ExtendedPostgreGenDialect;
import jp.co.tis.gsp.tools.dba.dialect.PostgresqlDialect;
import jp.co.tis.gsp.tools.dba.dialect.param.ExportParams;
import jp.co.tis.gsp.tools.dba.dialect.param.ImportParams;

public class PostgresGeneralTestDialect extends PostgresqlDialect {

    private static final List<String> USABLE_TYPE_NAMES = new ArrayList<String>();

    static {
        USABLE_TYPE_NAMES.add("int8");
        USABLE_TYPE_NAMES.add("bigserial");
        USABLE_TYPE_NAMES.add("bool");
        USABLE_TYPE_NAMES.add("bpchar");
        USABLE_TYPE_NAMES.add("date");
        USABLE_TYPE_NAMES.add("float8");
        USABLE_TYPE_NAMES.add("int4");
        USABLE_TYPE_NAMES.add("numeric");
        USABLE_TYPE_NAMES.add("float4");
        USABLE_TYPE_NAMES.add("serial");
        USABLE_TYPE_NAMES.add("int2");
        USABLE_TYPE_NAMES.add("text");
        USABLE_TYPE_NAMES.add("timestamp");
        USABLE_TYPE_NAMES.add("varchar");
    }

    public PostgresGeneralTestDialect() {
        GenDialectRegistry.deregister(org.seasar.extension.jdbc.dialect.PostgreDialect.class);
        GenDialectRegistry.register(org.seasar.extension.jdbc.dialect.PostgreDialect.class,
                new ExtendedPostgreGenDialect());
    }

    public void exportSchema(ExportParams params) throws MojoExecutionException {
        exportSchemaGeneral(params);
    }

    public void importSchema(ImportParams params) throws MojoExecutionException {
        importSchemaGeneral(params);
    }
}
