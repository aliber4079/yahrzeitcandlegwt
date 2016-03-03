package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public class YahrzeitImage extends Image {
	Yahrzeit m_yahrzeit=null;
	YahrzeitImage(){
		//super("silhouette.gif");
		addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				MyFlexTable.resizeFbIframe();
			}			
		});
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
}