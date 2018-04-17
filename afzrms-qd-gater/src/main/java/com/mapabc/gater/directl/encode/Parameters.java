/**
 * 
 */
package com.mapabc.gater.directl.encode;
/**
 * Parameters类用于获取定制XML超过3层以上节点内容，定制的XML内容最多支持5层节点，第五层节点内容不允许再包含子节点
 * 
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author 
 * 
 */
public class Parameters {

	private HashMap<String, String> subParamMap = new HashMap<String, String>();

	private HashMap<String, ArrayList<HashMap<String, HashMap<String, String>>>> subParamHashMap = new HashMap<String, ArrayList<HashMap<String, HashMap<String, String>>>>();

	private ArrayList<HashMap<String, HashMap<String, String>>> nodeList = new ArrayList<HashMap<String, HashMap<String, String>>>();

	/**
	 * 添加元素对应值
	 * 
	 * @param key
	 * @param value
	 * @author 
	 */
	protected void addParams(String key, String value) {
		this.subParamMap.put(key, value);
	}

	/**
	 * 添加包含子元素的集合
	 * 
	 * @param key
	 * @param value
	 * @author 
	 */
	protected void addSubSets(String key,
			ArrayList<HashMap<String, HashMap<String, String>>> value) {
		this.subParamHashMap.put(key, value);
	}

	protected void addSubNodeList(HashMap<String, HashMap<String, String>> o) {
		this.nodeList.add(o);
	}

	/**
	 * 获取XML接口第四层无子节点的节点内容
	 * 
	 * @return 第四层无子节点的节点内容
	 */
	public HashMap<String, String> getNoChildSubNodeFromLayer4() {
		return this.subParamMap;
	}
	
 

	/**
	 * 获取XML接口第3层有子节点的节点hash集合
	 * 
	 * @return 第3层有子节点的节点hash集合
	 */
	public HashMap<String, ArrayList<HashMap<String, HashMap<String, String>>>> getSubNodeListFromLayer3() {
		return this.subParamHashMap;
	}
	
	/**
	 * 获取XML接口第4层有子节点的节点hash集合
	 * 
	 * @return 第4层有子节点的节点hash集合
	 */
	public ArrayList<HashMap<String, HashMap<String, String>>> getSubNodeListFromLayer4() {
		return this.nodeList;
	}

//	/**
//	 * 获取第三层节点数据列表
//	 * 
//	 * @param thirdLayerLabel
//	 * @return 第三层节点数据列表 
//	 */
//	public ArrayList<HashMap<String, HashMap<String, String>>> getThirdAllNodeList(
//			String thirdLayerLabel) {
//		ArrayList<HashMap<String, HashMap<String, String>>> list = null;
//
//		if (thirdLayerLabel != null) {
//			list = this.subParamHashMap.get(thirdLayerLabel); 
//		}
//
//		return list;
//	}
//
//	/**
//	 * 获取第三层所有子节点数据列表
//	 * 
//	 * @param thirdLayerLabel 
//	 * @return 第三层所有子节点数据列表 
//	 */
//	public ArrayList<HashMap<String, HashMap<String, String>>> getThirdSubNodeList() {
//		return this.nodeList;
//	}

	/**
	 * @param subParamMap
	 *            the subParamMap to set
	 */
	protected void setSubParamMap(HashMap<String, String> subParamMap) {
		this.subParamMap = subParamMap;
	}

}
