import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.*;
import static java.lang.Integer.parseInt;

public class MisinformationClassifier {

    private ConcurrentHashMap<String, URLAndType> table;

    // Constructs a new MisinformationClassifier object using the given file name.
    public MisinformationClassifier(String fileName) {
        table = new ConcurrentHashMap<>();
        try (Scanner reader = new Scanner(new File(fileName))) {
            reader.nextLine();
            while (reader.hasNextLine()) {//Reads each line of the csv file and takes it into a HashMap.
                String arr[] = reader.nextLine().split(",");
                int length = arr.length;
                int category = parseInt(arr[length-1].replaceAll(".0", ""));
                String link = arr[length-2];
                String statement = arr[0];
                for (int i = 1; i < length-2; i++) {
                    statement += "," + arr[i];
                }
                URLAndType urlAndType = new URLAndType(link, category);
				for(String sentence:splitStatement(statement)) {
					//Adds each processed statement to the HashMap.
					table.put(sentence.trim().replace("\n", "").replace("\"","").replace("%",""), urlAndType);
				}
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    // Returns a URLAndType object which contains the relevant article link
    // and category number associated with the given text.
    public URLAndType getURLandType(String text) {
		//Looks for the processed sentence
      return table.get(text.trim().replace("\n", "").replace("\"","").replace("%",""));
    }
	
	private String[] splitStatement(String statement) {
		return statement.split("(?<=[.!?:])\\s");
	}
}
