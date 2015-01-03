package co.miranext.nosql;

import org.boon.core.reflection.fields.FieldAccess;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * DocumentRef annotation are added on the actual class
 *
 *
 */
public class DocumentRefMeta {

    private final String refIdFieldName;
    private final String fieldName;
    private final DocumentMeta meta;
    private final Class type;
    private final Class fieldType;

    /**
     *
     *
     * @param docType The document
     * @param fieldType The field
     * @param fieldName
     * @param refIdFieldName
     */
    public DocumentRefMeta(final Class docType,final Class fieldType,final  String fieldName,final  String refIdFieldName) {

        if ( docType.equals(Void.class) ) {
            this.type = fieldType;
        } else {
            type = docType;
        }
        this.fieldType =fieldType;
        this.refIdFieldName = refIdFieldName;
        this.fieldName = fieldName;
        this.meta = DocumentMeta.fromAnnotation(this.type);

    }


    public Class document() {
        return this.type;
    }
    public DocumentMeta getMeta() {
        return meta;
    }


    public String getRefIdFieldName() {
        return refIdFieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    /**
     * FIXME: This should be cached!
     *
     * @param fields
     * @return
     */
    public static Map<String,DocumentRefMeta> lookupDocumentMetaRefs(final Map<String,FieldAccess> fields) {

        Map<String,DocumentRefMeta> refMetas = null;
        for ( String beanField : fields.keySet() ) {

            FieldAccess fa = fields.get(beanField);
            Field field = fa.getField();
            if ( field == null ) {
                continue; //not actual field
            }
            DocumentRef docRef = field.getAnnotation(DocumentRef.class);
            if (  docRef != null ) {
                if ( refMetas == null ) {
                    refMetas = new HashMap<>();
                }
                DocumentRefMeta refMeta = new DocumentRefMeta(docRef.document(),field.getType(),beanField,beanField + "Id" );
                refMetas.put(beanField,refMeta);
            }

        }
        return refMetas;
    }
}
