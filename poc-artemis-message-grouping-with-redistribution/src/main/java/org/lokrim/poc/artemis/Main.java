package org.lokrim.poc.artemis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.jms.JMSException;

public class Main {

	public static void main(String[] args) throws JMSException, InterruptedException {
		System.out.println("Broker1 URL: " + Constants.BROKER1_URL);
		System.out.println("Broker2 URL: " + Constants.BROKER2_URL);
		System.out.println("Topic: " + Constants.TOPIC);
		System.out.println("Queue: " + Constants.QUEUE);
		System.out.println("Number of messages produced by test producer: " + Constants.NUMBER_OF_MESSAGES);
		System.out.println("Time to conume a message by test consumer: " + Constants.TIME_TO_CONSUME_MESSAGE + "ms");
		System.out.println("Message grouping enabled: " + Constants.MESSAGE_GROUPING_ENABLED);
		System.out.println();

		ExecutorService threadPool = Executors.newCachedThreadPool();

		// Start 2 shared durable subcriptions on address 'poc-index-request' named
		// 'poc-index-request-queue' that consume messages in background, collecting
		// them in the messagesConsumed list
		Consumer consumer1 = new Consumer("consumer1", Constants.BROKER1_URL);
		final List<Integer> messagesConsumed = Collections.synchronizedList(new ArrayList<>());
		final List<Integer> messagesConsumed1 = new ArrayList<>();
		final List<Integer> messagesConsumed2 = new ArrayList<>();
		Future<Void> consumer1Future = threadPool.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				System.out.println("[consumer1] Consumer started");
				try {
					consumer1.consume(m -> {
						messagesConsumed.add(m);
						messagesConsumed1.add(m);
					});
				} catch (Exception e) {
					System.err.println("[consumer1] Consumer failed");
					e.printStackTrace();
				}
				return null;
			}
		});
		Consumer consumer2 = new Consumer("consumer2", Constants.BROKER2_URL);
		Future<Void> consumer2Future = threadPool.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				System.out.println("[consumer2] Consumer started");
				try {
					consumer2.consume(m -> {
						messagesConsumed.add(m);
						messagesConsumed2.add(m);
					});
				} catch (Exception e) {
					System.err.println("[consumer2] Consumer failed");
					e.printStackTrace();
				}
				return null;
			}
		});

		// Send 100 messages to the address
		Producer producer = new Producer("(" + Constants.BROKER1_URL + "," + Constants.BROKER2_URL + ")");
		List<Integer> messagesProduced = producer.produce(Constants.NUMBER_OF_MESSAGES,
				Constants.MESSAGE_GROUPING_ENABLED);

		// Wait a few seconds and kill one consumer
		Thread.sleep(Constants.TIME_TO_CONSUME_MESSAGE * 5);
		consumer1Future.cancel(true);

		// Wait for consumption to complete
		Thread.sleep((long) (Constants.TIME_TO_CONSUME_MESSAGE * Constants.NUMBER_OF_MESSAGES * 1.2));
		consumer1Future.cancel(true);
		threadPool.shutdownNow();

		// Assert that all messages where consumed
		System.out.println("Messages produced: " + messagesProduced.stream().sorted().toList());
		System.out.println("Messages consumed: " + messagesConsumed.stream().sorted().toList());
		System.out.println("Messages consumed (by consumer1): " + messagesConsumed1.stream().sorted().toList());
		System.out.println("Messages consumed (by consumer2): " + messagesConsumed2.stream().sorted().toList());
		if (messagesConsumed.containsAll(messagesProduced)) {
			System.out.println("SUCCESS: Every message was consumed: redistribution worked");
		} else {
			System.out.println("FAILURE: Not every message was consumed: redistribution did not work");
		}
	}

}
