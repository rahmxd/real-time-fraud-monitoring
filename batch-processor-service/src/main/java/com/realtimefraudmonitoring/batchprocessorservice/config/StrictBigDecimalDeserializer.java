package com.realtimefraudmonitoring.batchprocessorservice.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;

public class StrictBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);

        // Ensure the node is a numeric type and not a string
        if (!node.isNumber()) {
            throw new IOException("Invalid type for BigDecimal: Expected numeric value but got " + node.toString());
        }

        // Parse the BigDecimal value
        return new BigDecimal(node.asText());
    }
}
