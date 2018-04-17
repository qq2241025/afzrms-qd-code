
package com.dt.afzrms.service.impl;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.TMarkersDao;
import com.dt.afzrms.po.TMarkers;
import com.dt.afzrms.service.TMarkersService;
import com.dt.afzrms.vo.UserVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2016年9月24日 下午9:51:01
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */
@Service
public class TMarkersServiceImpl implements TMarkersService {
    
	@Autowired
	TMarkersDao tMarkersDao;
	
	
	@Override
	public String findList(Integer pageNo, Integer pageSize, String poiName,
			UserVo user) {
		StringBuffer sql= new StringBuffer();
		sql.append(" select a.id,a.`name`,a.x,a.y,date_format(a.createTime,'%Y-%m-%d %T' ) createTime,a.`status`,a.remarker from T_MARKERS a where 1=1 and a.`status` = 1");
		//普通用户查询
		if(Const.COMMONUSER== user.getUserType()){
			sql.append(" and a.userId = '"+user.getId()+"' ");
		}
		
		if(!StringUtils.isEmpty(poiName)){
			sql.append(" and a.`name` like '%"+poiName+"%' ");
		}
		Page page = tMarkersDao.pagingQueryWithSqlReturnMap(pageNo, pageSize, sql.toString());
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		List list = page.getResult();
		int total =0 ;
		if(list!=null  && list.size() >0){
			total = page.getTotalCount();
			array = JSONArray.fromObject(list);
		}
		result.put("total", total);
		result.put("result", array);
		result.put("success", true);
		return result.toString();
	}

	@Override
	public void add(TMarkers markers) {
		 tMarkersDao.save(markers);
	}

	@Override
	public void update(TMarkers markers) {
		tMarkersDao.update(markers);
	}

	@Override
	public void delete(String[] ids) {
		String idss = this.listStr(ids);
		tMarkersDao.updateWithSql("update T_MARKERS  a set a.`status` = 0 where 1=1 and a.id in " + idss );
	}
	
	private String listStr(String[] deviceIds){
		StringBuffer para =new StringBuffer();
		para.append("(");
		int len = deviceIds.length;
		if(deviceIds!=null && len>0){
			for (int i = 0; i < len; i++) {
				String string = deviceIds[i];
				para.append("'"+string+"'");
				if(i < len-1){
					para.append(",");
				}
			}
		}
		return para.append(") ").toString();
	}

}
