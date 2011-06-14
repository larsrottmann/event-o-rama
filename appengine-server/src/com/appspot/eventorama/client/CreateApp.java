package com.appspot.eventorama.client;

import java.util.Date;

import com.appspot.eventorama.client.service.ApplicationsService;
import com.appspot.eventorama.client.service.ApplicationsServiceAsync;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CreateApp extends PopupPanel {

    private final ApplicationsServiceAsync applicationsService = GWT.create(ApplicationsService.class);
    
    
    public CreateApp() {
        super(true);
        
        VerticalPanel verticalPanel = new VerticalPanel();
        setWidget(verticalPanel);
        verticalPanel.setSize("100%", "100%");
        
        Grid grid = new Grid(3, 2);
        verticalPanel.add(grid);
        
        Label lblEvent = new Label("Event");
        grid.setWidget(0, 0, lblEvent);
        
        final TextBox textBox = new TextBox();
        textBox.setFocus(true);
        grid.setWidget(0, 1, textBox);
        
        Label label = new Label("Start");
        grid.setWidget(1, 0, label);
        
        final DateBox dateBox_1 = new DateBox();
        dateBox_1.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));
        grid.setWidget(1, 1, dateBox_1);
        
        Label lblStart = new Label("End");
        grid.setWidget(2, 0, lblStart);
        
        final DateBox dateBox = new DateBox();
        dateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));
        grid.setWidget(2, 1, dateBox);
        
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        verticalPanel.add(horizontalPanel);
        
        Button btnCreate = new Button("Create");
        btnCreate.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                final String title = textBox.getText().trim();
                if (!title.matches("^[0-9a-zA-Z\\.\\-]{3,11}$"))
                {
                    Window.alert("'" + title + "' is not a valid application title, allowed characters are 0-9, a-z, dot and dash (3 up to 11 characters).");
                    textBox.selectAll();
                    return;
                }
                
                final Date startDate = dateBox_1.getValue();
                if (startDate == null)
                {
                    Window.alert("Please select a valid application start date.");
                    return;
                }

                final Date expirationDate = dateBox.getValue();
                if (expirationDate == null)
                {
                    Window.alert("Please select a valid application expiration date.");
                    return;
                }
                else if (expirationDate.before(new Date(System.currentTimeMillis())))
                {
                    Window.alert("'" + expirationDate + "' is not a valid application expiration date.");
                    return;
                }
                else if (expirationDate.before(startDate))
                {
                    Window.alert("Expiration date cannot be before the application start date.");
                    return;
                }
                
                applicationsService.create(title, startDate, expirationDate, new AsyncCallback<Void>() {
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }

                    public void onSuccess(Void result) {
                        hide();
                        AppList.loadApps();
                    }
                });
            }
        });
        horizontalPanel.add(btnCreate);
        btnCreate.setText("Create");
        
        Button btnCancel = new Button("Cancel");
        btnCancel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        horizontalPanel.add(btnCancel);
    }

}
