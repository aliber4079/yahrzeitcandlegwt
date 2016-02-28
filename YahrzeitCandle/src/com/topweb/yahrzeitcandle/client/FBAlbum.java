package com.topweb.yahrzeitcandle.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public final class FBAlbum extends ArrayList<FBPhoto> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7353959587734568900L;
	String m_id=null,m_name=null;
	FBAlbum(String id, String name){
		this.m_id=id;
		this.m_name=name;
	}
	public   String getId() { return this.m_id ;}
	public   void setId(String id){this.m_id=id;}
	
	public  String getName() {
		return m_name;
	}
	public  void setName(String name) {
		this.m_name = name;
	}
	 
}
