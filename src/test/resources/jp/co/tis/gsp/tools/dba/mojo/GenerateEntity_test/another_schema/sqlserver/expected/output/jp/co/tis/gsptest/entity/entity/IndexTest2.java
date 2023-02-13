package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * IndexTest2エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "gspanother", name = "INDEX_TEST2", uniqueConstraints = { @UniqueConstraint(columnNames = { "SUB_ID_1", "SUB_ID_2" }) })
public class IndexTest2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** indexTest2Idプロパティ */
    private Long indexTest2Id;

    /** subId1プロパティ */
    private Long subId1;

    /** subId2プロパティ */
    private Long subId2;
    /**
     * indexTest2Idを返します。
     *
     * @return indexTest2Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INDEX_TEST2_ID", precision = 19, nullable = false, unique = true)
    public Long getIndexTest2Id() {
        return indexTest2Id;
    }

    /**
     * indexTest2Idを設定します。
     *
     * @param indexTest2Id
     */
    public void setIndexTest2Id(Long indexTest2Id) {
        this.indexTest2Id = indexTest2Id;
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
