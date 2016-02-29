package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public abstract class FBApi {
	 
	public native void get (String path) /*-{
		var cbobject=this;
	$wnd.console && $wnd.console.log("calling api with path " + path);
	 $wnd.FB.api(path,"GET",cbobject.@com.topweb.yahrzeitcandle.client.FBApi::apiCallback(Lcom/topweb/yahrzeitcandle/client/FBApiResponse;));
	}-*/;
	public abstract void apiCallback(FBApiResponse response);
}

class FBApiResponse extends JavaScriptObject {
	protected FBApiResponse(){}

	public native final JavaScriptObject getData() /*-{
		return this;
	}-*/;
}