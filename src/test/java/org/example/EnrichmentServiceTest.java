package org.example;

import org.example.message.Message;
import org.example.message.storage.MessageStorageInMemory;
import org.example.user.UserStorage;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnrichmentServiceTest {

    private EnrichmentService enrichmentService;

    @Before
    public void setUp() {
        EnrichmentServiceFactory enrichmentServiceFactory = new InMemoryStorageEnrichmentServiceFactory();
        enrichmentService = enrichmentServiceFactory.createEnrichmentService();
        UserStorage userStorage = enrichmentService.getMsisdnEnricher().getUserStorage();
        userStorage.add(TestData.getVasya());
        userStorage.add(TestData.getKolya());
    }

    @Test
    public void enrich() {
        String enriched = enrichmentService.enrich(TestData.CORRECT_VASYA_MESSAGE);
        assertThat(enriched).isEqualTo(TestData.CORRECT_VASYA_ENRICHED_MESSAGE.getContent());
        MessageStorageInMemory enrichedStorage = enrichmentService.getEnrichedMessageStorage();
        MessageStorageInMemory notEnrichedStorage = enrichmentService.getNotEnrichedMessageStorage();
        assertThat(enrichedStorage.get()).containsExactly(TestData.CORRECT_VASYA_ENRICHED_MESSAGE);
        assertThat(notEnrichedStorage.get().size()).isEqualTo(0);
    }

    @Test
    public void enrichIncorrect() {
        Message message = new Message("{\"action\":\"button_click\"," +
                "\"page\":\"book_card\",\"msisdn\":\"123123\"}",
                Message.EnrichmentType.MSISDN);
        String enriched = enrichmentService.enrich(message);
        Message expected = new Message("{\"action\":\"button_click\"," +
                "\"page\":\"book_card\",\"msisdn\":\"123123\"}",
                Message.EnrichmentType.MSISDN);
        assertThat(enriched).isEqualTo(expected.getContent());
        MessageStorageInMemory enrichedStorage = enrichmentService.getEnrichedMessageStorage();
        MessageStorageInMemory notEnrichedStorage = enrichmentService.getNotEnrichedMessageStorage();
        assertThat(notEnrichedStorage.get()).containsExactly(expected);
        assertThat(enrichedStorage.get().size()).isEqualTo(0);
    }
}