package persistence.sql.ddl;

import persistence.core.*;
import persistence.dialect.Dialect;

import java.util.Set;

public class DdlGenerator {

    private final EntityMetadataProvider entityMetadataProvider;
    private final Dialect dialect;

    public DdlGenerator(final EntityMetadataProvider entityMetadataProvider, final Dialect dialect) {
        this.entityMetadataProvider = entityMetadataProvider;
        this.dialect = dialect;
    }

    public String generateCreateDdl(final EntityMetadata<?> entityMetadata) {
        final StringBuilder builder = new StringBuilder();

        final String tableName = entityMetadata.getTableName();
        builder.append("create table ")
                .append(tableName)
                .append(" ")
                .append(generateColumnsClause(entityMetadata));

        return builder.toString();
    }

    private String generateColumnsClause(final EntityMetadata<?> entityMetadata) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(");

        entityMetadata.getColumns().forEach(column -> {
                    if (column.isOneToMany()) {
                        return;
                    }

                    if (column.isManyToOne()) {
                        final EntityManyToOneColumn entityManyToOneColumn = (EntityManyToOneColumn) column;
                        builder.append(generateManyToOneColumnsClause(entityManyToOneColumn));
                        return;
                    }

                    builder.append(generateColumnDefinition(column))
                            .append(",");
                }
        );

        builder.append(generateOneToManyColumnsClause(entityMetadata));

        builder.append(generatePKConstraintClause(entityMetadata));
        builder.append(")");
        return builder.toString();
    }

    private String generateManyToOneColumnsClause(final EntityManyToOneColumn manyToOneColumn) {
        final EntityMetadata<?> associatedEntityMetadata = entityMetadataProvider.getEntityMetadata(manyToOneColumn.getJoinColumnType());
        return generateAssociatedColumnsClause(manyToOneColumn, associatedEntityMetadata);
    }

    private String generateOneToManyColumnsClause(final EntityMetadata<?> entityMetadata) {
        final StringBuilder builder = new StringBuilder();
        final Set<EntityMetadata<?>> oneToManyAssociatedEntitiesMetadata = entityMetadataProvider.getOneToManyAssociatedEntitiesMetadata(entityMetadata);
        oneToManyAssociatedEntitiesMetadata.forEach(oneToManyAssociatedEntityMetadata ->
                oneToManyAssociatedEntityMetadata.getOneToManyColumns().forEach(oneToManyColumn ->
                        builder.append(generateAssociatedColumnsClause(oneToManyColumn, oneToManyAssociatedEntityMetadata))
                ));
        return builder.toString();
    }

    private String generateAssociatedColumnsClause(final EntityAssociatedColumn entityAssociatedColumn, final EntityMetadata<?> associatedEntityMetadata) {
        final StringBuilder builder = new StringBuilder();
        builder.append(entityAssociatedColumn.getName())
                .append(" ")
                .append(dialect.getColumnTypeMapper().getColumnName(associatedEntityMetadata.getIdType()))
                .append(generateNotNullClause(entityAssociatedColumn))
                .append(",")
                .append("foreign key(")
                .append(entityAssociatedColumn.getName())
                .append(") references ")
                .append(associatedEntityMetadata.getTableName())
                .append(" (")
                .append(associatedEntityMetadata.getIdName())
                .append(")")
                .append(",");
        return builder.toString();
    }

    private String generateColumnDefinition(final EntityColumn column) {
        final StringBuilder builder = new StringBuilder();
        builder.append(column.getName())
                .append(" ")
                .append(generateColumnTypeClause(column))
                .append(generateNotNullClause(column))
                .append(generateAutoIncrementClause(column));
        return builder.toString();
    }

    private String generateColumnTypeClause(final EntityColumn column) {
        final StringBuilder builder = new StringBuilder();
        builder.append(dialect.getColumnTypeMapper().getColumnName(column.getType()));
        if (column.isStringValued()) {
            builder.append("(")
                    .append(column.getStringLength())
                    .append(")");
        }
        return builder.toString();
    }

    private String generateAutoIncrementClause(final EntityColumn column) {
        if (column.isAutoIncrement()) {
            return " auto_increment";
        }

        return "";
    }

    private String generateNotNullClause(final EntityColumn column) {
        if (column.isNotNull()) {
            return " not null";
        }

        return "";
    }

    private String generatePKConstraintClause(final EntityMetadata<?> entityMetadata) {
        final StringBuilder builder = new StringBuilder();
        builder.append("CONSTRAINT PK_")
                .append(entityMetadata.getTableName())
                .append(" PRIMARY KEY (")
                .append(entityMetadata.getIdColumnName())
                .append(")");
        return builder.toString();
    }

    public String generateDropDdl(final EntityMetadata<?> entityMetadata) {
        final StringBuilder builder = new StringBuilder();
        builder.append("drop table ")
                .append(entityMetadata.getTableName());

        return builder.toString();
    }
}
