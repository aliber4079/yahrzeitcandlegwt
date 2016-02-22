package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;

public  class Console extends JavaScriptObject {
	protected Console() {}
	public static native void log (String s) /*-{
	$wnd.console && $wnd.console.log(s);
	}-*/;
	public static native void logAsObject(Object o) /*-{
	$wnd.console && $wnd.console.log(o);	
	}-*/;
}

