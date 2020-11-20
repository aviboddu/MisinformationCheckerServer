import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;
import static java.lang.Integer.parseInt;

public class MisinformationClassifier {

    private Hashtable<String, URLAndType> table;

    // Constructs a new MisinformationClassifier object using the given file name.
    public MisinformationClassifier(String fileName) {
        table = new Hashtable<>();
        try (Scanner reader = new Scanner(new File(fileName))) {
            reader.nextLine();
            while (reader.hasNextLine()) {
                String arr[] = reader.nextLine().split(",");
                int length = arr.length;
                int category = parseInt(arr[length-1].replaceAll(".0", ""));
                String link = arr[length-2];
                String statement = arr[0];
                for (int i = 1; i < length-2; i++) {
                    statement += "," + arr[i];
                }
                URLAndType urlAndType = new URLAndType(link, category);
                table.put(statement, urlAndType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    // Returns a URLAndType object which contains the relevant article link
    // and category number associated with the given text.
    public URLAndType getURLandType(String text) {
      return table.get(text.replace("\n", ""));
    }
    
	
    private String getKeywords(String statement) {
	String result = "";
	for(String word:statement.split("\\s+")) {
		if(!wordsToRemove.contains(word))
			result += word + " ";
	}
	return result.trim();
    }
}
