package dsh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Wrapper for any object with inferred or custom data type.
 * Supports casting, type checking, and basic arithmetic.
 * 
 * Author: Ryan Pointer
 * Version: 6/30/25
 */
public class Data {
    private final Object value;
    private final DataType type;

    private static final Map<Class<?>, DataType> customTypes = new ConcurrentHashMap<>();

    /**
     * Internal constructor
     */
    private Data(Object value, DataType type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Creates a Data instance with inferred type
     */
    public static Data of(Object value) {
        return new Data(value, inferType(value));
    }

    /**
     * Creates a Data instance with a custom type name
     */
    public static Data ofType(Object value, String typeName) {
        return new Data(value, DataType.custom(typeName));
    }

    /**
     * Registers a custom type for a class
     */
    public static void registerType(Class<?> clazz, String typeName) {
        customTypes.put(clazz, DataType.custom(typeName));
    }

    /**
     * Infers the DataType of the given value
     */
    private static DataType inferType(Object value) {
        if (value == null) return DataType.NULL;

        DataType customType = customTypes.get(value.getClass());
        if (customType != null) return customType;

        if (value instanceof Integer) return DataType.INTEGER;
        if (value instanceof Long) return DataType.LONG;
        if (value instanceof Double) return DataType.DOUBLE;
        if (value instanceof Float) return DataType.FLOAT;
        if (value instanceof Boolean) return DataType.BOOLEAN;
        if (value instanceof String) return DataType.STRING;
        if (value instanceof List) return DataType.LIST;
        if (value instanceof Map) return DataType.MAP;
        if (value instanceof Collection) return DataType.COLLECTION;
        if (value.getClass().isArray()) return DataType.ARRAY;

        return DataType.OBJECT;
    }

    /**
     * Converts the value to a given target type
     */
    @SuppressWarnings("unchecked")
    public <T> T as(Class<T> targetType) {
        if (value == null) return null;

        if (targetType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        if (value instanceof Number && isNumericType(targetType)) {
            return castNumeric((Number) value, targetType);
        }

        if (targetType == String.class) {
            return (T) value.toString();
        }

        if (targetType == Boolean.class || targetType == boolean.class) {
            return (T) castToBoolean(value);
        }

        if (targetType == List.class && value instanceof Collection && !(value instanceof List)) {
            return (T) new ArrayList<>((Collection<?>) value);
        }

        return (T) value;
    }

    /**
     * Casts a number to a specific numeric type
     */
    @SuppressWarnings("unchecked")
    private <T> T castNumeric(Number num, Class<T> targetType) {
        if (targetType == Integer.class || targetType == int.class) return (T) Integer.valueOf(num.intValue());
        if (targetType == Long.class || targetType == long.class) return (T) Long.valueOf(num.longValue());
        if (targetType == Double.class || targetType == double.class) return (T) Double.valueOf(num.doubleValue());
        if (targetType == Float.class || targetType == float.class) return (T) Float.valueOf(num.floatValue());
        if (targetType == Short.class || targetType == short.class) return (T) Short.valueOf(num.shortValue());
        if (targetType == Byte.class || targetType == byte.class) return (T) Byte.valueOf(num.byteValue());

        throw new ClassCastException("Cannot cast to " + targetType.getSimpleName());
    }

    /**
     * Converts value to boolean
     */
    private Boolean castToBoolean(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0.0;
        }

        if (value instanceof String) {
            String str = value.toString().toLowerCase().trim();
            return "true".equals(str) || "1".equals(str) || "yes".equals(str) || "on".equals(str);
        }

        return Boolean.valueOf(value.toString());
    }

    /**
     * Checks if the type is numeric
     */
    private boolean isNumericType(Class<?> type) {
        return type == Integer.class || type == int.class ||
               type == Long.class || type == long.class ||
               type == Double.class || type == double.class ||
               type == Float.class || type == float.class ||
               type == Short.class || type == short.class ||
               type == Byte.class || type == byte.class;
    }

    public int asInt() { return as(Integer.class); }
    public long asLong() { return as(Long.class); }
    public double asDouble() { return as(Double.class); }
    public float asFloat() { return as(Float.class); }
    public boolean asBoolean() { return as(Boolean.class); }
    public String asString() { return value == null ? null : as(String.class); }
    public <T> List<T> asList() { return as(List.class); }
    public <K, V> Map<K, V> asMap() { return as(Map.class); }

    public boolean isNull() { return type == DataType.NULL; }
    public boolean isNumeric() { return type.isNumeric(); }
    public boolean isString() { return type == DataType.STRING; }
    public boolean isBoolean() { return type == DataType.BOOLEAN; }
    public boolean isList() { return type == DataType.LIST; }
    public boolean isMap() { return type == DataType.MAP; }
    public boolean isCollection() { return type.isCollection(); }
    public boolean isCustomType() { return type.isCustom(); }

    /**
     * Gets the raw stored value
     */
    public Object getRawValue() { return value; }

    /**
     * Gets the DataType
     */
    public DataType getType() { return type; }

    /**
     * Adds two numeric values
     */
    public Data add(Data other) {
        if (!isNumeric() || !other.isNumeric()) {
            throw new UnsupportedOperationException("Addition requires numeric types");
        }

        if (type == DataType.DOUBLE || other.type == DataType.DOUBLE) {
            return Data.of(asDouble() + other.asDouble());
        }
        if (type == DataType.FLOAT || other.type == DataType.FLOAT) {
            return Data.of(asFloat() + other.asFloat());
        }
        if (type == DataType.LONG || other.type == DataType.LONG) {
            return Data.of(asLong() + other.asLong());
        }

        return Data.of(asInt() + other.asInt());
    }

    /**
     * Subtracts one Data from another
     */
    public Data subtract(Data other) {
        if (!isNumeric() || !other.isNumeric()) {
            throw new UnsupportedOperationException("Subtraction requires numeric types");
        }

        if (type == DataType.DOUBLE || other.type == DataType.DOUBLE) {
            return Data.of(asDouble() - other.asDouble());
        }
        if (type == DataType.FLOAT || other.type == DataType.FLOAT) {
            return Data.of(asFloat() - other.asFloat());
        }
        if (type == DataType.LONG || other.type == DataType.LONG) {
            return Data.of(asLong() - other.asLong());
        }

        return Data.of(asInt() - other.asInt());
    }

    /**
     * Multiplies two Data values
     */
    public Data multiply(Data other) {
        if (!isNumeric() || !other.isNumeric()) {
            throw new UnsupportedOperationException("Multiplication requires numeric types");
        }

        if (type == DataType.DOUBLE || other.type == DataType.DOUBLE) {
            return Data.of(asDouble() * other.asDouble());
        }
        if (type == DataType.FLOAT || other.type == DataType.FLOAT) {
            return Data.of(asFloat() * other.asFloat());
        }
        if (type == DataType.LONG || other.type == DataType.LONG) {
            return Data.of(asLong() * other.asLong());
        }

        return Data.of(asInt() * other.asInt());
    }

    /**
     * Divides this value by another
     */
    public Data divide(Data other) {
        if (!isNumeric() || !other.isNumeric()) {
            throw new UnsupportedOperationException("Division requires numeric types");
        }

        return Data.of(asDouble() / other.asDouble());
    }

    /**
     * Compares Data instances by type and value
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Data data = (Data) obj;
        return type == data.type && 
               (value == null ? data.value == null : value.equals(data.value));
    }

    /**
     * Computes hash code
     */
    @Override
    public int hashCode() {
        return (value != null ? value.hashCode() : 0) * 31 + type.hashCode();
    }

    /**
     * Returns a string representation of this Data
     */
    @Override
    public String toString() {
        return String.format("Data{value=%s, type=%s}", value, type);
    }
}

/**
 * Represents a data type for values in the Data wrapper
 */
enum DataType {
    NULL, INTEGER, LONG, DOUBLE, FLOAT, BOOLEAN, STRING, 
    LIST, MAP, COLLECTION, ARRAY, OBJECT;

    private String customName;

    /**
     * Creates a custom DataType with a name
     */
    public static DataType custom(String name) {
        DataType custom = OBJECT;
        custom.customName = name;
        return custom;
    }

    /**
     * Returns true if this is a numeric type
     */
    public boolean isNumeric() {
        return this == INTEGER || this == LONG || this == DOUBLE || this == FLOAT;
    }

    /**
     * Returns true if this is a collection type
     */
    public boolean isCollection() {
        return this == LIST || this == COLLECTION || this == ARRAY;
    }

    /**
     * Returns true if this is a custom-defined type
     */
    public boolean isCustom() {
        return customName != null;
    }

    /**
     * Gets the custom name, if any
     */
    public String getCustomName() {
        return customName;
    }

    /**
     * Converts to string representation
     */
    @Override
    public String toString() {
        return customName != null ? customName : name();
    }
}
