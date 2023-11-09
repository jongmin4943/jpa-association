package persistence.core;

import domain.FixtureAssociatedEntity;
import extension.EntityMetadataExtension;
import jakarta.persistence.FetchType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(EntityMetadataExtension.class)
class EntityManyToOneColumnTest {

    private Class<?> mockClass;

    @Test
    @DisplayName("EntityAssociatedColumn 을 통해 @ManyToOne 필드의 정보를 가진 객체를 생성할 수 있다.")
    void defaultAssociatedEntityTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithManyToOne.class;
        final EntityAssociatedColumn associatedColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("withId"), "WithManyToOne");

        assertSoftly(softly -> {
            softly.assertThat(associatedColumn.getFetchType()).isEqualTo(FetchType.EAGER);
            softly.assertThat(associatedColumn.getName()).isEqualTo("withId_id");
            softly.assertThat(associatedColumn.getFieldName()).isEqualTo("withId");
            softly.assertThat(associatedColumn.getJoinColumnType()).isEqualTo(FixtureAssociatedEntity.WithId.class);
            softly.assertThat(associatedColumn.isInsertable()).isTrue();
            softly.assertThat(associatedColumn.isNotNull()).isFalse();
        });
    }

    @Test
    @DisplayName("@ManyToOne(fetch) 를 통해 FetchType.LAZY 로 설정 할 수 있다.")
    void fetchTypeLazyTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithManyToOneFetchTypeLAZY.class;
        final EntityAssociatedColumn associatedColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("withId"), "WithManyToOneFetchTypeLAZY");

        assertThat(associatedColumn.getFetchType()).isEqualTo(FetchType.LAZY);
    }

    @Test
    @DisplayName("@JoinColumn(name) 를 통해 조인 column 이름을 정할 수 있다.")
    void joinColumnNameTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithManyToOneJoinColumn.class;
        final EntityAssociatedColumn associatedColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("withId"), "WithManyToOneJoinColumn");

        assertSoftly(softly -> {
            softly.assertThat(associatedColumn.getName()).isEqualTo("join_pk");
            softly.assertThat(associatedColumn.getFieldName()).isEqualTo("withId");
            softly.assertThat(associatedColumn.getJoinColumnType()).isEqualTo(FixtureAssociatedEntity.WithId.class);
        });
    }

    @Test
    @DisplayName("@JoinColumn(insertable) 를 통해 column 의 insertable 여부를 결정할 수 있다.")
    void joinColumnInsertableTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithManyToOneInsertableFalse.class;
        final EntityAssociatedColumn associatedColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("withId"), "WithManyToOneInsertableFalse");

        assertThat(associatedColumn.isInsertable()).isFalse();
    }

    @Test
    @DisplayName("@JoinColumn(nullable) 를 통해 column 의 nullable 를 결정할 수 있다.")
    void joinColumnNullableTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithManyToOneNullableFalse.class;
        final EntityAssociatedColumn associatedColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("withId"), "WithManyToOneNullableFalse");

        assertThat(associatedColumn.isNotNull()).isTrue();
    }

    @Test
    @DisplayName("getAssociatedEntityMetadata 를 통해 연관관계 Entity 의 Metadata 를 반환 받을 수 있다.")
    void getAssociatedEntityMetadataTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.City.class;
        final EntityManyToOneColumn entityManyToOneColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("country"), "city");

        assertThat(entityManyToOneColumn.getAssociatedEntityMetadata())
                .isEqualTo(EntityMetadata.from(FixtureAssociatedEntity.Country.class));
    }

    @Test
    @DisplayName("getAssociatedEntityColumns 를 통해 연관관계 Entity column 들을 반환 받을 수 있다.")
    void getAssociatedEntityColumnsTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.City.class;
        final EntityManyToOneColumn entityManyToOneColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("country"), "city");

        assertThat(entityManyToOneColumn.getAssociatedEntityColumns())
                .extracting(EntityColumn::getName)
                .containsExactly("id", "name");
    }

    @Test
    @DisplayName("getAssociatedEntityColumnNamesWithAlias 를 통해 연관관계 Entity column 들을 Alias 와 함께 반환 받을 수 있다.")
    void getAssociatedEntityColumnNamesWithAliasTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.City.class;
        final EntityManyToOneColumn entityManyToOneColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("country"), "city");

        assertThat(entityManyToOneColumn.getAssociatedEntityColumnNamesWithAlias())
                .containsExactly("country.id", "country.name");
    }

    @Test
    @DisplayName("getNameWithAliasAssociatedEntity 를 통해 연관관계 Entity 의 id Column 이름을 Alias 와 함께 반환 받을 수 있다.")
    void getNameWithAliasAssociatedEntityTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.City.class;
        final EntityManyToOneColumn entityManyToOneColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("country"), "city");

        assertThat(entityManyToOneColumn.getNameWithAliasAssociatedEntity())
                .isEqualTo("city.country_id");
    }

    @Test
    @DisplayName("getAssociatedEntityTableName 를 통해 연관관계 Entity 의 tableName 을 반환 받을 수 있다.")
    void getAssociatedEntityTableNameTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.City.class;
        final EntityManyToOneColumn entityManyToOneColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("country"), "city");

        assertThat(entityManyToOneColumn.getAssociatedEntityTableName())
                .isEqualTo("country");
    }
}
