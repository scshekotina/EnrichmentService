package org.example;

import org.example.message.Message;
import org.example.message.storage.MessageStorageInMemory;
import org.example.user.UserStorage;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.TestData.*;

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
        String enriched = enrichmentService.enrich(CORRECT_VASYA_MESSAGE);
        assertThat(enriched).isEqualTo(CORRECT_VASYA_ENRICHED_MESSAGE.getContent());
        MessageStorageInMemory enrichedStorage = enrichmentService.getEnrichedMessageStorage();
        MessageStorageInMemory notEnrichedStorage = enrichmentService.getNotEnrichedMessageStorage();
        assertThat(enrichedStorage.get()).containsExactly(CORRECT_VASYA_ENRICHED_MESSAGE);
        assertThat(notEnrichedStorage.get().size()).isEqualTo(0);
    }

    @Test
    public void enrichIncorrect() {
        Message message = getStandardMessage("\"msisdn\":\"123123\"");
        String enriched = enrichmentService.enrich(message);
        Message expected = getStandardMessage("\"msisdn\":\"123123\"");
        assertThat(enriched).isEqualTo(expected.getContent());
        MessageStorageInMemory enrichedStorage = enrichmentService.getEnrichedMessageStorage();
        MessageStorageInMemory notEnrichedStorage = enrichmentService.getNotEnrichedMessageStorage();
        assertThat(notEnrichedStorage.get()).containsExactly(expected);
        assertThat(enrichedStorage.get().size()).isEqualTo(0);
    }
    @Test
    public void enrichTwoEntities() {
        Message notEnrichable = getStandardMessage("\"msisdn\":\"123123\"");
        String enrichedNotEnrichable = enrichmentService.enrich(notEnrichable);
        String enrichedEnrichable = enrichmentService.enrich(CORRECT_VASYA_MESSAGE);

        assertThat(enrichedNotEnrichable).isEqualTo(getStandardMessage("\"msisdn\":\"123123\"").getContent());
        assertThat(enrichedEnrichable).isEqualTo(CORRECT_VASYA_ENRICHED_MESSAGE.getContent());
        MessageStorageInMemory enrichedStorage = enrichmentService.getEnrichedMessageStorage();
        MessageStorageInMemory notEnrichedStorage = enrichmentService.getNotEnrichedMessageStorage();
        assertThat(notEnrichedStorage.get()).containsExactly(new Message(enrichedNotEnrichable, Message.EnrichmentType.MSISDN));
        assertThat(enrichedStorage.get()).containsExactly(CORRECT_VASYA_ENRICHED_MESSAGE);
    }
}