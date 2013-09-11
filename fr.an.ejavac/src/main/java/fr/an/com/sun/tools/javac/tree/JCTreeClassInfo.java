package fr.an.com.sun.tools.javac.tree;

import fr.an.com.sun.tools.javac.tree.JCTree;
import fr.an.com.sun.tools.javac.tree.JCTree.Tag;
import fr.an.com.sun.tools.javac.util.List;

public class JCTreeClassInfo<T extends JCTree> {
    // TODO...
    final Class<T> jcTreeClass;
    final String name;
    List<JCTreeFieldInfo<?>> fields; // can not be both final and immutale ... chicken and egg pb! 

    // in case there is a 1-1 mapping between tag and tree class!  (not for binaryOp, unaryOp, assignOp, ...)
    final JCTree.Tag jcTreeTag;
    // for binaryOp, unaryOp, assignOp, ...
    final JCTree.Tag[] jcTreeOpTags;

    /*pp*/ JCTreeClassInfo(Class<T> jcTreeClass,
            String name, Tag jcTreeTag, Tag[] jcTreeOpTags
            // , JCTreeFieldInfo[] fields
            ) {
        this.jcTreeClass = jcTreeClass;
        this.name = name;
        this.jcTreeTag = jcTreeTag;
        this.jcTreeOpTags = jcTreeOpTags;
        // this.fields = fields;
    }

    
    public List<JCTreeFieldInfo<?>> getFields() {
        return fields;
    }

    //        /*pp*/ void _initFields(JCTreeFieldInfo<?>[] fields) {
    //            this.fields = new ListBuffer<JCTreeFieldInfo<?>>().appendArray(fields).toList();
    //        }

    /*pp*/ void _addField(JCTreeFieldInfo<?> field) {
        if (fields == null) fields = List.nil();
        this.fields = fields.append(field);
    }

    public JCTreeFieldInfo<?> getField(String name) {
        JCTreeFieldInfo<?> res = null;
        for (JCTreeFieldInfo<?> f : fields) {
            if (f.fieldName.equals(name)) {
                res = f;
                break;
            }
        }
        return res;
    }

    public Class<? extends JCTree> getJcTreeClass() {
        return jcTreeClass;
    }

    public String getName() {
        return name;
    }

    public JCTree.Tag getJcTreeTag() {
        return jcTreeTag;
    }



}