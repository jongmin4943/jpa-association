package persistence.core;

public interface EntityColumn {

    String ALIAS_DELIMITER = ".";

    String getTableName();

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

    default boolean isOneToMany() {
        return this instanceof EntityOneToManyColumn;
    }

    default boolean isManyToOne() {
        return this instanceof EntityManyToOneColumn;
    }

    default boolean isField() {
        return this instanceof EntityFieldColumn;
    }

    default String getNameWithAlias() {
        return this.getTableName() + ALIAS_DELIMITER + this.getName();
    }

}
