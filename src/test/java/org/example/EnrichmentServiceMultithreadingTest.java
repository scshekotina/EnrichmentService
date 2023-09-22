package org.example;

import lombok.AllArgsConstructor;
import org.example.message.Message;
import org.example.user.UserStorage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.TestData.*;

public class EnrichmentServiceMultithreadingTest {
    private EnrichmentService enrichmentService;

    @AllArgsConstructor
    public class Task implements Runnable {
        private String msisdn;

        @Override
        public void run() {
            String enriched = enrichmentService.enrich(getStandardMessage(msisdn));
            System.out.println("Message: " + msisdn + "; Enriched message: " + enriched);
        }
    }

    @Before
    public void setUp() {
        EnrichmentServiceFactory enrichmentServiceFactory = new InMemoryStorageEnrichmentServiceFactory();
        enrichmentService = enrichmentServiceFactory.createEnrichmentService();
        UserStorage userStorage = enrichmentService.getMsisdnEnricher().getUserStorage();
        userStorage.add(TestData.getVasya());
        userStorage.add(TestData.getKolya());
    }

    @Test
    public void enrich() throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            futures.add(executorService.submit(() ->
                enrichmentService.enrich(getStandardMessage(",\"msisdn\":\"" + (12340 + finalI %10) + "\""))
            ));
        }
        executorService.shutdown();

        while(futures.size() < 100) {
            Thread.sleep(300);
        }

        for (int i = 0; i < 100; i++) {
            String expected = getTestStringForI(i);
            assertThat(futures.get(i).get()).isEqualTo(expected);
        }

        List<Message> expectedEnrichedStorage = new ArrayList<>();
        List<Message> expectedNotEnrichedStorage = new ArrayList<>();
        Message enrichedMessage = new Message(getTestStringForI(5), Message.EnrichmentType.MSISDN);
        for (int i = 0; i < 100; i++) {
            if (i % 10 == 5) {
                expectedEnrichedStorage.add(enrichedMessage);
            } else {
                expectedNotEnrichedStorage.add(new Message(getTestStringForI(i), Message.EnrichmentType.MSISDN));
            }
        }

        assertThat(enrichmentService.getEnrichedMessageStorage().get()).containsExactlyInAnyOrderElementsOf(expectedEnrichedStorage);
        assertThat(enrichmentService.getNotEnrichedMessageStorage().get()).containsExactlyInAnyOrderElementsOf(expectedNotEnrichedStorage);

    }

    private String getTestStringForI(int i) {
        StringBuilder expected = new StringBuilder("{\"action\":\"button_click\",\"page\":\"book_card\"," +
                "\"msisdn\":\"" + (12340 + i % 10) + "\"");
        if (i%10 == 5) {
            expected.append(",\"enrichment\":{\"firstname\":\"Vasya\",\"lastname\":\"Petrov\"}");
        }
        expected.append("}");
        return expected.toString();
    }

}
