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
	HebrewDateMonthInput him;
	HebrewDateDayInput hid;
	TextBox hiy;
	GregDateMonthInput gmon_in;
	TextBox gday_in;
	TextBox gyear_in;
	TextBox m_name;
	//HebrewDate m_hebDate=null;
	Yahrzeit m_honoree=null;
	MyFlexTable m_myFlexTable=null;
	
	EditRowModifyHandler(
			TextBox namebox,
			HebrewDateMonthInput him,
			HebrewDateDayInput hid,
			TextBox hiy,
			Yahrzeit h,
			MyFlexTable f
			)
			{
		this.m_name=namebox;
		this.him=him;
		this.hid=hid;
		this.hiy=hiy;
		m_honoree=h;
		m_myFlexTable=f;
			}
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		//HebrewYear
		boolean isvalid=true;
		int hebday=0,hebyear=0;

		
		//hebrew
		try{
		hebday=hid.getDay();
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
			hebyear=Integer.parseInt(hiy.getValue());
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
				m_honoree.setHebMonth(him.getMonth());
				m_honoree.setHebDay(hid.getDay());
				m_honoree.setHebYear(Integer.parseInt(hiy.getValue()));
					m_myFlexTable.modifyRowRequest(m_honoree);
				
				
			}
	}

}
