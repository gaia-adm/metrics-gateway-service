package com.hp.gaia.mgs.services;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by belozovs on 6/30/2015.
 */
public class PropertiesKeeperService {

    private final static String PROP_FILE_NAME="default.properties";

    private static PropertiesKeeperService instance = null;
    private static Properties properties = null;

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

    public Properties getProperties() throws IOException {
        return  properties;
    }

}
