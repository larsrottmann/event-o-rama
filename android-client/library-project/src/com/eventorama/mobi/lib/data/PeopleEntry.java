package com.eventorama.mobi.lib.data;

import com.google.gson.annotations.SerializedName;

public class PeopleEntry {
	
	@SerializedName("id")
	private final int server_id;
	private final String name;
	private final float lat;
	private final float lon;
	private final float accuracy;
	@SerializedName("location-upate")
	private final long location_update;
	
	
	public PeopleEntry(int server_id, String name, float lat, float lon, float accuracy,
			long location_update) {
		
		this.server_id = server_id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.accuracy = accuracy;
		this.location_update = location_update;
	}


	public final int getServerId() {
		return server_id;
	}


	public final String getName() {
		return name;
	}


	public final float getLat() {
		return lat;
	}


	public final float getLon() {
		return lon;
	}


	public final float getAccuracy() {
		return accuracy;
	}


	public final long getLocation_update() {
		return location_update;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("serverid: ").append(server_id);
		sb.append(" name: ").append(name);
		sb.append(" Lat: ").append(lat);
		sb.append(" Long: ").append(lon);
		sb.append(" accuracy: ").append(accuracy);
		sb.append(" loc_update: ").append(location_update);
		
		return sb.toString();
	}
	
}
