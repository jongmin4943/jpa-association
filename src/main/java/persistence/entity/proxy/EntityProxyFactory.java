package persistence.entity.proxy;

import net.sf.cglib.proxy.*;
import persistence.core.EntityAssociatedColumn;
import persistence.core.EntityManyToOneColumn;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaders;
import persistence.util.ReflectionUtils;

public class EntityProxyFactory {

    public static final String GET = "get";
    private final EntityMetadataProvider entityMetadataProvider;
    private final EntityLoaders entityLoaders;

    public EntityProxyFactory(final EntityMetadataProvider entityMetadataProvider, final EntityLoaders entityLoaders) {
        this.entityMetadataProvider = entityMetadataProvider;
        this.entityLoaders = entityLoaders;
    }

    public void initProxy(final Object ownerId, final Object owner, final EntityAssociatedColumn proxyColumn) {
        final String proxyFieldName = proxyColumn.getFieldName();
        final Object proxyOneToManyFieldValue = createProxy(proxyColumn, ownerId);
        ReflectionUtils.injectField(owner, proxyFieldName, proxyOneToManyFieldValue);
    }

    public <T> void initManyToOneProxy(final Object manyToOneEntityId, final T owner, final EntityManyToOneColumn manyToOneColumn) {
        final String proxyFieldName = manyToOneColumn.getFieldName();
        final Object proxyManyToManyFieldValue = createManyToOneProxy(manyToOneColumn, manyToOneEntityId);
        ReflectionUtils.injectField(owner, proxyFieldName, proxyManyToManyFieldValue);
    }

    private Object createProxy(final EntityAssociatedColumn proxyColumn, final Object joinColumnId) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyColumn.getType());
        enhancer.setCallback(getOneToManyLazyLoader(proxyColumn, joinColumnId));
        return enhancer.create();
    }

    private Object createManyToOneProxy(final EntityAssociatedColumn proxyColumn, final Object manyToOneEntityId) {
        final Enhancer enhancer = new Enhancer();
        final Class<?> targetColumnType = proxyColumn.getType();
        enhancer.setSuperclass(targetColumnType);
        final Callback[] callbacks = new Callback[]{
                (MethodInterceptor) (obj, method, args, proxy) -> manyToOneEntityId, // 인덱스 0
                getManyToOneLazyLoader(proxyColumn, manyToOneEntityId) // 인덱스 1
        };
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackFilter(method -> {
            // FIXME Id 접근 자체를 탐지하려면 어떻게 해야할까?
            final EntityMetadata<?> targetEntityMetadata = entityMetadataProvider.getEntityMetadata(targetColumnType);
            if (method.getName().equalsIgnoreCase(GET + targetEntityMetadata.getIdColumnFieldName())) {
                return 0; // id return
            }
            return 1; // lazy init
        });
        return enhancer.create();
    }

    private LazyLoader getOneToManyLazyLoader(final EntityAssociatedColumn proxyColumn, final Object joinColumnId) {
        return () -> {
            final Class<?> associatedEntityClassType = proxyColumn.getJoinColumnType();
            final EntityLoader<?> associatedEntityLoader = entityLoaders.getEntityLoader(associatedEntityClassType);
            return associatedEntityLoader.loadAllByOwnerId(proxyColumn.getNameWithAliasAssociatedEntity(), joinColumnId);
        };
    }

    private LazyLoader getManyToOneLazyLoader(final EntityAssociatedColumn proxyColumn, final Object manyToOneEntityId) {
        return () -> {
            final Class<?> associatedEntityClassType = proxyColumn.getJoinColumnType();
            final EntityLoader<?> associatedEntityLoader = entityLoaders.getEntityLoader(associatedEntityClassType);
            return associatedEntityLoader.loadById(manyToOneEntityId).orElse(null);
        };
    }

}
