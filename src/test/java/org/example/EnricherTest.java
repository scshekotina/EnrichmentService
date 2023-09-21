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
    public void setUp(){
        enricher = new Enricher(getStandardUserStorage());
    }

    @Test
    public void enrich() {
        Message enriched = enricher.enrich(CORRECT_VASYA_MESSAGE);
        assertThat(enriched).isEqualTo(CORRECT_VASYA_ENRICHED_MESSAGE);
    }

    @Test
    public void enrichWithEnrichment() {
        Message message = getStandardMessage("\"msisdn\" : \"12345\"",
                "\"enrichment\": {\"firstname\":\"Lyucya\",\"lastname\":\"Weisman\"}",
                "\"tree value\":{\"first\":\"first value\",\"second tree value\":" +
                "{\"one\":\"one\",\"two\":\"two\"}}");
        Message enriched = enricher.enrich(message);
        Message expected = getStandardMessage("\"msisdn\" : \"12345\"",
                "\"enrichment\": {\"firstname\":\"Lyucya\",\"lastname\":\"Weisman\"}",
                "\"tree value\":{\"first\":\"first value\",\"second tree value\":" +
                        "{\"one\":\"one\",\"two\":\"two\"}}");
        assertThat(enriched).isEqualTo(expected);
    }

    @Test
    public void enrichWithEmptyEnrichment() {
        Message message = getStandardMessage("\"msisdn\":\"12345\",",
                "\"enrichment\":\"\"}");
        Message enriched = enricher.enrich(message);
        assertThat(enriched).isEqualTo(CORRECT_VASYA_ENRICHED_MESSAGE);
    }

    @Test
    public void enrichNotFoundMsisdn() {
       Message message = getStandardMessage();
        assertThrows(IncorrectEnrichmentDataException.class, () -> enricher.enrich(message));
    }

    @Test
    public void enrichIncorrectEnrichmentType() {
        Message message = new Message(getStandardMessage().getContent(), null);
        assertThrows(UnsupportedEnrichmentTypeException.class, () -> enricher.enrich(message));
    }

    @Test
    public void enrichCouldNotFindMsisdn() {
        Message message = getStandardMessage("\"msisdn\":\"000\"");
        assertThrows(InvalidEnrichmentException.class, () -> enricher.enrich(message));
    }

}