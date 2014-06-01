package zz.yy.xx.webservices.finance.controllers;


import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zz.yy.xx.webservices.finance.models.Parameters;
import zz.yy.xx.webservices.finance.models.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * using different methods to return either XML or JSON
 * could use switch inside method to decide on whether to return XML or JSON but then can't make use of "produces" annotation
 */
@Controller
public class IndexController {

    private Repository repository;
    private String ping = "<ping>pong</ping>";

    @Autowired
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/ping",
            method = RequestMethod.GET,
            headers = {"content-type=application/xml"},
            produces = {"application/xml"})
    @ResponseBody
    public String pingXml() {
        return ping;
    }

    @RequestMapping(value = "/ping",
            method = RequestMethod.GET,
            headers = {"content-type=application/json"},
            produces = {"application/json"})
    @ResponseBody
    public String pingJson() {
        return convertXmlStringToJson(ping);
    }

    @RequestMapping(value = "/{prefix:costcentres|projects|users}",
            method = RequestMethod.GET,
            headers = {"content-type=application/xml"},
            produces = {"application/xml"})
    @ResponseBody
    public String getCostCentresOrProjectsOrUsersXml(@PathVariable("prefix") String prefix) throws IOException {
        String endPoint = generateEndPointString(new String[]{prefix});
        return repository.getDataWithNoParameters(endPoint);
    }

    @RequestMapping(value = "/{prefix:costcentres|projects|users}",
            method = RequestMethod.GET,
            headers = {"content-type=application/json"},
            produces = {"application/json"})
    @ResponseBody
    public String getCostCentresOrProjectsOrUsersJson(@PathVariable("prefix") String prefix) throws IOException {
        String endPoint = generateEndPointString(new String[]{prefix});
        String xml = repository.getDataWithNoParameters(endPoint);
        return convertXmlStringToJson(xml);
    }

    @RequestMapping(value = "/{prefix:users}/{postfix:costcentres}",
            method = RequestMethod.GET,
            headers = {"content-type=application/xml"},
            produces = {"application/xml"})
    @ResponseBody
    public String getAllCostCentreViewersXml(@PathVariable("prefix") String prefix,
                                             @PathVariable("postfix") String postfix) throws IOException {
        String endPoint = generateEndPointString(new String[]{prefix, postfix});
        return repository.getDataWithNoParameters(endPoint);
    }

    @RequestMapping(value = "/{prefix:users}/{postfix:costcentres}",
            method = RequestMethod.GET,
            headers = {"content-type=application/json"},
            produces = {"application/json"})
    @ResponseBody
    public String getAllCostCentreViewersJson(@PathVariable("prefix") String prefix,
                                              @PathVariable("postfix") String postfix) throws IOException {
        String endPoint = generateEndPointString(new String[]{prefix, postfix});
        String xml = repository.getDataWithNoParameters(endPoint);
        return convertXmlStringToJson(xml);
    }

    @RequestMapping(value = {
            "/{prefix:costcentres}/{ids:[a-zA-Z0-9|]+}/{postfix:projects|projects-summaries|users}",
            "/{prefix:projects}/{ids:[a-zA-Z0-9|]+}/{postfix:summaries|balances|transactions}",
            "/{prefix:users}/{ids:[a-zA-Z0-9|]+}/{postfix:projects|projects-summaries}",
            "/{prefix:users}/{ids:[a-zA-Z0-9|]+}/{postfix:costcentres-access}"},
            method = RequestMethod.GET,
            headers = {"content-type=application/xml"},
            produces = {"application/xml"})
    @ResponseBody
    public String getCostCentresOrProjectsOrUsersEndpointsXml(@PathVariable("prefix") String prefix,
                                                              @PathVariable("ids") String ids,
                                                              @PathVariable("postfix") String postfix,
                                                              HttpServletRequest request) throws IOException {
        String endPoint = generateEndPointString(new String[]{prefix, postfix});
        Parameters parameters = getParameterObjectFromSession(request);
        return repository.getDataWithParameters(endPoint, ids, parameters);
    }

    @RequestMapping(value = {
            "/{prefix:costcentres}/{ids:[a-zA-Z0-9|]+}/{postfix:projects|projects-summaries|users}",
            "/{prefix:projects}/{ids:[a-zA-Z0-9|]+}/{postfix:summaries|balances|transactions}",
            "/{prefix:users}/{ids:[a-zA-Z0-9|]+}/{postfix:projects|projects-summaries}",
            "/{prefix:users}/{ids:[a-zA-Z0-9|]+}/{postfix:costcentres-access}"},
            method = RequestMethod.GET,
            headers = {"content-type=application/json"},
            produces = {"application/json"})
    @ResponseBody
    public String getCostCentresOrProjectsOrUsersEndpointsJson(@PathVariable("prefix") String prefix,
                                                               @PathVariable("ids") String ids,
                                                               @PathVariable("postfix") String postfix,
                                                               HttpServletRequest request) throws IOException {
        String endPoint = generateEndPointString(new String[]{prefix, postfix});
        Parameters parameters = getParameterObjectFromSession(request);
        String xml = repository.getDataWithParameters(endPoint, ids, parameters);
        return convertXmlStringToJson(xml);
    }


    private String generateEndPointString(String[] endPoint) {
        StringBuffer endPointString = new StringBuffer();

        for (String temp : endPoint) {
            endPointString.append("." + temp);
        }

        return endPointString.toString();
    }

    private Parameters getParameterObjectFromSession(HttpServletRequest request) {
        Parameters parameters = (Parameters) request.getSession().getAttribute(request.getParameter("apiKey") + request.getRemoteHost());
        return parameters;
    }

    private String convertXmlStringToJson(String xml) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        JSON json = xmlSerializer.read(xml);
        return json.toString(2);
    }

    @ExceptionHandler(Exception.class)
    public void handleException(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}
