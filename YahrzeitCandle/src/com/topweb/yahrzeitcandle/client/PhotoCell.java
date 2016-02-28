package com.topweb.yahrzeitcandle.client;



import com.google.gwt.cell.client.AbstractCell;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

	public class PhotoCell extends AbstractCell<FBPhoto> {
		
		
		
		 @Override
	     public void render(Context context, FBPhoto value, SafeHtmlBuilder sb) {
		  sb.appendHtmlConstant("<img src=\""+ value.getUrl() + "\"></img>");

		 }
		 

}
