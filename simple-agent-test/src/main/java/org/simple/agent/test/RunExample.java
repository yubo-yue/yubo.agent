package org.simple.agent.test;

import java.util.Random;

import org.simple.agent.metric.Measured;

public class RunExample {
	private Random random = new Random();
	@Measured
	public void doSleep() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
	}
	
	@Measured
	private void doTask() {
		try {
			Thread.sleep(random.nextInt(1000));
		} catch (InterruptedException e) {
		}
	}
	
	@Measured
	public void doWork() {
		for(int i = 0 ; i < random.nextInt(10) ; i++) {
			doTask();
		}
	}
	
	public static void main(String[] args) {
		RunExample test = new RunExample();
		while(true) {
			test.doWork();
			test.doSleep();
		}
	}
	

}
