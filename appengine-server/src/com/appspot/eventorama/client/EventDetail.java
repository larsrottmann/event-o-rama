package com.appspot.eventorama.client;

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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventDetail extends Composite {

    private Key eventKey;

    private ApplicationsServiceAsync applicationsService = GWT.create(ApplicationsService.class);

    private VerticalPanel mainPanel = new VerticalPanel();
    private Label errorLabel = new Label();

    private Grid eventGrid = new Grid(4, 2);
    private Label nameLabel = new Label("Name");
    private Label nameValueLabel = new Label();
    private Label startLabel = new Label("Start Date");
    private Label startValueLabel = new Label();
    private Label endLabel = new Label("End Date");
    private Label endValueLabel = new Label();
    private Label linkLabel = new Label("Download");
    private Anchor linkAnchor = new Anchor();

    private HorizontalPanel eventButtonPanel = new HorizontalPanel();
    private Button backButton = new Button("Back");
    private Button removeButton = new Button("Remove");

    public EventDetail(Key key) {
        eventKey = key;

        errorLabel.setVisible(false);
        errorLabel.setStyleName("error", true);

        eventGrid.setWidget(0, 0, nameLabel);
        eventGrid.setWidget(0, 1, nameValueLabel);
        eventGrid.setWidget(1, 0, startLabel);
        eventGrid.setWidget(1, 1, startValueLabel);
        eventGrid.setWidget(2, 0, endLabel);
        eventGrid.setWidget(2, 1, endValueLabel);
        eventGrid.setWidget(3, 0, linkLabel);
        eventGrid.setWidget(3, 1, linkAnchor);

        nameLabel.setStyleName("eventElement", true);
        nameValueLabel.setStyleName("eventElement", true);
        startLabel.setStyleName("eventElement", true);
        startValueLabel.setStyleName("eventElement", true);
        endLabel.setStyleName("eventElement", true);
        endValueLabel.setStyleName("eventElement", true);
        linkLabel.setStyleName("eventElement", true);
        linkAnchor.setStyleName("eventElement", true);
        backButton.setStyleName("eventButton", true);
        removeButton.setStyleName("eventButton", true);

        eventButtonPanel.add(backButton);
        eventButtonPanel.add(removeButton);

        backButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                RootPanel.get("content_body").remove(EventDetail.this);
                RootPanel.get("content_body").add(new EventList());
            }
        });

        removeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                applicationsService.delete(eventKey, new AsyncCallback<Void>() {
                    public void onSuccess(Void result) {
                        RootPanel.get("content_body").remove(EventDetail.this);
                        RootPanel.get("content_body").add(new EventList());
                    }

                    public void onFailure(Throwable caught) {
                        showError("Error deleting event: " + caught.getMessage());
                    }
                });
            }
        });

        initWidget(mainPanel);
        mainPanel.add(errorLabel);
        mainPanel.add(eventGrid);
        mainPanel.add(eventButtonPanel);

        loadEvent();
    }

    private void loadEvent() {
        errorLabel.setVisible(false);

        applicationsService.get(eventKey, new AsyncCallback<Application>() {
            public void onSuccess(Application result) {
                updateGrid(result);
            }

            public void onFailure(Throwable caught) {
                showError("Error loading event: " + caught.getMessage());
            }
        });
    }

    private void updateGrid(Application result) {
        nameValueLabel.setText(result.getTitle());
        startValueLabel.setText(DateTimeFormat.getFormat("dd.MM.yyyy").format(result.getStartDate()));
        endValueLabel.setText(DateTimeFormat.getFormat("dd.MM.yyyy").format(result.getExpirationDate()));
        linkAnchor.setText(result.getLocalDownloadUrl());
        linkAnchor.setHref(result.getLocalDownloadUrl());
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
