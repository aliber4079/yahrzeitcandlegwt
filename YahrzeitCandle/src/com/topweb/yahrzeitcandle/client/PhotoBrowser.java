package com.topweb.yahrzeitcandle.client;



import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

import java.util.HashMap;

//import org.mortbay.util.ajax.JSON;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.google.gwt.view.client.TreeViewModel.DefaultNodeInfo;

 

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.cell.client.ValueUpdater; 
import com.google.gwt.json.client.JSONObject; 
import com.google.gwt.json.client.JSONParser;

//me?fields=albums.fields(id,name,photos.fields(id,picture.type(small)))
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

  public class PhotoBrowser  {
	    private final static SingleSelectionModel<FBPhoto> selectionModel = new SingleSelectionModel<FBPhoto>();

	  

		
	static List<FBAlbum> albums=null;
	  
    static Button m_confirmbutton=new Button("Select");
    static Button m_uploadbutton=new Button ("Upload...");
    static Yahrzeit activeYahrzeit=null;
    static Button m_cancelbutton=new Button("Cancel");
    public static DialogBox d=new DialogBox();
	static HashMap<String,FBAlbum> m_aid_to_album = new HashMap<String,FBAlbum>();
	//static ListDataProvider<FBPhoto> photoDataProvider=null;
	static CellBrowser browser=null;
	//private static ListDataProvider<FBAlbum> albumDataProvider=null;
	public PhotoBrowser(){
	    d.addCloseHandler(new CloseHandler<PopupPanel>(){
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				Console.log("onclose event");
				d.setText("");
				activeYahrzeit=null;
				MyFlexTable.changeClearButtons.removeFromParent();
			}
	    });
		}
	public static List<FBAlbum> parseAlbumDataJ(FBAlbumData o){
		List<FBAlbum> l = new ArrayList<FBAlbum>();
		for (int i=0;i<o.size();i++){
			FBAlbum a = new FBAlbum(o.getId(i),o.getName(i));
			JsArray<PhotoNative> p=o.getPhotos(i);
			if (p==null){
				continue;
			}
			for (int j=0;j<p.length();j++){
				FBPhoto p1 = new FBPhoto(p.get(j).getId(),p.get(j).getUrl());
				a.addPhoto(p1);
			}
			l.add(a);
		}
		return l;
	}
	
private static class CustomTreeModel implements TreeViewModel {
	
	ListDataProvider<FBAlbum> albumdataprovider =null;
    public CustomTreeModel() {
	    		
	     m_confirmbutton.addClickHandler(new ClickHandler(){
		@Override
		public void onClick(ClickEvent event) {
			if (selectionModel.getSelectedObject()!=null) {
				String pid=selectionModel.getSelectedObject().getId();
				Console.log("you chose " + pid);
				MyFlexTable.addPhotoRequest(activeYahrzeit,selectionModel.getSelectedObject());
				selectionModel.setSelected(selectionModel.getSelectedObject(),false);
			}
			d.hide();
			}
	    });
	     m_cancelbutton.addClickHandler(new ClickHandler(){
		@Override
		public void onClick(ClickEvent event) {
			if(selectionModel.getSelectedObject()!=null)
				selectionModel.setSelected(selectionModel.getSelectedObject(),false);
			d.hide();
			
		}});
		     
	     m_uploadbutton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showUploader();
			}
	    	 
	     });
		     
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
            	Console.log("onSelectionChange");
              if (selectionModel.getSelectedObject() != null) {
                m_confirmbutton.setEnabled(true);
              } else {
            	  Console.log("nothing is selected");
            	  m_confirmbutton.setEnabled(false);
              }
            }
          });
		}

		/**
	     * Get the {@link NodeInfo} that provides the children of the specified
	     * value.
	     */
	  
	    public <T> NodeInfo<?> getNodeInfo(T value) {
	    	if (value==null){
				// LEVEL 0, list of albums
				albumdataprovider = new ListDataProvider<FBAlbum>(albums);
				return new DefaultNodeInfo<FBAlbum>(albumdataprovider,new AbstractCell<FBAlbum>(){
					@Override
					public void render(com.google.gwt.cell.client.Cell.Context context, FBAlbum value,
							SafeHtmlBuilder sb) {
						if (value != null) {
				              sb.appendEscaped(value.getName());
				        }
					}
				});	
			} else if (value instanceof FBAlbum) {
		        // LEVEL 1.
				 
				ListDataProvider<FBPhoto> photodataprovider = 
					new ListDataProvider<FBPhoto>(((FBAlbum)value).getPhotos());
				return new DefaultNodeInfo<FBPhoto>(photodataprovider,new PhotoCell(),selectionModel,null);
			}
			return null;
	    }

	    /**
	     * Check if the specified value represents a leaf node. Leaf nodes cannot be
	     * opened.
	     */
	    public boolean isLeaf(Object value) {
	      // The maximum length of a value is ten characters.
	      if (value instanceof FBPhoto)
	    	  return true; 	    	
	      return false;
	    }
	}
	 
