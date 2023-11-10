package domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FixtureAssociatedEntity {

    @Entity
    public static class WithId {
        @Id
        private Long id;
    }


    @Entity
    public static class WithOneToMany {
        @Id
        private Long id;

        @OneToMany
        List<WithId> withIds;
    }

    @Entity
    public static class WithManyToOne {
        @Id
        private Long id;

        @ManyToOne
        WithId withId;
    }


    @Entity
    public static class WithOneToManyFetchTypeEAGER {
        @Id
        private Long id;

        @OneToMany(fetch = FetchType.EAGER)
        List<WithId> withIds;
    }

    @Entity
    public static class WithManyToOneFetchTypeLAZY {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        WithId withId;
    }

    @Entity
    public static class WithOneToManyJoinColumn {
        @Id
        private Long id;

        @OneToMany
        @JoinColumn(name = "join_pk")
        List<WithId> withIds;
    }

    @Entity
    public static class WithManyToOneJoinColumn {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "join_pk")
        WithId withId;

    }

    @Entity
    public static class WithTwoOneToManyColumns {
        @Id
        private Long id;

        @OneToMany
        @JoinColumn(name = "join_pk")
        List<WithId> lazyWithIds;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn
        List<WithId> eagerWithIds;
    }

    @Entity
    public static class WithTwoManyToOneColumns {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "join_pk")
        WithId lazyWithId;

        @ManyToOne
        @JoinColumn(name = "join_pk")
        WithId eagerWithId;
    }

    @Entity
    public static class WithOneToManyInsertableFalse {
        @Id
        private Long id;

        @OneToMany
        @JoinColumn(insertable = false)
        List<WithId> withIds;
    }

    @Entity
    public static class WithManyToOneInsertableFalse {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(insertable = false)
        WithId withId;
    }

    @Entity
    public static class WithOneToManyNullableFalse {
        @Id
        private Long id;

        @OneToMany
        @JoinColumn(nullable = false)
        List<WithId> withIds;
    }

    @Entity
    public static class WithManyToOneNullableFalse {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(nullable = false)
        WithId withId;
    }

    @Entity
    @Table(name = "orders")
    public static class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String orderNumber;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "order_id")
        private List<OrderItem> orderItems;

        protected Order() {
        }

        public Order(final Long id, final String orderNumber) {
            this.id = id;
            this.orderNumber = orderNumber;
        }

        public Order(final String orderNumber) {
            this(null, orderNumber);
        }

        public Long getId() {
            return id;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public List<OrderItem> getOrderItems() {
            return orderItems;
        }

        public void addOrderItem(final OrderItem orderItem) {
            if(Objects.isNull(this.orderItems)) {
                this.orderItems = new ArrayList<>();
            }
            this.orderItems.add(orderItem);
        }
    }

    @Entity
    @Table(name = "order_items")
    public static class OrderItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String product;

        private Integer quantity;

        public OrderItem() {
        }

        public OrderItem(final Long id, final String product, final Integer quantity) {
            this.id = id;
            this.product = product;
            this.quantity = quantity;
        }

        public OrderItem(final String product, final Integer quantity) {
            this(null, product, quantity);
        }

        public Long getId() {
            return id;
        }

        public String getProduct() {
            return product;
        }

        public Integer getQuantity() {
            return quantity;
        }

    }

    @Entity
    @Table(name = "lazy_orders")
    public static class OrderLazy {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String orderNumber;

        @OneToMany(fetch = FetchType.LAZY)
        @JoinColumn(name = "lazy_order_id")
        private List<OrderLazyItem> orderItems;

        protected OrderLazy() {
        }

        public OrderLazy(final Long id, final String orderNumber) {
            this.id = id;
            this.orderNumber = orderNumber;
        }

        public OrderLazy(final String orderNumber) {
            this(null, orderNumber);
        }

        public Long getId() {
            return id;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public List<OrderLazyItem> getOrderItems() {
            return orderItems;
        }

        public void addOrderItem(final OrderLazyItem orderItem) {
            if(Objects.isNull(this.orderItems)) {
                this.orderItems = new ArrayList<>();
            }
            this.orderItems.add(orderItem);
        }
    }

    @Entity
    @Table(name = "lazy_order_items")
    public static class OrderLazyItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String product;

        private Integer quantity;

        public OrderLazyItem() {
        }

        public OrderLazyItem(final Long id, final String product, final Integer quantity) {
            this.id = id;
            this.product = product;
            this.quantity = quantity;
        }

        public OrderLazyItem(final String product, final Integer quantity) {
            this(null, product, quantity);
        }

        public Long getId() {
            return id;
        }

        public String getProduct() {
            return product;
        }

        public Integer getQuantity() {
            return quantity;
        }

    }


    @Entity
    @Table(name = "city")
    public static class City {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;


        @ManyToOne
        private Country country;


        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Country getCountry() {
            return country;
        }
    }

    @Entity
    @Table(name = "country")
    public static class Country {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    @Entity
    @Table(name = "lazy_city")
    public static class LazyCity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "lazy_country_id")
        private LazyCountry country;


        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public LazyCountry getCountry() {
            return country;
        }
    }

    @Entity
    @Table(name = "lazy_country")
    public static class LazyCountry {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
