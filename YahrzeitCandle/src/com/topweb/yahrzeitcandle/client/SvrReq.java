package com.topweb.yahrzeitcandle.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class SvrReq extends JavaScriptObject {
	protected SvrReq() {}
	public native final void setUid(int uid) /*-{
		this.uid=uid;
	}-*/;
	public native final void setFbAuthResponse(FBAuthResponse authResponse) /*-{
		this.authResponse=authResponse;
	}-*/;
	public native final void setMethod(String method) 
	/*-{
		this.method=method; 
	}-*/;
	
	public native final String getMethod() /*-{
		return this.method; 
		}-*/;
	public native final String getStatus() /*-{
		return this.status; 
		}-*/;
	public native final void setYahrzeits(JsArray<Yahrzeit> j) /*-{
	 this.yahrzeitlist=j;
	}-*/;
	public native final void setAllowEmail(boolean allow) /*-{
	this.allow_email=allow;
	}-*/;
	public native final void setAllowPublish(boolean allow) /*-{
	this.allow_publish=allow;
	}-*/;
	public native final void setPhoto(String pid) /*-{
	this.pid=pid;
	}-*/;
	public native final void setAccessToken(String src) /*-{
	this.access_token=src;
	}-*/;
	public native final JsArray<Yahrzeit> getYahrzeitList() /*-{
		return this.yahrzeitlist;
	}-*/;
	public native final UserPrefs getUserPrefs() /*-{
		return this.userprefs;
	}-*/;

}
