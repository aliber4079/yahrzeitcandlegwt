package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Perm extends JavaScriptObject {
	protected Perm() {}
	public final native String getPermission() /*-{
		return   this.permission;
	}-*/;
	 
	public final native String getStatus() /*-{
		return   this.status;
	}-*/;
	
}
