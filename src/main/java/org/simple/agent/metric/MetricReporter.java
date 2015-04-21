package org.simple.agent.metric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;

public class MetricReporter {
	private final static Logger LOGGER = LoggerFactory.getLogger(MetricReporter.class);
	
	private static MetricRegistry metricRegistry = new MetricRegistry();
}
