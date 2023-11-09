# jpa-association

## Association

### 1단계 - OneToMany (FetchType.EAGER)
```java
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;
}


@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;
}
```
> 하나의 주문(Order)에 여러 개의 주문 아이템(OrderItem)이 속할 수 있는 경우를 생각해보겠습니다.
이때, 주문과 주문 아이템은 One-to-Many 단방향 관계를 가지게 됩니다.
즉, 하나의 주문은 여러 개의 주문 아이템을 가질 수 있지만, 각 주문 아이템은 어떤 주문에 속해 있는지 알 수 없습니다.

- 요구사항 1 - Join Query 만들기

임의의 데이터를 넣어 데이터를 가져와보자 - Sql 쿼리문을 수정해보자
```java
public class CustomSelect {

}
```
- [x] @OneToMany(fetch) 정보를 Metadata 에 저장한다.
- [x] @JoinColumn 정보를 Metadata 에 저장한다.
- [x] @OneToMany Generic 클래스 정보를 Metadata 에 저장한다.
- [x] Entity `Order` 와 `OrderItem` 의 Metadata 를 이용해 create 쿼리를 만든다.
<br> 목표 쿼리문 : `create table order_items (id bigint not null auto_increment,product varchar(255),quantity int,order_id bigint,foreign key(order_id) references order (id),CONSTRAINT PK_order_items PRIMARY KEY (id))`
- [x] Entity `Order` 와 `OrderItem` 의 Metadata 를 이용해 select 쿼리를 만든다.
<br> 목표 쿼리문 : `select orders.id, orders.orderNumber, order_items.id, order_items.product, order_items.quantity from orders left join order_items on orders.id = order_items.order_id where orders.id=1`

- 요구사항 2 - Join Query 를 만들어 Entity 화 해보기
<br>`FetchType.EAGER 인 경우`
- [x] RowMapper 를 left join 시에도 적용 가능하게 변경
- [x] Order 조회해서 Entity 화 및 검증

### 2단계 - LazyLoading by Proxy
- 요구사항 1 - CGLib Proxy 적용 
  - 도움 사이트 - [CGLIB를 이용한 프록시 객체 만들기](https://javacan.tistory.com/entry/114)
```java
class HelloTarget {
    public String sayHello(String name) {
        return "Hello " + name;
    }

    public String sayHi(String name) {
        return "Hi " + name;
    }

    public String sayThankYou(String name) {
        return "Thank You " + name;
    }
}
```
- [x] CGLib 의존성을 추가한다.
- [x] 위의 HelloTarget class 를 CGLib Proxy 객체로 생성한다.
- [x] HelloTarget 메서드의 결과를 CGLib Proxy 메서드를 이용해 대문자로 반환하도록 한다.
- [x] CGLib Proxy 도움 사이트를 보며 다양하게 써본다.

- 요구사항 2 - 조회 시 프록시 객체를 사용해 적용해보자
- [x] Entity 를 Load 할시 OneToManyColumn 이 Lazy 인 경우 Proxy 객체를 생성해서 넣어둔다.
- [x] OneToManyColumn 의 메서드 호출 시 load 한다.

### 3단계 - OneToMany (FetchType.LAZY)
- 요구사항 1 - CGLib Proxy 적용
- [x] 테이블에 임의의 데이터를 넣고 가져오기
- [x] Proxy 를 활용해 Lazy loading 을 구현
- [x] Query 확인
- [x] Entity 객체 만들어 보기

### 4단계 - ManyToOne (개인적 진행)
```java
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @ManyToOne
    private Country country;
}

@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
```
- 요구사항 1 - Join Query 만들기
- [x] @ManyToOne(fetch) 정보를 Metadata 에 저장한다.
- [x] @JoinColumn 정보를 Metadata 에 저장한다.
- [x] @ManyToOne 클래스 정보를 Metadata 에 저장한다.
- [x] Entity `City` 와 `Country` 의 Metadata 를 이용해 create 쿼리를 만든다.
  <br> city 목표 쿼리문 : `create table city (id bigint not null auto_increment,name varchar(255),country_id bigint,foreign key(country_id) references country (id),CONSTRAINT PK_city PRIMARY KEY (id))`
- [x] Entity `City` 와 `Country` 의 Metadata 를 이용해 select 쿼리를 만든다.
  <br> city 목표 쿼리문 : `select city.id, city.name, country.id, country.name from city left join country on country.id = city.country_id where city.id=1`

- 요구사항 2 - Join Query 를 만들어 Entity 화 해보기
- [ ] ManyToOneColumnMapper 추가 해서 Entity 화 하기
