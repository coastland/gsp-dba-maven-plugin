package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * IndexTest3エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "INDEX_TEST3")
public class IndexTest3 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** indexTest3Idプロパティ */
    private Long indexTest3Id;

    /** subId1プロパティ */
    private Long subId1;

    /** subId2プロパティ */
    private Long subId2;
    /**
     * indexTest3Idを返します。
     *
     * @return indexTest3Id
     */
    @Id
    @Column(name = "INDEX_TEST3_ID", precision = 19, nullable = false, unique = true)
    public Long getIndexTest3Id() {
        return indexTest3Id;
    }

    /**
     * indexTest3Idを設定します。
     *
     * @param indexTest3Id
     */
    public void setIndexTest3Id(Long indexTest3Id) {
        this.indexTest3Id = indexTest3Id;
    }
    /**
     * subId1を返します。
     *
     * @return subId1
     */
    @Column(name = "SUB_ID_1", precision = 19, nullable = false, unique = false)
    public Long getSubId1() {
        return subId1;
    }

    /**
     * subId1を設定します。
     *
     * @param subId1
     */
    public void setSubId1(Long subId1) {
        this.subId1 = subId1;
    }
    /**
     * subId2を返します。
     *
     * @return subId2
     */
    @Column(name = "SUB_ID_2", precision = 19, nullable = false, unique = false)
    public Long getSubId2() {
        return subId2;
    }

    /**
     * subId2を設定します。
     *
     * @param subId2
     */
    public void setSubId2(Long subId2) {
        this.subId2 = subId2;
    }
}
