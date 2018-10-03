import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * This class contains methods and attributes that help to build the shell
 * command using the file selected for processing and executes the shell 
 * command - catching various exceptions. 
 * @author carolinesmith
 *
 */
public class Command {

	public static StringBuffer command;
	public static StringBuffer output;
	public static Process p;
	public static boolean file_Problem;
	public static boolean cancelledIOError;
	public static boolean interrupted_Error;

	/**
	 * This static method takes the user input - file selected for processing and
	 * the organ type and file type - it builds the shell command that will 
	 * be called to execute the processing script in Blender using this information
	 * and creates a new file name for the processing mesh. 
	 * @param filetype  String that specifies file format to convert .stl.
	 * 					This is either 'fbx' or 'glb' and is set via 
	 * 					combobox in GUI. Default is 'glb'
	 * @param organ     String that specifies type of organ the mesh is.
	 * 					This controls the material that is applied. 
	 */
	public static void build_Command_From_Selection(String filetype, String organ) {

		if (organ == null) {
			organ = "Lung";
		}
		if (filetype == null) {
			filetype = "glb";
		}

		// create new file name with .fbx or .glb extension
		Main_Controller.new_fileName = Main_Controller.new_fileName.substring(0,
				(Main_Controller.new_fileName.length() - 3));
		if (filetype == "fbx") {
			Main_Controller.new_fileName += "fbx";
		} else {
			Main_Controller.new_fileName += "glb";
		}
		System.out.println(Main_Controller.new_fileName);

		// CREATE THE STRING COMMAND USING THE UPLOADED FILE
		output = new StringBuffer();

		command = new StringBuffer();
		command.append(Main.blender_Path);
		command.append("/Blender/blender.app/Contents/MacOS/blender --background --python ");
	    command.append(Main.blender_Path);
	    command.append("/decimate.py -- ");
	    System.out.println(command.toString());
		
		command.append(Main_Controller.filePath);
		command.append(" ");
		command.append(Main_Controller.outPath);
		command.append(Main_Controller.new_fileName);
		command.append(" ");
		command.append(organ);
		command.append(" ");
		command.append(filetype);

	}

	/**
	 * This static method takes the String command passed to it and executes this
	 * in shell by calling Runtime.getRuntime().exec(command).
	 * It checks for output from the shell and if not as expected - alerts user
	 * about file problem. It also catches IOException (If Blender or the processing
	 * script cannot be found) and InteruptedException.
	 * 
	 * @param command2  The String shell command that will be executed.
	 * @return          The String output from shell.
	 * @throws InterruptedException  
	 * @throws IOException
	 */
	public static String process_Command(String command2) throws InterruptedException, IOException {

		StringBuffer s = new StringBuffer();

		try {
			p = Runtime.getRuntime().exec(command2);
			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				s.append(line);

			}
		    System.out.println(s.toString());
			String output = s.toString();
			
			if (!output.contains("Finished") ) {
				file_Problem = true;
				System.out.println("problem with file");
			}
			
			
			p.destroy();

		} catch (InterruptedException e) {
			interrupted_Error = true;
		} 
		catch (IOException e) {
			cancelledIOError = true;
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return s.toString();
		//return "\n";

	}

}
