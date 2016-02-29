package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
 
	final class FBAlbumData extends FBApiResponse {
		protected FBAlbumData(){}
		public  native int size() /*-{
			$wnd.console.log("returning size: " + this.albums.data.length);
			return this.albums.data.length;
		}-*/;
		
		public native String getName(int i) /*-{return this.albums.data[i].name;}-*/;
		public native String getId(int i) /*-{return this.albums.data[i].id;}-*/;
		public native JsArray<PhotoNative> getPhotos(int i) /*-{
			if (this.albums.data[i].photos!=null) {
				return this.albums.data[i].photos.data;
			} else {
				return null;
			}
		}-*/;
	}

