package com.appspot.eventorama.client;

import java.util.List;

import com.appspot.eventorama.client.service.ApplicationsService;
import com.appspot.eventorama.client.service.ApplicationsServiceAsync;
import com.appspot.eventorama.shared.model.Application;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable;

public class AppList extends Composite {

    
    private VerticalPanel mainPanel;
    private static FlexTable appsFlexTable;
    private Button btnCreate;
    

    public AppList() {
        
        mainPanel = new VerticalPanel();
        initWidget(mainPanel);
        
        appsFlexTable = new FlexTable();
        appsFlexTable.setText(0, 0, "Event");
        appsFlexTable.setText(0, 1, "Start Date");
        appsFlexTable.setText(0, 2, "End Date");
        appsFlexTable.setText(0, 3, "Active");
        appsFlexTable.setText(0, 4, "Remove");
        
        loadApps();
        
        mainPanel.add(appsFlexTable);
        
        btnCreate = new Button("Create");
        btnCreate.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                final PopupPanel popup = new CreateApp();
                
                popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        popup.center();
                    }
                  });

            }
        });
        mainPanel.add(btnCreate);
    }


    public static void loadApps() {
        final ApplicationsServiceAsync applicationsService = GWT.create(ApplicationsService.class);
        applicationsService.getList(new AsyncCallback<List<Application>>() {

            public void onFailure(Throwable caught) {
                Window.alert(caught.toString());
            }

            public void onSuccess(List<Application> result) {
                for (Application app : result)
                {
                    final int row = appsFlexTable.getRowCount();
                    appsFlexTable.setText(row, 0, app.getTitle());
                    appsFlexTable.setText(row, 1, DateTimeFormat.getFormat("dd.MM.yyyy").format(app.getStartDate()));
                    appsFlexTable.setText(row, 2, DateTimeFormat.getFormat("dd.MM.yyyy").format(app.getExpirationDate()));
                    appsFlexTable.setText(row, 3, Boolean.toString(app.isActive()));

                    // Add a button to remove this stock from the table.
                    Button removeAppButton = new Button("x");
                    removeAppButton.addStyleDependentName("remove");
                    removeAppButton.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            // TODO
                            appsFlexTable.removeRow(row);
                        }
                    });
                    appsFlexTable.setWidget(row, 4, removeAppButton);
                    
                }
            }

        });
    }

}
