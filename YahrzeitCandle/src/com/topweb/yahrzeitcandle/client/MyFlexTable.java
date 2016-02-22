package com.topweb.yahrzeitcandle.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sf.hebcal.HebrewDate;
import net.sf.hebcal.HebrewDateException;

import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayUtils;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dev.jjs.ast.JAbsentArrayDimension;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
//import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class MyFlexTable {

	public FlexTable getTable() {return flexTable;}
	private final DialogBox deleteDialog=new DialogBox();
	//public FlexTable cast<FlexTable>(){return getTable();}
	public static List<YahrzeitContainer> m_YahrzeitContainerList=new ArrayList<YahrzeitContainer>();
    public static FlowPanel changeClearButtons = new FlowPanel();
    public static Button clearImageButton = new Button("clear");
    public static Button changeImageButton = new Button("change");
	static FlexTable flexTable=null;
	MyFlexTable() {
		flexTable=new FlexTable();
	    flexTable.setText(0, 0, "Honoree");
	    flexTable.setText(0, 1, "Hebrew date");
	    flexTable.setText(0, 2, "Starts Evening Before");
	    flexTable.setText(0, 3,  "Photo");
	    Console.log("added photo column");
	    flexTable.setText(0, 4, "Edit");
	    flexTable.setText(0, 5, "Delete");
	    changeClearButtons.add(clearImageButton);
		changeClearButtons.add(changeImageButton);
		Console.log("adding clearImageButton.addClickHandler");
		clearImageButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				
				YahrzeitImage curImage=
					(YahrzeitImage)
					((AbsolutePanel)
							((Widget)event.getSource()).getParent().getParent()).getWidget(0);
				Console.log("clear " + curImage.getYahrzeit().getDbId());
				boolean confirmdel=Window.confirm("don't use a photo for this Yahrzeit?");
				if (confirmdel){
					clearPhotoRequest(curImage);
				}
			}
	    });
	    changeImageButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				YahrzeitImage curImage=
					(YahrzeitImage)
					((AbsolutePanel)((Widget)event.getSource()).getParent().getParent()).getWidget(0);
				PhotoBrowser.activeYahrzeit=curImage.getYahrzeit();
				Console.log("active Yahrzeit: " + PhotoBrowser.activeYahrzeit.getName());
				/*Label l = new Label("Please wait...");
				l.setPixelSize(600, 200);
				PhotoBrowser.d.setText("Photo for Yahrzeit of " + PhotoBrowser.activeYahrzeit.getName());
				PhotoBrowser.d.setWidget(l);
				PhotoBrowser.d.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			          public void setPosition(int offsetWidth, int offsetHeight) {
			        	  Console.log("browser.getOffsetHeight: " + offsetHeight);
			            int top = (Window.getScrollTop() + offsetHeight / 3);
			            PhotoBrowser.d.setPopupPosition(5, top);
			          }
			        });
				PhotoBrowser.getAlbums();*/
				PhotoBrowser.showUploader();
				
			}
	    });	
	    flexTable.setCellPadding(6);
	      flexTable.getRowFormatter().addStyleName(0, "watchListHeader");
	      flexTable.addStyleName("watchList");
	      
	      flexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
	      flexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
	      flexTable.getCellFormatter().addStyleName(0, 3, "photoColumn");
	      flexTable.getCellFormatter().addStyleName(0, 4, "editColumn");
	      flexTable.getCellFormatter().addStyleName(0, 5, "watchListRemoveColumn");
		    new PhotoBrowser();
	}
	public void deleteRowsConfirmed(JsArray<Yahrzeit> yahrzeitlist){
		for (int i=0; i < yahrzeitlist.length();i++) {
			int dbid=yahrzeitlist.get(i).getDbId();
			//System.out.println("rowcount before removing row: " + flexTable.getRowCount());
			int index = getYahrzeitIndexFromDatabaseId(dbid);
			int row=index+1;
			m_YahrzeitContainerList.remove(index);
			//System.out.println("deleting from row " + row);
			dumpContainerList();
			flexTable.removeRow(row);
		}
		setStatCount(m_YahrzeitContainerList.size());
			resizeFbIframe();
	}

	public static native void resizeFbIframe() /*-{
		//if ($wnd.console) console.log("resizeframe");
		
		$wnd.FB.Canvas.setSize();
		
	}-*/;
	  protected void addRowsConfirmed(JsArray<Yahrzeit> yahrzeitlist) {
		 //System.out.println("yahrzeitlist.length(): " + yahrzeitlist.length());
		  if (yahrzeitlist.length()==0)return;
		 List<YahrzeitContainer> incomingList= new ArrayList<YahrzeitContainer>();

		 //transfer list from jsarray to arraylist for sorting
		for (int i=0;i<yahrzeitlist.length();i++){
					Yahrzeit h = yahrzeitlist.get(i);
					YahrzeitContainer yc = new YahrzeitContainer(h);
					m_YahrzeitContainerList.add(yc);
					           incomingList.add(yc);
				}
				Collections.sort(incomingList); //also figures out what display greg date to assign
				Collections.sort(m_YahrzeitContainerList);
				

				//now decide where to put them
				ListIterator<YahrzeitContainer> incoming_iter=incomingList.listIterator();
				ListIterator<YahrzeitContainer> main_iter=m_YahrzeitContainerList.listIterator();

				//YahrzeitContainer incoming = incomingList.get(incoming_iter.nextIndex());
				while (incoming_iter.hasNext() ) {
					YahrzeitContainer incoming = incoming_iter.next();
				 while  (main_iter.hasNext()){
						YahrzeitContainer master = main_iter.next();
int idb=incoming.getYahrzeit().getDbId();
int mdb=master.getYahrzeit().getDbId();
		if (idb == mdb) {
						
					
					int index = main_iter.previousIndex();
					int row = index + 1; //to account for header row
				//	System.out.println ("row: " + row);
					
				//	System.out.println("rowcount: "+ flexTable.getRowCount());
					
					
					if (row < flexTable.getRowCount()) { 
						flexTable.insertRow(row); 
					}
				

				
					flexTable.setText(row, 0, incoming.getYahrzeit().getName());
				    flexTable.setText(row, 1, incoming.getYahrzeit().getHebDay()+ " " + YahrzeitCandle.heb_months[incoming.getYahrzeit().getHebMonth()-1]);
				    flexTable.setText(row, 2, YahrzeitCandle.format_pretty.format(incoming.getGregDisplayDate()));

				    
				    

				    if (incoming.getYahrzeit().getPhoto()!=null){
				        final AbsolutePanel placeholderImagePanel = new AbsolutePanel();
				     
					    FocusPanel fPanel = new FocusPanel();					
				    	YahrzeitImage placeholderImage=new YahrzeitImage(incoming.getYahrzeit());
				    						    
				    	Console.log("setting photo to " + incoming.getYahrzeit().getPhoto());
				    	fPanel.addMouseOverHandler(new MouseOverHandler() {
							@Override
							public void onMouseOver(MouseOverEvent event) {
								placeholderImagePanel.add(changeClearButtons,0,0);
							}				    	
				    	});
				    	fPanel.addMouseOutHandler(new MouseOutHandler(){

							@Override
							public void onMouseOut(MouseOutEvent event) {
								changeClearButtons.removeFromParent();
							}
				    	
				    	});
				    	placeholderImagePanel.add(placeholderImage);
					    fPanel.add(placeholderImagePanel);
					    flexTable.setWidget(row, 3, fPanel );

				    	
				    } else {
				    	YAddPhotoButton setPhotoButton = new YAddPhotoButton(incoming.getYahrzeit());
				    	setPhotoButton.addClickHandler(new ClickHandler(){
							@Override
							public void onClick(ClickEvent event) {
								PhotoBrowser.activeYahrzeit=((YAddPhotoButton)event.getSource()).getYahrzeit();
								Console.log("active Yahrzeit: " + PhotoBrowser.activeYahrzeit.getName());
								  if (!YahrzeitCandle.fqlAllowPhotos) {
									YahrzeitCandle.fblogin("user_photos");
								}else {
									
									PhotoBrowser.showUploader();

								}
							}
				    	});
					    flexTable.setWidget(row, 3, setPhotoButton );

				    }
				    
				    
				    
				    flexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
				    flexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
				    flexTable.getCellFormatter().addStyleName(row, 3, "photoColumn");
				    flexTable.getCellFormatter().addStyleName(row, 4, "editColumn");
				    flexTable.getCellFormatter().addStyleName(row, 5, "watchListRemoveColumn");
				    addDelButton(incoming.getYahrzeit());
				    addEditButton(incoming.getYahrzeit());
				    
				    break; //breaks out of inner loop, outer loop
				    		//retrieves next incoming item
				  	}					
			
					}
				}
	
	  				resizeFbIframe();
	  				setStatCount(m_YahrzeitContainerList.size());
				 }

		
			

		private void addEditButton(Yahrzeit h){
			
			Button editButton = new Button("edit");
		    editButton.getElement().setId("editbutton_"+h.getDbId());
		    editButton.addStyleDependentName("edit");
	    
		    editButton.addClickHandler(new ClickHandler () {
		    		
		    	public void onClick(ClickEvent event) {

		    		int dbid=Integer.parseInt(((Button)event.getSource()).getElement().getId().split("_")[1]);
		    //		System.out.println("my dbid is " + dbid);
    				editRow(dbid);	
		    		
		    	}
				
		    });
		    int dbid=h.getDbId();
		    int row=getYahrzeitIndexFromDatabaseId(dbid)+1;
		  //  System.out.println("editbutton row is " + row );
		    flexTable.setWidget(row,4,editButton);
		}
		private void addDelButton(Yahrzeit h) {
		    Button removeYahrButton = new Button("x");
		    removeYahrButton.getElement().setId("delbutton_"+h.getDbId());

		    removeYahrButton.addStyleDependentName("remove");
		   
		    removeYahrButton.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
	
					int dbid=Integer.parseInt(((Button)event.getSource()).getElement().getId().split("_")[1]);
					
				
				

				boolean confirmdel=Window.confirm("delete selected yahrzeit?");
				if (confirmdel)
					deleteRowRequest(dbid);
		       
		      } // end onclick method
		    });//end onclick block

		    int row=getYahrzeitIndexFromDatabaseId(h.getDbId())+1;
		    
		    
		    
		    flexTable.setWidget(row, 5, removeYahrButton);
		    }
		protected void deleteRowRequest(int dbid) {

			SvrReq svr=(SvrReq)JavaScriptObject.createObject();
	        svr.setMethod("delete");
			svr.setFbAuthResponse(YahrzeitCandle.fbAuthResponse);
	        Yahrzeit h[] = {getYahrzeitContainerFromDbId(dbid).getYahrzeit()};
	        svr.setYahrzeits(JsArrayUtils.readOnlyJsArray(h));
	        submitData(svr);
	}
		private YahrzeitContainer getYahrzeitContainerFromDbId(int dbid) {
			// TODO Auto-generated method stub
			int row=0;
			YahrzeitContainer h = null;
			for (ListIterator<YahrzeitContainer> i=m_YahrzeitContainerList.listIterator();i.hasNext(); ){
    			
    			row=i.nextIndex();
    			h=i.next();
    			
    			if (h.getYahrzeit().getDbId()==dbid ){
    				break;
    			}
    		}
			return h;
		}

		private static int getYahrzeitIndexFromDatabaseId(int dbid) {
			// TODO Auto-generated method stub

			//System.out.println("m_YahrzeitContainerList size: " +  m_YahrzeitContainerList.size());
			ListIterator<YahrzeitContainer> i=m_YahrzeitContainerList.listIterator();
			 while (i.hasNext() ){
 				YahrzeitContainer h = i.next();
    			if (h.getYahrzeit().getDbId()==dbid ){
    				break;
    			}
    		}
			return i.previousIndex();
		}
	  void submitData(SvrReq req) {
		  try {
		  RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, YahrzeitCandle.JSON_URL);
		  builder.setHeader("Content-Type", "application/json");
		  builder.setHeader("Accept", "application/json");
		   
		    Request request = builder.sendRequest(JsonUtils.stringify(req), 
		    	new RequestCallback() {
		    	  public void onError(Request request, Throwable exception) {
		          //displayError("Couldn't retrieve JSON");
		          YahrzeitCandle.sw_singleton.freeAddFields();
		        }
				@Override
				public void onResponseReceived(Request request, Response response) {
					SvrReq req =JsonUtils.<SvrReq> safeEval(response.getText());

		if (200 == response.getStatusCode()) {
			if (req.getStatus().compareToIgnoreCase("OK")==0) {
		
				//methods for status OK
					if (req.getMethod().compareToIgnoreCase("pong")==0){
								Console.log("pong");
					} else 
					if (req.getMethod().compareToIgnoreCase("resync")==0) {
						m_YahrzeitContainerList.removeAll(m_YahrzeitContainerList);
						clearRows();
						YahrzeitCandle.sw_singleton.newHonoree.setReadOnly(false);
						JsArray<Yahrzeit> yahrzeitlist=req.getYahrzeitList();
						if (yahrzeitlist==null || yahrzeitlist.length()==0) {
							setStatCount(0);
						} else {
							addRowsConfirmed(yahrzeitlist);
						}
					}
				} //status OK
			else if (req.getStatus().compareToIgnoreCase("error")==0){
				
				if (req.getMethod().compareToIgnoreCase("need_auth")==0){
					Console.log("need to re-auth");
					//YahrzeitCandle.setloginstatus();
				}
				
			} //status error
			}//end if 200
		} 
 }); //end new request callback
} /*end try*/ catch (RequestException e) {
		      Console.log(e.getMessage());
	    } 
	  }//end submit data
	  
	  
	  public void setStatCount(int count) {
		
		YahrzeitCandle.ycstats.setText(count>0 ?
				count+ " Yahrzeit" + (count>1 ? "s" :"")+" in list" 
		:
			    "Your Yahrzeit list is empty."
			);
	  }

	  public void dumpContainerList() {
		  
		  if (true)return;
			for (ListIterator<YahrzeitContainer> i = m_YahrzeitContainerList.listIterator();
			  i.hasNext(); ) {
				YahrzeitContainer yc1=i.next();
			//	System.out.println ("dbid: " + yc1.getYahrzeit().getDbId());
			//	System.out.println ("index: " + i.nextIndex()+"\n");
			}

	  }

		protected void modifyRowConfirmed(Yahrzeit h) {
			//is sorted position still same in master list
			int initial_position=getYahrzeitIndexFromDatabaseId(h.getDbId());
			YahrzeitContainer yc = m_YahrzeitContainerList.get(initial_position);
			yc.setYahrzeit(h);
			Collections.sort(m_YahrzeitContainerList);
			//dumpContainerList();

			int new_position=getYahrzeitIndexFromDatabaseId(h.getDbId());
			System.out.println("initial position: " + initial_position);
			System.out.println("new position: " + new_position);
		/*	System.out.println("initial hebday: " + yc.getYahrzeit().getHebDay());
			System.out.println("filling in gregdate with " + h.getHebMonth() + " " + h.getHebDay());
			HebrewDate gregDisplay = YahrzeitCandle.calc_greg_date_todisplay(h.getHebMonth(),h.getHebDay());		
			System.out.println("gregdisplay format: " + gregDisplay.formatGregorianDate_English());
		*/	
			
			int initial_row=initial_position+1;
			int new_row = new_position+1;
			
			if (initial_position==new_position){
			//	System.out.println("modify: initial_position==new_position");
				
				flexTable.setText( initial_row,0 ,h.getName());
				 flexTable.setText(initial_row, 1, h.getHebDay()+ " " + YahrzeitCandle.heb_months[h.getHebMonth()-1]);
				 flexTable.setText(initial_row, 2, YahrzeitCandle.format_pretty.format(YahrzeitContainer.getGregDisplayDate(h)));
				 addDelButton(h);
				 addEditButton(h);
			}else{
				//who do I swap with
				flexTable.removeRow(initial_row);

				if (new_row<flexTable.getRowCount()){
					flexTable.insertRow(new_row);
					
					} 
				
				
					flexTable.setText( new_row,0 ,h.getName());
					 flexTable.setText(new_row, 1, h.getHebDay()+ " " + YahrzeitCandle.heb_months[h.getHebMonth()-1]);
					 flexTable.setText(new_row, 2, YahrzeitCandle.format_pretty.format(YahrzeitContainer.getGregDisplayDate(h)));
					
					 
					 addDelButton(h);
					 addEditButton(h);
					      flexTable.getCellFormatter().addStyleName(new_row, 1, "watchListNumericColumn");
					      flexTable.getCellFormatter().addStyleName(new_row, 2, "watchListNumericColumn");
					      flexTable.getCellFormatter().addStyleName(new_row, 3, "photoColumn");
					      flexTable.getCellFormatter().addStyleName(new_row, 4, "editColumn");
					      flexTable.getCellFormatter().addStyleName(new_row, 5, "watchListRemoveColumn");
						 
					
				
				
			}
			 for (int i=1; i<flexTable.getRowCount();i++){
//			 	  ((PhotoAnchor)flexTable.getWidget(i, 3)).setHref("#");
				  ((Button)(flexTable.getWidget(i, 4))).setEnabled(true);
				  ((Button)(flexTable.getWidget(i, 5))).setEnabled(true);
			       flexTable.getRowFormatter().removeStyleName(i, "editTableRow");
			 	}
			flexTable.removeStyleName("editMode");


		
	}
		
		
		
		protected void clearRows() {
			flexTable.removeAllRows();
		    flexTable.setText(0, 0, "Yahrzeit");
		    flexTable.setText(0, 1, "Hebrew date");
		    flexTable.setText(0, 2, "Starts Evening Before");
		    flexTable.setText(0, 3, "Photo");
		    flexTable.setText(0, 4, "Edit");
		    flexTable.setText(0, 5, "Delete");
			
		    flexTable.setCellPadding(6);
	      flexTable.getRowFormatter().addStyleName(0, "watchListHeader");
	      flexTable.addStyleName("watchList");
	      
	      flexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
	      flexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
	      flexTable.getCellFormatter().addStyleName(0, 3, "photoColumn");
	      flexTable.getCellFormatter().addStyleName(0, 4, "editColumn");
		    flexTable.getCellFormatter().addStyleName(0, 5, "watchListRemoveColumn");
	//	System.out.println ("after init new table row count is " + flexTable.getRowCount());
	}
		 
