package com.apps.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * @author zhengang.he
 *
 */
public class JsonMenuUtils {
	
	private String jsonText = "{}" ;
	private List<Module> list = null;
	
	String filename = "/topmenu.json";
    public JsonMenuUtils(){
    	InputStream  instream = JsonMenuUtils.class.getResourceAsStream(filename);
    	StringBuffer context = new StringBuffer();
    	list = new ArrayList<Module>();
        try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                context.append(line);
            }
            jsonText = context.toString();
            this.readJsonData();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    public void readJsonData(){
    	JSONObject jsonText =JSONObject.parseObject(this.jsonText);
    	JSONArray list=jsonText.getJSONArray("result");
    	String root = "INSERT INTO `T_MODULE` VALUES ('1', '功能模块', '功能模块', null, '1', '0', null, '', '', null);";
    	StringBuffer textList= new StringBuffer();
    	textList.append(root+"\r\n");
    	if(list !=null && list.size()>0){
    		System.out.println(list.size());
    		for (int i=0;i<list.size();i++) {
				JSONObject json = list.getJSONObject(i); 
				//String id = json.getString("id");
				String text = json.getString("text");
				String pid = json.getString("pid");
				String modulename = json.getString("modulename");
				String modulepath = json.getString("modulepath");
				
				//Module newnod= new Module(id,text,pid,modulename,modulepath);
				//list.add(newnod);
				int index  = i +2;
			    String txt = "INSERT INTO `T_MODULE` VALUES ('"+index+"', '"+text+"', '"+text+"', '"+pid+"', '1', '"+index+"', '"+modulepath+"', '', '"+modulepath+"', '"+modulename+"');";	
			    textList.append(txt+"\r\n");
			}
    	}
    	System.out.println(textList.toString());
    }
    
    
    public List<Module> getList() {
		return list;
	}
	public void setList(List<Module> list) {
		this.list = list;
	}
}
