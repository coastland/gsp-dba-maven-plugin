package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * SEQUENCE_TEST
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "PUBLIC", name = "SEQUENCE_TEST")
public class SequenceTest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** SEQUENCE_TEST_ID */
    private Long sequenceTestId;

    /** SUB_ID_1 */
    private Long subId1;

    /** SUB_ID_2 */
    private Long subId2;
    /**
     * SEQUENCE_TEST_IDを返します。
     *
     * @return SEQUENCE_TEST_ID
     */
    @Id
    @GeneratedValue(generator = "PUBLIC.SEQUENCE_TEST_ID_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "PUBLIC.SEQUENCE_TEST_ID_SEQ", sequenceName = "PUBLIC.SEQUENCE_TEST_ID_SEQ", initialValue = 1, allocationSize = 1)
    @Column(name = "SEQUENCE_TEST_ID", precision = 64, nullable = false, unique = true)
    public Long getSequenceTestId() {
        return sequenceTestId;
    }

    /**
     * SEQUENCE_TEST_IDを設定します。
     *
     * @param sequenceTestId SEQUENCE_TEST_ID
     */
    public void setSequenceTestId(Long sequenceTestId) {
        this.sequenceTestId = sequenceTestId;
    }
    /**
     * SUB_ID_1を返します。
     *
     * @return SUB_ID_1
     */
    @Column(name = "SUB_ID_1", precision = 64, nullable = false, unique = false)
    public Long getSubId1() {
        return subId1;
    }

    /**
     * SUB_ID_1を設定します。
     *
     * @param subId1 SUB_ID_1
     */
    public void setSubId1(Long subId1) {
        this.subId1 = subId1;
    }
    /**
     * SUB_ID_2を返します。
     *
     * @return SUB_ID_2
     */
    @Column(name = "SUB_ID_2", precision = 64, nullable = false, unique = false)
    public Long getSubId2() {
        return subId2;
    }

    /**
     * SUB_ID_2を設定します。
     *
     * @param subId2 SUB_ID_2
     */
    public void setSubId2(Long subId2) {
        this.subId2 = subId2;
    }
}
