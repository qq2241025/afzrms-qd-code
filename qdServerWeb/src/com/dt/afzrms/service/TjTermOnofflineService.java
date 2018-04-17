package com.dt.afzrms.service;

import java.util.Date;

import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TjTermOnofflineVo;

/**
 * @Title tj term alarm service interface
 * @Description TODO
 * @author
 * @createDate 2015年3月31日 下午3:30:46
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface TjTermOnofflineService {
	PageVo<TjTermOnofflineVo> findList(Integer pageNo, Integer pageSize, Date beginTime, Date endTime)
			throws Exception;
}
