package com.topweb.yahrzeitcandle.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;


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
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
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
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
 
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
//import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class MyFlexTable implements HasHandlers{

	public FlexTable getTable() {return flexTable;}
	private final DialogBox deleteDialog=new DialogBox();
	//public FlexTable cast<FlexTable>(){return getTable();}
	public static List<YahrzeitContainer> m_YahrzeitContainerList=new ArrayList<YahrzeitContainer>();
    public static FlowPanel changeClearButtons = new FlowPanel();
    public static Button clearImageButton = new Button("clear");
    public static Button changeImageButton = new Button("change");
	public static FlexTable flexTable=null;
	//static Map<String,String>photos_to_url_map=new HashMap<String,String>();
	 private HandlerManager handlerManager;


	public HandlerRegistration addImageAddedHandler(ImageAddedHandler handler){
		 return handlerManager.addHandler( ImageAddedHandler.TYPE, handler);
	}
	@Override
	public void fireEvent(GwtEvent<?> event) {
		//handlerManager.getHandler(event., index).fireEvent(event);
		handlerManager.fireEvent(event);
	}

	
	MyFlexTable() {
		handlerManager = new HandlerManager(this);

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
	  protected void addRowsConfirmed(JsArray<Yahrzeit> yahrzeitlist) { //can be called on resync
		 

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


				while (incoming_iter.hasNext() ) {
					YahrzeitContainer incoming = incoming_iter.next();

					//photos section
					/* Set<String>has_photos=new HashSet<String>(0);
					 String photoslist=new String();
						if (incoming.getYahrzeit().getPhoto()!=null){
						if (has_photos.size()==0){
							photoslist=incoming.getYahrzeit().getPhoto();
							has_photos.add(incoming.getYahrzeit().getPhoto());
						} else {
							if (!has_photos.contains(incoming.getYahrzeit().getPhoto())){
								photoslist+=("," + incoming.getYahrzeit().getPhoto());
								has_photos.add(incoming.getYahrzeit().getPhoto());
							}
						}
					}//end photos section				
					*/
					
				 while  (main_iter.hasNext()){
					YahrzeitContainer master = main_iter.next();
					int idb=incoming.getYahrzeit().getDbId();
					int mdb=master.getYahrzeit().getDbId();
					if (idb == mdb) {
						int index = main_iter.previousIndex();
						int row = index + 1; //to account for header row
						if (row < flexTable.getRowCount()) { 
							flexTable.insertRow(row); 
						}
					flexTable.setText(row, 0, incoming.getYahrzeit().getName());
				    flexTable.setText(row, 1, incoming.getYahrzeit().getHebDay()+ " " + YahrzeitCandle.heb_months[incoming.getYahrzeit().getHebMonth()]);
				    flexTable.setText(row, 2, YahrzeitCandle.format_pretty.format(incoming.getGregDisplayDate()));

				    
				    

				    if (incoming.getYahrzeit().getPhoto()!=null){
				        final AbsolutePanel placeholderImagePanel = new AbsolutePanel();
				     
					    FocusPanel fPanel = new FocusPanel();					
				    	final YahrzeitImage placeholderImage=new YahrzeitImage(incoming.getYahrzeit());
				    	
				    	//Console.log("setting photo to " + incoming.getYahrzeit().getPhoto());//existing photos
				    	placeholderImage.setUrl(incoming.getYahrzeit().getPhoto().getUrl());
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
								  if (YahrzeitCandle.perms.get("user_photos")==null || YahrzeitCandle.perms.get("user_photos").compareTo("granted")!=0) {
									  new FBLogin(){

										@Override
										public void apiCallback(FBAuthResponse response) {
											 Console.log(response.getStatus());
											
										}
										  
									  }.login("user_photos");
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

				//photos section
				/*if (has_photos.size()>0){
					Console.log("ids: " + photoslist);
					String graphquery="/me?ids=" + photoslist + "&fields=id,picture.type(normal)";
					Console.log("graphquery: " + graphquery);
					
					new FBApiGetPhotos().get(graphquery);
				}//end photos section
	*/
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
			//svr.setFbAuthResponse(YahrzeitCandle.fbAuthResponse);
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
			// 

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
			  Console.logAsObject(req);
		  RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, YahrzeitCandle.JSON_URL);
		  builder.setHeader("Content-Type", "application/json");
		  builder.setHeader("Accept", "application/json");
		  req.setFbAuthResponse(YahrzeitCandle.fbAuthResponse);
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
							resizeFbIframe();
						} else {
							addRowsConfirmed(yahrzeitlist);
						}
					}else if (req.getMethod().equalsIgnoreCase("add")){
						YahrzeitCandle.sw_singleton.freeAddFields();
					    YahrzeitCandle.addPanel.setVisible(false);
					    YahrzeitCandle.addPanel1.setVisible(false);
					    YahrzeitCandle.addYahrButton.setEnabled(true);
					    JsArray<Yahrzeit> yahrzeitlist=req.getYahrzeitList();
					   	if (yahrzeitlist.length()!=m_YahrzeitContainerList.size()+1) {
							resync();
							return;
						}
						else{
							System.out.println("add: no resync needed");
							addRowsConfirmed(yahrzeitlist);
						}
				} else if (req.getMethod().equalsIgnoreCase("delete")){
						//System.out.println("delete callback reached");
						JsArray<Yahrzeit> yahrzeitlist=req.getYahrzeitList();
						
						if (yahrzeitlist.length()!=m_YahrzeitContainerList.size()-1) {
							resync();
						} else {
							System.out.println ("delete: no resync needed");

						    	//System.out.println("delete id " + h.getDbId());
						    	deleteRowsConfirmed(yahrzeitlist);
						}
					}
				 else if (req.getMethod().equalsIgnoreCase("modify")) {
					 JsArray<Yahrzeit> yahrzeitlist=req.getYahrzeitList();
						Yahrzeit h = null;
						for (int i=0;i<yahrzeitlist.length();i++){
						    h=yahrzeitlist.get(i);
						    if (h!=null){
						    	modifyRowConfirmed(h);
						    }
						}
						setAllMyButtonsEnabled(true);
						YahrzeitCandle.addYahrButton.setEnabled(true);
				 } else if (req.getMethod().equalsIgnoreCase("add_photo")){ //gets called when choosing from fbalbums
						PhotoBrowser.d.hide();

					    final AbsolutePanel placeholderImagePanel = new AbsolutePanel();
					    FocusPanel fPanel = new FocusPanel();					

						Console.log("got to add_photo rec'd from gwt.php: " );
						//find the anchor that was clicked and repop with new photo
						
						Yahrzeit h = req.getYahrzeitList().get(0);
						Console.log("id is " + h.getDbId() + ", photo is " + h.getPhoto());
						if (h==null || h.getPhoto()==null) return;
						int row= getYahrzeitIndexFromDatabaseId(h.getDbId()) + 1;
						Console.log("position is "+ row);
						flexTable.getWidget(row, 3).removeFromParent();
						YahrzeitImage placeholderImage=new YahrzeitImage(h);
					    placeholderImage.setUrl(h.getPhoto().getUrl());
				    	//Console.log("setting photo to " + h.getPhoto());
				       
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
				    	for (YahrzeitContainer y : m_YahrzeitContainerList){ //update master list
							if (y.getYahrzeit().getDbId()==h.getDbId()){
								y.getYahrzeit().setPhoto(h.getPhoto());
							}
				    	}
					    
					    
					    /*String graphquery="/me?ids=" + h.getPhoto() + "&fields=id,picture.type(normal)";
					    Console.log("graphquery: " + graphquery);
					    new FBApiGetPhotos().get(graphquery);*/						
					} else if (req.getMethod().equalsIgnoreCase("clear_photo")) {
						Yahrzeit h = req.getYahrzeitList().get(0);
						int row= getYahrzeitIndexFromDatabaseId(h.getDbId()) + 1;
						Console.log("position is "+ row);
						FocusPanel p = (FocusPanel)flexTable.getWidget(row,3);
				    	for (YahrzeitContainer y : m_YahrzeitContainerList){ //update master list
							if (y.getYahrzeit().getDbId()==h.getDbId()){
								y.getYahrzeit().setPhoto(null);
							}
				    	}

						p.removeFromParent();
						YAddPhotoButton setPhotoButton = new YAddPhotoButton(h);
				    	setPhotoButton.addClickHandler(new ClickHandler(){
							@Override
							public void onClick(ClickEvent event) {
								PhotoBrowser.activeYahrzeit=((YAddPhotoButton)event.getSource()).getYahrzeit();
								Console.log("active Yahrzeit: " + PhotoBrowser.activeYahrzeit.getName());
								  if (YahrzeitCandle.perms.get("user_photos")==null || YahrzeitCandle.perms.get("user_photos").compareTo("granted")!=0) {
									  new FBLogin(){

										@Override
										public void apiCallback(FBAuthResponse response) {
											 Console.log(response.getStatus());
											
										}
										  
									  }.login("user_photos");
								}else {
									
									PhotoBrowser.showUploader();

								}
							}
				    	});
					    flexTable.setWidget(row, 3, setPhotoButton );
					}
				}//status OK
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
			
			int initial_row=initial_position+1;
			int new_row = new_position+1;
			
			if (initial_position==new_position){
				
				flexTable.setText( initial_row,0 ,h.getName());
				 flexTable.setText(initial_row, 1, h.getHebDay()+ " " + YahrzeitCandle.heb_months[h.getHebMonth()]);
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
					 flexTable.setText(new_row, 1, h.getHebDay()+ " " + YahrzeitCandle.heb_months[h.getHebMonth()]);
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
	}
		
		void setAllMyButtonsEnabled(boolean enabled){
			for (Widget w : flexTable){
				if (w instanceof Button) {
					 ((Button)w).setEnabled(enabled);
				}
			}
		}
private void editRow(int dbid) {
		setAllMyButtonsEnabled(false);
		YahrzeitCandle.addYahrButton.setEnabled(false);
		flexTable.addStyleName("editMode");
		YahrzeitContainer h =getYahrzeitContainerFromDbId(dbid);
		if (h==null) {
			return;
		}
		  final TextBox editme = new TextBox();
		  editme.setValue(h.getYahrzeit().getName());
		  editme.setVisibleLength((editme.getValue().length()));
		  int row = getYahrzeitIndexFromDatabaseId(dbid)+1;
		  flexTable.setWidget(row, 0, editme);
		   
		  HebmonthsDropdown him=YahrzeitCandle.newHebrewDate_mon;
		  him.setMonth(h.getYahrzeit().getHebMonth());
		  HebrewDateDayInput hid = YahrzeitCandle.newHebrewDate_day;
		  hid.setDay(h.getYahrzeit().getHebDay());

		  TextBox hiy=YahrzeitCandle.newHebrewYear;
		  hiy.setValue("" + (h.getYahrzeit().getHebYear()==0 ? YahrzeitCandle.m_thisHebrewYear :
			  h.getYahrzeit().getHebYear()));
		  hiy.setVisibleLength(4);
		  HorizontalPanel hebpanel=new HorizontalPanel();
		  hebpanel.add(hid.getTextBox());
		  hebpanel.add(him);
		  hebpanel.add(hiy);
		  HebrewDate hebDate=null;
		  try{
		  
			hebDate=new HebrewDate();
			hebDate.setHebrewDate(him.getMonth(),hid.getDay(),Integer.parseInt(hiy.getValue()));
			
		} catch (HebrewDateException e) {
			// 
			e.printStackTrace();
		}

	 
		 
		  HorizontalPanel gregPanel = new HorizontalPanel();
		  YahrzeitCandle.newGregDate_day.setText(""+hebDate.getGregorianDayOfMonth());
		  YahrzeitCandle.newGregDate_mon.setMonth(hebDate.getGregorianMonth());
		  YahrzeitCandle.newGregDate_year.setText(""+hebDate.getGregorianYear());
		  gregPanel.add(YahrzeitCandle.newGregDate_day);
		  gregPanel.add(YahrzeitCandle.newGregDate_mon);
		  gregPanel.add(YahrzeitCandle.newGregDate_year);
		   
	 
		   
		VerticalPanel datePanel=new VerticalPanel();
		datePanel.add(hebpanel);
		datePanel.add(gregPanel);
		flexTable.setWidget(row, 1, datePanel);

		  	
		  	
		
		Button editOkayButton=new Button("Confirm");
		editOkayButton.getElement().setId("editokay_"+dbid);
		EditRowModifyHandler editRowModifyHandler = new EditRowModifyHandler(
				editme, //name
	  			h.getYahrzeit(),
	  			this);
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
				 flexTable.setText(row, 1, h.getHebDay()+ " " + YahrzeitCandle.heb_months[h.getHebMonth()]);
				 flexTable.setText(row, 2, YahrzeitCandle.format_pretty.format(YahrzeitContainer.getGregDisplayDate(h)));
				 addDelButton(h);
				 addEditButton(h);
				setAllMyButtonsEnabled(true);
				YahrzeitCandle.addYahrButton.setEnabled(true);
			}
			
		}
		);
		flexTable.setWidget(row,5,editCancelButton);
		flexTable.getRowFormatter().setStyleName(row, "editTableRow");
		 hid.getTextBox().setFocus(true);
		}
		
	public void addRowRequest(Yahrzeit h1) {
		SvrReq svr=(SvrReq)JavaScriptObject.createObject();
		svr.setMethod("add");

		 Yahrzeit h[] = {h1};
	        svr.setYahrzeits(JsArrayUtils.readOnlyJsArray(h));
	        submitData(svr);
	   
	}
	public void modifyRowRequest(Yahrzeit h1){
		SvrReq svr=(SvrReq)JavaScriptObject.createObject();
		svr.setMethod("modify");

		 Yahrzeit h[] = {h1};
	        svr.setYahrzeits(JsArrayUtils.readOnlyJsArray(h));
	        submitData(svr);
	
	}
	public   void resync() {
		SvrReq svr = (SvrReq)JavaScriptObject.createObject();
		svr.setMethod("resync");
		
		 submitData(svr);
	}
	
	public static void addPhoto(Yahrzeit yahrzeit_with_photo) {
	
	}
	public static void addPhotoRequest(Yahrzeit h1, FBPhoto fbPhoto) {
		if (h1==null) return;
		SvrReq svr=(SvrReq)JavaScriptObject.createObject();
		svr.setMethod("add_photo");
		h1.setPhotoId(String.valueOf(fbPhoto.getId()));
		 Yahrzeit h[] = {h1};
	        svr.setYahrzeits(JsArrayUtils.readOnlyJsArray(h));
		YahrzeitCandle.yahrFlexTable.submitData(svr);
	}

	public static void clearPhotoRequest(YahrzeitImage fbPhoto) {
		SvrReq svr=(SvrReq)JavaScriptObject.createObject();
		svr.setMethod("clear_photo");
		Yahrzeit h[] = {fbPhoto.getYahrzeit()};
		svr.setYahrzeits(JsArrayUtils.readOnlyJsArray(h));
		YahrzeitCandle.yahrFlexTable.submitData(svr);	
	}
	public static void addPhotoComplete(Yahrzeit yahrzeit_with_photo) { // when uploading photo, not using chooser
		final AbsolutePanel placeholderImagePanel = new AbsolutePanel();
	    FocusPanel fPanel = new FocusPanel();					

		//find the anchor that was clicked and repop with new photo
		
		Console.log("id is " + yahrzeit_with_photo.getDbId() + ", photo is " + yahrzeit_with_photo.getPhoto());
		if (yahrzeit_with_photo.getPhoto()==null) return;
		int position= getYahrzeitIndexFromDatabaseId(yahrzeit_with_photo.getDbId()) + 1;
		Console.log("position is "+ position);
		flexTable.getWidget(position, 3).removeFromParent();
		YahrzeitImage placeholderImage=new YahrzeitImage(yahrzeit_with_photo);
	    
    	/*Console.log("setting photo to " + yahrzeit_with_photo.getPhoto());
    	for (YahrzeitContainer y : m_YahrzeitContainerList){
			if (y.getYahrzeit().getDbId()==yahrzeit_with_photo.getDbId()){
				y.getYahrzeit().setPhoto(yahrzeit_with_photo.getPhoto());
			}
    	}*/
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
	    
	    
	    /*String graphquery="/me?ids=" + yahrzeit_with_photo.getPhoto() + "&fields=id,picture.type(normal)";
		Console.log("graphquery: " + graphquery);
		new FBApiGetPhotos().get(graphquery);*/
	}
}
