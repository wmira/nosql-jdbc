package co.miranext.nosql;

import co.miranext.nosql.testbean.Parent;
import co.miranext.nosql.testbean.SingleChild;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Map;

/**
 * Test all the documentref stuff
 *
 */
public class DocumentRefTest {

    @Test
    public void testDocumentRefMeta() {

        Class cls = Parent.class;

        Map<String,FieldAccess> fields = BeanUtils.getFieldsFromObject(cls);

        Map<String,DocumentRefMeta> metas = DocumentRefMeta.lookupDocumentMetaRefs(fields);

        assertNotNull("should not be null",metas);
        assertEquals("Should be equal to 1", 1, metas.size());


        DocumentRefMeta refMeta = metas.get("singleChild");
        assertNotNull("key should be singleChild",refMeta);
        assertTrue(refMeta.getDocumentRef().document().equals(SingleChild.class));
        assertEquals("singleChild",refMeta.getFieldName());
        assertEquals("singleChildId", refMeta.getRefIdFieldName());

    }
}
