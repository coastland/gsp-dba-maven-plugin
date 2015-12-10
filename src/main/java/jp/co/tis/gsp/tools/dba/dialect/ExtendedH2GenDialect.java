package jp.co.tis.gsp.tools.dba.dialect;

import java.sql.Timestamp;

import javax.persistence.TemporalType;

import org.seasar.extension.jdbc.gen.internal.dialect.H2GenDialect;

/**
 * @author Masaya Seko
 */
public class ExtendedH2GenDialect extends H2GenDialect {

    public ExtendedH2GenDialect() {
        super();
        columnTypeMap.put("date", ExtendedH2ColumnType.DATE);

        columnTypeMap.put("timestamp", ExtendedH2ColumnType.TIMESTAMP);
        columnTypeMap.put("datetime", ExtendedH2ColumnType.DATETIME);
        columnTypeMap.put("smalldatetime", ExtendedH2ColumnType.SMALLDATETIME);

        columnTypeMap.put("boolean", ExtendedH2ColumnType.BOOLEAN);
        columnTypeMap.put("bool", ExtendedH2ColumnType.BOOL);
        columnTypeMap.put("bit", ExtendedH2ColumnType.BIT);
    }

    public static class ExtendedH2ColumnType extends H2ColumnType {
        private static ExtendedH2ColumnType DATE = new ExtendedH2ColumnType("date",
                java.sql.Date.class, false, TemporalType.DATE);

        private static ExtendedH2ColumnType TIMESTAMP = new ExtendedH2ColumnType("timestamp",
                Timestamp.class, false, TemporalType.TIMESTAMP);

        private static ExtendedH2ColumnType DATETIME = new ExtendedH2ColumnType("datetime",
                Timestamp.class, false, TemporalType.TIMESTAMP);

        private static ExtendedH2ColumnType SMALLDATETIME = new ExtendedH2ColumnType("smalldatetime",
                Timestamp.class, false, TemporalType.TIMESTAMP);

        private static ExtendedH2ColumnType BOOLEAN = new ExtendedH2ColumnType(
                "boolean", boolean.class);

        private static ExtendedH2ColumnType BOOL = new ExtendedH2ColumnType(
                "bool", boolean.class);

        private static ExtendedH2ColumnType BIT = new ExtendedH2ColumnType(
                "bit", boolean.class);

        public ExtendedH2ColumnType(String dataType, Class<?> attributeClass) {
            super(dataType, attributeClass);
        }

        public ExtendedH2ColumnType(String dataType, Class<?> attributeClass,
                boolean lob, TemporalType temporalType) {
            super(dataType, attributeClass, lob);
            super.temporalType = temporalType;
        }
    }
}
