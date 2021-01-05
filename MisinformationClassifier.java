import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MisinformationClassifier {
	
	//Constants
	private static final String MY_SQL_HOST = System.getenv('MY_SQL_HOST');
	private static final String MY_SQL_USER = System.getenv('MY_SQL_USER');
	private static final String MY_SQL_PASSWORD = System.getenv('MY_SQL_PASSWORD');

    private ConcurrentHashMap<String, URLAndType> table;

    // Constructs a new MisinformationClassifier object using the given file name.
    public MisinformationClassifier(String databaseName, String tableName) {
        table = new ConcurrentHashMap<>();
		String sqlCommand = "SELECT * FROM " + tableName;
		readDatabase(databaseName, tableName, sqlCommand);
    }
	
	//Updates the table with a new database
	public void updateTable(String databaseName, String tableName) {
		String sqlCommand = "SELECT * FROM " + tableName + " WHERE newlyScraped=TRUE";
		readDatabase(databaseName, tableName, sqlCommand);
	}
	
	//Updates the table by reading from a table in a database using a sql command
	private void readDatabase(String databaseName, String tableName, String sqlCommand) {
		String connectionUrl = "jdbc:mysql://" + MY_SQL_HOST + "/" + databaseName;
        try (Connection mySqlConnection = DriverManager.getConnection(connectionUrl, MY_SQL_USER, MY_SQL_PASSWORD); 
        PreparedStatement ps = mySqlConnection.prepareStatement(sqlCommand); 
        ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {//Reads each row from the sql command and takes it into a HashMap.
				int category = rs.getInt("misinformationType");
				String link = rs.getString("url");
				String statement = rs.getString("quote");
				alterDatabase(connectionUrl, tableName, link);
				URLAndType urlAndType = new URLAndType(link, category);
				for(String sentence:processStatement(statement)) {
					//Adds each processed statement to the HashMap.
					table.put(sentence, urlAndType);
				}
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
        }
		table.remove("");
	}

	//Sets the newlyScraped value of added entries to false
	private void alterDatabase(String connectionUrl, String tableName, String link) {
		String sqlCommand = "UPDATE " + tableName + " SET newlyScraped = FALSE WHERE url = '" + link + "'";
        try (Connection mySqlConnection = DriverManager.getConnection(connectionUrl, MY_SQL_USER, MY_SQL_PASSWORD); 
        PreparedStatement ps = mySqlConnection.prepareStatement(sqlCommand); 
        ResultSet rs = ps.executeQuery()) {
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
        }
	}
	
    // Returns a URLAndType object which contains the relevant article link
    // and category number associated with the given text.
    public URLAndType getURLandType(String text) {
		//Looks for the processed sentence
		return table.get(processStatement(text)[0]);
    }
	
	//Processes any and all quotes
	private String[] processStatement(String statement) {
		String[] sentences = statement.split("[.!?:]");
		for(int i = 0; i < sentences.length; i++) {
			sentences[i] = sentences[i].trim().replaceAll("[^\\w\\s]", "").replaceAll("\\s+", " ");
		}
		return sentences;
	}
}
