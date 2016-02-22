package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;

public class FBResponse extends JavaScriptObject {
	protected FBResponse() {}
	public final native FBAuthResponse getAuthResponse() /*-{return this.authResponse;}-*/;
	public final native String getPerms() /*-{return this.perms; }-*/;

}
