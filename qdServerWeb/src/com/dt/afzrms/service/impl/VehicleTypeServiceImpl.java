package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.TVehicleTypeDao;
import com.dt.afzrms.po.TVehicleType;
import com.dt.afzrms.service.VehicleTypeService;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.VehicleTypeVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title 车辆类型业务层接口实现类
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午4:59:29
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Service
public class VehicleTypeServiceImpl implements VehicleTypeService {

	@Autowired
	TVehicleTypeDao tVehicleTypeDao;

	@Override
	public PageVo<VehicleTypeVo> findList(Integer pageNo, Integer pageSize, String name) {
		String hql = "select t from TVehicleType t where 1=1";
		List<Object> values = new ArrayList<Object>();
		if (StringUtils.isNotEmpty(name)) {
			hql += " and t.name like ?";
			values.add("%" + name + "%");
		}
		hql += " order by t.name";
		Page<TVehicleType> pagingQuery = tVehicleTypeDao.pagingQuery(pageNo, pageSize, hql, values.toArray());
		PageVo<VehicleTypeVo> vo = new PageVo<VehicleTypeVo>();
		vo.setTotal(pagingQuery.getTotalCount());
		List<VehicleTypeVo> data = new ArrayList<VehicleTypeVo>(pagingQuery.getResult().size());
		for (TVehicleType tVehicleType : pagingQuery.getResult()) {
			VehicleTypeVo roleVo = helpPo2Vo(tVehicleType);
			data.add(roleVo);
		}
		vo.setData(data);
		return vo;
	}

	@Override
	public Integer add(String name, String uses, String remark, String createBy) {
		TVehicleType tVehicleType = new TVehicleType(name, uses, createBy, null, remark, null);
		tVehicleTypeDao.save(tVehicleType);
		return tVehicleType.getId();
	}

	@Override
	public Integer update(Integer id, String name, String description, String remark) {
		TVehicleType findById = tVehicleTypeDao.findById(TVehicleType.class, id);
		if (findById != null) {
			findById.setName(name);
			findById.setDescription(description);
			findById.setRemark(remark);
			tVehicleTypeDao.saveOrUpdate(findById);
			return findById.getId();
		}
		return null;
	}

	@Override
	public void delete(Integer[] ids) {
		tVehicleTypeDao.deleteByIds(ids, TVehicleType.class);
	}

	private VehicleTypeVo helpPo2Vo(TVehicleType tVehicleType) {
		if (tVehicleType == null) {
			return null;
		}
		VehicleTypeVo vehicleTypeVo = new VehicleTypeVo(tVehicleType.getId(), tVehicleType.getName(),
				tVehicleType.getDescription(), tVehicleType.getCreateBy(), tVehicleType.getCreateTime(),
				tVehicleType.getRemark());
		return vehicleTypeVo;
	}

}
