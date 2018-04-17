package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.RefUserRoleDao;
import com.dt.afzrms.dao.hibernate.TUserDao;
import com.dt.afzrms.po.RefUserRole;
import com.dt.afzrms.po.RefUserVehicleGroup;
import com.dt.afzrms.po.TDept;
import com.dt.afzrms.po.TRole;
import com.dt.afzrms.po.TUser;
import com.dt.afzrms.po.TVehicleGroup;
import com.dt.afzrms.service.UserService;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.UserVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title 用户业务层接口实现类
 * @Description TODO
 * @author
 * @createDate 2015年1月20日 上午11:16:05
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	TUserDao tUserDao;
	@Autowired
	RefUserRoleDao refUserRoleDao;

	@Override
	public List<UserVo> findByAccount(String account) {
		@SuppressWarnings("unchecked")
		List<TUser> findByProperty = (List<TUser>) tUserDao.findByProperty(TUser.class, "account", account);
		List<UserVo> userVos = new ArrayList<UserVo>(findByProperty.size());
		for (TUser user : findByProperty) {
			UserVo userVo = helpPo2Vo(user);
			userVos.add(userVo);
		}
		return userVos;
	}

	@Override
	public PageVo<Map> findList(Integer pageNo, Integer pageSize, Integer deptId, String account, String name) {
		
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select u.id,u.user_type userType,u.name,u.account,u.contact,u.is_deleted isDeleted,u.dept_id deptId,dy.`name` deptName,u.create_by createBy,date_format(u.create_time,'%Y-%m-%d %T') createTime,u.remark,rr.role_id roleId ,ro.`name` roleName, ");
		sql.append(" (select GROUP_CONCAT(CAST(vg.vehicleGroupId AS CHAR))  from REF_USER_VEHICLE_GROUP vg where vg.userId = u.id) as groupIds  "); //
		sql.append(" from T_USER  u    ");
		sql.append(" left join REF_USER_ROLE  rr on u.id = rr.user_id   ");
		sql.append(" left join T_ROLE ro on ro.id = rr.role_id   ");
		sql.append(" left join T_DEPT dy on dy.id = u.dept_id   ");
		sql.append(" where 1=1   ");
		 
		if (deptId != null) {
			sql.append(" and u.dept_id = '"+deptId+"'  ");
		}
		if (StringUtils.isNotEmpty(account)) {
			sql.append(" and u.account like '%"+account+"%'  ");
		}
		if (StringUtils.isNotEmpty(name)) {
			sql.append(" and u.name like '%"+name+"%'  ");
		}
		Page<?> pagingQuery = tUserDao.pagingQueryWithSqlReturnMap(pageNo, pageSize, sql.toString());
		List<Map> maplist = (List<Map>) pagingQuery.getResult();
		PageVo<Map> vo = new PageVo<Map>();
		if(maplist!=null && maplist.size() >0){
			int total = pagingQuery.getTotalCount();
			vo.setTotal(total);
			vo.setData(maplist);
		}else{
			vo.setTotal(0);
			vo.setData(new ArrayList<Map>());
		}
		return  vo;
	}

	@Override
	public Integer add(String account, String passwd, String name, Integer deptId, String contact, String remark,
			Integer roleId, String createBy,int userType,Integer[] groupIds) {
		//TODO
		TUser user = new TUser(new TDept(deptId), name, account, passwd, contact, Const.ISDELETEDYES, userType, createBy, null, remark,
				null, null);
		user = (TUser) tUserDao.save(user);
		int userId = user.getId();
		updateUserRole(user.getId(), roleId);
		this.updateUserGroup(userId, groupIds);
		return user.getId();
	}

	@Override
	public UserVo findById(Integer id) {
		TUser findById = tUserDao.findById(TUser.class, id);
		return helpPo2Vo(findById);
	}
	
	private void updateUserGroup(int userID ,Integer[] groupList){
		String hql = "delete from RefUserVehicleGroup where TUser.id = '"+userID+"' ";
		tUserDao.updateWithHql(hql);
		List<RefUserVehicleGroup> entities = new ArrayList<RefUserVehicleGroup>();
		for (int i = 0; i < groupList.length; i++) {
			int groupId = groupList[i];
			TVehicleGroup group =new TVehicleGroup(groupId);
			TUser user = new TUser();
			user.setId(userID);
			RefUserVehicleGroup grou= new RefUserVehicleGroup(group, user);
			entities.add(grou);
		}
		tUserDao.batchSave(entities);
	}
	
	

	@Override
	public Integer update(Integer id, String account, String name, Integer deptId, String contact,
			String remark, Integer roleId,int userType,Integer[] groupIds) {
		TUser findById = tUserDao.findById(TUser.class, id);
		int res = 0;
		if (findById != null) {
			if(findById.getUserType()== Const.SYSTEMUSER){
				res = 2; //系统管理不能随便编辑
			}else{
				findById.setAccount(account);
				findById.setName(name);
				findById.setTDept(new TDept(deptId));
				findById.setContact(contact);
				findById.setRemark(remark);
				tUserDao.saveOrUpdate(findById);
				updateUserRole(findById.getId(), roleId);
				this.updateUserGroup(findById.getId(), groupIds);
				res =1;
			}
		}
		return res;
	}

	@Override
	public int delete(Integer[] ids) {
		List<TUser> iscommUser = new ArrayList<TUser>(); 
		List<TUser> isSysUser = new ArrayList<TUser>();
		for (Integer userId : ids) {
			TUser findById = tUserDao.findById(TUser.class, userId);
			//是系统管理不删除
			if(findById!=null && findById.getUserType() == Const.SYSTEMUSER && Const.ISDELETEDNO == findById.getIsDeleted()){
				isSysUser.add(findById);
			}else{
				iscommUser.add(findById);
			}
		}
		if(isSysUser.size() ==0){
			for (TUser tUser : iscommUser) {
				tUserDao.delete(tUser);
			}
			return 1; //普通用户
		}else{
			return 2; //系统用户
		}
	}

	private void updateUserRole(Integer userId, Integer roleId) {
		@SuppressWarnings("unchecked")
		List<RefUserRole> findByProperty = (List<RefUserRole>) refUserRoleDao.findByProperty(RefUserRole.class,
				"TUser.id", userId);
		if (findByProperty != null && findByProperty.size() == 1) {
			RefUserRole refUserRole = findByProperty.get(0);
			if (refUserRole.getTRole().getId().intValue() == roleId.intValue()) {
				return;
			} else {
				refUserRoleDao.delete(refUserRole);
			}
		}
		RefUserRole refUserRole = new RefUserRole();
		TUser user = new TUser();
		user.setId(userId);
		refUserRole.setTUser(user);
		TRole role = new TRole();
		role.setId(roleId);
		refUserRole.setTRole(role);
		refUserRoleDao.save(refUserRole);
	}
	

	
	
	private UserVo helpPo2Vo(TUser user) {
		if (user == null) {
			return null;
		}
		Integer roleId = -1;
		String roleName = null;
		Set<RefUserRole> refUserRoles = user.getRefUserRoles();
		if (refUserRoles.size() > 0) {
			for (RefUserRole refUserRole : refUserRoles) {
				roleId = refUserRole.getTRole().getId();
				roleName = refUserRole.getTRole().getName();
			}
		}
		TDept tDept = user.getTDept();
		Integer deptId = tDept == null ? null : tDept.getId();
		String deptName = tDept == null ? "" : tDept.getName();
		UserVo userVo = new UserVo(user.getId(), deptId, deptName, user.getName(), user.getAccount(), user.getPasswd(),
				user.getContact(), user.getIsDeleted(), user.getUserType(), user.getCreateBy(), user.getCreateTime(),
				user.getRemark(), roleId, roleName);
		return userVo;
	}
}
