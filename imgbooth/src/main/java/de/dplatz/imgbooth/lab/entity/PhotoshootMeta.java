package de.dplatz.imgbooth.lab.entity;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class PhotoshootMeta {
	private int index;
	private int size;
	private String id;
	
	public PhotoshootMeta(String json) {
		super();
		JsonReader reader = Json.createReader(new StringReader(json));
		JsonObject obj = reader.readObject();
		this.id = obj.getString("id");
		this.index = obj.getInt("index");
		this.size = obj.getInt("size");
	}
	
	public PhotoshootMeta(String id, int index, int size) {
		super();
		this.id = id;
		this.index = index;
		this.size = size;
	}
	public int getIndex() {
		return index;
	}
	public int getSize() {
		return size;
	}
	
	public String getId() {
		return id;
	}
	@Override
	public String toString() {
		return id  + "(" + index + "/" + size + ")";
	}
}