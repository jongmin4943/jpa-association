package persistence.entity.mapper;

import domain.FixtureAssociatedEntity.City;
import domain.FixtureAssociatedEntity.Country;
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
class EntityEagerManyToOneMapperTest {
    @Test
    @DisplayName("EntityEagerManyToOneMapper 를 통해 ResultSet 들의 정보로 Entity 객체의 ManyToOneColumn 에 해당하는 필드에 값을 바인딩 할 수 있다.")
    void entityEagerManyToOneMapperTest() throws SQLException {
        final Class<City> clazz = City.class;
        final City city = ReflectionUtils.createInstance(clazz);
        final EntityColumns entityColumns = new EntityColumns(clazz, "city");
        final EntityColumnsMapper entityManyToOneMapper = EntityEagerManyToOneMapper.of(entityColumns.getManyToOneColumns());
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("country.id", Types.BIGINT, 10, 0);
        rs.addColumn("country.name", Types.VARCHAR, 255, 0);
        rs.addRow(333L, "testName");
        rs.next();

        entityManyToOneMapper.mapColumnsInternal(rs, city);

        final Country country = city.getCountry();
        assertSoftly(softly -> {
            softly.assertThat(country.getId()).isEqualTo(333L);
            softly.assertThat(country.getName()).isEqualTo("testName");
        });
    }
}
