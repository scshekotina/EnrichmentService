package org.example;

import org.example.user.UserStorage;
import org.junit.Before;
import org.junit.Test;

public class EnrichmentServiceTest {



    private EnrichmentService enrichmentService;

    @Before
    public void setUp() throws Exception {
        enrichmentService = new InMemoryStorageEnrichmentServiceFactory().createEnrichmentService();
        UserStorage userStorage = enrichmentService.getEnricher().getUserStorage();
        userStorage.add(TestData.getVasya());
        userStorage.add(TestData.getKolya());
    }

    @Test
    public void enrich() {
        String enriched = enrichmentService.enrich(TestData.CORRECT_VASYA);
        System.out.println(enriched);
    }
}