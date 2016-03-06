package com.topweb.yahrzeitcandle.client;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


//import net.sf.hebcal.HebrewDate;
import net.sf.hebcal.HebrewDate;
import net.sf.hebcal.HebrewDateException;


import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
 
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
//import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
 
 
import com.google.gwt.user.client.Timer;
 
//import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
//import com.google.gwt.user.client.ui.FlexTable;
//import com.google.gwt.user.client.ui.Hidden;
 
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
 
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
//import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
 

 
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class YahrzeitCandle implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 * 
	 */
	public static final String[]heb_months={
		"",
		"Nissan",
		"Iyar", 
		"Sivan", 
		"Tamuz", 
		"Av",
		"Elul",
		"Tishrei",
		"Cheshvan",
		"Kislev",
		"Tevet",
		"Shvat",
		"Adar",
		"Adar II"
		};
	public static final String[]greg_months={"","January",
		"February","March","April",
		"May", "June", "July",
		"August","September","October",
		"November","December",""};
	
	private static VerticalPanel mainPanel = new VerticalPanel();
	public static YahrzeitCandle sw_singleton=null;
	 
	  
	  static HorizontalPanel addPanel = new HorizontalPanel();
	  static HorizontalPanel addPanel1 = new HorizontalPanel();
	  static TextBox newHonoree = new TextBox();
	public static HebrewDate m_hebDate=new HebrewDate();
	public static ChangeHandler m_changedate = new ChangeHandler(){
			public void onChange(ChangeEvent e){
				//sw_singleton.displayError("onchange from " + ((Widget)e.getSource()).getTitle());
				validateDates(((Widget)e.getSource()).getTitle());
				
				}};
	  static HebmonthsDropdown newHebrewDate_mon=new HebmonthsDropdown(m_hebDate.getHebrewMonth());
	  static HebrewDateDayInput newHebrewDate_day=new HebrewDateDayInput();
	  static TextBox newHebrewYear = new TextBox(); 
	  //private 
	  static Button addYtoListButton = new Button("Add");
	  private static Button testAjaxButton = new Button("testAjax");
	  private static Label lastUpdatedLabel = new Label();
	  private static Label errDisplay = new Label("");
	static TextBox newGregDate_day=new TextBox();
	static TextBox newGregDate_year=new TextBox();
	static GregmonthsDropdown newGregDate_mon= new GregmonthsDropdown(YahrzeitCandle.m_hebDate.getGregorianMonth());
	static final String JSON_URL = /*GWT.getModuleBaseURL()+*/  "gwt.php";
	static final String photoUploaderURL = GWT.getModuleBaseURL()  + "gwt/photouploader.php";
	private static Timer displayTimer=null;
	public static int m_thisHebrewYear;
	private static Queue<String> displayq=new LinkedList<String>();
	  //private ArrayList<Yahrzeit> indexmap = new ArrayList<Yahrzeit>();
	  public  static DateTimeFormat format_db = DateTimeFormat.getFormat("y-M-d");
		public  static  DateTimeFormat format_pretty=DateTimeFormat.getFormat("EEEE MMMM d y");
		public static DateTimeFormat format_fromhebcal = DateTimeFormat.getFormat("MMMM d, y");
		  public static MyFlexTable yahrFlexTable = new MyFlexTable();
public static int userId=0;
public static FBAuthResponse fbAuthResponse=null;
public static CheckBox emailNotif= new CheckBox();

public static boolean fqlAllowPhotos=false,fqlAllowWall=false, fqlAllowEmail=false;

public static Label ycstats=new Label("Your Yahrzeit list");
public static Button addYahrButton=null, addCancelButton=null;

public static String AppName="yahrzeitcandle";
public static String willPublish="Yahrzeit Candle will post a note to your "+
	"wall on the evening of a Yahrzeit";

