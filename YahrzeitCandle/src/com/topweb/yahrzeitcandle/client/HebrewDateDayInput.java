package com.topweb.yahrzeitcandle.client;

import java.util.Date;
import java.util.Map;

//import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import net.sf.hebcal.HebrewDate;

public class HebrewDateDayInput {
private static Map<String, Integer> 
m_maxdays =  null;

 HebrewDateDayInput(){

	//m_maxdays=new HashMap<String,Integer>();
	m_day=new TextBox();
	
	m_day.setText("1");
	m_day.setVisibleLength(2);
	m_day.setTitle("hebday");
	//Date today=new Date();
	//HebrewDate h = new HebrewDate();
	m_day.setValue(""+YahrzeitCandle.m_hebDate.getHebrewDate());
	m_day.addChangeHandler (YahrzeitCandle.m_changedate);
	
		
}
	private TextBox m_day=null;

	public TextBox getTextBox() {
		// TODO Auto-generated method stub
		return m_day;
	}

	/*public String getDay() {
		// TODO Auto-generated method stub
		return m_day.getValue();
	}*/
	public int getDay(){
		return Integer.parseInt(m_day.getValue());
		
	}
	public void setDay(int day){
		m_day.setValue(""+day);
	}

	public void setEnabled(boolean b) {
		// TODO Auto-generated method stub
		m_day.setEnabled(b);
	}

	public void setId(String id) {
		// TODO Auto-generated method stub
		m_day.getElement().setId(id);
	}

	public void addChangeHandler(ChangeHandler h) {
		// TODO Auto-generated method stub
		m_day.addChangeHandler(h);
		
	}

}
