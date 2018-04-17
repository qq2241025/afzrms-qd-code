package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.TDeptDao;
import com.dt.afzrms.po.TDept;
import com.dt.afzrms.po.TTerminal;
import com.dt.afzrms.service.DeptService;
import com.dt.afzrms.service.TreeNodeVoService;
import com.dt.afzrms.util.TreeStructureUtil;
import com.dt.afzrms.vo.DeptTreeNodeVo;
import com.dt.afzrms.vo.DeptVo;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TerminalTreeNodeVo;
import com.dt.afzrms.vo.TreeNodeVo;
import com.dt.afzrms.vo.TreeVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title dept service impl
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午8:18:34
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class DeptServiceImpl implements DeptService {

	@Autowired
	TDeptDao tDeptDao;

	@Override
	public PageVo<DeptVo> findList(Integer pageNo, Integer pageSize, Integer parentId, String name) {
		// String hql = "select d,p.name from TDept d"
		// + " left join d.TDept p"
		// + " where p.id=?";
		String sql = "select d.id,d.name,d.parent_id,d.sort_num,d.duty,d.director,d.remark,p.name as pname"
				+ " from T_DEPT d" + " left join T_DEPT p on d.parent_id=p.id" + " where d.parent_id=?";
		List<Object> values = new ArrayList<Object>();
		values.add(parentId);
		if (StringUtils.isNotEmpty(name)) {
			sql += " and d.name like ?";
			values.add("%" + name + "%");
		}
		sql += " order by d.sort_num";
		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQueryWithSql = (Page<Object[]>) tDeptDao.pagingQueryWithSql(pageNo, pageSize, sql,
				values.toArray());
		List<DeptVo> data = new ArrayList<DeptVo>(pagingQueryWithSql.getResult().size());
		for (Object[] objs : pagingQueryWithSql.getResult()) {
			Integer _id = (Integer) objs[0];
			String _name = (String) objs[1];
			// TODO
			Integer _parentId = objs[2] == null ? null : (Integer) objs[2];
			Integer _sortNum = objs[3] == null ? null : (Integer) objs[3];
			String _duty = (String) objs[4];
			String _director = (String) objs[5];
			String _remark = (String) objs[6];
			String _parentName = (String) objs[7];
			DeptVo deptVo = new DeptVo(_id, _name, _parentId, _sortNum, _duty, _director, _remark, _parentName);
			data.add(deptVo);
		}
		return new PageVo<DeptVo>(pagingQueryWithSql.getTotalCount(), data);
	}

	@Override
	public TreeVo tree(Integer id) {
		String sql = "select d.id,d.name,d.parent_id,d.sort_num from T_DEPT d where find_in_set(d.id,queryDeptTree(?))"
				+ " order by d.parent_id,d.sort_num";
		Object values = id;
		@SuppressWarnings("unchecked")
		List<Object[]> findListWithSql = (List<Object[]>) tDeptDao.findListWithSql(sql, values);
		String parentId = String.valueOf(id);
		TreeVo makeTreeVo = TreeStructureUtil.makeTreeVo(parentId, findListWithSql, new TreeNodeVoService() {

			@Override
			public TreeNodeVo makeTreeNodeVo(Object[] objs) {
				Integer _id = (Integer) objs[0];
				String _name = (String) objs[1];
				// TODO
				Integer _parentId = objs[2] == null ? null : (Integer) objs[2];
				Integer _sortNum = objs[3] == null ? null : (Integer) objs[3];

				String parentId = (_parentId == null ? null : _parentId.toString());
				DeptTreeNodeVo deptTreeNodeVo = new DeptTreeNodeVo(_id.toString(), parentId, _name, true,
						new ArrayList<TreeNodeVo>(), _sortNum);
				return deptTreeNodeVo;
			}

		});
		return makeTreeVo;
	}

	@Override
	public Integer add(String name, Integer parentId, Integer sortNum, String duty, String director, String remark,
			String createBy) {
		TDept tDept = new TDept(new TDept(parentId), name, null, sortNum, duty, director, createBy, null, remark, null,
				null, null);
		tDeptDao.save(tDept);
		return tDept.getId();
	}

	@Override
	public Integer update(Integer id, String name, Integer parentId, Integer sortNum, String duty, String director,
			String remark) {
		TDept findById = tDeptDao.findById(TDept.class, id);
		if (findById != null) {
			findById.setName(name);
			findById.setTDept(new TDept(parentId));
			findById.setSortNum(sortNum);
			findById.setDuty(duty);
			findById.setDirector(director);
			findById.setRemark(remark);
			tDeptDao.saveOrUpdate(findById);
			return findById.getId();
		}
		return null;
	}

	@Override
	public void delete(Integer[] ids) {
		tDeptDao.deleteByIds(ids, TDept.class);
	}

	@Override
	public TreeVo treeTerminal() {
		// TODO
		TreeVo tree = tree(1);

		String hql = "select t,t.TDept from TTerminal t" + " order by t.TDept.id";
		@SuppressWarnings("unchecked")
		List<Object[]> findList2 = (List<Object[]>) tDeptDao.findList(hql);

		for (Object[] objs : findList2) {
			TTerminal tTerminal = (TTerminal) objs[0];
			TDept tDept = (TDept) objs[1];
			Integer deptId = tDept.getId();
			String typeColor = tTerminal.getTVehicleType().getColor();

			TerminalTreeNodeVo terminalTreeNodeVo = new TerminalTreeNodeVo(tTerminal.getDeviceId(), null,
					tTerminal.getName(), true, null, false, tTerminal.getSimcard(), tTerminal.getLocateType(), null,
					null, null, null, null, null, null, null);
			terminalTreeNodeVo.setColor(typeColor);

			// 递归遍历部门树
			TreeNodeVo treeNodeVo = deep(tree.getResult(), deptId);
			if (treeNodeVo != null) {
				treeNodeVo.getChildren().add(terminalTreeNodeVo);
				treeNodeVo.setLeaf(false);
			} else {
				// ERROR
				System.err.println("terminal dept relationship error:" + tTerminal.getDeviceId() + "," + deptId);
			}
		}

		return tree;
	}

	private TreeNodeVo deep(List<TreeNodeVo> treeNodeVos, Integer deptId) {
		TreeNodeVo re = null;
		for (TreeNodeVo treeNodeVo : treeNodeVos) {
			if (treeNodeVo instanceof TerminalTreeNodeVo) {
				continue;
			}
			DeptTreeNodeVo deptTreeNodeVo = (DeptTreeNodeVo) treeNodeVo;
			if (deptTreeNodeVo.getId().equals(deptId.toString())) {
				re = deptTreeNodeVo;
				break;
			}
			if (deptTreeNodeVo.getChildren().size() > 0) {
				TreeNodeVo deep = deep(deptTreeNodeVo.getChildren(), deptId);
				if (deep != null) {
					re = deep;
					break;
				}
			}
		}
		return re;
	}
}
