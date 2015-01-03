package co.miranext.nosql;

import co.miranext.nosql.testbean.Channel;
import co.miranext.nosql.testbean.DocOverrideTable;
import co.miranext.nosql.testbean.Parent;
import co.miranext.nosql.testbean.ParentWithExtras;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class DocumentMetaTest {

    @Test
    public void testDocumentMeta() {

        DocumentMeta meta = DocumentMeta.fromAnnotation(Parent.class);

        assertEquals("parent",meta.getTableName());
        assertEquals("data",meta.getColumnName());
        assertEquals("id",meta.getIdField());
        assertArrayEquals(new String[]{}, meta.getExtras());
        assertArrayEquals(new ColumnExtra[]{}, meta.getColumnExtras());

        DocumentMeta meta2 = DocumentMeta.fromAnnotation(DocOverrideTable.class);
        assertEquals("someother_tablename",meta2.getTableName());
    }


    @Test
    public void testDocumentMetaExtras() {

        DocumentMeta meta = DocumentMeta.fromAnnotation(ParentWithExtras.class);

        assertEquals("parent_with_extras",meta.getTableName());
        assertEquals("data",meta.getColumnName());
        assertEquals("id",meta.getIdField());
        assertArrayEquals(new String[]{"record_id","owner_id"}, meta.getExtras());
        assertArrayEquals(new ColumnExtra[]{new ColumnExtra("auto:record_id"),new ColumnExtra("owner_id")}, meta.getColumnExtras());

        assertTrue(meta.getColumnExtras()[0].isAuto());

    }
}
