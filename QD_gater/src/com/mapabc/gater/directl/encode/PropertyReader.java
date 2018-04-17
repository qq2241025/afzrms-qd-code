package com.mapabc.gater.directl.encode;

import java.util.Properties;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.ParseConfigParamUtil;

public class PropertyReader {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(PropertyReader.class);

	
    protected Properties properties = null;
    
    /** Creates a new instance of PropertyReader */

    public PropertyReader(String propertyFile){
        try {
			loadPropertyFile(propertyFile);
		 } catch (Exception e) {
			e.printStackTrace();
		}
    }

    // public String getProperty(String prop) { return properties.getProperty(prop); }
    private void loadPropertyFile(String propertyFile) throws Exception {
        // Get our class loader
        ClassLoader cl = getClass().getClassLoader();
        java.io.InputStream in;

        if (cl != null) {
            in = cl.getResourceAsStream(propertyFile);
        } else {
            in = ClassLoader.getSystemResourceAsStream(propertyFile);
        }
        if (in == null) {
            throw new Exception("Configuration file '" + propertyFile + "' not found");
        } else {
            try {
                properties = new Properties();

                // Load the configuration file into the properties table
                properties.load(in);
            } finally {
                // Close the input stream
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ex) {
                    	log.info("PropertyReader - Exception found. "
                                + ex.toString());
                        throw ex;
                    }
                }
            }
        }
    }

    public String getProperty(String propertyName) {
        String val = "";
        try {

            val = properties.getProperty(propertyName);
        } catch (Exception ex) {
        	log.info("getProperty() - error reading property - " + propertyName + ".\n" + ex);
        }

        return val;
    }
}
