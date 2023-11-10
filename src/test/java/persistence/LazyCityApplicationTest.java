package persistence;

import domain.FixtureAssociatedEntity.LazyCity;
import domain.FixtureAssociatedEntity.LazyCountry;
import net.sf.cglib.proxy.Enhancer;
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

public class LazyCityApplicationTest extends IntegrationTestEnvironment {

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        final EntityMetadataProvider entityMetadataProvider = EntityMetadataProvider.getInstance();
        final EntityMetadata<LazyCountry> lazyCountryEntityMetadata = entityMetadataProvider.getEntityMetadata(LazyCountry.class);
        final EntityMetadata<LazyCity> lazyCityEntityMetadata = entityMetadataProvider.getEntityMetadata(LazyCity.class);
        final String createLazyCountryDdl = ddlGenerator.generateCreateDdl(lazyCountryEntityMetadata);
        final String createLazyCityDdl = ddlGenerator.generateCreateDdl(lazyCityEntityMetadata);
        jdbcTemplate.execute(createLazyCountryDdl);
        jdbcTemplate.execute(createLazyCityDdl);
        saveDummyLazyCountry();
        saveDummyLazyCity();
        final EntityManagerFactory entityManagerFactory = new SimpleEntityManagerFactory(entityMetadataProvider, new EntityScanner(Application.class), persistenceEnvironment);
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    @DisplayName("entityManager.find 를 통해 LazyCity 를 LazyCountry 과 함께 조회할 수 있다.")
    void LazyCityFetchEagerFindTest() {
        final LazyCity lazyCity = entityManager.find(LazyCity.class, 4L);

        final LazyCountry lazyCountry = lazyCity.getCountry();
        assertSoftly(softly -> {
            softly.assertThat(Enhancer.isEnhanced(lazyCountry.getClass())).isTrue();
            softly.assertThat(lazyCity.getId()).isEqualTo(4L);
            softly.assertThat(lazyCity.getName()).isEqualTo("밴쿠버");
            softly.assertThat(lazyCountry.getId()).isEqualTo(2L);
            softly.assertThat(lazyCountry.getName()).isEqualTo("캐나다");
        });
    }


    private void saveDummyLazyCity() {
        jdbcTemplate.execute("insert into lazy_city (name, lazy_country_id) values ('서울', 1)");
        jdbcTemplate.execute("insert into lazy_city (name, lazy_country_id) values ('부산', 1)");
        jdbcTemplate.execute("insert into lazy_city (name, lazy_country_id) values ('인천', 1)");
        jdbcTemplate.execute("insert into lazy_city (name, lazy_country_id) values ('밴쿠버', 2)");
        jdbcTemplate.execute("insert into lazy_city (name, lazy_country_id) values ('토론토', 2)");
        jdbcTemplate.execute("insert into lazy_city (name, lazy_country_id) values ('워싱턴', 3)");
        jdbcTemplate.execute("insert into lazy_city (name, lazy_country_id) values ('뉴욕', 3)");
        jdbcTemplate.execute("insert into lazy_city (name, lazy_country_id) values ('시애틀', 3)");
        jdbcTemplate.execute("insert into lazy_city (name, lazy_country_id) values ('도쿄', 4)");
        jdbcTemplate.execute("insert into lazy_city (name, lazy_country_id) values ('오사카', 4)");
    }

    private void saveDummyLazyCountry() {
        jdbcTemplate.execute("insert into lazy_country (name) values ('한국')");
        jdbcTemplate.execute("insert into lazy_country (name) values ('캐나다')");
        jdbcTemplate.execute("insert into lazy_country (name) values ('미국')");
        jdbcTemplate.execute("insert into lazy_country (name) values ('일본')");
    }
}
