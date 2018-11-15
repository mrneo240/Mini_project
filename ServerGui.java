/** ***************************
 *XX XX - XXX
 * CIST 2372-60273
 * Mini Project: Chat-Program, Client+Server chat program
 * This project implements a full chat server and client that can be used to send messages 
 * back and forth using sockets, all wrapped up in a nice gui
 * ServerGui.java - The gui implementation that displays the server manager and lets users connect
 * Copyright (C) 2018XX XX
 **************************** */
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.*;
import java.io.*;
import java.net.*;

public class ServerGui extends Application {

	private Scene scene;
    private BorderPane root;
    
    private ServerManager server;
	public ServerManager getServer(){
		return server;
    }
    
    String userName = "Server";
	
    @Override
    public void start(Stage primaryStage) {
        //Create main pane for each part of the gui to render
        root = new BorderPane();
        //Add fancy border
        root.setStyle("-fx-border-color: #000000;\n" +
                      "-fx-border-width: 1px;");

        //Add panes to the root pane
        root.setCenter(populateContentPane());
        root.setBottom(populateBottomPane());
        root.setTop(populateTopPane());

        //Setup a default scene for the panes to live on
        scene = new Scene(root, 400, 400);

        primaryStage.setTitle("Chat-Program Server");
        primaryStage.setScene(scene);
        //Disallow reszing and set plain window style
        primaryStage.setResizable(true);
        primaryStage.initStyle(StageStyle.UNIFIED);
        //Tada! Present it to the user
        primaryStage.show();

		(server = new ServerManager(this)).start();
		((Text) scene.lookup("#status")).setText("Server Started!");
        
        //Intercept window close request in order to ask user to confirm
        primaryStage.setOnCloseRequest((event) -> {
                //eat the event to prevent the app from closing even if cancelled.
                event.consume();
                //Show alert with confirmation options to the user
                Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to exit?");
                //wait for reply and handle appropiately.
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        //Close the app
						server.killAll();
                        Platform.exit();
                    }
                });
            }
        );   
    }
    
    //Used for sending message from GUI input to server then to all clients
	private void sendMessageToClients(TextField text){
		String message = text.getText();
		server.sendMessageToAllClients(message);
		addMessage(String.format("Server: %s",message));
	}
    
    //used by server or anyone to add text to the GUI
	public void addMessage(String text) {
		TextArea conversation = (TextArea)scene.lookup("#conversation");
		conversation.appendText(text+"\n");
	}

    //Returns a completely built pane ready to add to the scene.
    private Pane populateBottomPane() {
        //Create a new HBox with padding and styling
        HBox hbox = new HBox(4);
		hbox.setStyle("-fx-background-color: #cfcfc4;");
        hbox.setPadding(new Insets(8));
		
		//Data to be used by the Labels and their InputFields
        String[] promptText = new String[]{ "Enter message here..."};
        String[] toolTips = new String[]{"This will be sent to all Client!"};
        
        int i=0;
        for (String name : promptText) {
             
            //Create new input with prompt text to help user
            TextField input = new TextField();
			input.setId("input");
            input.setPromptText(promptText[i]);
            
            //Add a helpful tooltip to the input
            input.setTooltip(new Tooltip(toolTips[i]));
			
			input.setOnAction((event) -> {
				sendMessageToClients((TextField)event.getSource());
				((TextField)event.getSource()).clear();
			});
            hbox.getChildren().add(input);

            //Ensure scaling works
			HBox.setHgrow(input, Priority.ALWAYS);
			HBox.setHgrow(hbox, Priority.ALWAYS);
        }
        
        //Data to be used by the buttons and their tooltips
        String[] buttonNames = new String[]{"Send"};
        String[] tooltips = new String[]{ "Sends text to the clients!"};
        
        //temp vars
        Button btn = null;
        //loop through our button list and create them using that data
        for (String name : buttonNames) {
            btn = new Button();
            btn.setText(name);
            btn.setOnAction((event) -> {
                sendMessageToClients((TextField)scene.lookup("#input"));
				((TextField)scene.lookup("#input")).clear();
				
            });
            //Add tooltip to the button
            Tooltip tip = new Tooltip(tooltips[i++]);
            btn.setTooltip(tip);
            //add the button to our pane
            //paneButtons.getChildren().add(btn);
            hbox.getChildren().add(btn);
        }
        
        //return our newly built pane
        return hbox;
    }

    //returns a complete pane for use at the top of our scene
    private Pane populateTopPane() {
        
        //create new HBox for simple layout and add styling
        HBox pane = new HBox(4);
        pane.setStyle("-fx-background-color: #add8e6;");
        pane.setPadding(new Insets(8));
        
        //Create actual text label and add styling
        Text title = new Text("Chat-Program Server");
        title.setStyle("-fx-font: 24 arial;");
		Text status = new Text("waiting....");
		status.setId("status");

        //add text to the pane
        pane.getChildren().addAll(title,status);
        
        //return our newly built pane
        return pane;
    }

    //returns a complete pane for use at the top of our scene
    private Pane populateContentPane() {
		//Create the Textarea to hold our chat conversation
        TextArea textArea = new TextArea();
		textArea.setId("conversation");
		textArea.setEditable(false);
         //create new Vbox for simple layout and add styling

        VBox paneContent = new VBox(textArea);
		paneContent.setPadding(new Insets(8,8,8,8));
		VBox.setVgrow(textArea, Priority.ALWAYS);

        //return our newly built pane
        return paneContent;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
