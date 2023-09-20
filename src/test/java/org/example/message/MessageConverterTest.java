package org.example.message;

import org.example.TestData;
import org.example.exception.enrichment.IncorrectEnrichmentDataException;
import org.example.exception.enrichment.UnsupportedEnrichmentTypeException;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.TestData.*;
import static org.junit.Assert.*;

public class MessageConverterTest {

    @Test
    public void getValidMessageContentMap() {
        assertThat(MessageConverter.getValidMessageContentMap(TestData.CORRECT_VASYA_MESSAGE))
                .containsExactlyInAnyOrderEntriesOf(getNewStandardVasyaContentMap());
    }

    @Test
    public void getValidMessageContentMapWithTree() {
        Map<String, Object> content = MessageConverter.getValidMessageContentMap(CORRECT_VASYA_ENRICHED_MESSAGE);
        assertThat(content).containsExactlyInAnyOrderEntriesOf(getNewStandardEnrichedVasyaContentMap());
    }


    @Test
    public void getValidMessageContentMapInvalidEnrichmentType() {
        Message invalidJson = new Message("{\"action\": \"button_click\", " +
                "\"page\": \"book_card\", \"msisdn\": \"12345\"}",
                null);
        assertThrows(UnsupportedEnrichmentTypeException.class, () ->
                MessageConverter.getValidMessageContentMap(invalidJson));
    }

    @Test
    public void getValidMessageContentMapInvalidJson() {
        Message invalidJson = new Message("{\"action\": , " +
                ": \"book_card\", \"msisdn\": \"12345\"}",
                Message.EnrichmentType.MSISDN);
        assertThrows(IncorrectEnrichmentDataException.class, () ->
                MessageConverter.getValidMessageContentMap(invalidJson));
    }

    @Test
    public void getValidMessageContentMapEmptyMsisdn() {
        Message invalidJson = new Message("{\"action\": \"button_click\", " +
                "\"page\": \"book_card\", \"msisdn\": \"\"}",
                Message.EnrichmentType.MSISDN);
        assertThrows(IncorrectEnrichmentDataException.class, () ->
                MessageConverter.getValidMessageContentMap(invalidJson));
    }

    @Test
    public void getValidMessageContentMapNoMsisdn() {
        Message invalidJson = new Message("{\"action\": \"button_click\", " +
                "\"page\": \"book_card\"}",
                Message.EnrichmentType.MSISDN);
        assertThrows(IncorrectEnrichmentDataException.class, () ->
                MessageConverter.getValidMessageContentMap(invalidJson));
    }

    @Test
    public void getValidMessageContentMapMsisdnInTree() {
        Message invalidJson = new Message(
                "{\"action\": \"button_click\", \"page\": \"book_card\"," +
                        "\"enrichment\": { \"msisdn\": \"88005553535\", \"lastName\": \"Ivanov\"}",
                Message.EnrichmentType.MSISDN);
        assertThrows(IncorrectEnrichmentDataException.class, () ->
                MessageConverter.getValidMessageContentMap(invalidJson));
    }
}