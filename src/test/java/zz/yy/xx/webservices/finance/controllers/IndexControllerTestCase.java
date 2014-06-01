package zz.yy.xx.webservices.finance.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zz.yy.xx.webservices.finance.models.Parameters;
import zz.yy.xx.webservices.finance.models.Repository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ContextConfiguration(locations = {"classpath:root-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class IndexControllerTestCase {

    private Repository mockedRepository;
    @Autowired
    private IndexController indexController;
    private HttpServletRequest mockedHttpServletRequest;
    private Parameters parameters;
    private final String fakeXml = "<fake>some data in here</fake>";
    private final String fakeJson = "[\"some data in here\"]";


    @Before
    public void setUp() throws IOException {
        parameters = new Parameters();
        mockedRepository = mock(Repository.class);
        when(mockedRepository.getDataWithNoParameters(anyString())).thenReturn(fakeXml);
        when(mockedRepository.getDataWithParameters(anyString(), anyString(), Matchers.<Parameters>anyObject())).thenReturn(fakeXml);
        indexController.setRepository(mockedRepository);
        mockedHttpServletRequest = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);
        when(mockedHttpServletRequest.getSession().getAttribute(anyString())).thenReturn(parameters);
    }

    @Test
    public void testGetCostCentresOrProjectsOrUsersXmlCallsGetDataWithNoParameters() throws IOException {
        String xml = indexController.getCostCentresOrProjectsOrUsersXml("prefix");
        verify(mockedRepository, times(1)).getDataWithNoParameters(".prefix");
        assertEquals(xml, fakeXml);
    }

    @Test
    public void testGetCostCentresOrProjectsOrUsersJsonCallsGetDataWithNoParameters() throws IOException {
        String json = indexController.getCostCentresOrProjectsOrUsersJson("prefix");
        verify(mockedRepository, times(1)).getDataWithNoParameters(".prefix");
        assertEquals(json, fakeJson);
    }

    @Test
    public void testGetAllCostCentreViewersXmlCallsGetDataWithNoParameters() throws IOException {
        String xml = indexController.getAllCostCentreViewersXml("prefix", "postfix");
        verify(mockedRepository, times(1)).getDataWithNoParameters(".prefix.postfix");
        assertEquals(xml, fakeXml);
    }

    @Test
    public void testGetAllCostCentreViewersJsonCallsGetDataWithNoParameters() throws IOException {
        String json = indexController.getAllCostCentreViewersJson("prefix", "postfix");
        verify(mockedRepository, times(1)).getDataWithNoParameters(".prefix.postfix");
        assertEquals(json, fakeJson);
    }

    @Test
    public void testGetCostCentresOrProjectsOrUsersEndpointsXmlCallsGetDataWithParameters() throws IOException {
        String xml = indexController.getCostCentresOrProjectsOrUsersEndpointsXml("prefix", "pathVariables", "postfix", mockedHttpServletRequest);
        verify(mockedRepository, times(1)).getDataWithParameters(".prefix.postfix", "pathVariables", parameters);
        assertEquals(xml, fakeXml);
    }

    @Test
    public void testGetCostCentresOrProjectsOrUsersEndpointsJsonCallsGetDataWithParameters() throws IOException {
        String json = indexController.getCostCentresOrProjectsOrUsersEndpointsJson("prefix", "pathVariables", "postfix", mockedHttpServletRequest);
        verify(mockedRepository, times(1)).getDataWithParameters(".prefix.postfix", "pathVariables", parameters);
        assertEquals(json, fakeJson);
    }
}
