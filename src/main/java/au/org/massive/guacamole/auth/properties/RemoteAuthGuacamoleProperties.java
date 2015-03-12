package au.org.massive.guacamole.auth.properties;

import org.glyptodon.guacamole.properties.IntegerGuacamoleProperty;
import org.glyptodon.guacamole.properties.StringGuacamoleProperty;

/**
 * Created by simonyu on 6/03/15.
 */
public class RemoteAuthGuacamoleProperties {

    /**
     * This class should not be instantiated.
     */
    private RemoteAuthGuacamoleProperties() {
    }

    public static final StringGuacamoleProperty ENV_ATTRIBUTE = new StringGuacamoleProperty() {
        @Override
        public String getName() {
            return "required-env-attribute";
        }
    };

    /**
     * The URL of the MySQL server hosting the guacamole authorization tables.
     */
    public static final StringGuacamoleProperty MYSQL_HOSTNAME = new StringGuacamoleProperty() {

        @Override
        public String getName() {
            return "mysql-hostname";
        }
    };

    /**
     * The port of the MySQL server hosting the guacamole authorization tables.
     */
    public static final IntegerGuacamoleProperty MYSQL_PORT = new IntegerGuacamoleProperty() {

        @Override
        public String getName() {
            return "mysql-port";
        }

    };

    /**
     * The name of the MySQL database containing the guacamole authorization tables.
     */
    public static final StringGuacamoleProperty MYSQL_DATABASE = new StringGuacamoleProperty() {

        @Override
        public String getName() {
            return "mysql-database";
        }

    };

    /**
     * The username used to connect to the MySQL database containing the guacamole authorization tables.
     */
    public static final StringGuacamoleProperty MYSQL_USERNAME = new StringGuacamoleProperty() {

        @Override
        public String getName() {
            return "mysql-username";
        }

    };

    /**
     * The password used to connection to the MySQL database containing the guacamole authentication tables.
     */
    public static final StringGuacamoleProperty MYSQL_PASSWORD = new StringGuacamoleProperty() {

        @Override
        public String getName() {
            return "mysql-password";
        }

    };

}
