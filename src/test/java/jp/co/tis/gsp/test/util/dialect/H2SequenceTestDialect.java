package jp.co.tis.gsp.test.util.dialect;

import jp.co.tis.gsp.tools.dba.dialect.H2Dialect;

import javax.persistence.GenerationType;

public class H2SequenceTestDialect extends H2Dialect {

    @Override
    public GenerationType getGenerationType() {
        return GenerationType.SEQUENCE;
    }
}
