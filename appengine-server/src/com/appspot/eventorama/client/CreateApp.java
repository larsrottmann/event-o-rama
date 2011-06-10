package com.appspot.eventorama.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Button;

public class CreateApp extends PopupPanel {

    public CreateApp() {
        super(true);
        
        VerticalPanel verticalPanel = new VerticalPanel();
        setWidget(verticalPanel);
        verticalPanel.setSize("100%", "100%");
        
        Grid grid = new Grid(3, 2);
        verticalPanel.add(grid);
     //   grid.setSize("214px", "85px");
        
        Label lblEvent = new Label("Event");
        grid.setWidget(0, 0, lblEvent);
        
        TextBox textBox = new TextBox();
        grid.setWidget(0, 1, textBox);
        
        Label label = new Label("Start");
        grid.setWidget(1, 0, label);
        
        DateBox dateBox_1 = new DateBox();
        grid.setWidget(1, 1, dateBox_1);
        
        Label lblStart = new Label("End");
        grid.setWidget(2, 0, lblStart);
        
        DateBox dateBox = new DateBox();
        grid.setWidget(2, 1, dateBox);
        
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        verticalPanel.add(horizontalPanel);
        
        Button btnCreate = new Button("Create");
        horizontalPanel.add(btnCreate);
        btnCreate.setText("Create");
        
        Button btnCancel = new Button("Cancel");
        horizontalPanel.add(btnCancel);
        
        
    }

}
