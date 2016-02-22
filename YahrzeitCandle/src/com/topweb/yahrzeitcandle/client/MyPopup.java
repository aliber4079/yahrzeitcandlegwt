package com.topweb.yahrzeitcandle.client;


import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class MyPopup extends PopupPanel {
	public MyPopup(){super(true);
	setWidget(new Label("Not editable while in demo mode. Click the login button to begin entering Yahrzeits."));
	//setGlassEnabled(true);
	}

}
