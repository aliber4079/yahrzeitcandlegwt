package com.topweb.yahrzeitcandle.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MyYesNoDialog extends DialogBox {

MyFlexTable flexTable=null;

    public MyYesNoDialog(MyFlexTable table) {
    	flexTable=table;
      // Set the dialog box's caption.
      setText("Delete selected Yahrzeit?");

      // Enable animation.
     // setAnimationEnabled(true);

      // Enable glass background.
      setGlassEnabled(true);

      // DialogBox is a SimplePanel, so you have to set its widget property to
      // whatever you want its contents to be.
      Button ok = new Button("delete");
      Button cancel = new Button("cancel");
      ok.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          MyYesNoDialog.this.hide();
        }
      });
      cancel.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            MyYesNoDialog.this.hide();
          }
        });
      HorizontalPanel buttonPanel = new HorizontalPanel();
      buttonPanel.add(ok);
      buttonPanel.add(cancel);
      setWidget(buttonPanel);
    }
  }
