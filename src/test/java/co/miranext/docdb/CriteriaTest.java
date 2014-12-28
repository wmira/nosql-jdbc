package co.miranext.docdb;

import co.miranext.docdb.postgresql.PsqlJsonFieldCriterion;
import org.junit.Test;
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
        assertEquals("column=?",cr.toSQLString());

        FieldCriterion fieldCriterion = new PsqlJsonFieldCriterion("column","id","abc");

        assertEquals("id",fieldCriterion.getField());
        assertEquals("abc",fieldCriterion.getValue());
        assertEquals("column->>'id'=?",fieldCriterion.toSQLString());


        Criteria criteria = new Criteria();
        criteria.add(cr);
        criteria.add(fieldCriterion);

        assertEquals("column=?" + Criteria.SQL_STMT_DELIM + "column->>'id'=?",criteria.toSQLString());

    }
}
