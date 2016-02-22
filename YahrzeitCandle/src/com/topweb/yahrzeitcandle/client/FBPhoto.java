package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;

public class FBPhoto  {
	String src_small,src_big;
	String id,aid;
	public FBPhoto () {}
	public FBPhoto(FBPhotoAlbumData fbPhotoAlbumData) {
		this.id=fbPhotoAlbumData.getPid();
		this.src_small=fbPhotoAlbumData.getSrcSmall();
		this.src_big=fbPhotoAlbumData.getSrcBig();
		this.aid=fbPhotoAlbumData.getAid();

	}
	public  String getSrcSmall() { return this.src_small ;}
	public  String getSrcBig() { return this.src_big ;}
	public  void setUrl(String s) {this.src_small=s;}
	public String getId() { return this.id ;}
	public void setId(String id) {this.id=id;}
	public  String getAid() {return this.aid;}
	public void setPid(String pid) {
		this.id=pid;
		
	}
}
