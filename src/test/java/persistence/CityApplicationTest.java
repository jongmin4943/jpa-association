package persistence;

import domain.FixtureAssociatedEntity.City;
import domain.FixtureAssociatedEntity.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.EntityManagerFactory;
import persistence.entity.manager.SimpleEntityManagerFactory;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class CityApplicationTest extends IntegrationTestEnvironment {

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        final EntityMetadataProvider entityMetadataProvider = EntityMetadataProvider.getInstance();
        final EntityMetadata<Country> countryEntityMetadata = entityMetadataProvider.getEntityMetadata(Country.class);
        final EntityMetadata<City> cityEntityMetadata = entityMetadataProvider.getEntityMetadata(City.class);
        final String createCountryDdl = ddlGenerator.generateCreateDdl(countryEntityMetadata);
        final String createCityDdl = ddlGenerator.generateCreateDdl(cityEntityMetadata);
        jdbcTemplate.execute(createCountryDdl);
        jdbcTemplate.execute(createCityDdl);
        saveDummyCountry();
        saveDummyCity();
        final EntityManagerFactory entityManagerFactory = new SimpleEntityManagerFactory(entityMetadataProvider, new EntityScanner(Application.class), persistenceEnvironment);
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    @DisplayName("entityManager.find 를 통해 City 를 Country 과 함께 조회할 수 있다.")
    void cityFetchEagerFindTest() {
        final City city = entityManager.find(City.class, 1L);

        final Country country = city.getCountry();
        assertSoftly(softly -> {
            softly.assertThat(city.getId()).isEqualTo(1L);
            softly.assertThat(city.getName()).isEqualTo("서울");
            softly.assertThat(country.getId()).isEqualTo(1L);
            softly.assertThat(country.getName()).isEqualTo("한국");
        });
    }


    private void saveDummyCity() {
        jdbcTemplate.execute("insert into city (name, country_id) values ('서울', 1)");
        jdbcTemplate.execute("insert into city (name, country_id) values ('부산', 1)");
        jdbcTemplate.execute("insert into city (name, country_id) values ('인천', 1)");
        jdbcTemplate.execute("insert into city (name, country_id) values ('밴쿠버', 2)");
        jdbcTemplate.execute("insert into city (name, country_id) values ('토론토', 2)");
        jdbcTemplate.execute("insert into city (name, country_id) values ('워싱턴', 3)");
        jdbcTemplate.execute("insert into city (name, country_id) values ('뉴욕', 3)");
        jdbcTemplate.execute("insert into city (name, country_id) values ('시애틀', 3)");
        jdbcTemplate.execute("insert into city (name, country_id) values ('도쿄', 4)");
        jdbcTemplate.execute("insert into city (name, country_id) values ('오사카', 4)");
    }

    private void saveDummyCountry() {
        jdbcTemplate.execute("insert into country (name) values ('한국')");
        jdbcTemplate.execute("insert into country (name) values ('캐나다')");
        jdbcTemplate.execute("insert into country (name) values ('미국')");
        jdbcTemplate.execute("insert into country (name) values ('일본')");
    }
}
