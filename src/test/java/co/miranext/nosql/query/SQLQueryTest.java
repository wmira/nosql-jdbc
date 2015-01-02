package co.miranext.nosql.query;

import co.miranext.nosql.testbean.Parent;

import co.miranext.nosql.testbean.Personnel;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 *
 */
public class SQLQueryTest {

    @Test
    public void testSQLAlias() {

        SQLObjectQuery queryBuilder = new SQLObjectQuery(Parent.class);
        String query = queryBuilder.toSQLSelectQuery();
        assertEquals("SELECT a.data a1 , b.data b1 FROM parent a LEFT OUTER JOIN single_child b ON b.data->>'id'=a.data->>'singleChildId'",query);



    }


    @Test
    public void testSQLAlias2() {

        SQLObjectQuery<Personnel> queryBuilder = new SQLObjectQuery<>(Personnel.class);
        String query = queryBuilder.toSQLSelectQuery();
        String expected = "SELECT a.data a1 , a.row_id a2 , b.data b1 FROM personnel a LEFT OUTER JOIN channel b ON b.data->>'id'=a.data->>'defaultChannelId'";

        assertEquals(expected,query);
    }
}
