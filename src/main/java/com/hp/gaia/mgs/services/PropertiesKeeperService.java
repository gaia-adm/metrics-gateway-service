package com.hp.gaia.mgs.services;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by belozovs on 6/30/2015.
 * Return property from default.properties or environment variable if set to override defaults
 */
public class PropertiesKeeperService {

    private final static String PROP_FILE_NAME="default.properties";

    private static PropertiesKeeperService instance = null;
    private static Properties properties = null;
    private static Map<String, String> envVar = System.getenv();



    private PropertiesKeeperService() throws IOException {
        properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROP_FILE_NAME));
    }

    public static PropertiesKeeperService getInstance() throws IOException {
        if(instance == null){
            instance = new PropertiesKeeperService();
        }
        return instance;
    }

    public String getEnvOrPropAsString(String name) {
        if(envVar.get(name) != null){
            return envVar.get(name);
        } else {
            return properties.getProperty(name);
        }
    }

}
