import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class from which the start method is executed and 
 * the main scene launched on the stage.
 * @author carolinesmith
 *
 */
public class Main extends Application {

	public static Stage thestage;
	public static String blender_Path;

	/**
	 * 
	 * The start method instantiates the primary stage and loads the main.fml file.
	 * Gets the absolute path of the directory jar file is located to 
	 * pass to Command.java to build the shell command - this is needed
	 * to point to the location of the Blender program (which should be in 
	 * the same directory as the jar file.
	 * @author carolinesmith
	 *
	 */
	@Override
	public void start(Stage primaryStage) {
		try {

			thestage = primaryStage;

			//Gets the absolute path of this application on user's system
			
			String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			System.out.println(decodedPath);
			
			String file3 = new File(decodedPath).getParentFile().getPath();
			blender_Path = file3;
			System.out.println(blender_Path);

			//Loads the main scene from fxml
			Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
			Scene scene = new Scene(root, 1108, 783);
			thestage.setScene(scene);
			thestage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * The main method that calls the start methods on application loading.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	

}
