package com.topweb.yahrzeitcandle.client;

import com.google.gwt.user.client.ui.Anchor;

public class PhotoAnchor extends Anchor {
	public PhotoAnchor (Yahrzeit y) {super();m_Yahrzeit=y;}
	Yahrzeit m_Yahrzeit;
	public Yahrzeit getYahrzeit() { return m_Yahrzeit;}
	public void setPhoto(String srcSmall) {
		setHTML("<img src=\""+ srcSmall +"\"></img>");
		
	}

}
