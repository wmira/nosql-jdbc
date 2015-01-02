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

    private DocumentRef documentRef;
    private String refIdFieldName;
    private String fieldName;
    private DocumentMeta meta;

    public DocumentRefMeta(final DocumentRef documentRef,final  String fieldName,final  String refIdFieldName) {
        this.documentRef = documentRef;
        this.refIdFieldName = refIdFieldName;
        this.fieldName = fieldName;
        this.meta = DocumentMeta.fromAnnotation(documentRef.document());
    }

    public DocumentMeta getMeta() {
        return meta;
    }

    public DocumentRef getDocumentRef() {
        return documentRef;
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
                DocumentRefMeta refMeta = new DocumentRefMeta(docRef,beanField,beanField + "Id" );
                refMetas.put(beanField,refMeta);
            }

        }
        return refMetas;
    }
}