public static void showUploader(){


	    // Because we're going to add a FileUpload widget, we'll need to set the
	    // form to use the POST method, and multipart MIME encoding.
	    // Create a panel to hold all of the form widgets.
	    final VerticalPanel panel = new VerticalPanel();
	    panel.add(new Label("Upload ..."));
	    Hidden authresponse= new Hidden("authResponse",(new JSONObject(YahrzeitCandle.fbAuthResponse)).toString());
	    panel.add(authresponse);
	    Hidden yahrzeit = new Hidden("yahrzeit",(new JSONObject(activeYahrzeit).toString()));
	    panel.add(yahrzeit);
	    Button gotoChooser = new Button("browse Facebook albums...");
	    gotoChooser.addClickHandler(new ClickHandler(){
	    	@Override
	    	public void onClick(ClickEvent e){
	    		showPhotoBrowser();
	    	}
	    });
    	final FormPanel form = new FormPanel();
	    // Create a FileUpload widget.
	    form.setAction(YahrzeitCandle.JSON_URL);
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);
	    form.setWidget(panel);
	    HorizontalPanel h = new HorizontalPanel();
	    // Add a 'submit' button.
	    h.add(new Button("Submit", new ClickHandler() {
	      public void onClick(ClickEvent event) {
	    		form.submit();
	      }
	    }));
	    h.add(new Button("Cancel", new ClickHandler() {
		      public void onClick(ClickEvent event) {
			        d.hide();
			      }
			    }));
	    h.add(gotoChooser);
	    panel.add(h);
	    if (YahrzeitCandle.perms.get("publish_actions")==null || YahrzeitCandle.perms.get("publish_actions").compareTo("granted")!=0){
	    	Button b=new Button("Upload..");
	    	b.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					//request publish actions perm
					Console.log("onclick");
					 new FBLogin(){
							@Override
							public void apiCallback(FBAuthResponse response) {
								Console.log("in api callback");
								 Console.log(response.getStatus());
							}
						  }.login("publish_actions");
						  
				}
	    	});
	    	panel.add(b);
	    } else {
		    Hidden maxsize=new Hidden("MAX_FILE_SIZE","5000000");
		    panel.add(maxsize);
		    final FileUpload upload = new FileUpload();
		    upload.setName("photoUploader");
	    	panel.add(upload);


		    // Add an event handler to the form.
		    form.addSubmitHandler(new FormPanel.SubmitHandler() {
			      public void onSubmit(SubmitEvent event) {
			        // This event is fired just before the form is submitted. We can take
			        // this opportunity to perform validation.
			        if (upload.getFilename().length() == 0) {
			          Window.alert("Please select a file to upload");
			          event.cancel();
			        }
		    		((Label)panel.getWidget(0)).setText("Please wait...");
			      }
			    });
			    
			
		    form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			      public void onSubmitComplete(SubmitCompleteEvent event) {
			        // When the form submission is successfully completed, this event is
			        // fired. Assuming the service returned a response of type text/html,
			        // we can get the result text here (see the FormPanel documentation for
			        // further explanation).
			    	  
			SvrReq s =  (SvrReq)(JSONParser.parseLenient(event.getResults()).isObject().getJavaScriptObject());
			
			 Yahrzeit yahrzeit_with_photo=s.getYahrzeitList().get(0);
			    	  Console.log ("addSubmitCompleteHandler: pid is ");
			    	  Console.logAsObject(yahrzeit_with_photo);
			    	  Console.log("response: " + s.getStatus());
			    	 if (yahrzeit_with_photo!=null && s.getStatus().compareToIgnoreCase("OK")==0) {
			    		 d.hide();
			    		 MyFlexTable.addPhotoComplete(yahrzeit_with_photo);
			    		
			    	 } else {
			    		 Window.alert("error uploading photo");
			    		 	d.hide();
			    	 }
			      }
			    });
	    }
	    form.setPixelSize(600, 200);
    	d.setWidget(form);

			d.setText("Photo for Yahrzeit of " + activeYahrzeit.getName());

			d.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
		          public void setPosition(int offsetWidth, int offsetHeight) {
		        	  fbGetOffsetTop(offsetHeight);
		          }
		        });
			   
	}
	
	 
public  native static int fbGetOffsetTop(int offsetHeight) /*-{
	$wnd.console && $wnd.console.log("in fbGetOffsetTop " + offsetHeight);
	$wnd.FB.Canvas.getPageInfo(function(ci){
	 newtop = parseInt(ci.scrollTop) - parseInt(ci.offsetTop);
	 newtop = (newtop + offsetHeight / 3);
	//$wnd.console && $wnd.console.log("fb offset top: " + newtop);
	@com.topweb.yahrzeitcandle.client.PhotoBrowser::finishResizing(I)(newtop);
	});
	
	
}-*/;

public static void finishResizing(int newtop){
	Console.log("fb offset top: " + newtop);
   PhotoBrowser.d.setPopupPosition(5, newtop);
    MyFlexTable.resizeFbIframe();
}


public static  void showPhotoBrowser(){
	
	
	Label l = new Label("Please wait...");
	l.setPixelSize(600, 200);
	d.setWidget(l);
	
	FBApi api=new FBApi(){

		@Override
		public void apiCallback(JavaScriptObject response) {
			albums=parseAlbumDataJ((FBAlbumData)response);
			 
			
			/*for (int i=0;i<la.size();i++){
		    	Console.log(la.get(i).getName());
		    	for (int j=0;j<la.get(i).getPhotos().size();j++){
		    		Console.log(la.get(i).getPhotos().get(j).getId());
		    	}
		    }
			
			if (true) return;*/
			TreeViewModel model = new CustomTreeModel();
			CellBrowser.Builder<TreeViewModel> b 
			 =new CellBrowser.Builder<TreeViewModel>(model,null);
			b.pagerFactory(null);
			browser=b.build();
			HorizontalPanel hPanel=new HorizontalPanel();
		    hPanel.add(m_confirmbutton);
		    hPanel.add(m_cancelbutton);
		    hPanel.add(m_uploadbutton);
			VerticalPanel vPanel = new VerticalPanel();

		    vPanel.add(browser);
		    vPanel.add(hPanel);
		    
		    //populate cellbrowser
		    browser.setSize("600px", "200px");
		    browser.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		    m_confirmbutton.setEnabled(false);
			d.setWidget(vPanel);
		}
		
	};
	api.get("/me?fields=albums.fields(id,name,photos.fields(id,picture.type(small)))");

  }
}


