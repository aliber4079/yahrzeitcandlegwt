package com.topweb.yahrzeitcandle.client;

import java.util.Date;


public class YahrzeitContainer implements Comparable<YahrzeitContainer> {

	private Yahrzeit m_yahrzeit;
	public Yahrzeit getYahrzeit() {return m_yahrzeit;}
	YahrzeitContainer(Yahrzeit h) {m_yahrzeit=h;}

	public Date getGregDisplayDate() {
		if (m_yahrzeit.getGregDate()!=null) 
			return  YahrzeitCandle.format_db.parse(m_yahrzeit.getGregDate()) ; 
		else
			return YahrzeitCandle.format_fromhebcal.parse(YahrzeitCandle.calc_greg_date_todisplay(m_yahrzeit.getHebMonth(),m_yahrzeit.getHebDay()).formatGregorianDate_English());
	}
	
	public static Date getGregDisplayDate (Yahrzeit y) {
		return YahrzeitCandle.format_fromhebcal.parse(YahrzeitCandle.calc_greg_date_todisplay(y.getHebMonth(),y.getHebDay()).formatGregorianDate_English());
		
	}
	
	@Override
	public int compareTo(YahrzeitContainer arg0) {
		//System.out.println("my date: " + m_yahrzeit.getGregDate() + " their date: " + 
				//(((HonoreeContainer)arg0).getHonoree().getGregDate()));
		
		Date myDate=getGregDisplayDate();
		Date theirdate=arg0.getGregDisplayDate();
		//Date theirdate=YahrzeitCandle.format_db.parse(((YahrzeitContainer)arg0).getYahrzeit().getGregDate());
	//	System.out.println("parsed date: " + myDate.toString() + " " + theirdate.toString());
	//	System.out.println("compareto returning "+ myDate.compareTo(theirdate));
		return myDate.compareTo(theirdate);
	}
	public void setYahrzeit(Yahrzeit h) {
		m_yahrzeit=h;
		
	}
	
	
	
}
