package eu.dm2e.grafeo.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;

import java.io.File;


/**
 * Config singleton handling all configuration from properties/XML files.
 *
 *
 * @author Konstantin Baierer
 */
public enum Config {
    INSTANCE
    ;

    private static final String CONFIG_FILE = "/config.xml";
    private final Configuration config;
//	private Logger log = LoggerFactory.getLogger(Config.class.getName());

    private Config() {
        Configuration c;
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        builder.setFile(new File(CONFIG_FILE));
        try {
            c =  builder.getConfiguration();
        } catch (ConfigurationException e) {
            c = null;
            throw new RuntimeException(e);
        }
        config = c;
    }

    /**
     * Get the Configuration
     *
     */
    public static Configuration getConfig() {
        return INSTANCE.config;
    }


//	public static String getString(String string) {
//		String conf =  config.getString(string);
//		if (null == conf) {
//			log.error("Undefined config option " + string);
//			throw new RuntimeException("Undefined config option " + string);
//		}
//		return conf;
//	}

    /**
     * Get a configuration setting.
     *
     * <pre>{@code
     * String baseUri = Config.get(ConfigProp.BASE_URI)
     * }</pre>
     *
     * @return Config value as String
     */
    public static String get(String configProp) {
        return INSTANCE.config.getString(configProp);
    }
    /**
     * Set a configuration setting.
     *
     * Think carefully before using this, if config needs change, change the config file(s).
     *
     */
    public static void set(String configProp, String value) {
        INSTANCE.config.setProperty(configProp, value);
    }

    /**
     * Whether the backing config singleton is null.
     *
     * @return true if there was an error initiating the config, false otherwise
     */
    public static boolean isNull() {
        return INSTANCE.config == null;
    }

//	public static String getEndpointQuery

//	public static String ENDPOINT_QUERY = getString("dm2e.ws.sparql_endpoint");
//	public static String ENDPOINT_UPDATE = getString("dm2e.ws.sparql_endpoint_statements");

}
