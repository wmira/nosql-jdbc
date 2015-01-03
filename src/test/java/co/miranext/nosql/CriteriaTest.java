package co.miranext.nosql;

import co.miranext.nosql.criteria.ColumnCriterion;
import co.miranext.nosql.criteria.Criteria;
import co.miranext.nosql.criteria.FieldCriterion;
import co.miranext.nosql.postgresql.PgsqlJsonRepository;
import co.miranext.nosql.query.SQLObjectQuery;
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
        assertEquals("column=?" + SQLObjectQuery.SQL_AND_DELIMITER + "data->>'id'=?", SQLObjectQuery.toSQLCriteriaFilter(null,DocumentMeta.fromAnnotation(document.getClass()),criteria,
                PgsqlJsonRepository.CRITERION_TRANSFORMER));

        ColumnExtra extra = new ColumnExtra("col");
        ColumnExtra extraAuto = new ColumnExtra("auto:record_id");

        assertEquals("col",extra.getColumn());
        assertFalse(extra.isAuto());


        assertEquals("record_id", extraAuto.getColumn());
        assertTrue(extraAuto.isAuto());
    }


}

@Document(table="samp")
class Samp {

}
