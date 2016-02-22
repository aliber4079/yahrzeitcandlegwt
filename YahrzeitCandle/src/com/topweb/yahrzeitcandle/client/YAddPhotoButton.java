package com.topweb.yahrzeitcandle.client;

import com.google.gwt.user.client.ui.Button;

public class YAddPhotoButton extends Button {
Yahrzeit m_yahrzeit=null;
	public YAddPhotoButton(Yahrzeit y){
		super("set photo");
		m_yahrzeit=y;
	}
	public Yahrzeit getYahrzeit() {
		// TODO Auto-generated method stub
		return m_yahrzeit;
	}

}
