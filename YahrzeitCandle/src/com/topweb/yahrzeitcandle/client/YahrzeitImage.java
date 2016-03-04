package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;

public class YahrzeitImage extends Image implements ImageAddedHandler{
	Yahrzeit m_yahrzeit=null;
	YahrzeitImage(){
		//super("silhouette.gif");
		addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				MyFlexTable.resizeFbIframe();
			}
		});
		YahrzeitCandle.yahrFlexTable.addImageAddedHandler(this);
		
	}
	
	YahrzeitImage(Yahrzeit y){
		 
		//	setUrl(y.getSrcSmall());
		setYahrzeit(y);
		addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				MyFlexTable.resizeFbIframe();
			}
		});
		YahrzeitCandle.yahrFlexTable.addImageAddedHandler(this);

	}
	public void setYahrzeit(Yahrzeit yahrzeit){
		//FB.api
		m_yahrzeit=yahrzeit;
		 //+yahrzeit.getPhoto());
	}
	public Yahrzeit getYahrzeit(){
		return m_yahrzeit;
	}

	public void clear() {
		setUrl("");
	}

	@Override
	public void onImageAdded(ImageAddedEvent event) {
		Console.log("on image added");
		Console.log("photos to url length in event: " + event.photos_to_url.length());
		for (int i=0;i< event.photos_to_url.length();i++){
			Console.log("comparing " + event.photos_to_url.get(i).getId() + " to " + m_yahrzeit.getPhoto());
			if (event.photos_to_url.get(i).getId()==String.valueOf(m_yahrzeit.getPhoto())){
				Console.log("setting photo to " + event.photos_to_url.get(i).getUrl());
				setUrl(event.photos_to_url.get(i).getUrl());
				break;
			}
		}
		 
	}
	
	 
}