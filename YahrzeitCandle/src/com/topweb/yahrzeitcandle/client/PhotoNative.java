package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;

	  final class PhotoNative extends JavaScriptObject {
			protected PhotoNative(){}
			public native String getUrl() /*-{
				//html entity decode, needed when getting an url back from an uploaded photo
			    var txt = document.createElement("textarea");
    			txt.innerHTML = this.picture;
    			return txt.value;
				}-*/;
			public native String getId() /*-{return this.id;}-*/;
			public native static PhotoNative create(String id, String url) /*-{
				return {id: id, picture: url};
			}-*/;
		}

