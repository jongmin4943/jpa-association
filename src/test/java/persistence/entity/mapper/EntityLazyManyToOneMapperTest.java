package persistence.entity.mapper;

import domain.FixtureAssociatedEntity;
import extension.EntityMetadataExtension;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.core.EntityColumns;
import persistence.util.ReflectionUtils;

import java.sql.SQLException;
import java.sql.Types;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(EntityMetadataExtension.class)
class EntityLazyManyToOneMapperTest {

    @Test
    @DisplayName("EntityLazyManyToOneMapper 를 통해 ManyToOne(Lazy) 필드에 Id 만 가지고 있는 객체를 주입되어야 한다.")
    void entityLazyManyToOneMapperTest() throws SQLException {
        final Class<FixtureAssociatedEntity.LazyCity> clazz = FixtureAssociatedEntity.LazyCity.class;
        final FixtureAssociatedEntity.LazyCity city = ReflectionUtils.createInstance(clazz);
        final EntityColumns entityColumns = new EntityColumns(clazz, "lazy_city");
        final EntityColumnsMapper entityManyToOneMapper = EntityLazyManyToOneMapper.of(entityColumns.getLazyManyToOneColumns());

        entityManyToOneMapper.mapColumnsInternal(createCountryResultSet(), city);

        final FixtureAssociatedEntity.LazyCountry country = city.getCountry();
        assertSoftly(softly -> {
            softly.assertThat(country.getId()).isEqualTo(111L);
            softly.assertThat(country.getName()).isNull();
        });
    }

    private static SimpleResultSet createCountryResultSet() throws SQLException {
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("lazy_city.id", Types.BIGINT, 10, 0);
        rs.addColumn("lazy_city.name", Types.VARCHAR, 255, 0);
        rs.addColumn("lazy_city.lazy_country_id", Types.BIGINT, 10, 0);
        rs.addRow(333L, "testName", 111L);
        rs.next();
        return rs;
    }
}
