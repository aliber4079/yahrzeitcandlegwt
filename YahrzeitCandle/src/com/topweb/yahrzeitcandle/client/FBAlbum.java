package com.topweb.yahrzeitcandle.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class FBAlbum {
	List<FBPhoto>m_photos =null;
	public FBAlbum() {}
	public FBAlbum(FBPhotoAlbumData fbPhotoAlbumData) {
			m_photos=new ArrayList<FBPhoto>();
			this.size=fbPhotoAlbumData.getSize();
			this.name=fbPhotoAlbumData.getName();
			this.id=fbPhotoAlbumData.getAid();
	}
	int size;
	String name;
	String id;
	public int getSize() {return this.size;}
	public  String getName() { return this.name ;}
	public void setName(String s) {this.name=s;}
	public  String getId() { return this.id ;}
	public  void setId(String id) {this.id=id;}
	public void addPhoto(FBPhoto fbPhoto) {
		m_photos.add(fbPhoto);
	}
	public List<FBPhoto> getPhotos() {
			return m_photos;
	}
}
