package com.mapabc.gater.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.LogFactory;

public class PropertyReader {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(PropertyReader.class);

	protected Properties properties = null;
	private static Hashtable<String,Properties> proHash=new Hashtable<String,Properties>();


	/** Creates a new instance of PropertyReader 
	 * @throws Exception */

	public PropertyReader(String propertyFile) throws Exception {
		 Properties prop=proHash.get(propertyFile);
		    if(prop!=null){
			properties=prop;
		    }else{
			loadPropertyFile(propertyFile);
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
			throw new Exception("Configuration file '" + propertyFile
					+ "' not found");
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
						log.info(
								"PropertyReader - Exception found. "
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
			log.info(
					"getProperty() - error reading property - " + propertyName
							+ ".\n" + ex);
		}

		return val;
	}
	
	public HashMap<String, String> getAllValue(){
		HashMap<String, String> retMap = new HashMap<String, String>();
		Set  sets = properties.keySet();
		 
		for (Object key:sets)
		{
			String tmpKey = key.toString();
			String value = properties.getProperty(tmpKey);
			retMap.put(tmpKey, value);
		}
		
		return retMap;
	}
	
	public static void main(String[] args){
		
		String name = "load.properties";
		try {
			PropertyReader pr = new PropertyReader(name);
			HashMap<String, String> map = pr.getAllValue();
			System.out.println(map.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
