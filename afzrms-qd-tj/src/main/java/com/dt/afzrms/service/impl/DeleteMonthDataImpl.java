package com.dt.afzrms.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.TTerminalDao;
import com.dt.afzrms.service.DeleteMonthDataService;

@Service
public class DeleteMonthDataImpl implements DeleteMonthDataService{

	@Autowired
	private TTerminalDao tTerminalDao;

	@Override
	public void deleteMonthData(int month) throws Exception{
		// TODO Auto-generated method stub
		String sql = "delete  from T_LOCRECORD  where 1=1 and input_time <= DATE_SUB(CURDATE(),INTERVAL -"+month+" MONTH)";
		Session session = tTerminalDao.getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(sql);
		query.executeUpdate();
	}
    /**
     * 每月分表存
     */
	@Override
	public void setExecuteEveryMonthPartTableTask() {
		// TODO Auto-generated method stub
		String TableName="T_LOCRECORD";
		String sql = "{CALL PART_TABLE_MONTH('"+TableName+"','"+this.getCurrenDate()+"')}";
		Session session = tTerminalDao.getHibernateTemplate().getSessionFactory().getCurrentSession();
		SQLQuery  query = session.createSQLQuery(sql);
		query.executeUpdate();
	}
    private  String getCurrenDate() {
	 	SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateTimeFormatter.format(new Date());
        return date;
	}
}
