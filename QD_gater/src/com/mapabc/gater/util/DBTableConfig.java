package com.mapabc.gater.util;

/**
 * 加载数据表名、字段名等配置到内存
 */

import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mapabc.gater.lbsgateway.bean.TableConfigBean;

public class DBTableConfig extends Hashtable<String, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static DBTableConfig instance = null;
	private static final String ConfigFilename = "table-config.xml";

	public static synchronized DBTableConfig getInstance() {
		if (instance == null) {
			instance = new DBTableConfig();
		}
		return instance;
	}

	private DBTableConfig() {
	}

	public void loadXml() {
		InputStream is = getClass().getResourceAsStream("/" + ConfigFilename);
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(is);
			Element root = doc.getRootElement();
			if (root != null) {
				this.loadTableInfo(root);
				this.loadProcedureInfo(root);
			}

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {

				}
			}
		}
	}

	// 加载存储过程配置
	private void loadProcedureInfo(Element root) {
		Element proElement = root.element("procedures");
		List<?> proList = proElement.elements("procedure");
		if (proList != null) {
			for (int i = 0; i < proList.size(); i++) {
				Element proEmt = (Element) proList.get(i);
				String id = proEmt.attributeValue("id");
				String name = proEmt.getTextTrim();
				instance.put(id, name);
			}
		}
	}

	// 读取表配置信息
	private void loadTableInfo(Element root) {
		Element tables = root.element("tables");
		List<?> list = tables.elements("table");
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {

				TableConfigBean tcb = new TableConfigBean();

				Element element = (Element) list.get(i);
				String key = element.attributeValue("id"); // 表标识
				String name = element.attributeValue("name");// 表名称
				List<?> columnList = element.elements("colum");// 列字段元素

				Hashtable<String, String> column = new Hashtable<String, String>();

				for (int j = 0; j < columnList.size(); j++) {
					Element ce = (Element) columnList.get(j);
					String colId = ce.attributeValue("id"); // 列标识
					String colName = ce.getTextTrim(); // 列字段名
					column.put(colId, colName);
				}

				Element seqElement = element.element("sequence");// 对应的表SEQUCENCE
				// String seqId = seqElement.attributeValue("id"); //
				// 表SEQUENCE标识
				String seqName = seqElement.getTextTrim(); // SEQUNCE名称

				tcb.setColumns(column);
				tcb.setSequence(seqName);
				tcb.setTableName(name);

				instance.put(key, tcb);

				System.out.print("");
			}
		}
	}

	public static void main(String[] args) {
		DBTableConfig config = DBTableConfig.getInstance();
		config.loadXml();
	}

}
