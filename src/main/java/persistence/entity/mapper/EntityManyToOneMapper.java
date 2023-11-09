package persistence.entity.mapper;

import persistence.core.EntityColumn;
import persistence.core.EntityManyToOneColumn;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EntityManyToOneMapper extends EntityColumnsMapper {

    private final List<EntityManyToOneColumn> manyToOneColumns;

    private EntityManyToOneMapper(final List<EntityManyToOneColumn> manyToOneColumns) {
        this.manyToOneColumns = manyToOneColumns;
    }

    public static EntityColumnsMapper of(final List<EntityManyToOneColumn> manyToOneColumns) {
        return new EntityManyToOneMapper(manyToOneColumns);
    }

    @Override
    public <T> void mapColumnsInternal(final ResultSet resultSet, final T instance) throws SQLException {
        for (final EntityManyToOneColumn manyToOneColumn : manyToOneColumns) {
            final Class<?> joinColumnType = manyToOneColumn.getJoinColumnType();
            final Object innerInstance = ReflectionUtils.createInstance(joinColumnType);

            for (final EntityColumn associatedEntityColumn : manyToOneColumn.getAssociatedEntityColumns()) {
                final String fieldName = associatedEntityColumn.getFieldName();
                final String columnName = associatedEntityColumn.getNameWithAlias();
                final Object object = resultSet.getObject(columnName);
                ReflectionUtils.injectField(innerInstance, fieldName, object);
            }

            ReflectionUtils.injectField(instance, manyToOneColumn.getFieldName(), innerInstance);
        }
    }

}
