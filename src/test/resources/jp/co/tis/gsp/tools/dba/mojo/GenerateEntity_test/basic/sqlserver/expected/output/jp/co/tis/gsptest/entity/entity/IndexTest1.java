package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * IndexTest1エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "INDEX_TEST1", uniqueConstraints = { @UniqueConstraint(columnNames = { "SUB_ID_1", "SUB_ID_2" }) })
public class IndexTest1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** indexTest1Idプロパティ */
    private Long indexTest1Id;

    /** subId1プロパティ */
    private Long subId1;

    /** subId2プロパティ */
    private Long subId2;
    /**
     * indexTest1Idを返します。
     *
     * @return indexTest1Id
     */
    @Id
    @Column(name = "INDEX_TEST1_ID", precision = 19, nullable = false, unique = true)
    public Long getIndexTest1Id() {
        return indexTest1Id;
    }

    /**
     * indexTest1Idを設定します。
     *
     * @param indexTest1Id
     */
    public void setIndexTest1Id(Long indexTest1Id) {
        this.indexTest1Id = indexTest1Id;
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
