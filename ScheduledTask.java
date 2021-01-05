import java.util.TimerTask;
import java.util.Date;
import java.lang.ProcessBuilder;
import java.lang.Exception;

//Task to repeatedly do
public class ScheduledTask extends TimerTask {
	private MisinformationClassifier classifier;
	private String databaseName;
	private String tableName;
	
	public ScheduledTask(MisinformationClassifier inputClassifier, String dbName, String tName) {
		classifier = inputClassifier;
		databaseName = dbName;
		tableName = tName;
	}
	
	public void run(){
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("python", "pythonWebScraper/PythonWebscraper.py");//Runs the python script to update the database
			Process process = processBuilder.start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		classifier.updateTable(databaseName, tableName);//Once the python script is complete, updates the classifier from the database
	}
}