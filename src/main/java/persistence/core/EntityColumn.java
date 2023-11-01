package persistence.core;

import jakarta.persistence.Id;

import java.lang.reflect.Field;

public interface EntityColumn {
    String getName();

    boolean isNotNull();

    Class<?> getType();

    boolean isStringValued();

    int getStringLength();

    String getFieldName();

    boolean isInsertable();

    boolean isAutoIncrement();

    default boolean isId() {
        return this instanceof EntityIdColumn;
    }

    static EntityColumn from(final Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            return new EntityIdColumn(field);
        }

        return new EntityFieldColumn(field);
    }
}
