package com.topweb.yahrzeitcandle.client;

import net.sf.hebcal.HebrewDate;
import net.sf.hebcal.HebrewDateException;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditRowDateChangeHandler implements ChangeHandler {
	HebrewDateMonthInput him;
	HebrewDateDayInput hid;
	TextBox hiy;
	GregDateMonthInput gmon_in;
	TextBox gday_in;
	TextBox gyear_in;
	HebrewDate m_hebDate=null;
	int row;
	
	EditRowDateChangeHandler(
			HebrewDateMonthInput him,
			HebrewDateDayInput hid,
			TextBox hiy,
			GregDateMonthInput gmon_in,
			TextBox gday_in,
			TextBox gyear_in,
			int row
			)
			{
		this.him=him;
		this.hid=hid;
		this.hiy=hiy;
		this.gmon_in=gmon_in;
		this.gday_in=gday_in;
		this.gyear_in=gyear_in;
		this.row=row;
		m_hebDate=new HebrewDate();
			}
	public void onChange(ChangeEvent event){
		boolean isvalid=false;
		int hebday,hebyear,gregday,gregyear;
		hebday = hebyear= gregday= gregyear=0;
		
		String src=((Widget)event.getSource()).getElement().getId();
		
			
			
			
			
		
		if (src.equals("him")|| src.equals("hid")|| src.equals("hiy"))
		{
			try{
			hebday=hid.getDay();
		}catch(Exception e){hebday=-1;}
		if (hebday==-1) {
			YahrzeitCandle.displayError("hebday must be numeric");
			
		
		}else if(hebday <31 && hebday>0){ 
			//valid day
			isvalid=true;
			
		}else 
			YahrzeitCandle.displayError("hebday out of range: " + hebday);
		
		
		//HebrewYear
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
		
		if (isvalid){
			//update gregdate
			try {
				m_hebDate.setHebrewDate(him.getMonth(), hebday, hebyear);
				gday_in.setValue(""+m_hebDate.getGregorianDayOfMonth());
				gmon_in.setMonth(m_hebDate.getGregorianMonth());
				System.out.println("new gregyear is " + m_hebDate.getGregorianYear());
				gyear_in.setValue(""+m_hebDate.getGregorianYear());
				
				//also set upcoming date
				m_hebDate=(YahrzeitCandle.calc_greg_date_todisplay(him.getMonth(), hebday));
				MyFlexTable.flexTable.setText(row, 2, YahrzeitCandle.format_pretty.format(YahrzeitCandle.format_fromhebcal.parse(m_hebDate.formatGregorianDate_English())));
				
				
				
				return;
			} catch (HebrewDateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		} else
		 if (src.equals("gmon_in")|| src.equals("gday_in") || src.equals("gyear_in")){
				
		try {
		gregday=Integer.parseInt(gday_in.getValue());
		}catch(Exception e){gregday=-1;}
		if (gregday==-1) {
			YahrzeitCandle.displayError("gregday must be numeric");
			
		
		}else if(gregday <31 && gregday>0){
			//valid date 
			isvalid=true;
			
		}else 
			YahrzeitCandle.displayError("gregday out of range: " + gregday);
		
		try {
		gregyear=Integer.parseInt(gyear_in.getValue()); }
		catch (Exception e) {gregyear=-1;}
			
		
		if (gregyear==-1){
			YahrzeitCandle.displayError("gregyear must be numeric");
			isvalid=false;
		}else if (gregyear<=2020 && gregyear>=0){
			//valid date
			
		}
		else{
				YahrzeitCandle.displayError("gregyear out of range: " + gregyear);
				isvalid=false;
		}
		if (isvalid){
			//System.out.println("setting month, day, yr to "
			//		+ newGregDate_mon.getMonth() + " " + gregday + " " +  gregyear);
			try {
				m_hebDate.setDate(gmon_in.getMonth(), gregday, gregyear);
				him.setMonth(m_hebDate.getHebrewMonth());
				hid.setDay(m_hebDate.getHebrewDate());
				hiy.setValue(""+m_hebDate.getHebrewYear());
				
				//also display upcoming date
				m_hebDate=(YahrzeitCandle.calc_greg_date_todisplay(him.getMonth(), hid.getDay()));
				MyFlexTable.flexTable.setText(row, 2, YahrzeitCandle.format_pretty.format(YahrzeitCandle.format_fromhebcal.parse(m_hebDate.formatGregorianDate_English())));

				
			} catch (HebrewDateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	  }
	}
}
		
	

