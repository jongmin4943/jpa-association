package persistence.entity.mapper;

import persistence.core.EntityManyToOneColumn;
import persistence.core.EntityMetadata;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EntityLazyManyToOneMapper extends EntityColumnsMapper {

    private final List<EntityManyToOneColumn> manyToOneColumns;

    private EntityLazyManyToOneMapper(final List<EntityManyToOneColumn> manyToOneColumns) {
        this.manyToOneColumns = manyToOneColumns;
    }

    public static EntityColumnsMapper of(final List<EntityManyToOneColumn> manyToOneColumns) {
        return new EntityLazyManyToOneMapper(manyToOneColumns);
    }

    @Override
    public <T> void mapColumnsInternal(final ResultSet resultSet, final T instance) throws SQLException {
        for (final EntityManyToOneColumn manyToOneColumn : manyToOneColumns) {
            final String columnName = manyToOneColumn.getNameWithAlias();
            final Object manyToOneEntityId = resultSet.getObject(columnName);
            
            final Object manyToOneFieldInstance = createManyToOneFieldInstance(manyToOneColumn, manyToOneEntityId);
            final String manyToOneFieldName = manyToOneColumn.getFieldName();
            ReflectionUtils.injectField(instance, manyToOneFieldName, manyToOneFieldInstance);
        }
    }

    private Object createManyToOneFieldInstance(final EntityManyToOneColumn manyToOneColumn, final Object manyToOneEntityId) {
        final EntityMetadata<?> manyToOneEntityMetadata = manyToOneColumn.getAssociatedEntityMetadata();
        final Object innerInstance = ReflectionUtils.createInstance(manyToOneColumn.getJoinColumnType());
        ReflectionUtils.injectField(innerInstance, manyToOneEntityMetadata.getIdColumnName(), manyToOneEntityId);
        return innerInstance;
    }

}
