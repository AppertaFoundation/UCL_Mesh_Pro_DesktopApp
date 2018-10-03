import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * This class is the Controller class for the main.fxml file.
 * It controls the flow of the GUI between different states of mesh
 * processing. 
 * @author carolinesmith
 *
 */
public class Main_Controller {

	public static String filePath, outPath, new_fileName, saved_File;
	public static String organ, file_Type;
	
	File file;
	Alert alert;
	Timeline timeline_Cancel = new Timeline(new KeyFrame(Duration.millis(420000), ae -> check_Finished()));
	
	@FXML
	Label file_Path_Label, status,  small_Update, comment2, cancel_Message;
	@FXML
	Label updateMessage;
	@FXML
	AnchorPane done_Anchor;
	@FXML
	ImageView gif_ImageView;
	@FXML
	ComboBox export_Type, organ_Selector;
	@FXML
	VBox web_ViewBox2, web_ViewBox1;
	@FXML
	Hyperlink hyperLink1, hyperLink2;
	@FXML
	Button cancel_Button, continue_Button;

//	BackgroundTask task;
	public static BackgroundTask task = new BackgroundTask();
	

	/**
	 * This method is called when the users clicks to upload a file
	 * It opens a file chooser and gets the path and file selected.
	 * If the file is not of type .stl an alert is presented to user
	 * and the operation aborted. 
	 * Otherwise the selection is used to set the file paths in the 
	 * shell command in Command.java
	 * 
	 */
	public void select_Mesh() {
		
		refresh_Page();

		final FileChooser fileChooser = new FileChooser();
		file = fileChooser.showOpenDialog(Main.thestage);

		if (file != null) {
			System.out.println(file.toString());
			filePath = file.toString();

			// Alert if file type is incorrect - must be .stl file
			if (!(filePath.substring((filePath.length() - 4), filePath.length()).equals(".stl"))) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning");
				alert.setHeaderText("Sorry that file type is not accepted");
				alert.setContentText("Please upload a .stl file for processing");
				alert.showAndWait();
				filePath = null;
			}

			else {
				filePath = file.toString();
				String[] outPath_Array = filePath.split("/");

				// get directory path (without file name)
				outPath = filePath.replace(outPath_Array[outPath_Array.length - 1], "");
				System.out.println(outPath);
				new_fileName = outPath_Array[outPath_Array.length - 1];

				file_Path_Label.setText(filePath);
			}
		}

	}

	/**
	 * This method checks that a file has been selected (and that the file paths 
	 * for the command are set). If they are it calls 'build_Command_From_Selection'
	 * in Command.java. It updates the GUI that mesh processing has been started - 
	 * stopwatch gif is presented and lables are set. Then after a second delay 
	 * (to allow GUI to be set) the background task is called (to process the mesh)
	 * 
	 */
	public void process_Mesh() {

		if (filePath != null) {

			file_Type = (String) export_Type.getSelectionModel().getSelectedItem();
			organ = (String) organ_Selector.getSelectionModel().getSelectedItem();
			
			Command.build_Command_From_Selection(file_Type, organ);

			updateMessage.setText("Thanks! Your mesh is being processed...");
			small_Update.setText("(This may take some time. Files over 100MB may take over 5minutes)");
			gif_ImageView.setVisible(true);
			gif_ImageView.setImage(new Image(this.getClass().getResource("Images/stopwtach.gif").toExternalForm()));

			// Call start_Background_Task() with slight delay - to allow above
			// labels to be set
			Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> start_Background_Task()));
			timeline.play();

		} else {

			// ALERT IF NO FILE SELECTED
			alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("A file has not been selected");
			alert.setContentText("Please upload a file for processing");
			alert.showAndWait();
		}

	}

	/**
	 * This method starts the background task by creating a new thread that takes 
	 * the static instance of BackgroundTask.java as parameter and calling 'start()'. 
	 * It checks for the status of the task on completion - if success then 'updateLabel_Succes()'
	 * is called, if IOException then 'handle_IOException()' is called, if InterruptedException
	 * then 'handle_InterruptedException()' is called. 
	 * A timeline (timeline_Cancel) is also played whilst the task is in execution - This 
	 * calls 'check_Finished' after 7 minutes if task is still in execution.
	 */
	public void start_Background_Task() {

		// progress_Bar.progressProperty().bind(task.progressProperty());
		// progress_Ind.progressProperty().bind(task.progressProperty());
		// status.textProperty().bind(task.messageProperty());

		new Thread(task).start();
		
		
		task.setOnSucceeded(e -> {
			if (Command.file_Problem == true) {
				handle_fileProblem();
				System.out.println("file problem");
			}
			else if (Command.cancelledIOError == true) {
				handle_IOException();
				System.out.println("picked up IO .... exception");
				
			}
			else if (Command.interrupted_Error == true) {
				handle_InterruptedExeption();
				System.out.println("picked up interrupted exception");
				
			}
			else {
				updateLabels_Success();
			}
			
		});

		task.setOnFailed(e -> {
			updateLabels_Failed();
		});
		
		timeline_Cancel.play();

	}
	
	/**
	 * This methods is called after 7 minutes of background task execution: if
	 * the task is still running the GUI presents to the user the options to 
	 * continue processing or cancel. If continue the timeline is played again. 
	 * If cancelled the timeline is stopped. 
	 */
	private void check_Finished() {
		if (task.isRunning()) {
			gif_ImageView.setVisible(false);
			
			cancel_Message.setVisible(true);
			cancel_Button.setVisible(true);
			continue_Button.setVisible(true);
			
		}
	}

	/**
	 * This method updates the GUI on successful completion of 
	 * mesh processing.
	 */
	private void updateLabels_Success() {
		
		updateMessage.setText("Success!");
		done_Anchor.setVisible(true);

		String message = "Your processed mesh has been saved in: ";
		saved_File = outPath + new_fileName;
		small_Update.setText(message);
		gif_ImageView.setVisible(false);
		hyperLink1.setText(saved_File);
		comment2.setText("View glb files");
		hyperLink2.setText("here");

	}

	/**
	 * This method updates the GUI on unsucesful completion of
	 * mesh proessing.
	 */
	private void updateLabels_Failed() {
		
		updateMessage.setText("Sorry there was a problem during processing. "
				+ "Please check you .stl file");
		refresh_Page();
	}

	/**
	 * This method opens a file chooser to show the user the 
	 * processing file after completion of processing. 
	 */
	public void show_File() {
		
		final FileChooser fileChooser2 = new FileChooser();
		fileChooser2.setInitialDirectory(new File(outPath));
		File file2 = fileChooser2.showOpenDialog(Main.thestage);
	}

	/**
	 * This methods open a gltf2 viewer in a browser where user can drag and 
	 * drop the new .glb file to view the mesh
	 * @return
	 */
	public Boolean open_glbViewer() {
		Boolean notOpen = false;
		
		try {
			Desktop.getDesktop().browse(new URI("https://gltf-viewer.donmccurdy.com/"));
		} catch (IOException e1) {
			notOpen = true;
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			notOpen = true;
		}
		return notOpen;
	}
	
	/**
	 * This method is called when the user chooses to continue with mesh processing/
	 * It is called from a button that is presented after 7 minutes processing. 
	 */
	public void continue_Processing() {
		
			gif_ImageView.setVisible(true);
			cancel_Button.setVisible(false);
			cancel_Message.setVisible(false);
			continue_Button.setVisible(false);
			
			timeline_Cancel = new Timeline(new KeyFrame(Duration.millis(420000), ae -> check_Finished()));
			timeline_Cancel.play();
		}
	
	/**
	 * This method is called when there is an Interrupted Exception during
	 * proecssing - it alerts the user through the GUI.
	 */
	public void handle_InterruptedExeption() {
		updateMessage.setText("Mesh Processing was interupted - please try again "
				+ "or check your .stl file");
		updateMessage.wrapTextProperty();
		gif_ImageView.setVisible(false);
		small_Update.setText("");
		file_Path_Label.setText("");
	}
	
	/**
	 * This method updates the GUI when there is a problem with the file 
	 * detected during mesh processing. 
	 */
	public void handle_fileProblem() {
		updateMessage.setText("There was a problem with the file. Please ensure "
				+ "valid .stl file.");
		updateMessage.wrapTextProperty();
		gif_ImageView.setVisible(false);
		small_Update.setText("");
		file_Path_Label.setText("");
		
	}
	
	/**
	 * This method updates the GUI when there is an IOException during mesh
	 * processing. 
	 */
	public void handle_IOException() {
		updateMessage.setText("There was a problem locating Blender or the Processing Script. "
				+ "Please ensure they are in the same folder as Mesh_Pro App");
		updateMessage.wrapTextProperty();
		gif_ImageView.setVisible(false);
		small_Update.setText("");
		file_Path_Label.setText("");
	}
	
	/**
	 * This method updates the GUI when the applciation is refreshed or a mesh
	 * processing is cancelled. It also resets all the static variables that build
	 * the shell command, stops the timeline that is played during processing and 
	 * calls 'cancel()' on the background task thread. 
	 */
	public void refresh_Page() {
		
		updateMessage.setText("");
		small_Update.setText("");
		file_Path_Label.setText("");
		done_Anchor.setVisible(false);
		filePath = null;
		outPath = null;
		new_fileName = null;
		hyperLink1.setText("");
		hyperLink2.setText("");
		comment2.setText("");
		new_fileName = null;
		saved_File = null;
		gif_ImageView.setVisible(false);
		cancel_Button.setVisible(false);
		cancel_Message.setVisible(false);
		continue_Button.setVisible(false);
		Command.file_Problem = false;
		Command.cancelledIOError = false;
		Command.interrupted_Error = false;
		
		// cancel thread
		System.out.println(task);
		task.cancel(true);
		timeline_Cancel.stop();
		task = new BackgroundTask();
		
	}

	/**
	 * This methods lays out the GUI on main.fxml loading and lays out the 
	 * second tab with a web view engine - that runs the Hololens web app from 
	 * Immanuel Baskaran: https://goshmhif.azurewebsites.net/Hololens-Webapp/#/add
	 */
	@FXML
	public void initialize() {
		
		done_Anchor.setVisible(false);
		cancel_Button.setVisible(false);
		cancel_Message.setVisible(false);
		continue_Button.setVisible(false);
		
		export_Type.getItems().addAll("fbx", "glb");
		organ_Selector.getItems().addAll("Skin", "Brain", "Lung", "Heart", "Bone");

		
		// Lays out the webview engine on third tab with hololens web app
		WebView web_View2 = new WebView();
		web_View2.setPrefHeight(783);
		web_View2.setPrefWidth(1108);
		WebEngine engine = web_View2.getEngine();
		engine.load("https://goshmhif.azurewebsites.net/Hololens-Webapp/#/add");
		engine.setJavaScriptEnabled(true);
		

		web_ViewBox2.getChildren().addAll(web_View2);

	}

}
