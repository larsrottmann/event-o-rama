package com.eventorama.mobi.lib.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivityElement {

	
	private final int internal_id;
	@Expose	private final long timestamp;
	@Expose	private final String text;
	@Expose	private final int type;
	@Expose @SerializedName("user-id") private final int user_id;

	
	public ActivityElement(int internal_id, long timestamp, String text,
			int type, int user_id) {

		this.internal_id = internal_id;
		this.timestamp = timestamp;
		this.text = text;
		this.type = type;
		this.user_id = user_id;
	}
	
	public final long getTimestamp() {
		return timestamp;
	}
	
	public final String getText() {
		return text;
	}
	
	public final int getType() {
		return type;
	}

	public final int getUser_id() {
		return user_id;
	}

	public final int getInternal_id() {
		return internal_id;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(ActivityElement.class.getName());
		sb.append(internal_id);
		sb.append(timestamp);
		sb.append(text);
		sb.append(type);
		sb.append(user_id);
		
		return sb.toString();
	}

}
