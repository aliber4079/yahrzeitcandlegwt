package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;

	  final class PhotoNative extends JavaScriptObject {
			protected PhotoNative(){}
			public native String getUrl() /*-{return this.picture;}-*/;
			public native String getId() /*-{return this.id;}-*/;
			
		}

