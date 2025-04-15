set -e

echo "### Grouping handlers configuration ###"
echo "CONFIG_INSTANCE_DIR=${CONFIG_INSTANCE_DIR}"

if grep -q "ss-0" ${CONFIG_INSTANCE_DIR}/etc/broker.xml;
then
	echo "Configuring LOCAL grouping-handler..."
	cp /amq/scripts/local-grouping-handler.xml ${CONFIG_INSTANCE_DIR}/etc/local-grouping-handler.xml
	includeGroupingHandler="<xi:include href=\"amq-broker/etc/local-grouping-handler.xml\"/>"
	connectorURL="tcp://10.0.1.2:61616"
#	connectorURL="tcp://artemis-broker-tcp-acceptor-0-svc-ing-activemq-artemis.localhost:80"
elif grep -q "ss-1" ${CONFIG_INSTANCE_DIR}/etc/broker.xml;
then
	echo "Configuring REMOTE grouping-handler..."
	cp /amq/scripts/remote-grouping-handler.xml ${CONFIG_INSTANCE_DIR}/etc/remote-grouping-handler.xml
	includeGroupingHandler="<xi:include href=\"amq-broker/etc/remote-grouping-handler.xml\"/>"
	connectorURL="tcp://10.0.1.2:61626"
#	connectorURL="tcp://artemis-broker-tcp-acceptor-1-svc-ing-activemq-artemis.localhost:80"
elif grep -q "ss-2" ${CONFIG_INSTANCE_DIR}/etc/broker.xml;
then
        echo "Configuring REMOTE grouping-handler..."
	cp /amq/scripts/remote-grouping-handler.xml ${CONFIG_INSTANCE_DIR}/etc/remote-grouping-handler.xml
	includeGroupingHandler="<xi:include href=\"amq-broker/etc/remote-grouping-handler.xml\"/>"
	connectorURL="tcp://10.0.1.2:61636"
#	connectorURL="tcp://artemis-broker-tcp-acceptor-2-svc-ing-activemq-artemis.localhost:80"
else
   echo "ERROR: This configuration allows at most 3 brokers"
   exit 1
fi

sed -i "s|</cluster-connections>|</cluster-connections>\n${includeGroupingHandler}|g" ${CONFIG_INSTANCE_DIR}/etc/broker.xml
sed -i -E "s|<connector name=\"artemis\">.+</connector>|<connector name=\"artemis\">$connectorURL</connector>|g" ${CONFIG_INSTANCE_DIR}/etc/broker.xml

#ls -la "${CONFIG_INSTANCE_DIR}"
#cat ${CONFIG_INSTANCE_DIR}/etc/broker.xml

echo "#### Grouping handlers configuration done. ####"
