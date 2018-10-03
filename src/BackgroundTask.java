import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.concurrent.Task;

/**
 * This class controls the background task execution - which is run on 
 * a separate thread to the GUI. It extends JavaFX 'Task' and overrides
 * 'call()' and 'cancel()'. 
 * @author carolinesmith
 *
 */
public class BackgroundTask extends Task<Integer> {

	/**
	 * This method overrides Javafx.concurrent.Task#call() method.
	 * It calls 'process_Commmand()' from Command.java to start
	 * shell command execution, and catches IOExceptions and Interrupted
	 * Exceptions. 
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected Integer call() {

		try {
			Command.process_Command(Command.command.toString());
		} 
		catch (InterruptedException e) { 
			e.printStackTrace();
			return null;
		}
		catch (IOException e){
			e.printStackTrace();
			return null;
		}
		System.out.println("done!");
		return null;
	}
	
	/**
	 * This methods override javafx.concurrent.Task#cancel() method.
	 * It prints out the cancelled message to assist in debugging. 
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		updateMessage("Cancelled!");
		System.out.println("cancelled");
		return super.cancel(mayInterruptIfRunning);
	}

	protected void updateProgress(double workDone) {
		updateMessage("progress!" + workDone);
		//super.updateProgress(workDone);
	}

}
