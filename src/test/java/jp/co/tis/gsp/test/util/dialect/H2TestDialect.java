package jp.co.tis.gsp.test.util.dialect;

import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;

import jp.co.tis.gsp.tools.dba.dialect.H2Dialect;

public class H2TestDialect extends H2Dialect {

	public H2TestDialect() {
		GenDialectRegistry.register(jp.co.tis.gsp.test.util.dialect.MockDialect.class, new ExtendedH2TestGenDialect());
	}

}