public static String willEmail="Yahrzeit Candle will send a reminder email a day before the Yahrzeit begins";
public static String wontEmail="Check box to allow Yahrzeit Candle to send reminder emails";
public static String permsRequested=null;
public static String intro_msg=null;
private static HTML introLabel = new HTML();
public static String access_token=null;
public static Map<String,String> perms=new HashMap<String,String>();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		sw_singleton=this;
		

		installResumeHandler();
		ScriptElement e = Document.get().createScriptElement();
		e.setAttribute("src", "//connect.facebook.net/en_US/sdk.js");
		e.setPropertyBoolean("async", true);
		Document.get().getElementById("fb-root").appendChild(e);
		
	}
	
	public static native void installResumeHandler()/*-{
		$wnd.resumehandler=function(){
			$wnd.FB.getLoginStatus(function(response) {
     			$wnd.console && $wnd.console.log(response);
				if (response.status!="connected"){
					$wnd.console && $wnd.console.log("not logged in");
					$wnd.top.location="https://www.facebook.com/dialog/oauth?client_id=98869004584&redirect_uri=https://apps.facebook.com/yahrzeitcandle/";
					return;
				}
				$wnd.console && $wnd.console.log("storing auth response");
				$wnd.console && $wnd.console.log(response.authResponse);
				@com.topweb.yahrzeitcandle.client.YahrzeitCandle::fbAuthResponse=response.authResponse;
				$wnd.FB.api("/me/permissions",function (response) {
     				if (response && !response.error) {
       					// handle the result
				       $wnd.console && $wnd.console.log("got perms");
				       $wnd.console && $wnd.console.log(response);
				       @com.topweb.yahrzeitcandle.client.YahrzeitCandle::processPermsResponse(Lcom/google/gwt/core/client/JsArray;)(response.data);
     				}
   				});
			});
		}
	}-*/;

	public static void processPermsResponse( JsArray<Perm> theperms) {
		
		for (int i=0;i<theperms.length();i++){
			Perm p = theperms.get(i);
			perms.put(p.getPermission(),p.getStatus());
			
		}
		
		newHonoree.setReadOnly(false);
		//emailNotif.setVisible(true);
		
		intro_msg=MyTextResources.INSTANCE.getIntroMsg().getText();
		introLabel.setHTML(intro_msg);
	    addPanel.add(newHonoree);
	    addPanel.add(newHebrewDate_mon);
	    addPanel.add(newHebrewDate_day.getTextBox());
	    m_thisHebrewYear=m_hebDate.getHebrewYear();
	     newHebrewYear.setVisibleLength(4);
	     newHebrewYear.setText(""+m_thisHebrewYear);
	     newHebrewYear.setTitle("hebyear");
	     newHebrewYear.addChangeHandler(m_changedate);
	    addPanel.add(newHebrewYear);
	    addPanel1.add(newGregDate_mon);
	    newGregDate_day.setVisibleLength(2);
	    newGregDate_day.setTitle("gregday");
	   // Date today = new Date();
	    newGregDate_day.setValue(""+m_hebDate.getGregorianDayOfMonth());//DateTimeFormat.getFormat("d").format(today));
	    newGregDate_day.addChangeHandler(m_changedate);
	    addPanel1.add(newGregDate_day);
	    newGregDate_year.setVisibleLength(4);
	    newGregDate_year.setTitle("gregyear");
	    //Date today=new Date();
	    newGregDate_year.setValue(""+m_hebDate.getGregorianYear());//*DateTimeFormat.getFormat("y").format(today));
	    newGregDate_year.addChangeHandler(m_changedate);
	    addPanel1.add(newGregDate_year);
	    
	    
	    //addPanel.add();
	    addPanel1.add(addYtoListButton);
	     addCancelButton= new Button("cancel");
	    addCancelButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				addPanel.setVisible(false);
				addPanel1.setVisible(false);
				addYahrButton.setEnabled(true);
				yahrFlexTable.setAllMyButtonsEnabled(true);
				
			}
	    	
	    }
	    
	    );
	    addPanel1.add(addCancelButton);
	    
	    
	    emailNotif.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
	    	@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
	    		
				boolean allow_email=event.getValue();
				setAllowEmailsRequest(allow_email);
				 
			} //end  onvaluechange
	      } //end anon constructor
	    );//end addvaluechangehandler
	    
	    
	    
	    // Assemble Main panel.
	    emailNotif.setVisible(true);
	     
	    
	   
	    mainPanel.add(emailNotif);
 	    introLabel.addStyleName("introLabel");
	    mainPanel.add(introLabel);
	    
	    
	    addYahrButton = new Button("Add a Yahrzeit");
	    addYahrButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				sw_singleton.resetAddFields();
				addYahrButton.setEnabled(false);
				addPanel.setVisible(true);
				addPanel1.setVisible(true);
				yahrFlexTable.setAllMyButtonsEnabled(false);
			}
	    
	    }
	    );
	    
	    
	    addPanel.setVisible(false);
		addPanel1.setVisible(false);
		addYahrButton.setEnabled(true);

	    HorizontalPanel addButtonPanel = new HorizontalPanel();
	    addButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    addButtonPanel.setWidth("760px");
	   addButtonPanel.setSpacing(10);
	    addButtonPanel.add(addYahrButton);
	    mainPanel.add(addButtonPanel);
	    mainPanel.add(addPanel);
	    mainPanel.add(addPanel1);
	    mainPanel.add(errDisplay);
	    ycstats.addStyleName(".h1");
	    mainPanel.add(ycstats);
	   // yahrFlexTable=new MyFlexTable();
	    mainPanel.add(yahrFlexTable.getTable());
	    
	   // mainPanel.add(lastUpdatedLabel);
	    // Associate the Main panel with the HTML host page.
	    RootPanel.get("yahrList").add(mainPanel);
	 // Move cursor focus to the input box.
	    newHonoree.setFocus(true);

	 // Listen for mouse events on the Add button.
	    addYtoListButton.addClickHandler(new ClickHandler() {
	      public void onClick(ClickEvent event) {
	    	  addYtoListButton.setEnabled(false);
	    	  addCancelButton.setEnabled(false);
	        addHonoree();
	      }
	    });
	    /**
	     * Make call to remote server.
	     */
	    
	    
	    
	    testAjaxButton.addClickHandler(new ClickHandler() {
			

			@Override
			public void onClick(ClickEvent event) {
				// 
				// Send request to server and catch any errors.
			    errDisplay.setText(null);
				SvrReq svr = (SvrReq)JavaScriptObject.createObject();
				svr.setMethod("ping");
				svr.setFbAuthResponse(YahrzeitCandle.fbAuthResponse);
				 
				yahrFlexTable.submitData(svr);
				
			}
		});
	    
	    //mainPanel.add(testAjaxButton);

	      addPanel.addStyleName("addPanel");
	      addPanel1.addStyleName("addPanel");
	       

			yahrFlexTable.resync();
	      
	      
	    	
		  }

	


	
	 


	protected static void displayError(String string) {
		
		displayq.add(string);
		if (displayTimer!=null){
			return;
		}
		
		
		displayTimer= new Timer() {
			
        @Override
        public void run() {
        	
           errDisplay.setText("");
         if(displayq.size()>0) {
    		errDisplay.setText(displayq.poll());
        	
    		schedule(5000);
         } else
        	 if (displayq.isEmpty())displayTimer=null;

        }
      };
	  errDisplay.setText(displayq.poll());
      displayTimer.schedule(5000);		

	}

	
	protected static void addHonoree() {
		
		final String Yahrzeit = newHonoree.getText().trim();
	    newHonoree.setFocus(true);
	    if (Yahrzeit.length()==0) return;

	    //disable fields
	    newHonoree.setEnabled(false);
	    newHebrewDate_mon.setEnabled(false);
	    newHebrewDate_day.setEnabled(false);
	    newHebrewYear.setEnabled(false);
	    newGregDate_mon.setEnabled(false);
	    newGregDate_day.setEnabled(false);
	    newGregDate_year.setEnabled(false);
	    
	    //submit
	    Yahrzeit h1 = (Yahrzeit)JavaScriptObject.createObject();
		h1.setName(Yahrzeit);
		h1.setHebDay(newHebrewDate_day.getDay());
		h1.setHebMonth(newHebrewDate_mon.getMonth());
		h1.setHebYear(Integer.parseInt(newHebrewYear.getValue()));

		yahrFlexTable.addRowRequest(h1);
	}
	    
	    
	 
	public static void setAllowEmailsRequest(boolean allow_email){
		
	    final String perm="email";
		if(allow_email==true && perms.get(perm).compareTo("granted")!=0){
			//means we want email but dont have permissions 
			new FBLogin(){
				@Override
				public void apiCallback(FBAuthResponse response) {
				 Console.logAsObject(response);
				 if(response.getPermsGranted().contains(perm)){
					YahrzeitCandle.perms.put(perm, "granted");
					SvrReq svr=(SvrReq)JavaScriptObject.createObject();
			        svr.setMethod("allow_email");
					svr.setFbAuthResponse(YahrzeitCandle.fbAuthResponse);
					svr.setAllowEmail(true);
			        YahrzeitCandle.yahrFlexTable.submitData(svr);
				 }
				}
			  }.login(perm);
		} else {
			SvrReq svr=(SvrReq)JavaScriptObject.createObject();
	        svr.setMethod("allow_email");
			svr.setFbAuthResponse(YahrzeitCandle.fbAuthResponse);
			svr.setAllowEmail(allow_email);
	        yahrFlexTable.submitData(svr);
		}
	}
	
	public static int calc_month_offset(int month) {
		 return month>6 ? month-7 : month+6 ;
		}
	    

