package com.mapabc.gater.lbsgateway.bean;

public class User {
	private String id = "";
	
	private String name = "";
	
	private String password = "";

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
}
