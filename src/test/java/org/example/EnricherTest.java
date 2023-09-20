package org.example;

import org.example.exception.enrichment.IncorrectEnrichmentDataException;
import org.example.exception.enrichment.InvalidEnrichmentException;
import org.example.exception.enrichment.UnsupportedEnrichmentTypeException;
import org.example.message.Message;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.TestData.*;
import static org.junit.Assert.assertThrows;

public class EnricherTest {

    private Enricher enricher;

    @Before
    public void setUp() throws Exception {
        enricher = new Enricher(getStandardUserStorage());
    }

    @Test
    public void enrich() {
        Message enriched = enricher.enrich(CORRECT_VASYA_MESSAGE);
        assertThat(enriched).isEqualTo(CORRECT_VASYA_ENRICHED_MESSAGE);
    }

    @Test
    public void enrichWithEnrichment() {
        Message message = new Message("{\"action\":\"button_click\", " +
                "\"page\":\"book_card\",\"msisdn\":\"12345\"," +
                "\"enrichment\":{\"firstname\":\"Lyucya\",\"lastname\":\"Weisman\"}," +
                "\"tree value\":{\"first\":\"first value\",\"second tree value\":" +
                "{\"one\":\"one\",\"two\":\"two\"}}}",
                Message.EnrichmentType.MSISDN);
        Message enriched = enricher.enrich(message);
        Message expected = new Message("{\"action\":\"button_click\"," +
                "\"page\":\"book_card\",\"msisdn\":\"12345\"," +
                "\"tree value\":{\"first\":\"first value\",\"second tree value\":" +
                "{\"one\":\"one\",\"two\":\"two\"}}," +
                "\"enrichment\":{\"firstname\":\"Vasya\",\"lastname\":\"Petrov\"}}",
                Message.EnrichmentType.MSISDN);
        assertThat(enriched).isEqualTo(expected);
    }

    @Test
    public void enrichWithEmptyEnrichment() {
        Message message = new Message("{\"action\":\"button_click\"," +
                "\"page\":\"book_card\",\"msisdn\":\"12345\"," +
                "\"enrichment\":\"\"}",
                Message.EnrichmentType.MSISDN);
        Message enriched = enricher.enrich(message);
        assertThat(enriched).isEqualTo(CORRECT_VASYA_ENRICHED_MESSAGE);
    }

    @Test
    public void enrichNotFoundMsisdn() {
       Message message = new Message("{\"action\":\"button_click\"," +
                "\"page\":\"book_card\"}",
                Message.EnrichmentType.MSISDN);
        assertThrows(IncorrectEnrichmentDataException.class, () -> enricher.enrich(message));
    }

    @Test
    public void enrichIncorrectEnrichmentType() {
        Message message = new Message("{\"action\":\"button_click\"," +
                "\"page\":\"book_card\"}", null);
        assertThrows(UnsupportedEnrichmentTypeException.class, () -> enricher.enrich(message));
    }

    @Test
    public void enrichCouldNotFindMsisdn() {
        Message message = new Message("{\"action\":\"button_click\"," +
                "\"page\":\"book_card\",\"msisdn\":\"000\"}",
                Message.EnrichmentType.MSISDN);
        assertThrows(InvalidEnrichmentException.class, () -> enricher.enrich(message));
    }

}