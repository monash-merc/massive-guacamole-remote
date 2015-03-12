package au.org.massive.guacamole.auth;

import au.org.massive.guacamole.auth.properties.RemoteAuthGuacamoleProperties;
import org.apache.commons.lang.StringUtils;
import org.glyptodon.guacamole.GuacamoleException;
import org.glyptodon.guacamole.net.auth.Credentials;
import org.glyptodon.guacamole.net.auth.simple.SimpleAuthenticationProvider;
import org.glyptodon.guacamole.properties.GuacamoleProperties;
import org.glyptodon.guacamole.protocol.GuacamoleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by simonyu on 5/03/15.
 */
public class RemoteMySQLAuthenticationProvider extends SimpleAuthenticationProvider {

    /**
     * Logger for this class.
     */
    private Logger logger = LoggerFactory.getLogger(RemoteMySQLAuthenticationProvider.class);

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";


    @Override
    public Map<String, GuacamoleConfiguration> getAuthorizedConfigurations(Credentials credentials) throws GuacamoleException {

        //get the required property name from the guacamole properties file
        String requiredEnvAttribute = GuacamoleProperties.getRequiredProperty(RemoteAuthGuacamoleProperties.ENV_ATTRIBUTE);
        if (StringUtils.isBlank(requiredEnvAttribute)) {
            throw new GuacamoleException("the required env attribute must be specified");
        }

        //get http servlet request from the Credentials object
        HttpServletRequest request = credentials.getRequest();
        //get the required env attribute values from the request header
        String envAttributeValue = request.getHeader(requiredEnvAttribute);

        //define an empty configurations
        Map<String, GuacamoleConfiguration> configurations = new HashMap<String, GuacamoleConfiguration>();

        //if there is no value in the header, just return an empty configurations
        if (StringUtils.isBlank(envAttributeValue)) {
            return configurations;
            // envAttributeValue = "xiaoming.Yu@monash.edu";
        }

//        Enumeration<String> eHeaders = request.getHeaderNames();
//        System.out.println("======== check shib attributes from the header  ....");
//        while (eHeaders.hasMoreElements()) {
//            String name = eHeaders.nextElement();
//            Object object = request.getHeader(name);
//            System.out.println("======= name values pairs : " + name + " - " + object);
//        }

        //get mysql database connection parameters
        String mySQLHost = GuacamoleProperties.getRequiredProperty(RemoteAuthGuacamoleProperties.MYSQL_HOSTNAME);
        int mySQLPort = GuacamoleProperties.getRequiredProperty(RemoteAuthGuacamoleProperties.MYSQL_PORT);
        String mySQLDatabase = GuacamoleProperties.getRequiredProperty(RemoteAuthGuacamoleProperties.MYSQL_DATABASE);
        String mySQLUserName = GuacamoleProperties.getRequiredProperty(RemoteAuthGuacamoleProperties.MYSQL_USERNAME);
        String mySQLPassword = GuacamoleProperties.getRequiredProperty(RemoteAuthGuacamoleProperties.MYSQL_PASSWORD);


        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            //initialize the jdbc driver
            Class.forName(JDBC_DRIVER);
            //connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://" + mySQLHost + ":" + mySQLPort + "/" + mySQLDatabase, mySQLUserName, mySQLPassword);
            //query sql
            String sql = "SELECT con.* FROM vnc_connection as con inner join vnc_user as user on con.user_id = user.id where lower(user.email)=?";
            //prepared statement
            pStmt = conn.prepareStatement(sql);
            //set the query parameter condition
            pStmt.setString(1, envAttributeValue.toLowerCase());
            //execute the query
            rs = pStmt.executeQuery();
            //check the result sets
            while (rs.next()) {
                //create the configuration if any
                GuacamoleConfiguration configuration = new GuacamoleConfiguration();
                String name = rs.getString("name");
                configuration.setParameter("hostname", rs.getString("host_name"));
                configuration.setParameter("port", String.valueOf(rs.getInt("port")));
                configuration.setParameter("password", rs.getString("password"));
                configuration.setProtocol(rs.getString("protocol"));
                configurations.put(name, configuration);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new GuacamoleException("failed to connect to database", ex);
        } finally {
            //close the result sets, statement and connection
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pStmt != null) {
                    pStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                //just log the warn exceptions
                logger.warn("failed to close database.", ex);
            }
        }
        return configurations;
    }
}
