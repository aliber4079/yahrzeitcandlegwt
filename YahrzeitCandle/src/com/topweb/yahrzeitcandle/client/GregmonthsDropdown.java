package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class GregmonthsDropdown extends Composite {
	 @UiField ListBox listBox;
	private static GregDropdownUiBinder uiBinder = GWT.create(GregDropdownUiBinder.class);

	interface GregDropdownUiBinder extends UiBinder<Widget, GregmonthsDropdown> {
	}

	public GregmonthsDropdown(int month) {
		initWidget(uiBinder.createAndBindUi(this));
		setMonth(month);
	} 
	public void setEnabled(boolean b){
		  
	}
	 @UiHandler("listBox")
	 void handleChange(ChangeEvent e){
	  Console.log("change " + listBox.getSelectedValue());
	  YahrzeitCandle.validateDates("gregmonth");
	 }
	 public int getMonth(){
		 return Integer.parseInt(listBox.getSelectedValue());
	 }
	 public void setMonth(int month){
		 setSelectedValue(month);
		Console.log("set selected index " + month);
	 }
	 public void setSelectedValue(int val){
		for (int i=0; i<listBox.getItemCount();i++ ) {
			if(Integer.parseInt(listBox.getValue(i))==val){
				listBox.setSelectedIndex(i);
				return;
			}
		}
	 }
}
