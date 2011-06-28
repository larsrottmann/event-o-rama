package com.appspot.eventorama.client;

import java.util.Date;
import java.util.List;

import com.appspot.eventorama.client.service.ApplicationsService;
import com.appspot.eventorama.client.service.ApplicationsServiceAsync;
import com.appspot.eventorama.shared.model.Application;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

public class AppList extends Composite {

    private ApplicationsServiceAsync applicationsService = GWT.create(ApplicationsService.class);

    private RootPanel errorPanel;

    private VerticalPanel mainPanel = new VerticalPanel();
    private FlexTable appsFlexTable = new FlexTable();
    private VerticalPanel addAppsPanel = new VerticalPanel();
    private Label addAppsLabel = new Label("New Event");
    private Grid appsGrid = new Grid(4, 2);
    private Label eventLabel = new Label("Name");
    private TextBox eventTextBox = new TextBox();
    private Label startLabel = new Label("Start Date");
    private DateBox startDateBox = new DateBox();
    private Label expirationLabel = new Label("End Date");
    private DateBox expirationDateBox = new DateBox();
    private Button addAppButton = new Button("Add");

    public AppList() {
        errorPanel = RootPanel.get("error");
        errorPanel.setVisible(false);

        appsFlexTable.setText(0, 0, "Event Name");
        appsFlexTable.setText(0, 1, "Start Date");
        appsFlexTable.setText(0, 2, "End Date");
        appsFlexTable.setText(0, 3, "Active");
        appsFlexTable.setText(0, 4, "Remove");

        startDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));
        expirationDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));

        appsGrid.setWidget(0, 0, addAppsLabel);
        appsGrid.setWidget(1, 0, eventLabel);
        appsGrid.setWidget(1, 1, eventTextBox);
        appsGrid.setWidget(2, 0, startLabel);
        appsGrid.setWidget(2, 1, startDateBox);
        appsGrid.setWidget(3, 0, expirationLabel);
        appsGrid.setWidget(3, 1, expirationDateBox);

        addAppsPanel.setBorderWidth(1);
        addAppsPanel.add(appsGrid);
        addAppsPanel.add(addAppButton);

        initWidget(mainPanel);
        mainPanel.add(appsFlexTable);
        mainPanel.add(addAppsPanel);

        eventTextBox.setFocus(true);

        addAppButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addApp();
            }
        });

        refreshApps();
    }

    private void refreshApps() {
        errorPanel.setVisible(false);
        applicationsService.getList(new AsyncCallback<List<Application>>() {
            public void onFailure(Throwable caught) {
                showError("Error loading data: " + caught.getMessage());
            }

            public void onSuccess(List<Application> result) {
                updateTable(result);
            }
        });
    }

    private void updateTable(List<Application> result) {
        for (int i = 1; i < appsFlexTable.getRowCount(); i++) {
            appsFlexTable.removeRow(i);
        }

        for (int i = 0; i < result.size(); i++) {
            updateTableRow(result.get(i), i+1);
        }
    }

    private void updateTableRow(final Application app, final int row) {
        final Key appKey = app.getKey();

        appsFlexTable.setText(row, 0, app.getTitle());
        appsFlexTable.setText(row, 1, DateTimeFormat.getFormat("dd.MM.yyyy").format(app.getStartDate()));
        appsFlexTable.setText(row, 2, DateTimeFormat.getFormat("dd.MM.yyyy").format(app.getExpirationDate()));
        appsFlexTable.setText(row, 3, Boolean.toString(app.isActive()));

        // Add a button to remove this stock from the table.
        Button removeAppButton = new Button("x");
        removeAppButton.addStyleDependentName("remove");
        removeAppButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                applicationsService.delete(appKey, new AsyncCallback<Void>() {
                    public void onFailure(Throwable caught) {
                        showError("Error deleting data: " + caught.getMessage());
                    }

                    public void onSuccess(Void result) {
                        refreshApps();
                    }
                });
            }
        });
        appsFlexTable.setWidget(row, 4, removeAppButton);
    }

    private void addApp() {
        errorPanel.setVisible(false);
        final String title = eventTextBox.getText().trim();
        if (!title.matches("^[0-9a-zA-Z\\.\\-\\s]{3,20}$")) {
            showError("'" + title + "' is not a valid event title, allowed characters are 0-9, a-z, space, dot and dash (3 up to 20 characters).");
            eventTextBox.selectAll();
            return;
        }

        final Date startDate = startDateBox.getValue();
        if (startDate == null) {
            showError("Please select a valid event start date.");
            return;
        }

        final Date expirationDate = expirationDateBox.getValue();
        if (expirationDate == null) {
            showError("Please select a valid event end date.");
            return;
        } else if (expirationDate.before(new Date(System.currentTimeMillis()))) {
            showError("'" + expirationDate + "' is not a valid event end date.");
            return;
        } else if (expirationDate.before(startDate)) {
            showError("End date cannot be before the event start date.");
            return;
        }

        final Application app = new Application();
        app.setTitle(title);
        app.setStartDate(startDate);
        app.setExpirationDate(expirationDate);
        final int row = appsFlexTable.getRowCount() + 1;

        applicationsService.create(app, new AsyncCallback<Key>() {
            public void onFailure(Throwable caught) {
                showError("Error creating data: " + caught.getMessage());
            }

            public void onSuccess(Key result) {
                eventTextBox.setText(null);
                startDateBox.setValue(null);
                expirationDateBox.setValue(null);
                if (result != null) {
                    Main.showMessage("App added", true);
                    app.setKey(result);
                    updateTableRow(app, row);
                } else {
                    showError("Failed to create new event");
                }
            }
        });
    }

    private void showError(String message) {
        errorPanel.getElement().setInnerText(message);
        errorPanel.setVisible(true);
    }
}
