package jp.co.tis.gsp.test.util.dialect;

import org.seasar.extension.jdbc.gen.internal.dialect.H2GenDialect;

public class ExtendedH2TestGenDialect extends H2GenDialect {

	public ExtendedH2TestGenDialect() {
		super();
		columnTypeMap.put("date", ExtendedH2ColumnType.DATE);
	}

	public static class ExtendedH2ColumnType extends H2ColumnType {
		private static ExtendedH2ColumnType DATE = new ExtendedH2ColumnType("date", String.class);

		public ExtendedH2ColumnType(String dataType, Class<?> attributeClass) {
			super(dataType, attributeClass);
		}
	}

}