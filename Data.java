package dsh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * A data wrapper class which carries its precise type
 * and enforces safe operations.
 *
 * Author: Ryan Pointer
 * Version: 6/27/25
 */
public class Data {
    private final Object value;
    private final DataType type;
    
    // Private constructor - use factory methods instead
    private Data(Object value, DataType type) {
        this.value = value;
        this.type = type;
    }
    
    // Factory methods for type inference
    public static Data of(Object value) {
        return new Data(value, inferType(value));
    }
    
    public static Data of(int value) {
        return new Data(value, DataType.INTEGER);
    }
    
    public static Data of(long value) {
        return new Data(value, DataType.LONG);
    }
    
    public static Data of(double value) {
        return new Data(value, DataType.DOUBLE);
    }
    
    public static Data of(float value) {
        return new Data(value, DataType.FLOAT);
    }
    
    public static Data of(boolean value) {
        return new Data(value, DataType.BOOLEAN);
    }
    
    public static Data of(String value) {
        return new Data(value, DataType.STRING);
    }
    
    public static Data of(List<?> value) {
        return new Data(value, DataType.LIST);
    }
    
    public static Data of(Map<?, ?> value) {
        return new Data(value, DataType.MAP);
    }
    
    // Type inference logic
    private static DataType inferType(Object value) {
        if (value == null) return DataType.NULL;
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
    
    // Safe getters with type checking
    public <T> T getValue(Class<T> expectedType) {
        if (value == null) {
            if (type == DataType.NULL) return null;
            throw new IllegalStateException("Value is null but type is " + type);
        }
        
        if (!expectedType.isAssignableFrom(value.getClass())) {
            throw new ClassCastException(
                "Cannot cast " + type + " to " + expectedType.getSimpleName()
            );
        }
        
        return expectedType.cast(value);
    }
    
    // Convenience getters
    public int asInt() {
        if (type == DataType.INTEGER) {
            return (Integer) value;
        }
        if (type == DataType.LONG) {
            return ((Long) value).intValue();
        }
        if (type == DataType.FLOAT) {
            return ((Float) value).intValue();
        }
        if (type == DataType.DOUBLE) {
            return ((Double) value).intValue();
        }
        throw new ClassCastException("Cannot cast " + type + " to Integer");
    }
    
    public long asLong() {
        if (type == DataType.INTEGER) {
            return ((Integer) value).longValue();
        }
        return getValue(Long.class);
    }
    
    public double asDouble() {
        if (type == DataType.INTEGER) {
            return ((Integer) value).doubleValue();
        }
        if (type == DataType.LONG) {
            return ((Long) value).doubleValue();
        }
        if (type == DataType.FLOAT) {
            return ((Float) value).doubleValue();
        }
        return getValue(Double.class);
    }
    
    public float asFloat() {
        if (type == DataType.INTEGER) {
            return ((Integer) value).floatValue();
        }
        if (type == DataType.LONG) {
            return ((Long) value).floatValue();
        }
        return getValue(Float.class);
    }
    
    public boolean asBoolean() {
        return getValue(Boolean.class);
    }
    
    public String asString() {
        if (value == null) return null;
        if (type == DataType.STRING) return (String) value;
        return value.toString();
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> asList() {
        return getValue(List.class);
    }
    
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> asMap() {
        return getValue(Map.class);
    }
    
    // Type checking methods
    public boolean isNull() {
        return type == DataType.NULL;
    }
    
    public boolean isNumeric() {
        return type == DataType.INTEGER || type == DataType.LONG || 
               type == DataType.DOUBLE || type == DataType.FLOAT;
    }
    
    public boolean isString() {
        return type == DataType.STRING;
    }
    
    public boolean isBoolean() {
        return type == DataType.BOOLEAN;
    }
    
    public boolean isList() {
        return type == DataType.LIST;
    }
    
    public boolean isMap() {
        return type == DataType.MAP;
    }
    
    public boolean isCollection() {
        return type == DataType.LIST || type == DataType.COLLECTION;
    }
    
    // Getters
    public Object getRawValue() {
        return value;
    }
    
    public DataType getType() {
        return type;
    }
    
    // Safe operations
    public Data add(Data other) {
        if (!this.isNumeric() || !other.isNumeric()) {
            throw new UnsupportedOperationException("Addition only supported for numeric types");
        }
        
        if (this.type == DataType.DOUBLE || other.type == DataType.DOUBLE) {
            return Data.of(this.asDouble() + other.asDouble());
        }
        if (this.type == DataType.FLOAT || other.type == DataType.FLOAT) {
            return Data.of(this.asFloat() + other.asFloat());
        }
        if (this.type == DataType.LONG || other.type == DataType.LONG) {
            return Data.of(this.asLong() + other.asLong());
        }
        return Data.of(this.asInt() + other.asInt());
    }
    
    public Data subtract(Data other) {
        if (!this.isNumeric() || !other.isNumeric()) {
            throw new UnsupportedOperationException("Subtraction only supported for numeric types");
        }
        
        if (this.type == DataType.DOUBLE || other.type == DataType.DOUBLE) {
            return Data.of(this.asDouble() - other.asDouble());
        }
        if (this.type == DataType.FLOAT || other.type == DataType.FLOAT) {
            return Data.of(this.asFloat() - other.asFloat());
        }
        if (this.type == DataType.LONG || other.type == DataType.LONG) {
            return Data.of(this.asLong() - other.asLong());
        }
        return Data.of(this.asInt() - other.asInt());
    }
    
    public Data multiply(Data other) {
        if (!this.isNumeric() || !other.isNumeric()) {
            throw new UnsupportedOperationException("Multiplication only supported for numeric types");
        }
        
        if (this.type == DataType.DOUBLE || other.type == DataType.DOUBLE) {
            return Data.of(this.asDouble() * other.asDouble());
        }
        if (this.type == DataType.FLOAT || other.type == DataType.FLOAT) {
            return Data.of(this.asFloat() * other.asFloat());
        }
        if (this.type == DataType.LONG || other.type == DataType.LONG) {
            return Data.of(this.asLong() * other.asLong());
        }
        return Data.of(this.asInt() * other.asInt());
    }
    
    public Data divide(Data other) {
        if (!this.isNumeric() || !other.isNumeric()) {
            throw new UnsupportedOperationException("Division only supported for numeric types");
        }
        
        // Always return double for division to handle precision
        return Data.of(this.asDouble() / other.asDouble());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Data data = (Data) obj;
        return type == data.type && 
               (value == null ? data.value == null : value.equals(data.value));
    }
    
    @Override
    public int hashCode() {
        return (value != null ? value.hashCode() : 0) * 31 + type.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Data{value=%s, type=%s}", value, type);
    }
}

/**
 * Enum defining all supported data types
 */
enum DataType {
    NULL,
    INTEGER,
    LONG,
    DOUBLE,
    FLOAT,
    BOOLEAN,
    STRING,
    LIST,
    MAP,
    COLLECTION,
    ARRAY,
    OBJECT
}