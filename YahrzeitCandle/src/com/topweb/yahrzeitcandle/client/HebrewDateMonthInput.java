package com.topweb.yahrzeitcandle.client;

import net.sf.hebcal.HebrewDate;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;

public class HebrewDateMonthInput {

	HebrewDateMonthInput(){
		
	 m_ListBox=new ListBox();
	 m_ListBox.setTitle("hebmonth");
	 for (int i=0;i<YahrzeitCandle.heb_months.length;i++){
		 m_ListBox.addItem(YahrzeitCandle.heb_months[i]);
	 }
	 //HebrewDate h = new HebrewDate();
	 //System.out.println ("hebrew month: " + h.getHebrewMonth());
	 m_ListBox.setItemSelected(YahrzeitCandle.m_hebDate.getHebrewMonth()-1,true);
	 m_ListBox.addChangeHandler(YahrzeitCandle.m_changedate);
	}
	public ListBox getListBox (){
		return m_ListBox;
	
	}
	public int getMonth(){
		
		return m_ListBox.getSelectedIndex()+1;
	}
	
	private ListBox m_ListBox=null;

	public void setMonth(int gregorianMonth) {
		// TODO Auto-generated method stub
		m_ListBox.setSelectedIndex(gregorianMonth-1);
		
	}
	public void setEnabled(boolean b) {
		// TODO Auto-generated method stub
		m_ListBox.setEnabled(b);
	}
	public void addChangeHandler (ChangeHandler c) {
		// TODO Auto-generated method stub
		m_ListBox.addChangeHandler(c);
	}
	public void setId(String id) {
		// TODO Auto-generated method stub
		m_ListBox.getElement().setId(id);
	}
}
	

