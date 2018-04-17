package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.TVehicleBrandDao;
import com.dt.afzrms.po.TVehicleBrand;
import com.dt.afzrms.service.VehicleBrandService;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.VehicleBrandVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title 车辆品牌业务层接口实现类
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午4:59:29
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Service
public class VehicleBrandServiceImpl implements VehicleBrandService {

	@Autowired
	TVehicleBrandDao tVehicleBrandDao;

	@Override
	public PageVo<VehicleBrandVo> findList(Integer pageNo, Integer pageSize, String name) {
		String hql = "select t from TVehicleBrand t where 1=1";
		List<Object> values = new ArrayList<Object>();
		if (StringUtils.isNotEmpty(name)) {
			hql += " and t.name like ?";
			values.add("%" + name + "%");
		}
		hql += " order by t.name";
		Page<TVehicleBrand> pagingQuery = tVehicleBrandDao.pagingQuery(pageNo, pageSize, hql, values.toArray());
		PageVo<VehicleBrandVo> vo = new PageVo<VehicleBrandVo>();
		vo.setTotal(pagingQuery.getTotalCount());
		List<VehicleBrandVo> data = new ArrayList<VehicleBrandVo>(pagingQuery.getResult().size());
		for (TVehicleBrand tVehicleBrand : pagingQuery.getResult()) {
			VehicleBrandVo roleVo = helpPo2Vo(tVehicleBrand);
			data.add(roleVo);
		}
		vo.setData(data);
		return vo;
	}

	@Override
	public Integer add(String name, String uses, String remark, String createBy) {
		TVehicleBrand tVehicleBrand = new TVehicleBrand(name, uses, createBy, null, remark, null);
		tVehicleBrandDao.save(tVehicleBrand);
		return tVehicleBrand.getId();
	}

	@Override
	public Integer update(Integer id, String name, String description, String remark) {
		TVehicleBrand findById = tVehicleBrandDao.findById(TVehicleBrand.class, id);
		if (findById != null) {
			findById.setName(name);
			findById.setDescription(description);
			findById.setRemark(remark);
			tVehicleBrandDao.saveOrUpdate(findById);
			return findById.getId();
		}
		return null;
	}

	@Override
	public void delete(Integer[] ids) {
		tVehicleBrandDao.deleteByIds(ids, TVehicleBrand.class);
	}

	private VehicleBrandVo helpPo2Vo(TVehicleBrand tVehicleBrand) {
		if (tVehicleBrand == null) {
			return null;
		}
		VehicleBrandVo vehicleBrandVo = new VehicleBrandVo(tVehicleBrand.getId(), tVehicleBrand.getName(),
				tVehicleBrand.getDescription(), tVehicleBrand.getCreateBy(), tVehicleBrand.getCreateTime(),
				tVehicleBrand.getRemark());
		return vehicleBrandVo;
	}

}
