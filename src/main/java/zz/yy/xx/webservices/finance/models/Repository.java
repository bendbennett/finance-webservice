package zz.yy.xx.webservices.finance.models;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


@Component
public class Repository {

    private String sqlStatement;
    @Autowired
    private SimpleJdbcDaoSupport simpleJdbcDaoSupport;
    private Resource resource;
    private final String resourceLocation = "/database.properties";
    private Properties props;
    private final String storedProcPrefix = "finance.storedProc";
    private List<Object> args;

    /**
     * Constructor
     * empty constructor required for proxy-based class wrapping
     * this is implictly created (by Java) even if not explicitly defined, but must be defined if using autowired constructor
     */
    public Repository() {
    }

    /**
     * Constructor
     *
     * @param simpleJdbcDaoSupport SimpleJdbcDaoSupport
     */
    @Autowired
    public Repository(SimpleJdbcDaoSupport simpleJdbcDaoSupport) throws IOException {
        this.simpleJdbcDaoSupport = simpleJdbcDaoSupport;
        resource = new ClassPathResource(resourceLocation);
        props = PropertiesLoaderUtils.loadProperties(resource);
    }


    public void setSimpleJdbcDaoSupport(SimpleJdbcDaoSupport simpleJdbcDaoSupport) {
        this.simpleJdbcDaoSupport = simpleJdbcDaoSupport;
    }

    @Cacheable("Repository")
    public String getDataWithNoParameters(String endPoint) throws NullPointerException {
        sqlStatement = getStoredProcedureName(endPoint);
        args = new ArrayList<Object>();

        return getXml();
    }

    @Cacheable("Repository")
    public String getDataWithParameters(String endPoint, String pathVariable, Parameters parameters) throws NullPointerException {
        sqlStatement = getStoredProcedureName(endPoint);

        args = new ArrayList<Object>();
        args.add(String.valueOf(pathVariable));

        if (isRequiredForStoredProcedure(endPoint, "payrollNumber")) {
            args.add(String.valueOf(parameters.getPayrollNumber()));
        }

        if (isRequiredForStoredProcedure(endPoint, "username")) {
            args.add(String.valueOf(parameters.getUsername()));
        }

        return getXml();
    }


    private String getStoredProcedureName(String endpoint) throws NullPointerException {
        String storedProcedureName = props.getProperty(storedProcPrefix + endpoint);

        if (storedProcedureName == null) {
            throw new NullPointerException(storedProcPrefix + endpoint + " - not found in properties file");
        }

        return storedProcedureName;
    }

    private boolean isRequiredForStoredProcedure(String storedProcedureSuffix, String authorisationType) throws NullPointerException {
        String isRequired = props.getProperty(storedProcPrefix + storedProcedureSuffix + "." + authorisationType);

        if (isRequired == null) {
            throw new NullPointerException(storedProcPrefix + storedProcedureSuffix + "." + authorisationType + " - not found in properties file");
        }

        return Boolean.parseBoolean(isRequired);
    }

    private String getXml() {
        JdbcTemplate jdbcTemplate = simpleJdbcDaoSupport.getJdbcTemplate();
        List<String> xmlList;

        if (args.size() == 0) {
            xmlList = jdbcTemplate.query(sqlStatement, new RowMapper() {
                public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getString(1);
                }
            });
        } else {
            xmlList = jdbcTemplate.query(sqlStatement, args.toArray(new Object[args.size()]), new RowMapper() {
                public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getString(1);
                }
            });
        }

        StringBuffer buffer = new StringBuffer();

        for (String temp : xmlList) {
            buffer.append(temp);
        }

        String bufferOutput = buffer.toString();
        String strippedString = bufferOutput.replaceAll("[^a-zA-Z0-9&%-\\/;#:><\\s'\"]", "");
        String trimmedStrippedString = strippedString.trim();

        if (trimmedStrippedString.length() == 0) return "<empty></empty>";

        return trimmedStrippedString;
    }

}
