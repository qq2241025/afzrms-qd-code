package com.apps.menu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
public class JSONMenu {
	/** 
     * @param args 
     */  
    //驱动程序就是之前在classpath中配置的JDBC的驱动程序的JAR 包中  
    public static final String DBDRIVER = "com.mysql.jdbc.Driver";  
    //连接地址是由各个数据库生产商单独提供的，所以需要单独记住  
    public static final String DBURL = "jdbc:mysql://localhost:3306/dbgis";  
    //连接数据库的用户名  
    public static final String DBUSER = "root";  
    //连接数据库的密码  
    public static final String DBPASS = "root";  
      
    private Connection con = null;
    
    private Statement stmt  =null;
    
    
    public Map<String,List<ModuleMenu>>  list = null;
    
    public List<ModuleMenu> moduleList = null;
    
    
    public JSONMenu() {
    	list= new HashMap<String,List<ModuleMenu>>();
    	
    	moduleList= new ArrayList<ModuleMenu>();
        try {
        	Class.forName(DBDRIVER); //1、使用CLASS 类加载驱动程序  
			con = DriverManager.getConnection(DBURL,DBUSER,DBPASS);
			this.readDBData();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
    }
    
    public void CloseAll() throws SQLException{
    	if(con!=null){
    		con.close();
    	}
    	if(stmt!=null){
    		stmt.close();
    	}
    }
    
    public void readDBData() {
    	Statement stmt;
		try {
			stmt = con.createStatement();
			String sql = "select a.id ,a.name,a.description,a.parent_id,a.sort_num,a.url_path,a.js_classname,a.is_used,a.is_visible,a.grade from t_module a where 1=1 order by id  ";
	    	ResultSet  result = stmt.executeQuery(sql); //执行SQL 语句，查询数据库  
	        while (result.next()){  
	        	 String id = result.getString("id");
	        	 String name = result.getString("name");
	        	 String description = result.getString("description");
	        	 String parent_id = result.getString("parent_id");
	        	 String sort_num = result.getString("sort_num");
	        	 String url_path = result.getString("url_path");
	        	 String js_classname = result.getString("js_classname");
	        	 String is_used = result.getString("is_used");
	        	 String is_visible = result.getString("is_visible");
	        	 String grade = result.getString("grade");
	        	 ModuleMenu module = new ModuleMenu(id,name,description,parent_id,sort_num,url_path,js_classname,is_used,is_visible, grade);
	        	 moduleList.add(module);
//	        	 List<ModuleMenu> oldlist = list.get(parent_id);
//	        	 
//	        	 if(oldlist!=null && oldlist.size()>0){
//	        		 oldlist.add(module);
//	        	 }else{
//	        		 oldlist=new  ArrayList<ModuleMenu>();
//	        		 oldlist.add(module);
//	        		 list.put(parent_id, oldlist);
//	        	 }
	        }  
	        this.CloseAll();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
    }
    
    public void getDataList(){
    	JSONObject res = new JSONObject();
    	List<ModuleMenu> lists = this.getModuleList(); 
    	JSONArray array = new JSONArray();
    	for (int i = 0; i < lists.size(); i++) {
			  ModuleMenu mdo = lists.get(i);
			  String nodeId= mdo.getId();
			  JSONObject json = new JSONObject();
			  json.put("id", nodeId);
			  json.put("text", mdo.getText());
			  json.put("pid", mdo.getPid()!=null?mdo.getPid():"");
			  json.put("modulename", mdo.getModulename()!=null?mdo.getModulename():"");
			  json.put("modulepath", mdo.getModulepath()!=null?mdo.getModulepath():"");
			  array.add(json);
		}
    	res.put("result", array);
    	res.put("totla", lists.size());
    	System.out.println(res.toString());
    }
    
    public void getJSONString(){
    	JSONObject res = new JSONObject();
    	long start = System.currentTimeMillis();
    	List<ModuleMenu> lists = this.getModuleList(); 
    	String pid = "1";
    	JSONArray data = this.eachJSONObject(lists, pid);
    	long end = System.currentTimeMillis();
    	long mid = end - start;
    	System.out.println("共消耗"+mid+"ms");
    	res.put("result", data);
    	res.put("totla", lists.size());
    	System.out.println(res.toString());
    };
    
    
   

	public List<ModuleMenu> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<ModuleMenu> moduleList) {
		this.moduleList = moduleList;
	}

	public Map<String, List<ModuleMenu>> getList() {
		return list;
	}

	public void setList(Map<String, List<ModuleMenu>> list) {
		this.list = list;
	}
    //递归
	public JSONArray eachJSONObject(List<ModuleMenu> list,String pid){
    	JSONArray aray = new JSONArray();
    	if(list!=null && list.size() > 0){
    		  for (ModuleMenu mdo : list) {
    			  String nodeId= mdo.getId(),pider =mdo.getPid() ;
    			  JSONObject json = new JSONObject();
    			  json.put("id", nodeId);
    			  json.put("text", mdo.getText());
    			  json.put("pid", mdo.getPid());
    			  json.put("modulename", mdo.getModulename());
    			  json.put("modulePath", mdo.getModulepath());
    			  if( pid.equals(pider)){
    				  JSONArray newlist= this.eachJSONObject(list, nodeId);
    				  if(newlist.size()>0){
    					  json.put("leaf", false);
    					  json.put("children", newlist);
    				  }else{
    					  json.put("leaf", true);
    				  }
    				  aray.add(json);
    			  }
    	      }
    	}
    	return aray;
    }
    
    public static void main(String[] args) {
    	JSONMenu men = new JSONMenu();
    	men.getDataList(); //列表数据
    	//men.getJSONString(); //树形菜单数据
	}
}
/**
 * @author zhengang.he
 *
 */
class ModuleMenu{
	private String id ;
	private String text ;
	private String description ;
	private String pid ;
	private String sort_num ;
	private String modulepath ;
	private String modulename ;
	private String is_used ;
	private String is_visible ;
	private String grade ;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getSort_num() {
		return sort_num;
	}
	public void setSort_num(String sort_num) {
		this.sort_num = sort_num;
	}
	public String getModulepath() {
		return modulepath;
	}
	public void setModulepath(String modulepath) {
		this.modulepath = modulepath;
	}
	public String getModulename() {
		return modulename;
	}
	public void setModulename(String modulename) {
		this.modulename = modulename;
	}
	public String getIs_used() {
		return is_used;
	}
	public void setIs_used(String is_used) {
		this.is_used = is_used;
	}
	public String getIs_visible() {
		return is_visible;
	}
	public void setIs_visible(String is_visible) {
		this.is_visible = is_visible;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public ModuleMenu(String id, String text, String description, String pid,
			String sort_num, String modulePath, String moduleName,
			String is_used, String is_visible, String grade) {
		super();
		this.id = id;
		this.text = text;
		this.description = description;
		this.pid = pid;
		this.sort_num = sort_num;
		this.modulepath = modulePath;
		this.modulename = moduleName;
		this.is_used = is_used;
		this.is_visible = is_visible;
		this.grade = grade;
	}
	public ModuleMenu() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
