package sample.cafekiosk.spring.api.controller.product;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sample.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private ProductService productService;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("신규 상품을 등록한다.")
  void createProduct() throws Exception {
    // given
    ProductCreateRequest request = ProductCreateRequest.builder()
        .productType(ProductType.HANDMADE)
        .sellingStatus(ProductSellingStatus.SELLING)
        .name("아메리카노")
        .price(4000)
        .build();

    // when
    // then
    mockMvc.perform(
        post("/api/v1/products/new")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("신규 상품을 등록할 때 상품 타입은 필수값이다.")
  void createProductWithOutType() throws Exception {
    // given
    ProductCreateRequest request = ProductCreateRequest.builder()
        .sellingStatus(ProductSellingStatus.SELLING)
        .name("아메리카노")
        .price(4000)
        .build();

    // when
    // then
    mockMvc.perform(
            post("/api/v1/products/new")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("상품 타입은 필수입니다."))
        .andExpect(jsonPath("$.data").isEmpty())
    ;
  }

  @Test
  @DisplayName("신규 상품을 등록할 때 상품 판매상태는 필수값이다.")
  void createProductWithOutSellingStatus() throws Exception {
    // given
    ProductCreateRequest request = ProductCreateRequest.builder()
        .productType(ProductType.HANDMADE)
        .name("아메리카노")
        .price(4000)
        .build();

    // when
    // then
    mockMvc.perform(
            post("/api/v1/products/new")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("상품 판매상태는 필수입니다."))
        .andExpect(jsonPath("$.data").isEmpty())
    ;
  }

  @Test
  @DisplayName("신규 상품을 등록할 때 상품 이름은 필수값이다.")
  void createProductWithOutName() throws Exception {
    // given
    ProductCreateRequest request = ProductCreateRequest.builder()
        .productType(ProductType.HANDMADE)
        .sellingStatus(ProductSellingStatus.SELLING)
        .price(4000)
        .build();

    // when
    // then
    mockMvc.perform(
            post("/api/v1/products/new")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("상품 이름은 필수입니다."))
        .andExpect(jsonPath("$.data").isEmpty())
    ;
  }

  @Test
  @DisplayName("신규 상품을 등록할 때 상품 가격은 양수이다.")
  void createProductWithZeroPrice() throws Exception {
    // given
    ProductCreateRequest request = ProductCreateRequest.builder()
        .productType(ProductType.HANDMADE)
        .sellingStatus(ProductSellingStatus.SELLING)
        .name("아메리카노")
        .price(0)
        .build();

    // when
    // then
    mockMvc.perform(
            post("/api/v1/products/new")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("상품 가격은 양수여야 합니다."))
        .andExpect(jsonPath("$.data").isEmpty())
    ;
  }

  @Test
  @DisplayName("판매 상품을 조회한다.")
  void getSellingProducts() throws Exception {
    // given
    List<ProductResponse> result = List.of();
    when(productService.getSellingProducts()).thenReturn(result);

    // when
    // then
    mockMvc.perform(
            get("/api/v1/products/selling")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("200"))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data").isArray())
    ;
  }
}