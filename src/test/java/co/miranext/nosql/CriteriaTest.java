package co.miranext.nosql;

import co.miranext.nosql.postgresql.PgsqlJsonRepository;
import co.miranext.nosql.postgresql.PgsqlJsonFieldCriterion;
import co.miranext.nosql.query.SQLObjectQuery;
import co.miranext.nosql.sql.SQLBuilder;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
/**
 *
 */
public class CriteriaTest {

    @Test
    public void testCriteriaSQLStrings() {

        ColumnCriterion cr = new ColumnCriterion("column","test");

        assertEquals("column",cr.getColumn());
        assertEquals("test",cr.getValue());
        assertEquals("column=?",cr.toSQLString(null));

        FieldCriterion fieldCriterion = new FieldCriterion("id","abc"); //new PsqlJsonFieldCriterion("column","id","abc");

        Criteria criteria = new Criteria();
        criteria.add(cr);
        criteria.add(fieldCriterion);


        Samp document = new Samp();
        Map<String,FieldAccess> fields = BeanUtils.getFieldsFromObject(document);
        assertEquals("column=?" + SQLBuilder.SQL_AND_DELIMITER + "data->>'id'=?", SQLObjectQuery.toSQLCriteriaFilter(null,DocumentMeta.fromAnnotation(document.getClass()),criteria,
                PgsqlJsonRepository.CRITERION_TRANSFORMER));

        ColumnExtra extra = new ColumnExtra("col");
        ColumnExtra extraAuto = new ColumnExtra("auto:record_id");

        assertEquals("col",extra.getColumn());
        assertFalse(extra.isAuto());


        assertEquals("record_id", extraAuto.getColumn());
        assertTrue(extraAuto.isAuto());
    }

    @Test
    public void testSqlBuilder() {


        Criteria criteria = new Criteria();
        criteria.add(new PgsqlJsonFieldCriterion("column","id","abc"));

        DocumentMeta meta = new DocumentMeta("tbl","data","id",new String[]{"auto:record","account_id"});

        String builderSelectRes = "SELECT data  , record , account_id FROM tbl  WHERE column->>'id'=?";

        assertEquals(builderSelectRes,SQLBuilder.createSqlSelect(meta,criteria,null));

    }
}

@Document(table="samp")
class Samp {

}
