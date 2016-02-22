package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;

public class PermsResponse extends JavaScriptObject {
	protected PermsResponse() {}
	public final native String getPermission() /*-{
		return   this.permission;
	}-*/;
	 
	public final native String getStatus() /*-{
		return   this.status;
	}-*/;
	
}
