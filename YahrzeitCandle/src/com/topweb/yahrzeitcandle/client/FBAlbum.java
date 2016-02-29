package com.topweb.yahrzeitcandle.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

 

class FBAlbum{
	String id;
	String name;
	List<FBPhoto> photos;
	FBAlbum(){
		photos=new ArrayList<FBPhoto>();
	}
	public List<String> getPhotoUrls() {
		List<String>urls=new ArrayList<String>();
		
		for (int i=0;i<photos.size();i++){
			urls.add(photos.get(i).getUrl());
		}
		return urls;
	}
	public FBAlbum(String id, String name) {
		photos=new ArrayList<FBPhoto>();
		this.id=id;
		this.name=name;
	}
	public void addPhoto(FBPhoto p){
		photos.add(p);
	}
	public String getName(){
		return this.name;
	}
	public List<FBPhoto> getPhotos(){
		return photos;
	}
}
