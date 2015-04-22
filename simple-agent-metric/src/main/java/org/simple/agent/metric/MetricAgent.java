package org.simple.agent.metric;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricAgent {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MetricAgent.class);

	public static void premain(String agentArguments,
			Instrumentation instrumentation) {
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		LOGGER.info("Runtime: {}: {}", runtimeMxBean.getName(),
				runtimeMxBean.getInputArguments());
		LOGGER.info("Starting agent with arguments {}", agentArguments);

		MetricReporter.startJmxReporter();

		if (agentArguments != null) {
			Map<String, String> properties = new HashMap<String, String>();
			for (String propertyAndValue : agentArguments.split(",")) {
				String[] tokens = propertyAndValue.split(":", 2);
				if (tokens.length != 2) {
					continue;
				}
				properties.put(tokens[0], tokens[1]);
			}

			String graphiteHost = properties.get("graphite.host");
			if (null != graphiteHost) {
				int graphitePort = 2003;
				String graphitePrefix = properties.get("graphite.prefix");
				if (graphitePrefix == null) {
					graphitePrefix = "test";
				}
				String graphitePortString = properties.get("graphite.port");
				if (graphitePortString != null) {
					try {
						graphitePort = Integer.parseInt(graphitePortString);
					} catch (Exception e) {
						LOGGER.info("Invalid graphite port {}: {}",
								e.getMessage());
					}
				}
				MetricReporter.startGraphiteReporter(graphiteHost,
						graphitePort, graphitePrefix);
			}
		}
		
		instrumentation.addTransformer(new TimedClassTransformer());
	}
}
