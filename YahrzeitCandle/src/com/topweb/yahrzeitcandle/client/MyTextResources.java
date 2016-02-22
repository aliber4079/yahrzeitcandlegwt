package com.topweb.yahrzeitcandle.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.core.client.GWT;



	interface MyTextResources extends ClientBundle {
		  MyTextResources INSTANCE = GWT.create(MyTextResources.class);

 		  @Source("intro_msg.txt")
		  TextResource getIntroMsg();
	}


