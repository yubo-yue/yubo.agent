package org.simple.agent.metric;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

public class MetricReporter {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(MetricReporter.class);

	private static MetricRegistry metricRegistry = new MetricRegistry();

	public static void startJmxReporter() {
		LOGGER.info("Init JMX report");
		JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.convertRatesTo(TimeUnit.MINUTES).build();
		
		jmxReporter.start();
		
	}
	
	public static void startGraphiteReporter(String host, int port, String prefix) {
		LOGGER.info("Init Graphite reporter: host={} port={} prefix={}", host, port, prefix);
		Graphite graphite = new Graphite(new InetSocketAddress(host, port));
		GraphiteReporter graphiteReport = GraphiteReporter.forRegistry(metricRegistry).prefixedWith(prefix).build(graphite);
		graphiteReport.start(1, TimeUnit.MINUTES);
	}
	
	public static void reportTime(String name, long timeInMs) {
		Timer timer = metricRegistry.timer(name);
		timer.update(timeInMs, TimeUnit.MILLISECONDS);
	}
}
