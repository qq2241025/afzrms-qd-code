package com.dt.afzrms.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dt.afzrms.service.TreeNodeVoService;
import com.dt.afzrms.vo.TreeNodeVo;
import com.dt.afzrms.vo.TreeVo;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年1月30日 下午6:06:32
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class TreeStructureUtil {

	public static TreeVo makeTreeVo(String parentId, List<Object[]> resultList, TreeNodeVoService treeNodeVoService) {
		TreeVo treeVo = new TreeVo();
		List<TreeNodeVo> result = new ArrayList<TreeNodeVo>();
		HashMap<String, TreeNodeVo> temp = new HashMap<String, TreeNodeVo>(resultList.size());

		TreeNodeVo rootNode = findRootNode(parentId, resultList, treeNodeVoService);
		result.add(rootNode);
		treeVo.setResult(result);

		deal(rootNode, resultList, treeNodeVoService, temp);
		
//		for (Object[] objs : resultList) {
//			TreeNodeVo makeTreeNodeVo = treeNodeVoService.makeTreeNodeVo(objs);
//
//			if (parentId.equals(makeTreeNodeVo.getId())) {// 根节点
//				result.add(makeTreeNodeVo);
//				treeVo.setResult(result);
//			} else {
//				TreeNodeVo parentTreeNodeVo = temp.get(makeTreeNodeVo.getParentId());
//				parentTreeNodeVo.getChildren().add(makeTreeNodeVo);
//				parentTreeNodeVo.setLeaf(false);
//			}
//			temp.put(makeTreeNodeVo.getId(), makeTreeNodeVo);
//		}
		return treeVo;
	}

	private static TreeNodeVo findRootNode(String parentId, List<Object[]> resultList,
			TreeNodeVoService treeNodeVoService) {
		for (Object[] objs : resultList) {
			// TODO
			if (parentId.equals(objs[0].toString())) {// 根节点
				TreeNodeVo makeTreeNodeVo = treeNodeVoService.makeTreeNodeVo(objs);
				return makeTreeNodeVo;
			}
		}
		return null;
	}

	private static void deal(TreeNodeVo parentTreeNodeVo, List<Object[]> resultList, TreeNodeVoService treeNodeVoService,
			HashMap<String, TreeNodeVo> temp) {
		for (Object[] objs : resultList) {
			Integer _parentId = objs[2] == null ? null : (Integer) objs[2];
			String parentId = (_parentId == null ? null : _parentId.toString());

			if (parentId != null && parentId.equals(parentTreeNodeVo.getId())) {// 是子节点
				TreeNodeVo sunTreeNodeVo = treeNodeVoService.makeTreeNodeVo(objs);
				parentTreeNodeVo.getChildren().add(sunTreeNodeVo);
				parentTreeNodeVo.setLeaf(false);
				deal(sunTreeNodeVo, resultList, treeNodeVoService, temp);
			}
		}
	}
}
