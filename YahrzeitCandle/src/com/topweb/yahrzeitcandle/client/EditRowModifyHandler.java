package com.topweb.yahrzeitcandle.client;

import net.sf.hebcal.HebrewDate;
import net.sf.hebcal.HebrewDateException;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditRowModifyHandler implements ClickHandler {
	 

	TextBox m_name;
	Yahrzeit m_honoree=null;
	MyFlexTable m_myFlexTable=null;
	
	EditRowModifyHandler(
			TextBox namebox,
			Yahrzeit h,
			MyFlexTable f
			)
			{
		this.m_name=namebox;
		m_honoree=h;
		m_myFlexTable=f;
			}
	@Override
	public void onClick(ClickEvent event) {
		//HebrewYear
		boolean isvalid=true;
		int hebday=0,hebyear=0;

		
		//hebrew
		try{
		hebday=YahrzeitCandle.newHebrewDate_day.getDay();
	}catch(Exception e){hebday=-1;}
	if (hebday==-1) {
		YahrzeitCandle.displayError("hebday must be numeric");
		isvalid=false;
	
	}else if(hebday <31 && hebday>0){ 
		//valid day
	}else {
		YahrzeitCandle.displayError("hebday out of range: " + hebday);
		isvalid=false;
	}
	
	

		
		try {
			hebyear=Integer.parseInt(YahrzeitCandle.newHebrewYear.getValue());
		}catch (Exception e){hebyear=-1;}
		if (hebyear==-1){
			YahrzeitCandle.displayError("hebyear must be numeric");
			isvalid=false;
		}else if (hebyear < 5800 && hebyear >0) {
			//valid date
		}else{
			YahrzeitCandle.displayError("hebyear out of range: "+hebyear);
			isvalid=false;
		}

		
		if (m_name.getValue()==null || m_name.getValue().length()==0)
			isvalid=false;
		
		
			if (isvalid){
				//System.out.println("setting month, day, yr to "
				//		+ newGregDate_mon.getMonth() + " " + gregday + " " +  gregyear);
				m_honoree.setName(m_name.getText());
				m_honoree.setHebMonth(YahrzeitCandle.newHebrewDate_mon.getMonth());
				m_honoree.setHebDay(YahrzeitCandle.newHebrewDate_day.getDay());
				m_honoree.setHebYear(Integer.parseInt(YahrzeitCandle.newHebrewYear.getValue()));
					m_myFlexTable.modifyRowRequest(m_honoree);
				
				
			}
	}

}