static HebrewDate calc_greg_date_todisplay(int y_hmon, int y_hebday) {
   
    HebrewDate gregEquiv = new HebrewDate();
    int hmon= gregEquiv.getHebrewMonth();
    int hebday = gregEquiv.getHebrewDate();
    
    int hebyear=gregEquiv.getHebrewYear();
    int hebyear1=0;
    if (calc_month_offset(y_hmon) < calc_month_offset(hmon) )
        hebyear1=hebyear+1;
     else if (calc_month_offset(y_hmon) > calc_month_offset(hmon) )
        hebyear1=hebyear;
     else { // months equal
    	 if (y_hebday>=hebday) hebyear1=hebyear;
         else
          hebyear1=hebyear+1;
         }

    try {
		gregEquiv.setHebrewDate(y_hmon,y_hebday,hebyear1);
	} catch (HebrewDateException e) {
		
		e.printStackTrace();
	}
     return gregEquiv;
    }
	








private void resetAddFields() {
	newHonoree.setValue("John Doe");
    addPanel.add(newHebrewDate_mon);
    addPanel.add(newHebrewDate_day.getTextBox());
    addPanel.add(newHebrewYear);
    addPanel1.add(newGregDate_mon);
    addPanel1.add(newGregDate_day);
    addPanel1.add(newGregDate_year);
    m_hebDate=new HebrewDate();
    newHebrewDate_mon.setMonth(m_hebDate.getHebrewMonth());
    newHebrewDate_day.setDay(m_hebDate.getHebrewDate());
    newHebrewYear.setValue(""+m_hebDate.getHebrewYear());
    newGregDate_mon.setMonth(m_hebDate.getGregorianMonth());
    newGregDate_day.setValue(""+m_hebDate.getGregorianDayOfMonth());
    newGregDate_year.setValue("" + m_hebDate.getGregorianYear());

    newHonoree.setEnabled(true);
    newHebrewDate_mon.setEnabled(true);
    newHebrewDate_day.setEnabled(true);
    newHebrewYear.setEnabled(true);
    newGregDate_mon.setEnabled(true);
    newGregDate_day.setEnabled(true);
    newGregDate_year.setEnabled(true);

    
}



