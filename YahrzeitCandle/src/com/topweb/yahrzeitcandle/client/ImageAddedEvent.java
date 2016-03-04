package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.GwtEvent;

public class ImageAddedEvent extends GwtEvent<ImageAddedHandler> {
    //public static Type<ImageAddedHandler> TYPE = new Type<ImageAddedHandler>();

    public final JsArray<PhotoNative> photos_to_url;
    public ImageAddedEvent (){
    	photos_to_url=null;	
    }
    
	public ImageAddedEvent(JsArray<PhotoNative> photos_to_url) {
		this.photos_to_url=photos_to_url;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ImageAddedHandler> getAssociatedType() {
		 
		return ImageAddedHandler.TYPE;
	}

	@Override
	protected void dispatch(ImageAddedHandler handler) {
		handler.onImageAdded(this);
	}

}
