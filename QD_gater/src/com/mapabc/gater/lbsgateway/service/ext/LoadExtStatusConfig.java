/**
 * 
 */
package com.mapabc.gater.lbsgateway.service.ext;

import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author 
 * 
 */
public class LoadExtStatusConfig {

	static LoadExtStatusConfig instance;
	private static Document document;
	private static ExtStatusConfig extStatusCfg;

	/**
	 * @return the extStatusCfg
	 */
	public static ExtStatusConfig getExtStatusCfg() {
		return extStatusCfg;
	}

	/**
	 * @param extStatusCfg the extStatusCfg to set
	 */
	public static void setExtStatusCfg(ExtStatusConfig extStatusCfg) {
		LoadExtStatusConfig.extStatusCfg = extStatusCfg;
	}

	public static LoadExtStatusConfig getInstance() {

		if (instance == null) {
			instance = new LoadExtStatusConfig();
		}

		return instance;
	}

	public void loadConfig() {
		InputStream is = getClass().getResourceAsStream(
				"/ext-status-config.xml");
		SAXReader reader = new SAXReader();
		try {

			document = reader.read(is);
			ExtStatusConfig cfg = this.loadExtStatusCfg();
			this.setExtStatusCfg(cfg);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {

				}
			}
		}

	}

	public static ExtStatusConfig loadExtStatusCfg() {
		ExtStatusConfig cfg = null;

		if (document != null) {

			Element root = document.getRootElement();
			Element classElement = root.element("class");
			String className = classElement.attributeValue("name");
			String tableName = classElement.attributeValue("table");

			cfg = new ExtStatusConfig();
			cfg.setClassName(className);
			cfg.setTableName(tableName);

			List<Element> propList = classElement.elements("property");
			Property[] ps = new Property[propList.size()];
			
			for (int i=0; i<propList.size(); i++) {
				Element e = propList.get(i);
				
				Property p = new Property();
				String name = e.attributeValue("name");
				String type = e.attributeValue("type");
				Element columnEmt = e.element("column");
				String column = columnEmt.getTextTrim();
				
				p.setColumn(column);
				p.setName(name);
				p.setType(type);
				ps[i] = p;
				
			}
			
			cfg.setProperteis(ps);

		}

		return cfg;
	}

}
