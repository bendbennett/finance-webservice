package zz.yy.xx.webservices.finance.models;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA.
 * User: ben
 * Date: 08/06/2012
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:root-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleJdbcDaoSupportTestCase {

    @Autowired
    SimpleJdbcDaoSupport simpleJdbcDaoSupport;

    @Test
    public void testSimpleJdbcDaoSupportAutowiredWithDatasource() {

        SQLServerDataSource dataSource = (SQLServerDataSource) simpleJdbcDaoSupport.getDataSource();
        Assert.assertEquals(SQLServerDataSource.class, dataSource.getClass());
    }
}
