package zz.yy.xx.webservices.finance.interceptors;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import zz.yy.xx.webservices.finance.models.Authentication;
import zz.yy.xx.webservices.finance.models.CacheModifier;
import zz.yy.xx.webservices.finance.models.ParameterHolder;
import zz.yy.xx.webservices.finance.models.Parameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * http://stackoverflow.com/questions/3230633/how-to-register-handler-interceptors-with-spring-mvc-3-0
 * http://stackoverflow.com/questions/4389224/is-it-possible-to-wire-a-spring-mvc-interceptor-using-annotations
 * <p/>
 * Only options to log return data from objects such as HttpServletRequest are:
 * 1. Use static logger (as below) - defeats purpose of AOP
 * 2. Push objects (e.g., HttpServletRequest) into Helper object then make method calls - unnatural way of coding
 * 3. Use full-blown AspectJ - seems like a lot of hassle (e.g., extra server config) and potentially troublesome
 * <p/>
 * Went with option 1 as a number of Spring classes use loggers defined within the classes
 */

@Component
public class SessionInterceptor extends HandlerInterceptorAdapter {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String apiKey;
    private String sessionKey;
    private Parameters parameters;
    private Authentication authentication;
    private CacheModifier cacheModifier;

    //not sure how to handle getting this to log using AOP (see above)
    private static final Logger logger = Logger.getLogger(SessionInterceptor.class);


    /**
     * Extends the HandlerInterceptorAdapter preHandle method() which is invoked on every call to every endpoint prior to calling Controller.
     * <p/>
     * Sequentially checks to see whether:
     * 0. verify content-type is set and is either application/xml or application/json
     * 1. apiKey is in request - if not throw 403
     * 2. Session contains parameters object for specific apiKey and if so, that both username and payroll number are populated - if so return true
     * 3. If parameters object is not in session, check that apiKey is part of the request - if not, issue bad request/unauthorised
     * 4. Verify parameters object contains username and payroll number - if not, issue bad request/unauthorised
     * 5. Add parameters object to session using key -> value where apiKey is key, parameters object is value
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  Object
     * @return boolean
     * @throws IOException
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        this.request = request;
        this.response = response;

        if (!isContentTypeSetAndCorrectType()) return false;
        if (isPingMethod()) return true;
        if (!isApiKeySet()) return false;
        if (!isParameterObjectInSessionOfCorrectType()) return false;
        cacheModifier.setTimeToLive();
        if (isParameterObjectInSessionCorrectlyPopulatedWithUsernameAndPayrollNumber()) return true;
        if (!authenticateAndPopulateSession()) return false;

        return true;
    }


    @Autowired
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }


    @Autowired
    public void setCacheModifier(CacheModifier cacheModifier) {
        this.cacheModifier = cacheModifier;
    }


    private boolean isContentTypeSetAndCorrectType() throws IOException {

        String contentType = request.getContentType();

        if (contentType == null) {
            logger.warn("contentType is not in request header");
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return false;
        }

        if (!contentType.equals("application/xml") && !contentType.equals("application/json")) {
            logger.warn("contentType is not application/xml or application/json");
            logger.warn("contentType = " + contentType);
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return false;
        }

        return true;
    }


    private boolean isPingMethod() {

        if (request.getServletPath().equals("/ping")) {
            return true;
        }

        return false;
    }


    private boolean isApiKeySet() throws IOException {

        //check apiKey appears in request parameters
        apiKey = request.getParameter("apiKey");

        if (null == apiKey) {
            logger.warn("apiKey is not in request parameters");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        logger.debug("apiKey = " + apiKey);
        logger.debug("consumer = " + request.getRemoteHost());
        logger.debug("sessionKey = " + apiKey + request.getRemoteHost());

        sessionKey = apiKey + request.getRemoteHost();

        return true;
    }


    private boolean isParameterObjectInSessionOfCorrectType() throws IOException {

        //retrieve "sessionKey" from session and verify that object of type Parameters
        try {
            parameters = (Parameters) request.getSession().getAttribute(sessionKey);

        } catch (ClassCastException classCastException) {
            logger.warn(classCastException.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }


    private boolean isParameterObjectInSessionCorrectlyPopulatedWithUsernameAndPayrollNumber() {

        //if "parameters" attribute set in session and contains object of type Parameters verify that username and password are populated
        if (parameters != null) {
            String username = parameters.getUsername();
            String payrollNumber = parameters.getPayrollNumber();

            if (username != null && payrollNumber != null) {
                logger.debug("parameters object retrieved from session, username=" + username + " and payrollNumber=" + payrollNumber);
                return true;
            }
        }

        return false;
    }


    private boolean authenticateAndPopulateSession() throws IOException {

        //if apiKey is in request parameters but "parameters" attribute is not set in session then call Authenticate using apiKey and IP address of server making this request
        if (parameters == null) {
            logger.debug("parameters object is not set in session");
            logger.debug("calling Authentication and then setting object in session if appropriate response");

            try {
                ParameterHolder parameterHolder = authentication.getDataForService(apiKey, request.getRemoteHost());
                parameters = parameterHolder.getParameters();
                request.getSession().setAttribute(sessionKey, parameters);

            } catch (RestClientException restClientException) {
                logger.warn(restClientException.getMostSpecificCause());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        return true;
    }
}
