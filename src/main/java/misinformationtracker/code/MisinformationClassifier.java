package misinformationtracker.code;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class MisinformationClassifier {

    private final String POLITIFACT_URL = "https://www.politifact.com/";
    private final String POLITIFACT_LIST_URL = "https://www.politifact.com/factchecks/list/?page=";

    private ConcurrentHashMap<String, URLAndType> table;

    public MisinformationClassifier() {
        table = new ConcurrentHashMap<>();
        updateTable();
    }

    //Updates the table by webscraping Politifact
    public void updateTable() {
        boolean reachedOldArticles = false;//If we've reached already found articles
        int oldPagesToCount = 10;//Number of old pages to check for updates
        for (int i = 1; i <= 686; i++) {
            try {
                Elements elements = processPage(POLITIFACT_LIST_URL + i);//Returns all articles on each page
                if (!elements.isEmpty()) {
                    for (Element e : elements) {//For each article
                        String[] sentences = processStatement(e.select("a").text());
                        URLAndType urlAndType = new URLAndType(POLITIFACT_URL +
                                e.select("a").attr("href"),
                                getType(e.select("img").attr("alt")));
                        if (urlAndType.getCategory() == -1)//Does not add any misinformation with invalid types
                            continue;
                        for (String sentence : sentences) {
                            //If we already added this article before.
                            if (!reachedOldArticles && !sentence.equals("") && table.containsKey(sentence))
                                reachedOldArticles = true;
                            table.put(sentence, urlAndType);
                        }
                    }
                } else {//If the elements are empty, we've reached the end anyway, and should stop reading
                    reachedOldArticles = true;
                    oldPagesToCount = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Checked Page Number " + i);
            if (reachedOldArticles)
                oldPagesToCount--;
            if (oldPagesToCount <= 0)
                break;
        }
        table.remove("");//Avoid odd bugs
    }

    //Returns the set of all articles on a given page
    private Elements processPage(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.getElementsByClass("m-statement__body");
    }


    //Processes any and all quotes
    public String[] processStatement(String statement) {
        String[] sentences = statement.split("[.!?:]");
        for (int i = 0; i < sentences.length; i++) {
            sentences[i] = sentences[i].trim().replaceAll("[^\\w\\s]", "")
                    .replaceAll("\\s+", " ");
        }
        return sentences;
    }

    //Converts the alt image text into a numerical representation of the misinformation type
    private int getType(String altText) {
        return switch (altText) {
            case "pants-fire" -> 0;
            case "false" -> 1;
            case "barely-true" -> 2;
            case "half-true" -> 3;
            case "mostly-true" -> 4;
            case "true" -> 5;
            default -> -1;
        };
    }

    // Returns a URLAndType object which contains the relevant article link
    // and category number associated with the given text.
    public URLAndType getURLandType(String text) {
        //Looks for the processed sentence
        return table.get(processStatement(text)[0]);
    }
}
