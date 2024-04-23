package sample.cafekiosk.spring.api.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.HOLD;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.STOP_SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.BAKERY;
import static sample.cafekiosk.spring.domain.product.ProductType.BOTTLE;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

class ProductNumberFactoryTest extends IntegrationTestSupport {

  @Autowired
  private ProductNumberFactory productNumberFactory;

  @Autowired
  private ProductRepository productRepository;

  @AfterEach
  void tearDown() {
    productRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("상품이 하나도 없는 경우 신규 상품을 등록하면 상품번호는 001이다.")
  void createNextProductNumberWhenIsEmpty() {
    // when
    String nextProductNumber = productNumberFactory.createNextProductNumber();

    // then
    assertThat(nextProductNumber).isEqualTo("001");
  }

  @Test
  @DisplayName("신규 상품을 등록하면 최근 등록한 상품번호에 +1 증가한 값이다.")
  void createNextProductNumber() {
    // given
    Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
    Product product2 = createProduct("002", BOTTLE, HOLD, "카페라떼", 4500);
    Product product3 = createProduct("003", BAKERY, STOP_SELLING, "팥빙수", 7000);

    productRepository.saveAll(List.of(product1, product2, product3));

    // when
    String nextProductNumber = productNumberFactory.createNextProductNumber();

    // then
    assertThat(nextProductNumber).isEqualTo("004");
  }

  private Product createProduct(String productNumber, ProductType productType,
      ProductSellingStatus sellingStatus, String name, int price) {
    return Product.builder()
        .productNumber(productNumber)
        .productType(productType)
        .sellingStatus(sellingStatus)
        .name(name)
        .price(price)
        .build();
  }

}