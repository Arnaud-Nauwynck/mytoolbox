package fr.an.com.sun.tools.javac.tree;


public class JCTreeFieldInfo<T> {
    // TODO...
    final JCTreeClassInfo<?> parent;
    final java.lang.reflect.Field field;
    final Class<T> fieldType;
    final Class<?> fieldListElementType;
    final String fieldName;

    /*pp*/ JCTreeFieldInfo(JCTreeClassInfo<?> parent,
            java.lang.reflect.Field field
            // Class<T> fieldType, String fieldName
            ) {
        this(parent, field, null);
    }
    
    /*pp*/ @SuppressWarnings("unchecked")
    JCTreeFieldInfo(JCTreeClassInfo<?> parent,
            java.lang.reflect.Field field
            // Class<T> fieldType, String fieldName
            , Class<?> fieldListElementType) {
        super();
        this.parent = parent;
        this.field = field;
        this.fieldType = (Class<T>) field.getType();
        this.fieldListElementType = fieldListElementType;
        this.fieldName = field.getName();
        parent._addField(this);
    }
    
    
}