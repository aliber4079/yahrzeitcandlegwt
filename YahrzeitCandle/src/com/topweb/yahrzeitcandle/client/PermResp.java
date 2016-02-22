package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;

public class PermResp extends JavaScriptObject {
	protected PermResp() {}
	public native final String getPerms() /*-{return this.perms;}-*/;

}
