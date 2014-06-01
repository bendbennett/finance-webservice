package zz.yy.xx.webservices.finance.models;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by IntelliJ IDEA.
 * User: ben
 * Date: 08/06/2012
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:root-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DataSourceTestCase {

    @Autowired
    SQLServerDataSource dataSource;

    @Test
    public void testDataSourceAutowiredWithCorrectValuesFromPropertiesFile() {

        String serverName = dataSource.getServerName();
        Assert.assertEquals("Finance.xx.yy.zz", serverName);

        int portNumber = dataSource.getPortNumber();
        Assert.assertEquals(1433, portNumber);

        String databaseName = dataSource.getDatabaseName();
        Assert.assertEquals("xxx", databaseName);

        String user = dataSource.getUser();
        Assert.assertEquals("yyy", user);
    }
}
