package sample.cafekiosk.spring.api.service.order;


import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.service.order.request.OrderCreateServiceRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

@Transactional
@Service
@RequiredArgsConstructor
public class OrderService {

  public static final String REDISSON_LOCK_KEY = "Lock:";
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;
  private final StockRepository stockRepository;

  private final RedissonClient redissonClient;

  public OrderResponse createOrder(OrderCreateServiceRequest request, LocalDateTime registeredDateTime) {
    List<String> productNumbers = request.getProductNumbers();
    List<Product> products = findProductsBy(productNumbers);

    deductStockQuantities(products);

    Order order = Order.create(products, registeredDateTime);
    Order savedOrder = orderRepository.save(order);
    return OrderResponse.of(savedOrder);
  }

  private void deductStockQuantities(List<Product> products) {
    List<String> stockProductNumbers = extractStockProductNumbers(products);

    Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);
    Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

    // 재고 차감 시도
    for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
      RLock lock = redissonClient.getLock(REDISSON_LOCK_KEY + stockProductNumber);
      boolean isLock = false;

      try {
        isLock = lock.tryLock(10, 1, TimeUnit.SECONDS);
        if (!isLock) {
          throw new IllegalArgumentException("재고 차감 중 락을 획득할 수 없습니다.");
        }
        Stock stock = stockMap.get(stockProductNumber);
        int quantity = productCountingMap.get(stockProductNumber).intValue();

        if (stock.isQuantityLessThan(quantity)) {
          throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
        }

        stock.deductQuantity(quantity);

      } catch (InterruptedException e) {
        throw new RuntimeException("인터럽트 예외가 발생하였습니다.");
      } finally {
        if (isLock) {
          lock.unlock();
        }
      }
    }
  }

  private List<Product> findProductsBy(List<String> productNumbers) {
    List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);
    Map<String, Product> productMap = products.stream()
        .collect(Collectors.toMap(Product::getProductNumber, p -> p));

    return productNumbers.stream()
        .map(productMap::get)
        .toList();
  }

  private static List<String> extractStockProductNumbers(List<Product> products) {
    // 재고 차감 체크가 필요한 상품들 filtering
    return products.stream()
        .filter(product -> ProductType.containsStockType(product.getProductType()))
        .map(Product::getProductNumber)
        .toList();
  }

  private Map<String, Stock> createStockMapBy(List<String> stockProductNumbers) {
    // 재고 엔티티 조회
    List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
    return stocks.stream()
        .collect(Collectors.toMap(Stock::getProductNumber, stock -> stock));
  }

  private static Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
    // 상품별 counting
    return stockProductNumbers.stream()
        .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
  }

}
