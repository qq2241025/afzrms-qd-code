package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.TProtocalTypeDao;
import com.dt.afzrms.po.TProtocalType;
import com.dt.afzrms.service.ProtocalTypeService;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.ProtocalTypeVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title 终端协议类型业务层接口实现类
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午4:59:29
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Service
public class ProtocalTypeServiceImpl implements ProtocalTypeService {

	@Autowired
	TProtocalTypeDao tProtocalTypeDao;

	@Override
	public PageVo<ProtocalTypeVo> findList(Integer pageNo, Integer pageSize, String name) {
		String hql = "select t from TProtocalType t where 1=1";
		List<Object> values = new ArrayList<Object>();
		if (StringUtils.isNotEmpty(name)) {
			hql += " and t.name like ?";
			values.add("%" + name + "%");
		}
		hql += " order by t.name";
		Page<TProtocalType> pagingQuery = tProtocalTypeDao.pagingQuery(pageNo, pageSize, hql, values.toArray());
		PageVo<ProtocalTypeVo> vo = new PageVo<ProtocalTypeVo>();
		vo.setTotal(pagingQuery.getTotalCount());
		List<ProtocalTypeVo> data = new ArrayList<ProtocalTypeVo>(pagingQuery.getResult().size());
		for (TProtocalType tProtocalType : pagingQuery.getResult()) {
			ProtocalTypeVo roleVo = helpPo2Vo(tProtocalType);
			data.add(roleVo);
		}
		vo.setData(data);
		return vo;
	}

	private ProtocalTypeVo helpPo2Vo(TProtocalType tProtocalType) {
		if (tProtocalType == null) {
			return null;
		}
		ProtocalTypeVo ProtocalTypeVo = new ProtocalTypeVo(tProtocalType.getProtocalType(), tProtocalType.getName(),
				tProtocalType.getDescription(), tProtocalType.getCreateBy(), tProtocalType.getCreateTime());
		return ProtocalTypeVo;
	}

}
