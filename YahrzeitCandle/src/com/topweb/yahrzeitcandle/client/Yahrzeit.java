package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;

public final class Yahrzeit extends JavaScriptObject { //implements Comparable<Honoree>{

		  // Overlay types always have protected, zero-arg ctors
		  protected Yahrzeit() { }
		  
		  public final native String getGregDate ()/*-{ return this.greg_date ;}-*/;
		  public final native void setGregDate(String d) /*-{ this.greg_date=d;}-*/;
		  public final native String getName() /*-{ return this.honoree; }-*/;
		  public final native int getHebMonth() /*-{ return this.date_month; }-*/;
		  public final native int getHebDay() /*-{ return this.date_day; }-*/;
		  public final native int getHebYear() /*-{ return this.date_year; }-*/;
		  public final native void setName(String name) /*-{ this.honoree = name; }-*/;
		  public final native int getDbId()  /*-{ return this.id; }-*/;
		public final native void setHebDay (int day) /*-{
			// TODO Auto-generated method stub
			this.date_day=day;
		}-*/;

		public final native void setHebMonth(int month) /*-{
			// TODO Auto-generated method stub
			this.date_month=month;
		}-*/;

		public final native void setHebYear(int year) /*-{
			// TODO Auto-generated method stub
			this.date_year=year;
		}-*/;

		public final native void setMethod(String method) /*-{
			this.method=method;
		}-*/;

		public final native void setDbId(int dbid)  /*-{

			this.id=dbid;
		}-*/;
		/*@Override
		public int compareTo(Honoree o) {
			DateTimeFormat format = DateTimeFormat.getFormat("y-M-d");
			return format.parse(this.getGregDate()).compareTo(format.parse(o.getGregDate()));
		}*/

		public final native String getPhoto() /*-{return this.photo;}-*/;
			
		public final native String getSrcSmall() /*-{return this.src_small;}-*/;
		public final native String getSrcBig() /*-{return this.src_big;}-*/;


		  
		  
		  
}
