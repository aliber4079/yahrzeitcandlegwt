package com.topweb.yahrzeitcandle.client;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class GregDateMonthInput {
	ListBox m_month=null;
	GregDateMonthInput (int gregmonth) {
		//m_maxdays=new HashMap<String,Integer>();
		m_month=new ListBox();
		//m_month.setWidth("75px");
		m_month.setTitle("gregmonth");
		for (int i=1;i<YahrzeitCandle.greg_months.length;i++){
		  	m_month.addItem(YahrzeitCandle.greg_months[i]);
		}
		
		
		m_month.setItemSelected(gregmonth-1, true);
		m_month.addChangeHandler (YahrzeitCandle.m_changedate);

	
	}
	GregDateMonthInput(){

		//m_maxdays=new HashMap<String,Integer>();
		m_month=new ListBox();
		m_month.setWidth("150px");
		m_month.setTitle("gregmonth");
		for (int i=1;i<YahrzeitCandle.greg_months.length;i++){
		  	m_month.addItem(YahrzeitCandle.greg_months[i]);
		
		}
		//Date today = new Date();
		int gregmonth=YahrzeitCandle.m_hebDate.getGregorianMonth();
		m_month.setItemSelected(gregmonth, true);
		 
	m_month.addChangeHandler (YahrzeitCandle.m_changedate);
		

	}

		public Widget getListBox() {
			// TODO Auto-generated method stub
			return m_month;
		}

		public void setMonth(int gregorianMonth) {
			// TODO Auto-generated method stub
			m_month.setItemSelected(gregorianMonth,true);
		}

		public int getMonth() {
			// TODO Auto-generated method stub
			return m_month.getSelectedIndex();
		}

		public void setEnabled(boolean b) {
			// TODO Auto-generated method stub
			m_month.setEnabled(b);
		}
		public void setId(String id) {
			// TODO Auto-generated method stub
			m_month.getElement().setId(id);
		}
		public void addChangeHandler(ChangeHandler h) {
			// TODO Auto-generated method stub
			m_month.addChangeHandler(h);
		}

	}


