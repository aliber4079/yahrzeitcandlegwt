package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public class FBAuthResponse extends JavaScriptObject {
	 
	protected FBAuthResponse () {}
	public native final String getStatus () /*-{return this.status}-*/;
	public native final int getUid () /*-{return this.authResponse.userID}-*/;
	public native final String getAccessToken() /*-{return this.authResponse.accessToken}-*/;
	public native final JavaScriptObject getResponse() /*-{return this.authResponse}-*/;
	public native final String getPermsGranted() /*-{
		if (this.authResponse && this.authResponse.grantedScopes) {
		return this.authResponse.grantedScopes;
		}
		return null;
	}-*/;
	
}