private void editRow(int dbid) {

			flexTable.addStyleName("editMode");
		//	System.out.println("request to edit row " + dbid);
			
			YahrzeitContainer h =getYahrzeitContainerFromDbId(dbid);
			if (h==null) {
				return;
			}
		  final TextBox editme = new TextBox();
		  editme.setValue(h.getYahrzeit().getName());
		  editme.setVisibleLength((editme.getValue().length()));
		  int row = getYahrzeitIndexFromDatabaseId(dbid)+1;
		  flexTable.setWidget(row, 0, editme);
		  
		  
		  
		  
		  
		  HorizontalPanel hebpanel=new HorizontalPanel();
		  HebrewDateMonthInput him=new HebrewDateMonthInput();
		  him.setMonth(h.getYahrzeit().getHebMonth());
		  HebrewDateDayInput hid = new HebrewDateDayInput();
		  hid.setDay(h.getYahrzeit().getHebDay());
		  
		  hebpanel.add(hid.getTextBox());
		  hebpanel.add(him.getListBox());
		  TextBox hiy=new TextBox();
		  hiy.setValue("" + (h.getYahrzeit().getHebYear()==0 ? YahrzeitCandle.m_thisHebrewYear :
			  h.getYahrzeit().getHebYear()));
		  hiy.setVisibleLength(4);
		  hebpanel.add(hiy);
		  HebrewDate hebDate=null;
		  try{
		  
			hebDate=new HebrewDate();
			hebDate.setHebrewDate(him.getMonth(),hid.getDay(),Integer.parseInt(hiy.getValue()));
			
		} catch (HebrewDateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("translates to" + hebDate.formatGregorianDate_English());
		  
		//him,hid,hiy
		//ChangeHandler c=null;
		him.setId("him");
		hid.setId("hid");
		hiy.getElement().setId("hiy");
		VerticalPanel datePanel=new VerticalPanel();
		
		
		  
		  HorizontalPanel gregPanel = new HorizontalPanel();
		  GregDateMonthInput gmon_in=new GregDateMonthInput(hebDate.getGregorianMonth());
		  TextBox gday_in=new TextBox();
		  gday_in.setValue(""+hebDate.getGregorianDayOfMonth());
		  gday_in.setVisibleLength(2);
		  TextBox gyear_in=new TextBox();
		  gyear_in.setValue(""+hebDate.getGregorianYear());
		  gyear_in.setVisibleLength(4);
		  gregPanel.add(gmon_in.getListBox());
		  	gregPanel.add(gday_in);
		  	gregPanel.add(gyear_in);

		  	gmon_in.setId("gmon_in");
		  	
		  	gday_in.getElement().setId("gday_in");
		  	gyear_in.getElement().setId("gyear_in");
		  	
		  	
		  	EditRowDateChangeHandler dateChangeHandler = new EditRowDateChangeHandler(
		  			him,hid,hiy,
		  			gmon_in,gday_in,gyear_in,
		  			row
		  			);
				
				
			
			him.addChangeHandler(dateChangeHandler);
			hid.addChangeHandler(dateChangeHandler);
			hiy.addChangeHandler(dateChangeHandler);

			gmon_in.addChangeHandler(dateChangeHandler);
			gday_in.addChangeHandler(dateChangeHandler);
			gyear_in.addChangeHandler(dateChangeHandler);
			
			datePanel.add(hebpanel);
		  	datePanel.add(gregPanel);
			  flexTable.setWidget(row, 1, datePanel);

		  	
		  	//flexTable.setWidget(row, 2, datePanel);
		
		
		Button editOkayButton=new Button("Confirm");
		editOkayButton.getElement().setId("editokay_"+dbid);
		EditRowModifyHandler editRowModifyHandler = new EditRowModifyHandler(
				editme, //name
	  			him,hid,hiy,
	  			//gmon_in,gday_in,gyear_in
	  			h.getYahrzeit(),
	  			this
	  			);
		editOkayButton.addClickHandler(editRowModifyHandler);
		flexTable.setWidget(row,4,editOkayButton);
		
		Button editCancelButton=new Button("Cancel");
		editCancelButton.getElement().setId("editcancel_"+dbid);
		editCancelButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event){
				flexTable.removeStyleName("editMode");
				int dbid=Integer.parseInt(((Button)event.getSource()).getElement().getId().split("_")[1]);
				YahrzeitContainer hc=getYahrzeitContainerFromDbId(dbid); 
				Yahrzeit h = hc.getYahrzeit();
				int row = getYahrzeitIndexFromDatabaseId(dbid)+1;
				flexTable.getRowFormatter().removeStyleName(row, "editTableRow");
				 
				 flexTable.setText(row, 0, h.getName());
				 flexTable.setText(row, 1, h.getHebDay()+ " " + YahrzeitCandle.heb_months[h.getHebMonth()-1]);
				 flexTable.setText(row, 2, YahrzeitCandle.format_pretty.format(YahrzeitContainer.getGregDisplayDate(h)));
				 addDelButton(h);
				 addEditButton(h);
				 for (int i=1; i<flexTable.getRowCount();i++){
					 	  
						  ((Button)(flexTable.getWidget(i, 4))).setEnabled(true);
						  ((Button)(flexTable.getWidget(i, 5))).setEnabled(true);
					     
				  }
				 
			}
			
		}
		);
		flexTable.setWidget(row,5,editCancelButton);
		
		
		
			  for (int i=1; i<flexTable.getRowCount();i++){
				  if (i!=row){
				 	  
					  ((Button)(flexTable.getWidget(i, 4))).setEnabled(false);
					  ((Button)(flexTable.getWidget(i, 5))).setEnabled(false);
						
				  }
				  else
					  flexTable.getRowFormatter().setStyleName(i, "editTableRow");
			  }
			  hid.getTextBox().setFocus(true);
		}
		
	public void addRowRequest(Yahrzeit h1) {
		SvrReq svr=(SvrReq)JavaScriptObject.createObject();
		svr.setMethod("add");
		svr.setFbAuthResponse(YahrzeitCandle.fbAuthResponse);

		 Yahrzeit h[] = {h1};
	        svr.setYahrzeits(JsArrayUtils.readOnlyJsArray(h));
	        submitData(svr);
	   
	}
	public void modifyRowRequest(Yahrzeit h1){
		SvrReq svr=(SvrReq)JavaScriptObject.createObject();
		svr.setMethod("modify");
		svr.setFbAuthResponse(YahrzeitCandle.fbAuthResponse);

		 Yahrzeit h[] = {h1};
	        svr.setYahrzeits(JsArrayUtils.readOnlyJsArray(h));
	        submitData(svr);
	
	}
	public   void resync() {
		SvrReq svr = (SvrReq)JavaScriptObject.createObject();
		svr.setMethod("resync");
		svr.setFbAuthResponse(YahrzeitCandle.fbAuthResponse.getResponse());
		 submitData(svr);
	}
	
	
	/*public void dialogInitCode() {
	 Button ok = new Button("delete");
     Button cancel = new Button("cancel");
     ok.addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {
           deleteDialog.hide();
         }
       });
       cancel.addClickHandler(new ClickHandler() {
           public void onClick(ClickEvent event) {
             deleteDialog.hide();
           }
         });
       HorizontalPanel buttonPanel = new HorizontalPanel();
       buttonPanel.add(ok);
       buttonPanel.add(cancel);
       deleteDialog.setWidget(buttonPanel);

deleteDialog.setGlassEnabled(true);
deleteDialog.setText("delete selected yahrzeit?");

	}// end dialog init code
	*/
	/*public static native void addPhotoRequest(Yahrzeit yahrzeit_with_photo) /*-{
		
		fql="select pid,aid,src_small,src_big from photo where object_id="+object_id;
		
		@com.topweb.yahrzeitcandle.client.Console::log(Ljava/lang/String;)("fql: " + fql);
		req={'method':'fql.query','query':fql};
		fqlCallback=
		$entry(@com.topweb.yahrzeitcandle.client.YahrzeitCandle::processUploadPhotoResponse(Lcom/google/gwt/core/client/JsArray;));
		$wnd.FB.api(req,fqlCallback);
		
	}-*/;
	
	
	public static void addPhoto(Yahrzeit yahrzeit_with_photo) {
	
	}
	public static void addPhotoRequest(Yahrzeit h1, FBPhoto fbPhoto) {
		if (h1==null) return;
		SvrReq svr=(SvrReq)JavaScriptObject.createObject();
		svr.setMethod("add_photo");
		svr.setFbAuthResponse(YahrzeitCandle.fbAuthResponse);
		svr.setPhoto(String.valueOf(fbPhoto.getId()));
		 
		 Yahrzeit h[] = {h1};
	        svr.setYahrzeits(JsArrayUtils.readOnlyJsArray(h));
	  
		YahrzeitCandle.yahrFlexTable.submitData(svr);
	}

	public static void clearPhotoRequest(YahrzeitImage fbPhoto) {
		SvrReq svr=(SvrReq)JavaScriptObject.createObject();
		svr.setMethod("clear_photo");
		svr.setFbAuthResponse(YahrzeitCandle.fbAuthResponse);
		svr.setPhoto(String.valueOf(fbPhoto.getYahrzeit().getDbId()));
		 
		YahrzeitCandle.yahrFlexTable.submitData(svr);	
	}
	public static void addPhotoComplete(Yahrzeit yahrzeit_with_photo) {
		final AbsolutePanel placeholderImagePanel = new AbsolutePanel();
	    FocusPanel fPanel = new FocusPanel();					

		//find the anchor that was clicked and repop with new photo
		
		Console.log("id is " + yahrzeit_with_photo.getDbId() + ", photo is " + yahrzeit_with_photo.getPhoto());
		if (yahrzeit_with_photo.getPhoto()==null) return;
		int position= getYahrzeitIndexFromDatabaseId(yahrzeit_with_photo.getDbId()) + 1;
		Console.log("position is "+ position);
		flexTable.getWidget(position, 3).removeFromParent();
		YahrzeitImage placeholderImage=new YahrzeitImage(yahrzeit_with_photo);
	    
    	Console.log("setting photo to " + yahrzeit_with_photo.getPhoto());
       
    	fPanel.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				placeholderImagePanel.add(changeClearButtons,0,0);
			}				    	
    	});
    	fPanel.addMouseOutHandler(new MouseOutHandler(){

			@Override
			public void onMouseOut(MouseOutEvent event) {
				changeClearButtons.removeFromParent();
			}
    	
    	});
    	 			    	
		placeholderImagePanel.add(placeholderImage);
	    fPanel.add(placeholderImagePanel);
	    flexTable.setWidget(position, 3, fPanel );
		
	}
	
}