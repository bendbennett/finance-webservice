package zz.yy.xx.webservices.finance.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import static org.mockito.Mockito.*;

@ContextConfiguration(locations = {"classpath:root-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RequestMappingTestCase {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private IndexController controller;
    private AnnotationMethodHandlerAdapter adapter;

    @Before
    public void setUp() {

        controller = mock(IndexController.class);
        adapter = new AnnotationMethodHandlerAdapter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testPingRequestCallsPingXml() throws Exception {

        request.setRequestURI("/ping");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).pingXml();
    }

    @Test
    public void testPingRequestCallsPingJson() throws Exception {

        request.setRequestURI("/ping");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).pingJson();
    }

    @Test
    public void testCostCentresRequestCallsGetCostCentresOrProjectsOrUsersXml() throws Exception {

        request.setRequestURI("/costcentres");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersXml("costcentres");
    }

    @Test
    public void testProjectsRequestCallsGetCostCentresOrProjectsOrUsersXml() throws Exception {

        request.setRequestURI("/projects");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersXml("projects");
    }

    @Test
    public void testUsersRequestCallsGetCostCentresOrProjectsOrUsersXml() throws Exception {

        request.setRequestURI("/users");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersXml("users");
    }

    @Test
    public void testCostCentresRequestCallsGetCostCentresOrProjectsOrUsersJson() throws Exception {

        request.setRequestURI("/costcentres");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersJson("costcentres");
    }

    @Test
    public void testProjectsRequestCallsGetCostCentresOrProjectsOrUsersJson() throws Exception {

        request.setRequestURI("/projects");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersJson("projects");
    }

    @Test
    public void testUsersRequestCallsGetCostCentresOrProjectsOrUsersJson() throws Exception {

        request.setRequestURI("/users");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersJson("users");
    }

    @Test
    public void testCostCentresProjectsRequestCallsGetCostCentresData_whenContentTypeXml() throws Exception {

        request.setRequestURI("/costcentres/123abc/projects");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsXml("costcentres", "123abc", "projects", request);
    }

    @Test
    public void testCostCentresProjectsSummariesRequestCallsGetCostCentresData_whenContentTypeXml() throws Exception {

        request.setRequestURI("/costcentres/123abc/projects-summaries");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsXml("costcentres", "123abc", "projects-summaries", request);
    }

    @Test
    public void testCostCentresUsersRequestCallsGetCostCentresData_whenContentTypeXml() throws Exception {

        request.setRequestURI("/costcentres/123abc/users");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsXml("costcentres", "123abc", "users", request);
    }

    @Test
    public void testCostCentresProjectsRequestCallsGetCostCentresData_whenContentTypeJson() throws Exception {

        request.setRequestURI("/costcentres/123abc/projects");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsJson("costcentres", "123abc", "projects", request);
    }

    @Test
    public void testCostCentresProjectsSummariesRequestCallsGetCostCentresData_whenContentTypeJson() throws Exception {

        request.setRequestURI("/costcentres/123abc/projects-summaries");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsJson("costcentres", "123abc", "projects-summaries", request);
    }

    @Test
    public void testCostCentresUsersRequestCallsGetCostCentresData_whenContentTypeJson() throws Exception {

        request.setRequestURI("/costcentres/123abc/users");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsJson("costcentres", "123abc", "users", request);
    }

    @Test
    public void testProjectsSummariesRequestCallsProjectsXml_whenContentTypeXml() throws Exception {

        request.setRequestURI("/projects/123abc/summaries");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsXml("projects", "123abc", "summaries", request);
    }

    @Test
    public void testProjectsBalancesRequestCallsProjectsXml_whenContentTypeXml() throws Exception {

        request.setRequestURI("/projects/123abc/balances");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsXml("projects", "123abc", "balances", request);
    }

    @Test
    public void testProjectsTransactionsRequestCallsProjectsXml_whenContentTypeXml() throws Exception {

        request.setRequestURI("/projects/123abc/transactions");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsXml("projects", "123abc", "transactions", request);
    }

    @Test
    public void testProjectsSummariesRequestCallsProjectsJson_whenContentTypeJson() throws Exception {

        request.setRequestURI("/projects/123abc/summaries");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsJson("projects", "123abc", "summaries", request);
    }

    @Test
    public void testProjectsBalancesRequestCallsProjectsJson_whenContentTypeXml() throws Exception {

        request.setRequestURI("/projects/123abc/balances");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsJson("projects", "123abc", "balances", request);
    }

    @Test
    public void testProjectsTransactionsRequestCallsProjectsJson_whenContentTypeJson() throws Exception {

        request.setRequestURI("/projects/123abc/transactions");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsJson("projects", "123abc", "transactions", request);
    }

    @Test
    public void testUsersCostCentresRequestCallsUsersAllCostCentresXml_whenContentTypeXml() throws Exception {

        request.setRequestURI("/users/costcentres");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getAllCostCentreViewersXml("users", "costcentres");
    }

    @Test
    public void testUsersCostCentresRequestCallsUsersCostCentresXml_whenContentTypeXml() throws Exception {

        request.setRequestURI("/users/abcdef|ghijkl/costcentres-access");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsXml("users", "abcdef|ghijkl", "costcentres-access", request);
    }

    @Test
    public void testUsersProjectsRequestCallsUsersXml_whenContentTypeXml() throws Exception {

        request.setRequestURI("/users/123abc/projects");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsXml("users", "123abc", "projects", request);
    }

    @Test
    public void testUsersProjectsSummariesRequestCallsUsersXml_whenContentTypeXml() throws Exception {

        request.setRequestURI("/users/123abc/projects-summaries");
        request.setMethod("GET");
        request.addHeader("content-type", "application/xml");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsXml("users", "123abc", "projects-summaries", request);
    }

    @Test
    public void testUsersCostCentresRequestCallsUsersAllCostCentresJson_whenContentTypeJson() throws Exception {

        request.setRequestURI("/users/costcentres");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getAllCostCentreViewersJson("users", "costcentres");
    }

    @Test
    public void testUsersProjectsRequestCallsUsersJson_whenContentTypeJson() throws Exception {

        request.setRequestURI("/users/123abc/projects");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsJson("users", "123abc", "projects", request);
    }

    @Test
    public void testUsersProjectsSummariesRequestCallsUsersJson_whenContentTypeJson() throws Exception {

        request.setRequestURI("/users/123abc/projects-summaries");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsJson("users", "123abc", "projects-summaries", request);
    }

    @Test
    public void testUsersCostCentresRequestCallsUsersCostCentresJson_whenContentTypeJson() throws Exception {

        request.setRequestURI("/users/abcdef|ghijkl/costcentres-access");
        request.setMethod("GET");
        request.addHeader("content-type", "application/json");

        adapter.handle(request, response, controller);
        verify(controller, times(1)).getCostCentresOrProjectsOrUsersEndpointsJson("users", "abcdef|ghijkl", "costcentres-access", request);
    }
}
