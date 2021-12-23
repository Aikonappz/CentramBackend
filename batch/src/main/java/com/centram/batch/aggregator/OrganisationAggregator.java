package com.centram.batch.aggregator;


import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class OrganisationAggregator implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        return newExchange;
    }
}
