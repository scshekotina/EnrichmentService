package org.example;

import org.example.exception.IncorrectEnrichmentDataException;
import org.example.exception.InvalidEnrichmentException;
import org.example.exception.UnsupportedEnrichmentTypeException;
import org.example.message.Message;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.example.TestData.getNewStandardEnrichedVasyaContentMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import static org.example.TestData.getStandardUserStorage;

public class EnricherTest {

    private Enricher enricher;

    @Before
    public void setUp() throws Exception {
        enricher = new Enricher(getStandardUserStorage());
    }

    @Test
    public void enrich() {
        Map<String, String> content = TestData.getNewStandardVasyaContentMap();
        Map<String, String> enriched = enricher.enrich(content, Message.EnrichmentType.MSISDN);
        assertThat(enriched).containsExactlyInAnyOrderEntriesOf(getNewStandardEnrichedVasyaContentMap());
    }

    @Test
    public void enrichNotFoundMsisdn() {
        Map<String, String> content = TestData.getNewStandardVasyaContentMap();
        content.remove("msisdn");
        assertThrows(IncorrectEnrichmentDataException.class, () -> enricher.enrich(content, Message.EnrichmentType.MSISDN));
    }

    @Test
    public void enrichIncorrectEnrichmentType() {
        Map<String, String> content = TestData.getNewStandardVasyaContentMap();
        assertThrows(UnsupportedEnrichmentTypeException.class, () -> enricher.enrich(content, null));
    }

    @Test
    public void enrichCouldNotFindMsisdn() {
        Map<String, String> content = TestData.getNewStandardVasyaContentMap();
        content.put("msisdn", "000");
        assertThrows(InvalidEnrichmentException.class, () -> enricher.enrich(content, Message.EnrichmentType.MSISDN));
    }

}