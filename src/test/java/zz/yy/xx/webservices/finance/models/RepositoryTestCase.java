package zz.yy.xx.webservices.finance.models;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * PowerMock required in order to be able to mock final getJdbcTemplate() method in SimpleJdbcDaoSupport class
 */

//@ContextConfig not used as directly creating appContext in setUp() owing to problems autowiring Repository which is required for testing caching
//@ContextConfiguration(locations = {"classpath:root-context.xml"})
@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleJdbcDaoSupport.class)
@PowerMockIgnore({"javax.management.*"})
public class RepositoryTestCase {

    private SimpleJdbcDaoSupport mockSimpleJdbcDaoSupport;
    private JdbcTemplate mockJdbcTemplate;
    private Repository repository;

    @Before
    public void setUp() throws IOException {
        List<String> xmlList = new ArrayList<String>();
        xmlList.add("<root></root>");

        mockSimpleJdbcDaoSupport = PowerMockito.spy(new SimpleJdbcDaoSupport());
        mockJdbcTemplate = mock(JdbcTemplate.class);
        when(mockSimpleJdbcDaoSupport.getJdbcTemplate()).thenReturn(mockJdbcTemplate);
        doReturn(xmlList).when(mockJdbcTemplate).query(anyString(), (RowMapper<Object>) Matchers.anyObject());

        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:root-context.xml");
        repository = (Repository) ctx.getBean("repository");
        repository.setSimpleJdbcDaoSupport(mockSimpleJdbcDaoSupport);
    }

    @Test
    public void testRepositoryCallsJdbcTemplateQueryOnce() throws IOException {
        String expectedXml = "<root></root>";
        String endpoint = ".costcentres";
        Assert.assertEquals(expectedXml, repository.getDataWithNoParameters(endpoint));
        verify(mockJdbcTemplate, times(1)).query(anyString(), (RowMapper<Object>) Matchers.anyObject());
    }

    @Test
    public void testRepositoryStripsIllegalCharactersFromXml() throws IOException {
        List<String> xmlList = new ArrayList<String>();
        xmlList.add("<root>!@£$%^&*()_+</root>");
        doReturn(xmlList).when(mockJdbcTemplate).query(anyString(), (RowMapper<Object>) Matchers.anyObject());

        //% and & should appear, but expected to see that * ( ) + removed on basis of regex
        String expectedXml = "<root>%&*()+</root>";
        String endpoint = ".costcentres";
        Assert.assertEquals(expectedXml, repository.getDataWithNoParameters(endpoint));
    }

    @Test
    public void testRepositoryReturnsEmptyXml_whenNoDataReturnedFromDatabse() throws IOException {
        List<String> xmlList = new ArrayList<String>();
        doReturn(xmlList).when(mockJdbcTemplate).query(anyString(), (RowMapper<Object>) Matchers.anyObject());

        String expectedXml = "<empty></empty>";
        String endpoint = ".costcentres";
        Assert.assertEquals(expectedXml, repository.getDataWithNoParameters(endpoint));
    }

    @Test(expected = NullPointerException.class)
    public void testRepositoryThrowsNullPointerException_whenStoredProcNotFoundInPropertiesFile() throws NullPointerException {
        repository.getDataWithNoParameters(".nonExistentStoredProc");
    }

    @Test(expected = NullPointerException.class)
    public void testRepositoryThrowsNullPointerException_whenStoredProcAuthorisationParamNotFoundInPropertiesFile() throws NullPointerException {
        repository.getDataWithParameters("users", "abc", new Parameters());
    }

    @Test
    public void testGetXmlCalledOnce_whenCacheEmpty_thenNoMoreTimesForSameEndpoint() {
        repository.getDataWithNoParameters(".costcentres");
        verify(mockSimpleJdbcDaoSupport, times(1)).getJdbcTemplate();
        repository.getDataWithNoParameters(".costcentres");
        verifyNoMoreInteractions(mockSimpleJdbcDaoSupport);
        repository.getDataWithNoParameters(".costcentres");
        verifyNoMoreInteractions(mockSimpleJdbcDaoSupport);
    }

    @Test
    public void testGetXmlCalledOnceForEachDifferentEndpoint_whenCacheEmpty() {
        repository.getDataWithNoParameters(".costcentres");
        repository.getDataWithNoParameters(".projects");
        repository.getDataWithNoParameters(".users");
        repository.getDataWithNoParameters(".users");
        verify(mockSimpleJdbcDaoSupport, times(3)).getJdbcTemplate();
    }

    @Test
    public void testGetXmlCalledOnce_whenCacheEmpty_thenNoMoreTimesForSameEndpoint_withParameters() {
        Parameters mockParameters = mock(Parameters.class);
        repository.getDataWithParameters(".users.costcentres-access", "abcdef", mockParameters);
        verify(mockSimpleJdbcDaoSupport, times(1)).getJdbcTemplate();
        repository.getDataWithParameters(".users.costcentres-access", "abcdef", mockParameters);
        verifyNoMoreInteractions(mockSimpleJdbcDaoSupport);
    }
}
