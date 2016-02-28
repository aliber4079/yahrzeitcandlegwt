package com.topweb.yahrzeitcandle.client;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class HebrewDateMonthInput {

	HebrewDateMonthInput(){
		
	 m_ListBox=new ListBox();
	 m_ListBox.setTitle("hebmonth");
	 for (int i=1;i<YahrzeitCandle.heb_months.length;i++){
		 m_ListBox.addItem(YahrzeitCandle.heb_months[i]);
	 }
	m_ListBox.setItemSelected(YahrzeitCandle.m_hebDate.getHebrewMonth(),true);
	 m_ListBox.addChangeHandler(YahrzeitCandle.m_changedate);
	}
	public ListBox getListBox (){
		return m_ListBox;
	
	}
	public int getMonth(){
		
		return m_ListBox.getSelectedIndex();
	}
	
	private ListBox m_ListBox=null;

	public void setMonth(int gregorianMonth) {
		m_ListBox.setSelectedIndex(gregorianMonth);
		
	}
	public void setEnabled(boolean b) {
		m_ListBox.setEnabled(b);
	}
	public void addChangeHandler (ChangeHandler c) {
		m_ListBox.addChangeHandler(c);
	}
	public void setId(String id) {
		m_ListBox.getElement().setId(id);
	}
}
	

