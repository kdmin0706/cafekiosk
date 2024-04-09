package sample.cafekiosk.spring.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static sample.cafekiosk.spring.domain.order.OrderStatus.INIT;
import static sample.cafekiosk.spring.domain.order.OrderStatus.PAYMENT_COMPLETED;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProductRepository productRepository;

  @Test
  @DisplayName("해당 일자에 결제 완료된 주문들을 조회한다.")
  void findOrdersBy() {
    // given
    LocalDateTime registeredDateTime = LocalDateTime.of(2024, 4, 9, 16, 0, 0);

    List<Product> products = List.of(
        createProduct("001", 1000),
        createProduct("002", 2000)
    );

    productRepository.saveAll(products);

    Order order1 = createOrder(products, PAYMENT_COMPLETED, registeredDateTime);
    Order order2 = createOrder(products, INIT, registeredDateTime);

    orderRepository.saveAll(List.of(order1, order2));

    // when
    List<Order> orders = orderRepository.findOrdersBy(
        LocalDateTime.of(2024, 4, 9, 0, 0, 0),
        LocalDateTime.of(2024, 4, 10, 0, 0, 0),
        PAYMENT_COMPLETED
    );

    // then
    assertThat(orders).hasSize(1)
        .extracting("registeredDateTime", "OrderStatus")
        .containsExactlyInAnyOrder(
            tuple(registeredDateTime, PAYMENT_COMPLETED)
        );
  }

  private static Order createOrder(List<Product> products, OrderStatus orderStatus, LocalDateTime now) {
    return Order.builder()
        .products(products)
        .orderStatus(orderStatus)
        .registeredDateTime(now)
        .build();
  }

  private Product createProduct(String productNumber, int price) {
    return Product.builder()
        .productNumber(productNumber)
        .price(price)
        .sellingStatus(SELLING)
        .name("menu")
        .build();
  }
}