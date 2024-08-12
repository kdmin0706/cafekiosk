# ☕ cafekiosk
- 음료와 베이커리 주문 관리를 위한 통합 키오스크 서비스입니다.

## ⚙ Tech Stack
- Language : Java
- Build : Gradle
- JDK : JDK 17
- DataBase : H2 Database
- Library : Google guava

## 🎯 ERD
![img.png](docs/img/erd.png)

## 💻 프로그램 기능 및 설계

### 주문 기능
- 주문 목록에 음료 추가 / 삭제 기능
- 주문 목록 전체 지우기
- 주문 목록 총 금액 계산하기
- 주문 생성하기 
- 한 종류의 음료 여러 잔을 한 번에 담는 기능
- 가게 운영 시간(10:00 ~ 22:00) 외에는 주문을 생성할 수 없다.

### 주문 처리 기능
- 상품 번호 리스트를 받아 주문 생성하기
- 주문은 주문 상태, 주문 등록 시간을 가진다.
- 주문의 총 금액을 계산할 수 있어야한다.
- 주문 생성 시 재고 확인 및 개수 차감 후 생성하기
- 재고는 상품 번호를 가진다.
- 재고와 관련 있는 상품 타입은 병 음료, 베이커리다.

### 상품 관리 기능
- 키오스크 주문을 위한 상품 후보 리스트 조회하기
- 상품의 판매 상태 : 판매중, 판매보류, 판매중지
  - 판매중, 판매보류인 상태의 상품을 화면에 보여준다
- id, 상품번호, 상품타입, 판매 상태, 상품 이름, 가격

### 상품 등록 기능
- 관리자 페이지에서 신규 상품을 등록할 수 있다.
- 상품명, 상품 타입, 판매 상태, 가격 등을 입력받는다.
