/*
 * Copyright (C) 2015 coastland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.tis.gsp.tools.dba.s2jdbc.gen;

import static org.junit.Assert.*;

import org.junit.Test;

public class ViewAnalyzerTest extends ViewAnalyzer {

	@Test
	public void test() {
		ViewAnalyzer va = new ViewAnalyzer();
		va.parse("SELECT * FROM DEPT, EMP WHERE DEPT.dept_cd = EMP.emp_cd");
		assertFalse("ジョインはダメよ", va.isSimple());
		va.parse("SELECT * FROM DEPT LEFT OUTER JOIN EMP ON DEPT.dept_cd = EMP.emp_cd");
		assertFalse("だからジョインはダメだって", va.isSimple());

		va.parse("SELECT * FROM DEPT");
		assertTrue("ジョインがなければOK", va.isSimple());

		va.parse("select `test_data`.`business_shop`.`TEST_2` AS `BRANCH_ID`,`test_data`.`business_shop`.`TEST_3` AS `NAME`,`test_data`.`business_shop`.`CRP_GRP_COD` AS `COMPANY_GROUP_CODE`,`test_data`.`business_shop`.`TEST_4` AS `BELONG_COMPANY_CODE`,`test_data`.`business_shop`.`TEST_5` AS `BELONG_SHOP_CODE`,`test_data`.`business_shop`.`TEST_1` AS `TEST_1` from `test_data`.`business_shop` where ((`test_data`.`business_shop`.`TEST_1` = (select max(`test_table`.`TEST_1`) AS `TEST_1` from `test_data`.`business_shop` `test_table` where ((`test_table`.`TEST_1` <= curdate()) and (`test_data`.`business_shop`.`TEST_2`= `test_table`.`TEST_2`)) group by `test_table`.`TEST_2`)) and (`test_data`.`business_shop`.`TEST_2` like '6%'))");
		assertTrue("これがシンプルだってー?? まぁ確かにシンプルか…", va.isSimple());

		va.parse("select `test_data`.`user_department`.`USER_DEPARTMENT_ID` AS `USER_DEPARTMENT_CD`,`test_data`.`user_department`.`USER_DEPARTMENT_NAME` AS `USER_DEPARTMENT_NAME` from `test_data`.`user_department`");
		assertTrue("これシンプルだろ", va.isSimple());
		System.out.println(va.getTableName());
	}

}
