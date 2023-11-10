package persistence.entity.proxy;

import net.sf.cglib.proxy.*;
import persistence.core.EntityAssociatedColumn;
import persistence.core.EntityManyToOneColumn;
import persistence.core.EntityMetadata;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaders;
import persistence.util.ReflectionUtils;

public class EntityProxyFactory {

    public static final String GET = "get";
    private final EntityLoaders entityLoaders;

    public EntityProxyFactory(final EntityLoaders entityLoaders) {
        this.entityLoaders = entityLoaders;
    }

    public void initOneToManyProxy(final Object ownerId, final Object owner, final EntityAssociatedColumn oneToManyColumn) {
        final String oneToManyFieldName = oneToManyColumn.getFieldName();
        final Object proxyOneToManyFieldValue = createOneToManyProxy(oneToManyColumn, ownerId);
        ReflectionUtils.injectField(owner, oneToManyFieldName, proxyOneToManyFieldValue);
    }

    private Object createOneToManyProxy(final EntityAssociatedColumn proxyColumn, final Object joinColumnId) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyColumn.getType());
        enhancer.setCallback(getOneToManyLazyLoader(proxyColumn, joinColumnId));
        return enhancer.create();
    }

    private LazyLoader getOneToManyLazyLoader(final EntityAssociatedColumn proxyColumn, final Object joinColumnId) {
        return () -> {
            final Class<?> associatedEntityClassType = proxyColumn.getJoinColumnType();
            final EntityLoader<?> associatedEntityLoader = entityLoaders.getEntityLoader(associatedEntityClassType);
            return associatedEntityLoader.loadAllByOwnerId(proxyColumn.getNameWithAliasAssociatedEntity(), joinColumnId);
        };
    }

    public <T> void initManyToOneProxy(final T owner, final EntityManyToOneColumn manyToOneColumn) {
        final Object manyToOneEntityId = extractManyToOneEntityId(owner, manyToOneColumn);

        final Object proxyManyToManyFieldValue = createManyToOneProxy(manyToOneColumn, manyToOneEntityId);
        ReflectionUtils.injectField(owner, manyToOneColumn.getFieldName(), proxyManyToManyFieldValue);
    }

    private <T> Object extractManyToOneEntityId(final T owner, final EntityManyToOneColumn manyToOneColumn) {
        final EntityMetadata<?> manyToOneEntityMetadata = manyToOneColumn.getAssociatedEntityMetadata();
        final Object manyToOneEntity = ReflectionUtils.getFieldValue(owner, manyToOneColumn.getFieldName());
        return ReflectionUtils.getFieldValue(manyToOneEntity, manyToOneEntityMetadata.getIdColumnFieldName());
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
            final EntityMetadata<?> targetEntityMetadata = proxyColumn.getAssociatedEntityMetadata();
            if (method.getName().equalsIgnoreCase(GET + targetEntityMetadata.getIdColumnFieldName())) {
                return 0; // id return
            }
            return 1; // lazy init
        });
        return enhancer.create();
    }

    private LazyLoader getManyToOneLazyLoader(final EntityAssociatedColumn proxyColumn, final Object manyToOneEntityId) {
        return () -> {
            final Class<?> associatedEntityClassType = proxyColumn.getJoinColumnType();
            final EntityLoader<?> associatedEntityLoader = entityLoaders.getEntityLoader(associatedEntityClassType);
            return associatedEntityLoader.loadById(manyToOneEntityId).orElse(null);
        };
    }

}
