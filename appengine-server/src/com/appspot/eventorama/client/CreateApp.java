package com.appspot.eventorama.client;

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
                applicationsService.create(textBox.getValue(), dateBox_1.getValue(), dateBox.getValue(), new AsyncCallback<Void>() {
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
