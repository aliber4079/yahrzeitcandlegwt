package com.topweb.yahrzeitcandle.client;

 

class FBPhoto{
	String id;
	String url;
	FBPhoto(String id, String url){
		this.id=id;
		this.url=url;
	}
	public String getId(){
		return this.id;
	}
	public String getUrl(){
		return this.url;
	}
}
