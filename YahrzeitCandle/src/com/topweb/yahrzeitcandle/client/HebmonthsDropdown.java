package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class HebmonthsDropdown extends Composite {
	 @UiField ListBox listBox;
	private static HebmonthsDropdownUiBinder uiBinder = GWT.create(HebmonthsDropdownUiBinder.class);

	interface HebmonthsDropdownUiBinder extends UiBinder<Widget, HebmonthsDropdown> {
	}

	public HebmonthsDropdown() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	public HebmonthsDropdown(int hebrewMonth) {
		initWidget(uiBinder.createAndBindUi(this));
		setMonth(hebrewMonth);
	}
	@UiHandler("listBox")
	 void handleChange(ChangeEvent e){
	  Console.log("change " + listBox.getSelectedValue());
	  YahrzeitCandle.validateDates("hebmonth");
	 }
	public int getMonth() {
		return Integer.parseInt(listBox.getSelectedValue());
	}
	public void setEnabled(boolean b) {
		 
	}
	public void setMonth(int hebrewMonth) {
		setSelectedValue(hebrewMonth);
		Console.log("set selected index " + hebrewMonth);
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
