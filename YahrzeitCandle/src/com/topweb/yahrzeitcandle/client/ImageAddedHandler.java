package com.topweb.yahrzeitcandle.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface ImageAddedHandler extends EventHandler {
 public void onImageAdded(ImageAddedEvent event);

 //public static final Type<ImageAddedHandler> TYPE = new Type<ImageAddedHandler>();
 public static Type<ImageAddedHandler> TYPE = new Type<ImageAddedHandler>();
 
}
