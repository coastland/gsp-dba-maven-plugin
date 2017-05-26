package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

/**
 * INDEX_TEST3
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "PUBLIC", name = "INDEX_TEST3")
public class IndexTest3 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** INDEX_TEST3_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INDEX_TEST3_ID")
    private Long indexTest3Id;

    /** SUB_ID_1 */
    @Column(name = "SUB_ID_1")
    private Long subId1;

    /** SUB_ID_2 */
    @Column(name = "SUB_ID_2")
    private Long subId2;
    /**
     * INDEX_TEST3_IDを返します。
     *
     * @return INDEX_TEST3_ID
     */
    public Long getIndexTest3Id() {
        return indexTest3Id;
    }

    /**
     * INDEX_TEST3_IDを設定します。
     *
     * @param indexTest3Id INDEX_TEST3_ID
     */
    public void setIndexTest3Id(Long indexTest3Id) {
        this.indexTest3Id = indexTest3Id;
    }
    /**
     * SUB_ID_1を返します。
     *
     * @return SUB_ID_1
     */
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
