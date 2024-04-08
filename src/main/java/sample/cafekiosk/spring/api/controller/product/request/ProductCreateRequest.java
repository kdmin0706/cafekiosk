package sample.cafekiosk.spring.api.controller.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

  @NotNull(message = "상품 타입은 필수입니다.")
  private ProductType productType;

  @NotNull(message = "상품 판매상태는 필수입니다.")
  private ProductSellingStatus sellingStatus;

  @NotBlank(message = "상품 이름은 필수입니다.")
  private String name;

  @Positive(message = "상품 가격은 양수여야 합니다.")
  private int price;

  @Builder
  public ProductCreateRequest(ProductType productType, ProductSellingStatus sellingStatus,
      String name, int price) {
    this.productType = productType;
    this.sellingStatus = sellingStatus;
    this.name = name;
    this.price = price;
  }

  public ProductCreateServiceRequest toServiceRequest() {
    return ProductCreateServiceRequest.builder()
        .productType(productType)
        .sellingStatus(sellingStatus)
        .name(name)
        .price(price)
        .build();
  }
}
