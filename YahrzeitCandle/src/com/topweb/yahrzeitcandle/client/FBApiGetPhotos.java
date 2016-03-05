package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class FBApiGetPhotos extends FBApi {
	@Override
	public void apiCallback(JavaScriptObject response) {
		Console.logAsObject(response);
		JsArray<PhotoNative> photos_to_url = (new Object(){
			public native JsArray<PhotoNative> getPhotos(JavaScriptObject response) /*-{
				photos=[]
				for (i in response){
					photos.push({id:response[i].id,picture:response[i].picture});
				}
				$wnd.console && $wnd.console.log(photos);
				return photos;
			}-*/;
		}.getPhotos(response));
		YahrzeitCandle.yahrFlexTable.fireEvent(new ImageAddedEvent(photos_to_url));
	}
}
