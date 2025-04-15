package org.lokrim.poc.artemis;

public final class Constants {

	/**
	 * Flag to control whether messages sent by the producer include groupId or not
	 */
	public static final boolean MESSAGE_GROUPING_ENABLED = true;

	/**
	 * The name of the address (topic)
	 */
	public static final String TOPIC = "poc-index-request";

	/**
	 * The name of the shared subscription queue.
	 */
	public static final String QUEUE = "poc-index-request-queue";

	/**
	 * The name of the groupId property in a message.
	 */
	public static final String PROPERTY_GROUPID = "_AMQ_GROUP_ID";

	/**
	 * The name of the id property in a message.
	 */
	public static final String PROPERTY_ID = "id";

	/**
	 * The time (in milliseconds) the consumer will take to "process" a message.
	 */
	public static final long TIME_TO_CONSUME_MESSAGE = 700;

	/**
	 * The number of messages produced in a single run to the Main.
	 */
	public static final int NUMBER_OF_MESSAGES = 40;

	/**
	 * The URL of the first node of the cluster.
	 */
	public static final String BROKER1_URL = "tcp://10.0.1.2:61616";

	/**
	 * The URL of the second node of the cluster.
	 */
	public static final String BROKER2_URL = "tcp://10.0.1.2:61626";

	private Constants() {
	}

}
