package com.example.weatherapp.data;

public class City {

	private String name;
	private String WOEID;
	
	public City(String name, String woeid)
	{
		this.name = name;
		WOEID = woeid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWOEID() {
		return WOEID;
	}

	public void setWOEID(String wOEID) {
		WOEID = wOEID;
	}
}
