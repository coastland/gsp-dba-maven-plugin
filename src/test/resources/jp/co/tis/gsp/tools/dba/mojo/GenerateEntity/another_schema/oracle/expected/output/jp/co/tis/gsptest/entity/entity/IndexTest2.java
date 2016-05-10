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
import javax.persistence.UniqueConstraint;

/**
 * INDEX_TEST2
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "GSPANOTHER", name = "INDEX_TEST2", uniqueConstraints = { @UniqueConstraint(columnNames = { "SUB_ID_1", "SUB_ID_2" }) })
public class IndexTest2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** INDEX_TEST2_ID */
    private Long indexTest2Id;

    /** SUB_ID_1 */
    private Long subId1;

    /** SUB_ID_2 */
    private Long subId2;
    /**
     * INDEX_TEST2_IDを返します。
     *
     * @return INDEX_TEST2_ID
     */
    @Id
    @GeneratedValue(generator = "GSPANOTHER.INDEX_TEST2_ID_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "GSPANOTHER.INDEX_TEST2_ID_SEQ", sequenceName = "GSPANOTHER.INDEX_TEST2_ID_SEQ", initialValue = 1, allocationSize = 1)
    @Column(name = "INDEX_TEST2_ID", precision = 18, nullable = false, unique = true)
    public Long getIndexTest2Id() {
        return indexTest2Id;
    }

    /**
     * INDEX_TEST2_IDを設定します。
     *
     * @param indexTest2Id INDEX_TEST2_ID
     */
    public void setIndexTest2Id(Long indexTest2Id) {
        this.indexTest2Id = indexTest2Id;
    }
    /**
     * SUB_ID_1を返します。
     *
     * @return SUB_ID_1
     */
    @Column(name = "SUB_ID_1", precision = 18, nullable = false, unique = false)
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
    @Column(name = "SUB_ID_2", precision = 18, nullable = false, unique = false)
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
