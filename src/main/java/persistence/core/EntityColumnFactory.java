package persistence.core;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class EntityColumnFactory {

    private static final Map<Predicate<Field>, BiFunction<Field, String, EntityColumn>> conditions;

    static {
        conditions = new LinkedHashMap<>();
        conditions.put(field -> field.isAnnotationPresent(Id.class), EntityIdColumn::new);
        conditions.put(field -> field.isAnnotationPresent(OneToMany.class), EntityOneToManyColumn::new);
        conditions.put(field -> field.isAnnotationPresent(ManyToOne.class), EntityManyToOneColumn::new);
    }

    public static EntityColumn create(final Field field, final String tableName) {
        return conditions.entrySet().stream()
                .filter(entry -> entry.getKey().test(field))
                .map(entry -> entry.getValue().apply(field, tableName))
                .findFirst()
                .orElseGet(() -> new EntityFieldColumn(field, tableName));
    }
}
