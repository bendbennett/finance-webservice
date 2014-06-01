package zz.yy.xx.webservices.finance.models;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;

import java.io.UnsupportedEncodingException;

import static org.mockito.Mockito.*;

@ContextConfiguration(locations = {"classpath:root-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthenticationTestCase {

    @Autowired
    private Authentication authentication;

    @Test
    public void testGetDataForServiceCallsRestTemplateGetForObjectWithCorrectParameters() throws UnsupportedEncodingException {
        RestOperations mockedRestOperations = mock(RestOperations.class);
        when(mockedRestOperations.getForObject(anyString(), eq(ParameterHolder.class))).thenReturn(new ParameterHolder());
        authentication.setRestTemplate(mockedRestOperations);
        authentication.getDataForService("apiKey", "consumer");
        verify(mockedRestOperations, times(1)).getForObject("http://xx.yy.zz/auth/?apiKey=apiKey&consumer=consumer&format=json", ParameterHolder.class);
    }
}
