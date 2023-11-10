package persistence.entity.mapper;

import persistence.core.EntityColumns;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityColumnsMapperChain {

    private final EntityColumnsMapper mapper;

    private EntityColumnsMapperChain(final EntityColumns columns) {
        this.mapper = EntityIdMapper.of(columns.getId())
                .next(EntityFieldMapper.of(columns.getFieldColumns()))
                .next(EntityEagerManyToOneMapper.of(columns.getEagerManyToOneColumns()))
//               // FIXME entityProxyFactory 를 넣어줄 방법이 없다..
//                .next(EntityLazyManyToOneMapper.of(columns.getLazyManyToOneColumns(), entityProxyFactory))
                .next(EntityOneToManyMapper.of(columns.getEagerOneToManyColumns()));
    }

    public static EntityColumnsMapperChain of(final EntityColumns columns) {
        return new EntityColumnsMapperChain(columns);
    }

    public <T> void mapColumns(final ResultSet resultSet, final T instance) throws SQLException {
        mapper.mapColumns(resultSet, instance);
    }
}
