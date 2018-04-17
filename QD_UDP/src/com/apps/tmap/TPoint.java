package com.apps.tmap;

public class TPoint {
    private double lng;
    private double lat;
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public TPoint( double lat,double lng) {
		super();
		this.lng = lng;
		this.lat = lat;
	}
	public TPoint() {
		super();
		// TODO Auto-generated constructor stub
	}
}
