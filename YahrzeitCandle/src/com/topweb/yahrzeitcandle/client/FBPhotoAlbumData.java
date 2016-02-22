package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class FBPhotoAlbumData extends JavaScriptObject {
	protected FBPhotoAlbumData () {}
	public final native JsArray<FBPhotoAlbumData> getData() /*-{
		return this.data}-*/;
	public final native void setData (JsArray<FBPhotoAlbumData> s) /*-{
		this.data=s; }-*/;
	public final native int getSize() /*-{return parseInt(this.size);}-*/;
	public final native String getName() /*-{ return this.name }-*/;
	public final native void setName(String s) /*-{this.name=s;}-*/;
	public final native String getPid() /*-{ return this.pid }-*/;
	
	public final native String getSrcSmall() /*-{ return this.src_small;}-*/;
	public final native String getSrcBig() /*-{return this.src_big;}-*/;
	public final native String getAid() /*-{return this.aid}-*/;
	
	

}
