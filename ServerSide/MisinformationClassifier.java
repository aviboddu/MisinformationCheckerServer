import java.util.*;

import java.io.FileReader;
import java.io.IOException;

public class MisinformationClassifier {
	private Map<String,URLAndType> database;
	private final Set<String> wordsToRemove;
	
	public MisinformationClassifier(String csvFile) {
		//Read the csv from original, filtering each statement
		//Fill up wordsToRemove with non-keywords (should be done carefully to avoid duplicates in the map)
	}
	
	public getURLandType(String statement) {
		statement = getKeywords(statement);
		return database.getOrDefault(statement,null);
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