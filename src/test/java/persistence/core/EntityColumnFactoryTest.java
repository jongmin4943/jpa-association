package persistence.core;

import domain.FixtureAssociatedEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class EntityColumnFactoryTest {

    @Test
    @DisplayName("EntityColumnFactory 로 EntityFieldColumn 을 생성 할 수 있다.")
    void createFieldTest() throws NoSuchFieldException {
        final Class<FixtureAssociatedEntity.Order> orderClass = FixtureAssociatedEntity.Order.class;

        final EntityColumn actual = EntityColumnFactory.create(orderClass.getDeclaredField("orderNumber"), "orders");

        assertThat(actual).isInstanceOf(EntityFieldColumn.class);
    }

    @Test
    @DisplayName("EntityColumnFactory 로 EntityIdColumn 을 생성 할 수 있다.")
    void createIdTest() throws NoSuchFieldException {
        final Class<FixtureAssociatedEntity.Order> orderClass = FixtureAssociatedEntity.Order.class;

        final EntityColumn actual = EntityColumnFactory.create(orderClass.getDeclaredField("id"), "orders");

        assertThat(actual).isInstanceOf(EntityIdColumn.class);
    }

    @Test
    @DisplayName("EntityColumnFactory 로 EntityOneToManyColumn 을 생성 할 수 있다.")
    void createOneToManyTest() throws NoSuchFieldException {
        final Class<FixtureAssociatedEntity.Order> orderClass = FixtureAssociatedEntity.Order.class;

        final EntityColumn actual = EntityColumnFactory.create(orderClass.getDeclaredField("orderItems"), "orders");

        assertThat(actual).isInstanceOf(EntityOneToManyColumn.class);
    }

    @Test
    @DisplayName("EntityColumnFactory 로 EntityManyToOneColumn 을 생성 할 수 있다.")
    void createManyToOneTest() throws NoSuchFieldException {
        final Class<FixtureAssociatedEntity.City> orderClass = FixtureAssociatedEntity.City.class;

        final EntityColumn actual = EntityColumnFactory.create(orderClass.getDeclaredField("country"), "city");

        assertThat(actual).isInstanceOf(EntityManyToOneColumn.class);
    }
}
