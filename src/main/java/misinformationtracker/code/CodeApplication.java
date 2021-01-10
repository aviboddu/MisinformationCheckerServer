package misinformationtracker.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableScheduling
public class CodeApplication {

    private static MisinformationClassifier classifier;

    public static void main(String[] args) {
        classifier = new MisinformationClassifier();
        SpringApplication.run(CodeApplication.class, args);
        classifier.updateTable(true);
    }

    @GetMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getQuery(@RequestParam(value = "s") String query) {
        return JSON(classifier.getURLandType(
                query.trim().replaceAll("[^\\w\\s]", "").replaceAll("\\s+", " ")));

    }

    private String JSON(URLAndType urlAndType) {
        if(urlAndType == null)
            return "{}";
        return "{\"url\":\"" + urlAndType.getURL() + "\",\"type\":" + urlAndType.getCategory() + "}";
    }

    @GetMapping(value = "/list")
    public String listQueries() {
        return classifier.getList().toString();
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 86400000)
    @Async
    public void updateData() {
        classifier.updateTable(false);//Updates the classifier
    }

}