public  void freeAddFields() {
	addYahrButton.setEnabled(true);
    newHonoree.setEnabled(true);
    newHebrewDate_mon.setEnabled(true);
    newHebrewDate_day.setEnabled(true);
    newHebrewYear.setEnabled(true);
    newGregDate_mon.setEnabled(true);
    newGregDate_day.setEnabled(true);
    newGregDate_year.setEnabled(true);
    addYtoListButton.setEnabled(true);
    addCancelButton.setEnabled(true);

}
	 



public static native void reloadAppUrl() /*-{
	top.location="http://apps.facebook.com/" + @com.topweb.yahrzeitcandle.client.YahrzeitCandle::AppName + "/";

	}-*/;
public static native void loginToFB() /*-{
			
	$wnd.FB.login(
		     function(response) {
		if (response.session)
			top.location="http://apps.facebook.com/" + @com.topweb.yahrzeitcandle.client.YahrzeitCandle::AppName + "/";
			
    	}, {perms:'email', display:'popup'});
	}-*/;
			
			
	public static void validateDates(String src){
		boolean isvalid=false;
		int hebday,hebyear,gregday,gregyear;
		hebday = hebyear= gregday= gregyear=0;
		
		
		if (src.equalsIgnoreCase("hebyear")||src.equalsIgnoreCase("hebday")||
				src.equalsIgnoreCase("hebmonth")) //validate hebmonth,year,set gregdate
		{
			try{
			hebday=newHebrewDate_day.getDay();
		}catch(Exception e){hebday=-1;}
		if (hebday==-1) {
			displayError("hebday must be numeric");
			
		
		}else if(hebday <31 && hebday>0){ 
			//valid day
			isvalid=true;
			
		}else 
			displayError("hebday out of range: " + hebday);
		
		
		//newHebrewYear
		try {
			hebyear=Integer.parseInt(newHebrewYear.getValue());
		}catch (Exception e){hebyear=-1;}
		if (hebyear==-1){
			displayError("hebyear must be numeric");
			isvalid=false;
		}else if (hebyear < 5800 && hebyear >0) {
			//valid date
		}else{
			displayError("hebyear out of range: "+hebyear);
			isvalid=false;
		}
		
		if (isvalid){
			//update gregdate
			try {
				m_hebDate.setHebrewDate(newHebrewDate_mon.getMonth(), hebday, hebyear);
				newGregDate_day.setValue(""+m_hebDate.getGregorianDayOfMonth());
				newGregDate_mon.setMonth(m_hebDate.getGregorianMonth());
				//System.out.println("new gregyear is " + m_hebDate.getGregorianYear());
				newGregDate_year.setValue(""+m_hebDate.getGregorianYear());
				return;
			} catch (HebrewDateException e) {
				
				e.printStackTrace();
			}
			
		}
		}
		
		//newGregDate_mon
		if (src.equalsIgnoreCase("gregyear")||src.equalsIgnoreCase("gregday")||
				src.equalsIgnoreCase("gregmonth"))
		try {
		gregday=Integer.parseInt(newGregDate_day.getValue());
		}catch(Exception e){gregday=-1;}
		if (gregday==-1) {
			displayError("gregday must be numeric");
			
		
		}else if(gregday <31 && gregday>0){
			//valid date 
			isvalid=true;
			
		}else 
			displayError("gregday out of range: " + gregday);
		
		try {
		gregyear=Integer.parseInt(newGregDate_year.getValue()); }
		catch (Exception e) {gregyear=-1;}
			
		
		if (gregyear==-1){
			displayError("gregyear must be numeric");
			isvalid=false;
		}else if (gregyear<=2020 && gregyear>=0){
			//valid date
			
		}
		else{
				displayError("gregyear out of range: " + gregyear);
				isvalid=false;
		}
		if (isvalid){
			//System.out.println("setting month, day, yr to "
			//		+ newGregDate_mon.getMonth() + " " + gregday + " " +  gregyear);
			try {
				m_hebDate.setDate(newGregDate_mon.getMonth(), gregday, gregyear);
				Console.log("m_hebDate.getHebrewMonth() is " + m_hebDate.getHebrewMonth());
				newHebrewDate_mon.setMonth(m_hebDate.getHebrewMonth());
				newHebrewDate_day.setDay(m_hebDate.getHebrewDate());
				newHebrewYear.setValue("" + m_hebDate.getHebrewYear());
			} catch (HebrewDateException e) {
				e.printStackTrace();
			}
		  }
		}

	public static void canEmail(boolean b) {
		emailNotif.setText(b?willEmail:wontEmail);
		emailNotif.setValue(b);
	}
}
