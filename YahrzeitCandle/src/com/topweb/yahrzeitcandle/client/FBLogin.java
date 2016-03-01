package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;


public abstract class FBLogin {
	 
	public native void login (String theperms) /*-{
		var cbobject=this;
	$wnd.console && $wnd.console.log("calling fblo with perms " + theperms);
	 $wnd.FB.login(cbobject.@com.topweb.yahrzeitcandle.client.FBLogin::apiCallback(Lcom/topweb/yahrzeitcandle/client/FBAuthResponse;),
	  {scope: theperms, return_scopes:true});
	}-*/;
	public abstract void apiCallback(FBAuthResponse response);
}

 
