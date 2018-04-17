package com.apps.menu;

public class Module {
    private String id;
    private String text;
    private String pid;
    private String modulename;
    private String modulepath;
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
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getModulename() {
		return modulename;
	}
	public void setModulename(String modulename) {
		this.modulename = modulename;
	}
	public String getModulepath() {
		return modulepath;
	}
	public void setModulepath(String modulepath) {
		this.modulepath = modulepath;
	}
	public Module(String id, String text, String pid, String modulename, String modulepath) {
		super();
		this.id = id;
		this.text = text;
		this.pid = pid;
		this.modulename = modulename;
		this.modulepath = modulepath;
	}
	public Module() {
		super();
		// TODO Auto-generated constructor stub
	}
  
}
