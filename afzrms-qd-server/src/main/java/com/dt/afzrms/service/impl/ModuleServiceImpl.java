package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.TModuleDao;
import com.dt.afzrms.service.ModuleService;
import com.dt.afzrms.service.TreeNodeVoService;
import com.dt.afzrms.util.TreeStructureUtil;
import com.dt.afzrms.vo.ModuleTreeNodeVo;
import com.dt.afzrms.vo.ModuleVo;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TreeNodeVo;
import com.dt.afzrms.vo.TreeVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title module service impl
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午8:18:34
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class ModuleServiceImpl implements ModuleService {

	@Autowired
	TModuleDao tModuleDao;

	@Override
	public PageVo<ModuleVo> findList(Integer pageNo, Integer pageSize, Integer parentId, String name) {
		String sql = "select d.id,d.name,d.parent_id,d.sort_num,d.url_path,d.js_classname,d.description,d.is_used,d.is_visible,p.name as pname"
				+ " from T_MODULE d" + " left join T_MODULE p on d.parent_id=p.id" + " where p.id=?";
		List<Object> values = new ArrayList<Object>();
		values.add(parentId);
		if (StringUtils.isNotEmpty(name)) {
			sql += " and d.name like ?";
			values.add("%" + name + "%");
		}
		sql += " order by d.sort_num";
		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQueryWithSql = (Page<Object[]>) tModuleDao.pagingQueryWithSql(pageNo, pageSize, sql,
				values.toArray());
		List<ModuleVo> data = new ArrayList<ModuleVo>(pagingQueryWithSql.getResult().size());
		for (Object[] objs : pagingQueryWithSql.getResult()) {
			Integer _id = (Integer) objs[0];
			String _name = (String) objs[1];
			// TODO
			Integer _parentId = objs[2] == null ? null : (Integer) objs[2];
			Integer _sortNum = objs[3] == null ? null : (Integer) objs[3];
			String _urlPath = (String) objs[4];
			String _jsClassname = (String) objs[5];
			String _description = (String) objs[6];
			Boolean _isUsed = objs[7] == null ? null : (Boolean) objs[7];
			Boolean _isVisible = objs[8] == null ? null : (Boolean) objs[8];
			String _parentName = (String) objs[9];
			ModuleVo deptVo = new ModuleVo(_id, _name, _description, _parentId, _sortNum, _urlPath, _jsClassname,
					_isUsed, _isVisible, _parentName);
			data.add(deptVo);
		}
		return new PageVo<ModuleVo>(pagingQueryWithSql.getTotalCount(), data);
	}

	@Override
	public TreeVo tree(Integer id) {
		String sql = "select d.id,d.name,d.parent_id,d.sort_num,d.url_path,d.js_classname,d.is_used,d.is_visible"
				+ " from T_MODULE d where find_in_set(d.id,queryModuleTree(?))" + " order by d.parent_id,d.sort_num";
		Object values = id;
		@SuppressWarnings("unchecked")
		List<Object[]> findListWithSql = (List<Object[]>) tModuleDao.findListWithSql(sql, values);

		String parentId = String.valueOf(id);
		TreeVo makeTreeVo = TreeStructureUtil.makeTreeVo(parentId, findListWithSql, new TreeNodeVoService() {

			@Override
			public TreeNodeVo makeTreeNodeVo(Object[] objs) {
				Integer _id = (Integer) objs[0];
				String _name = (String) objs[1];
				// TODO
				Integer _parentId = objs[2] == null ? null : (Integer) objs[2];
				Integer _sortNum = objs[3] == null ? null : (Integer) objs[3];
				String _urlPath = (String) objs[4];
				String _jsClassname = (String) objs[5];
				Boolean _isUsed = objs[6] == null ? null : (Boolean) objs[6];
				Boolean _isVisible = objs[7] == null ? null : (Boolean) objs[7];

				String parentId = (_parentId == null ? null : _parentId.toString());
				ModuleTreeNodeVo moduleTreeNodeVo = new ModuleTreeNodeVo(_id.toString(), parentId, _name, true,
						new ArrayList<TreeNodeVo>(), _sortNum, _urlPath, _jsClassname, _isUsed, _isVisible, false);
				return moduleTreeNodeVo;
			}
		});

		return makeTreeVo;
	}

	@Override
	public TreeVo tree(Integer id, Integer roleId) {
		String sql = "select d.id,d.name,d.parent_id,d.sort_num,d.url_path,d.js_classname,d.is_used,d.is_visible"
				+ " from T_MODULE d where find_in_set(d.id,queryModuleTree2(?,?))" + " order by d.parent_id,d.sort_num";
		Object[] values = { id, roleId };
		@SuppressWarnings("unchecked")
		List<Object[]> findListWithSql = (List<Object[]>) tModuleDao.findListWithSql(sql, values);

		String parentId = String.valueOf(id);
		TreeVo makeTreeVo = TreeStructureUtil.makeTreeVo(parentId, findListWithSql, new TreeNodeVoService() {

			@Override
			public TreeNodeVo makeTreeNodeVo(Object[] objs) {
				Integer _id = (Integer) objs[0];
				String _name = (String) objs[1];
				// TODO
				Integer _parentId = objs[2] == null ? null : (Integer) objs[2];
				Integer _sortNum = objs[3] == null ? null : (Integer) objs[3];
				String _urlPath = (String) objs[4];
				String _jsClassname = (String) objs[5];
				Boolean _isUsed = objs[6] == null ? null : (Boolean) objs[6];
				Boolean _isVisible = objs[7] == null ? null : (Boolean) objs[7];

				String parentId = (_parentId == null ? null : _parentId.toString());
				ModuleTreeNodeVo moduleTreeNodeVo = new ModuleTreeNodeVo(_id.toString(), parentId, _name, true,
						new ArrayList<TreeNodeVo>(), _sortNum, _urlPath, _jsClassname, _isUsed, _isVisible, false);
				return moduleTreeNodeVo;
			}
		});

		return makeTreeVo;
	}
}
