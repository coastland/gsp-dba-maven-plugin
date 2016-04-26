package jp.co.tis.gsp.test.util.dialect;

import jp.co.tis.gsp.tools.dba.dialect.H2Dialect;

public class NoPrintDialect extends H2Dialect {

	@Override
	public boolean canPrintTable() {
		return false;
	}

	@Override
	public boolean canPrintIndex() {
		return false;
	}

	@Override
	public boolean canPrintForeignKey() {
		return false;
	}

	@Override
	public boolean canPrintView() {
		return false;
	}

}