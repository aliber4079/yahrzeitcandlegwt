package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;

public class FBPhoto extends JavaScriptObject {
	//String id,aid;
	protected FBPhoto () {}
	public final native void setUrl(String s) /*-{ this.url=s;}-*/;
	public final native String getUrl() /*-{return this.url;}-*/;
	public final native String getId() /*-{ return this.id ;}-*/;
	public final native void setId(String id) /*-{this.id=id;}-*/;
	public final native String getAid() /*-{return this.aid;}-*/;
	public final native void setPid(String pid) /*-{this.id=pid }-*/;
	
}
