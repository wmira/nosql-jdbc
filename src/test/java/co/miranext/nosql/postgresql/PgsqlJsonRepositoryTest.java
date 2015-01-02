package co.miranext.nosql.postgresql;

import co.miranext.nosql.testbean.Personnel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.*;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PgsqlJsonRepositoryTest {





    private String channelJson = "{\"name\": \"Default Channel\", \"id\":\"X123\"}";
    private String personnelJson = "{\"fullname\": \"Some Person\", \"id\":\"P456\", \"defaultChannelId\": \"X123\" }";
    @Before
    public void setup() {

    }

    @Test
    public void testFind() throws Exception {

        final String SQL = "SELECT a.data a1 , a.row_id a2 , b.data b1 FROM personnel a LEFT OUTER JOIN channel b ON b.data->>'id'=a.data->>'defaultChannelId' WHERE a.data->>'id'=?";
        ResultSet rs = Mockito.mock(ResultSet.class);
        Connection con = Mockito.mock(Connection.class);
        PreparedStatement pstmt = Mockito.mock(PreparedStatement.class);
        DataSource dataSource = Mockito.mock(DataSource.class);

        when(dataSource.getConnection()).thenReturn(con);
        when(con.prepareStatement(SQL)).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getString("a1")).thenReturn(personnelJson);
        when(rs.getInt("a2")).thenReturn(5);
        when(rs.getString("b1")).thenReturn(channelJson);


        PgsqlJsonRepository jsonRepository = new PgsqlJsonRepository(dataSource);
        Personnel personnel = jsonRepository.find(Personnel.class, "P456");
        Mockito.verify(con).prepareStatement(SQL);



        assertNotNull(personnel);
        assertNotNull(personnel.getDefaultChannel());
        assertEquals(personnel.getDefaultChannelId(),"X123");

    }
}
