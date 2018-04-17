/**
 * 
 */
package com.mapabc.gater.lbsgateway;

import java.util.Hashtable;

import com.mapabc.gater.common.Const;
import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;

/**
 * @author
 *
 */
public class TerminalTypeList extends Hashtable<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static TerminalTypeList instance = null;

	public static TerminalTypeList getInstance() {
		if (instance == null) {
			instance = new TerminalTypeList();
		}
		return instance;
	}

	private TerminalTypeList() {
	}

	public synchronized void add(String key, TerminalTypeBean ttype) {
		if (instance.get(key) != null) {
			instance.remove(key);
		}
		instance.put(key, ttype);

	}

	public TerminalTypeBean getTerminalType(String key) {

		if (key != null && key.length() > 0) {
			return (TerminalTypeBean) instance.get(key);
		}

		return (TerminalTypeBean) instance.get(Const.PROTOCAL_TYPE_DEFAULT);
	}

}
