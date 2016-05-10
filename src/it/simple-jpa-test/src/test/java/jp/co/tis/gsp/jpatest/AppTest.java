package jp.co.tis.gsp.jpatest;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import jp.co.tis.gsptest.entity.Customer;
import jp.co.tis.gsptest.entity.OrderDetail;
import jp.co.tis.gsptest.entity.Product;
import jp.co.tis.gsptest.entity.TestOrder;

public class AppTest extends AbstractJpaTest {

	/**
	 * Hibernate JPAを使った簡単なテスト.
	 * 
	 * 現状、GSPが「CascadeType.*」属性付与をサポートしていないため、アチコチでpersist()したり、関連要素のid属性に値セットしたりしてます。
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJpa() throws Exception {

		// 顧客の登録
		Customer customer1 = new Customer();
		customer1.setCustomerName("顧客１");
		customer1.setAge(20);

		entityManager.persist(customer1);
		entityManager.flush();

		Customer customer2 = new Customer();
		customer2.setCustomerName("顧客２");
		customer2.setAge(30);

		entityManager.persist(customer2);
		entityManager.flush();

		Customer customer3 = new Customer();
		customer3.setCustomerName("顧客３");
		customer3.setAge(30);

		entityManager.persist(customer3);
		entityManager.flush();

		// 商品の登録
		Product product1 = new Product();
		product1.setProductName("商品１");

		entityManager.persist(product1);
		entityManager.flush();

		Product product2 = new Product();
		product1.setProductName("商品２");

		entityManager.persist(product2);
		entityManager.flush();

		Product product3 = new Product();
		product3.setProductName("商品３");

		entityManager.persist(product3);
		entityManager.flush();

		// 注文登録
		TestOrder order = new TestOrder();
		order.setCustomer(customer1);
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		order.setOrderDate(df.parse("2011/01/01"));
		entityManager.persist(order); // @CascadeType.*

// @@@@@ ：GSPが「CascadeType.ALL」属性の付与に対応していないので個別に取得 @@@@@
		OrderDetail od1 = new OrderDetail();
		od1.setTestOrder(order);
		od1.setProduct(product1);
		od1.setQuantity(20);
		od1.setUnitPrice(300);
		entityManager.persist(od1);
		entityManager.flush();
		entityManager.refresh(od1);
		
		OrderDetail od2 = new OrderDetail();
		od2.setTestOrder(order);
		od2.setProduct(product2);
		od2.setQuantity(30);
		od2.setUnitPrice(1100);
		entityManager.persist(od2);
		entityManager.flush();
		entityManager.refresh(od2);
		
		List<OrderDetail> orderList = new ArrayList<OrderDetail>();
		orderList.add(od1);
		orderList.add(od2);
		order.setOrderDetailList(orderList);
		
		entityManager.persist(order);
		entityManager.flush();
		entityManager.refresh(order);
		
		
		List<TestOrder> ordersOfQuery = entityManager.createQuery("SELECT T FROM " + TestOrder.class.getSimpleName() + " T").getResultList();
		assertThat(ordersOfQuery.size(), is(1));
		
		long assertOrderId = order.getOrderId();
		TestOrder findTestOrder = entityManager.find(TestOrder.class, assertOrderId);
		Assert.assertNotNull(findTestOrder);
		
		assertThat(findTestOrder.getOrderId(), is(assertOrderId));
		assertThat(findTestOrder.getCustomerId(), is(customer1.getCustomerId()));
		assertThat(findTestOrder.getOrderDate(), is(df.parse("2011/01/01")));
		
		List<OrderDetail> odList = findTestOrder.getOrderDetailList();
		
		assertThat(odList.size(), is(2));
		
		assertThat(odList.get(0).getProductId(), is(product1.getProductId()));
		assertThat(odList.get(1).getProductId(), is(product2.getProductId()));
		
		assertThat(odList.get(0).getQuantity(), is(20));
		assertThat(odList.get(1).getQuantity(), is(30));
		
		assertThat(odList.get(0).getUnitPrice(), is(300));
		assertThat(odList.get(1).getUnitPrice(), is(1100));
		
		findTestOrder.setOrderDate(df.parse("2012/02/02"));
		odList.get(0).setUnitPrice(900);

		entityManager.persist(findTestOrder);
		
		long assertOrderId2 = findTestOrder.getOrderId();
		TestOrder findTestOrder2 = entityManager.find(TestOrder.class, assertOrderId2);
		assertThat(findTestOrder2.getOrderDate(), is(df.parse("2012/02/02")));
		assertThat(findTestOrder2.getOrderDetailList().get(0).getUnitPrice(), is(900));
		
		entityManager.remove(findTestOrder2);
		
		TestOrder findTestOrder3 = entityManager.find(TestOrder.class, assertOrderId2);
		Assert.assertNull(findTestOrder3);

	}

}
