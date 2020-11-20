public class URLAndType {
 
    private final String url;
    private final int category;
    
    // Constructs a URLAndType object which contains the relevant article link the category number of misinformation.
    public URLAndType(String url, int category) {
        this.url = url;
        this.category = category;
    }
  
    // Returns the relevant article link.
    public String getURL() {
        return url;
    }

    // Returns the category number of misinformation.
    public int getCategory() {
        return category;
    }

}
