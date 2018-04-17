package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.RefRoleModuleDao;
import com.dt.afzrms.dao.hibernate.TRoleDao;
import com.dt.afzrms.po.RefRoleModule;
import com.dt.afzrms.po.TDept;
import com.dt.afzrms.po.TModule;
import com.dt.afzrms.po.TRole;
import com.dt.afzrms.service.RoleService;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.RoleVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午2:12:33
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Service
public class RoleServiceImpl implements RoleService {
	@Autowired
	TRoleDao tRoleDao;
	@Autowired
	RefRoleModuleDao refRoleModuleDao;

	@Override
	public PageVo<RoleVo> findList(Integer pageNo, Integer pageSize, Integer deptId, String name) {
		String hql = "select r from TRole r where 1=1";
		List<Object> values = new ArrayList<Object>();
		if (deptId != null) {
			hql += " and r.TDept.id=?";
			values.add(deptId);
		}
		if (StringUtils.isNotEmpty(name)) {
			hql += " and r.name like ?";
			values.add("%" + name + "%");
		}
		hql += " order by r.name";
		Page<TRole> pagingQuery = tRoleDao.pagingQuery(pageNo, pageSize, hql, values.toArray());
		PageVo<RoleVo> vo = new PageVo<RoleVo>();
		vo.setTotal(pagingQuery.getTotalCount());
		List<RoleVo> data = new ArrayList<RoleVo>(pagingQuery.getResult().size());
		for (TRole role : pagingQuery.getResult()) {
			RoleVo roleVo = helpPo2Vo(role);
			data.add(roleVo);
		}
		vo.setData(data);
		return vo;
	}

	@Override
	public List<Integer> findModuleList(Integer roleId) {
		@SuppressWarnings("unchecked")
		List<RefRoleModule> findByProperty = (List<RefRoleModule>) refRoleModuleDao.findByProperty(RefRoleModule.class,
				"TRole.id", roleId);
		int size = findByProperty == null ? 0 : findByProperty.size();
		List<Integer> temp = new ArrayList<Integer>(size);
		if (size > 0) {
			for (RefRoleModule refRoleModule : findByProperty) {
				temp.add(refRoleModule.getTModule().getId().intValue());
			}
		}
		return temp;
	}

	@Override
	public Integer add(String name, Integer deptId, String description, String remark, String createBy,
			Integer[] moduleIds) {
		TRole tRole = new TRole(new TDept(deptId), name, description, createBy, null, remark, null, null, null);
		tRoleDao.save(tRole);

		List<RefRoleModule> entities = new ArrayList<RefRoleModule>(moduleIds.length);
		for (Integer moduleId : moduleIds) {
			entities.add(new RefRoleModule(new TRole(tRole.getId()), new TModule(moduleId)));
		}
		refRoleModuleDao.batchSave(entities);
		return tRole.getId();
	}

	@Override
	public Integer update(Integer id, String name, Integer deptId, String description, String remark,
			Integer[] moduleIds) {
		TRole findById = tRoleDao.findById(TRole.class, id);
		if (findById != null) {
			findById.setName(name);
			findById.setTDept(new TDept(deptId));
			findById.setDescription(description);
			findById.setRemark(remark);
			tRoleDao.saveOrUpdate(findById);

			refRoleModuleDao.deleteByHql("delete from RefRoleModule where TRole.id=?", id);

			List<RefRoleModule> entities = new ArrayList<RefRoleModule>(moduleIds.length);
			for (Integer moduleId : moduleIds) {
				entities.add(new RefRoleModule(new TRole(id), new TModule(moduleId)));
			}
			refRoleModuleDao.batchSave(entities);
			return findById.getId();
		}
		return null;
	}

	@Override
	public void delete(Integer[] ids) {
		tRoleDao.deleteByIds(ids, TRole.class);
	}

	private RoleVo helpPo2Vo(TRole role) {
		if (role == null) {
			return null;
		}
		TDept tDept = role.getTDept();
		Integer deptId = tDept == null ? null : tDept.getId();
		String deptName = tDept == null ? "" : tDept.getName();
		RoleVo roleVo = new RoleVo(role.getId(), deptId, deptName, role.getName(), role.getDescription(),
				role.getCreateBy(), role.getCreateTime(), role.getRemark(), role.getRoleType());
		return roleVo;
	}

}
