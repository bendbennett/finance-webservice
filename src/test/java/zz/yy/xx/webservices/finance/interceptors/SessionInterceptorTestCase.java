package zz.yy.xx.webservices.finance.interceptors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClientException;
import zz.yy.xx.webservices.finance.models.Authentication;
import zz.yy.xx.webservices.finance.models.CacheModifier;
import zz.yy.xx.webservices.finance.models.ParameterHolder;
import zz.yy.xx.webservices.finance.models.Parameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.mockito.Mockito.*;


@ContextConfiguration(locations = {"classpath:root-context.xml"})
@RunWith(PowerMockRunner.class)
public class SessionInterceptorTestCase {

    private SessionInterceptor sessionInterceptor;
    private HttpServletRequest mockRequest;
    private HttpSession mockSession;
    private HttpServletResponse mockResponse;
    private Object handler;
    private Authentication mockAuthentication;
    private CacheModifier mockCacheModifier;

    @Before
    public void setUp() {
        sessionInterceptor = new SessionInterceptor();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        handler = new Object();
        mockAuthentication = mock(Authentication.class);
        mockCacheModifier = mock(CacheModifier.class);
        sessionInterceptor.setCacheModifier(mockCacheModifier);
        when(mockRequest.getServletPath()).thenReturn("trousers");
    }

    @Test
    public void testWhenContentTypeNotSet_thenResponseSendsUnsupportedMediaTypeAndReturnsFalse() throws IOException {

        when(mockRequest.getContentType()).thenReturn(null);

        boolean bool = sessionInterceptor.preHandle(mockRequest, mockResponse, handler);

        verify(mockResponse, times(1)).sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        Assert.assertEquals(false, bool);
    }

    @Test
    public void testWhenContentTypeSetToUnsupportedType_thenResponseSendsUnsupportedMediaTypeAndReturnsFalse() throws IOException {

        when(mockRequest.getContentType()).thenReturn("text/html");

        boolean bool = sessionInterceptor.preHandle(mockRequest, mockResponse, handler);

        verify(mockResponse, times(1)).sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        Assert.assertEquals(false, bool);
    }

    @Test
    public void testWhenPingRequest_thenReturnTrue() throws IOException {
        when(mockRequest.getContentType()).thenReturn("application/xml");
        when(mockRequest.getServletPath()).thenReturn("/ping");

        boolean bool = sessionInterceptor.preHandle(mockRequest, mockResponse, handler);

        Assert.assertEquals(true, bool);
    }

    @Test
    public void testWhenRetrievingParametersAttributeOfWrongTypeFromSession_thenResponseSendErrorAndReturnsFalse() throws IOException {

        when(mockRequest.getContentType()).thenReturn("application/xml");
        when(mockRequest.getParameter("apiKey")).thenReturn("123");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute(anyString())).thenReturn(new String());
        when(mockRequest.getRemoteHost()).thenReturn("0.1.2.3");

        boolean bool = sessionInterceptor.preHandle(mockRequest, mockResponse, handler);

        verify(mockResponse, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
        Assert.assertEquals(false, bool);
    }

    @Test
    public void testWhenParametersObjectContainingNonEmptyStrings_thenReturnTrue() throws IOException {

        Parameters parameters = new Parameters();
        parameters.setUsername("abcdef");
        parameters.setPayrollNumber("123456");

        when(mockRequest.getContentType()).thenReturn("application/xml");
        when(mockRequest.getParameter("apiKey")).thenReturn("123");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute(anyString())).thenReturn(parameters);

        boolean bool = sessionInterceptor.preHandle(mockRequest, mockResponse, handler);

        verify(mockCacheModifier, times(1)).setTimeToLive();
        Assert.assertEquals(true, bool);
    }

    @Test
    public void testWhenSuccessfulCallToAuthenticationGetDataForService_thenSessionPopulatedAndReturnTrue() throws IOException {

        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockRequest.getParameter("apiKey")).thenReturn("xyz");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute(anyString())).thenReturn(null);
        when(mockRequest.getRemoteHost()).thenReturn("0.1.2.3");
        ParameterHolder mockParameterHolder = mock(ParameterHolder.class);
        Parameters parameters = new Parameters();
        when(mockParameterHolder.getParameters()).thenReturn(parameters);
        when(mockAuthentication.getDataForService(anyString(), anyString())).thenReturn(mockParameterHolder);

        sessionInterceptor.setAuthentication(mockAuthentication);

        boolean bool = sessionInterceptor.preHandle(mockRequest, mockResponse, handler);

        verify(mockCacheModifier, times(1)).setTimeToLive();
        verify(mockSession, times(1)).setAttribute("xyz0.1.2.3", parameters);
        Assert.assertEquals(true, bool);

    }

    @Test
    public void testWhenRestClientExceptionThrown_thenResponseSendForbidden403AndReturnFalse() throws IOException {

        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockRequest.getParameter("apiKey")).thenReturn("xyz");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute(anyString())).thenReturn(null);
        when(mockRequest.getRemoteHost()).thenReturn("0.1.2.3");
        ParameterHolder mockParameterHolder = mock(ParameterHolder.class);
        Parameters parameters = new Parameters();
        when(mockParameterHolder.getParameters()).thenReturn(parameters);
        when(mockAuthentication.getDataForService(anyString(), anyString())).thenThrow(new RestClientException(""));

        sessionInterceptor.setAuthentication(mockAuthentication);

        boolean bool = sessionInterceptor.preHandle(mockRequest, mockResponse, handler);

        verify(mockResponse, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
        Assert.assertEquals(false, bool);
    }
}
