package com.apps.tmap;

public class Tile {
    private int zoom;
    private String url ;
    private int x;
    private int y;
	public int getZoom() {
		return zoom;
	}
	public void setZoom(int zoom) {
		this.zoom = zoom;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public Tile(int zoom, String url, int x, int y) {
		super();
		this.zoom = zoom;
		this.url = url;
		this.x = x;
		this.y = y;
	}
	public Tile() {
		super();
	}
	
   
   
}
