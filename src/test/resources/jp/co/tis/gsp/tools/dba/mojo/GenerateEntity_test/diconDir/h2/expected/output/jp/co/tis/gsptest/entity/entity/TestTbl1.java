package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * テストテーブル1
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "PUBLIC", name = "TEST_TBL1")
public class TestTbl1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TEST_TBL1_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEST_TBL1_ID", precision = 64, nullable = false, unique = true)
    public Long testTbl1Id;

    /** TEST_NAME */
    @Column(name = "TEST_NAME", length = 30, nullable = true, unique = false)
    public String testName;
}
