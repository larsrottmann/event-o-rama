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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

public class EventList extends Composite {

    private ApplicationsServiceAsync applicationsService = GWT.create(ApplicationsService.class);

    private VerticalPanel mainPanel = new VerticalPanel();
    private Label errorLabel = new Label();

    private FlexTable eventTable = new FlexTable();
    private Button newEventButton = new Button("New Event");

    private VerticalPanel createEventPanel = new VerticalPanel();
    private Label newEventLabel = new Label("New Event");
    private Grid createEventGrid = new Grid(4, 2);
    private Label eventNameLabel = new Label("Name");
    private TextBox eventNameTextBox = new TextBox();
    private Label eventStartLabel = new Label("Start Date");
    private DateBox eventStartDateBox = new DateBox();
    private Label eventEndLabel = new Label("End Date");
    private DateBox eventEndDateBox = new DateBox();
    private HorizontalPanel createEventButtonPanel = new HorizontalPanel();
    private Button createEventButton = new Button("Create");
    private Button cancelButton = new Button("Cancel");

    public EventList() {
        errorLabel.setVisible(false);
        errorLabel.setStyleName("error", true);

        eventTable.setText(0, 0, "Name");
        eventTable.setText(0, 1, "Start Date");
        eventTable.setText(0, 2, "End Date");
        eventTable.setText(0, 3, "Active");
        eventTable.setText(0, 4, "Remove");

        eventTable.setCellPadding(6);
        eventTable.addStyleName("eventList");
        eventTable.getRowFormatter().addStyleName(0, "eventListHeader");
        eventTable.getCellFormatter().addStyleName(0, 0, "eventListColumn");
        eventTable.getCellFormatter().addStyleName(0, 1, "eventListNumericColumn");
        eventTable.getCellFormatter().addStyleName(0, 2, "eventListNumericColumn");
        eventTable.getCellFormatter().addStyleName(0, 3, "eventListColumn");
        eventTable.getCellFormatter().addStyleName(0, 4, "eventListRemoveColumn");

        eventStartDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));
        eventEndDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));

        createEventGrid.setWidget(0, 0, newEventLabel);
        createEventGrid.setWidget(1, 0, eventNameLabel);
        createEventGrid.setWidget(1, 1, eventNameTextBox);
        createEventGrid.setWidget(2, 0, eventStartLabel);
        createEventGrid.setWidget(2, 1, eventStartDateBox);
        createEventGrid.setWidget(3, 0, eventEndLabel);
        createEventGrid.setWidget(3, 1, eventEndDateBox);

        newEventLabel.setStyleName("eventElement", true);
        newEventLabel.setStyleName("panelTitle", true);
        eventNameLabel.setStyleName("eventElement", true);
        eventNameTextBox.setStyleName("eventElement", true);
        eventStartLabel.setStyleName("eventElement", true);
        eventStartDateBox.setStyleName("eventElement", true);
        eventEndLabel.setStyleName("eventElement", true);
        eventEndDateBox.setStyleName("eventElement", true);
        createEventButton.setStyleName("eventButton", true);
        cancelButton.setStyleName("eventButton", true);

        createEventButtonPanel.add(createEventButton);
        createEventButtonPanel.add(cancelButton);

        createEventPanel.add(createEventGrid);
        createEventPanel.add(createEventButtonPanel);

        createEventButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                createEvent();
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                errorLabel.setVisible(false);
                mainPanel.remove(createEventPanel);
                mainPanel.add(newEventButton);
            }
        });

        newEventButton.setStyleName("eventButton", true);

        initWidget(mainPanel);
        mainPanel.add(errorLabel);
        mainPanel.add(eventTable);
        mainPanel.add(newEventButton);

        newEventButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                mainPanel.remove(newEventButton);
                mainPanel.add(createEventPanel);
                eventNameTextBox.setFocus(true);
            }
        });

        loadEvents();
    }

    private void loadEvents() {
        errorLabel.setVisible(false);

        Main.showMessage("Loading events ...", false);
        applicationsService.getList(new AsyncCallback<List<Application>>() {
            public void onSuccess(List<Application> result) {
                Main.hideMessage();
                updateTable(result);
            }

            public void onFailure(Throwable caught) {
                Main.hideMessage();
                showError("Error loading events: " + caught.getMessage());
            }
        });
    }

    private void updateTable(List<Application> result) {
        for (int i = 1; i < eventTable.getRowCount(); i++) {
            eventTable.removeRow(i);
        }

        for (int i = 0; i < result.size(); i++) {
            updateTableRow(result.get(i), i+1);
        }
    }

    private void updateTableRow(final Application app, final int row) {
        final Key eventKey = app.getKey();

        Anchor eventDetailAnchor = new Anchor(app.getTitle(), false);
        eventDetailAnchor.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                RootPanel.get("content_body").remove(EventList.this);
                RootPanel.get("content_body").add(new EventDetail(eventKey));
            }
        });
        eventTable.setWidget(row, 0, eventDetailAnchor);

        eventTable.setText(row, 1, DateTimeFormat.getFormat("dd.MM.yyyy").format(app.getStartDate()));
        eventTable.setText(row, 2, DateTimeFormat.getFormat("dd.MM.yyyy").format(app.getExpirationDate()));
        eventTable.setText(row, 3, Boolean.toString(app.isActive()));

        eventTable.getCellFormatter().addStyleName(row, 0, "eventListColumn");
        eventTable.getCellFormatter().addStyleName(row, 1, "eventListNumericColumn");
        eventTable.getCellFormatter().addStyleName(row, 2, "eventListNumericColumn");
        eventTable.getCellFormatter().addStyleName(row, 3, "eventListColumn");

        Button removeEventButton = new Button("x");
        removeEventButton.addStyleDependentName("remove");
        removeEventButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Main.showMessage("Deleting event ...", false);
                applicationsService.delete(eventKey, new AsyncCallback<Void>() {
                    public void onSuccess(Void result) {
                        Main.hideMessage();
                        loadEvents();
                        Main.showMessage("Event deleted", true);
                    }

                    public void onFailure(Throwable caught) {
                        Main.hideMessage();
                        showError("Error deleting event: " + caught.getMessage());
                    }
                });
            }
        });
        eventTable.setWidget(row, 4, removeEventButton);
        eventTable.getCellFormatter().addStyleName(row, 4, "eventListRemoveColumn");
    }

    private void createEvent() {
        errorLabel.setVisible(false);

        final String title = eventNameTextBox.getText().trim();
        if (!title.matches("^[0-9a-zA-Z\\.\\-\\s]{3,20}$")) {
            showError("'" + title + "' is not a valid event title, allowed characters are 0-9, a-z, space, dot and dash (3 up to 20 characters).");
            eventNameTextBox.selectAll();
            return;
        }

        final Date startDate = eventStartDateBox.getValue();
        if (startDate == null) {
            showError("Please select a valid event start date.");
            return;
        }

        final Date expirationDate = eventEndDateBox.getValue();
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
        final int row = eventTable.getRowCount() + 1;

        Main.showMessage("Creating event ...", false);
        applicationsService.create(app, new AsyncCallback<Key>() {
            public void onSuccess(Key result) {
                Main.hideMessage();
                eventNameTextBox.setText(null);
                eventStartDateBox.setValue(null);
                eventEndDateBox.setValue(null);
                if (result != null) {
                    Main.showMessage("Event created", true);
                    app.setKey(result);
                    updateTableRow(app, row);
                    mainPanel.remove(createEventPanel);
                    mainPanel.add(newEventButton);
                } else {
                    showError("Failed to create event");
                }
            }

            public void onFailure(Throwable caught) {
                Main.hideMessage();
                showError("Error creating event: " + caught.getMessage());
            }
        });
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
