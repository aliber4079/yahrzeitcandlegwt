package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;


public abstract class FBApi {
	 
	public native void get (String path) /*-{
		var cbobject=this;
	$wnd.console && $wnd.console.log("calling api with path " + path);
	 $wnd.FB.api(path,"GET",cbobject.@com.topweb.yahrzeitcandle.client.FBApi::apiCallback(Lcom/google/gwt/core/client/JavaScriptObject;));
	}-*/;
	public abstract void apiCallback(JavaScriptObject response);
}

 
