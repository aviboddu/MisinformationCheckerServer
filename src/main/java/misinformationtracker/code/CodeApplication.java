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
    }

    @GetMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
    public URLAndType getQuery(@RequestParam(value = "s") String query) {
        return classifier.getURLandType(
                query.trim().replaceAll("[^\\w\\s]", "").replaceAll("\\s+", " "));

    }

    @Scheduled(fixedDelay = 86400000, initialDelay = 86400000)
    @Async
    public void updateData() {
        classifier.updateTable();//Updates the classifier
    }

}
