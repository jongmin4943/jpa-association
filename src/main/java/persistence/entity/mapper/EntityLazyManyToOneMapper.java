package persistence.entity.mapper;

import persistence.core.EntityManyToOneColumn;
import persistence.entity.proxy.EntityProxyFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EntityLazyManyToOneMapper extends EntityColumnsMapper {

    private final List<EntityManyToOneColumn> manyToOneColumns;
    private final EntityProxyFactory entityProxyFactory;

    private EntityLazyManyToOneMapper(final List<EntityManyToOneColumn> manyToOneColumns, final EntityProxyFactory entityProxyFactory) {
        this.manyToOneColumns = manyToOneColumns;
        this.entityProxyFactory = entityProxyFactory;
    }

    public static EntityColumnsMapper of(final List<EntityManyToOneColumn> manyToOneColumns, final EntityProxyFactory entityProxyFactory) {
        return new EntityLazyManyToOneMapper(manyToOneColumns, entityProxyFactory);
    }

    @Override
    public <T> void mapColumnsInternal(final ResultSet resultSet, final T instance) throws SQLException {
        for (final EntityManyToOneColumn manyToOneColumn : manyToOneColumns) {
            final String columnName = manyToOneColumn.getNameWithAlias();
            final Object manyToOneEntityId = resultSet.getObject(columnName);
            // FIXME 이 책임을 여기서 가지는게 맞는것인가? 아니면 Id 를 어떻게 밖으로 끌고 갈것인가..
            entityProxyFactory.initManyToOneProxy(manyToOneEntityId, instance, manyToOneColumn);
        }
    }


}
