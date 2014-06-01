package zz.yy.xx.webservices.aspects;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zz.yy.xx.webservices.finance.controllers.IndexController;
import zz.yy.xx.webservices.finance.models.Repository;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ContextConfiguration(locations = {"classpath:root-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class LoggingAspectsTestCase {

    @Autowired
    private LoggingAspects loggingAspects;
    @Autowired
    private IndexController indexController;
    private Log mockedLogger;


    @Before
    public void setup() throws IOException {
        Repository mockedRepository = mock(Repository.class);
        when(mockedRepository.getDataWithNoParameters(anyString())).thenReturn("<fake>some data in here</fake>");
        indexController.setRepository(mockedRepository);

        mockedLogger = mock(Log.class);
        when(mockedLogger.isInfoEnabled()).thenReturn(true);
        loggingAspects.setLogger(mockedLogger);
    }

    @Test
    public void testLoggingFiresOnControllerMethod() throws Exception {
        indexController.getCostCentresOrProjectsOrUsersJson("projects");
        verify(mockedLogger, times(1)).isInfoEnabled();
        verify(mockedLogger, times(2)).info(anyString());
        verify(mockedLogger, times(1)).info("return value = [\"some data in here\"]");
    }

}
