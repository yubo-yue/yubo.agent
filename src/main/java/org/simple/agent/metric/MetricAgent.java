package org.simple.agent.metric;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricAgent {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetricAgent.class);
	
	public static void  premain(String agentArguments, Instrumentation instrumentation) {
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
	}
}
