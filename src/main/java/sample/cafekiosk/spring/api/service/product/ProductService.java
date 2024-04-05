package sample.cafekiosk.spring.api.service.product;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public List<ProductResponse> getSellingProducts() {
    List<Product> allBySellingTypeIn = productRepository.findAllBySellingStatusIn(
        ProductSellingStatus.forDisplay());

    return allBySellingTypeIn.stream().map(ProductResponse::of).toList();
  }

}