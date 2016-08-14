package com.mastek.POC;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

public class twitterSATopology {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// zookeeper hosts for the Kafka cluster
				ZkHosts zkHosts = new ZkHosts("localhost:2181");

				// Create the KafkaSpout configuration
				// Second argument is the topic name to read from
				// Third argument is the ZooKeeper root path for the spout to store the
				// consumer offsets
				// Fourth argument is consumer group id for storing the consumer offsets
				// in Zookeeper
				SpoutConfig kafkaConfig = new SpoutConfig(zkHosts,
						"kafkasink1", "/kafkasink1", "groupid");
				
				// Specify that the kafka messages are String
				kafkaConfig.scheme = new SchemeAsMultiScheme(new ParsetoJSON());
				
				// We want to consume all the first messages in
				// the topic every time we run the topology
				kafkaConfig.forceFromStart = true;
				
				// Now we create the topology
				TopologyBuilder builder = new TopologyBuilder();
				// set the kafka spout class
				builder.setSpout("kafkaspout", new KafkaSpout(kafkaConfig), 1);
				
				// set the transform bolt class
				builder.setBolt("FilterText", new FilterTweetsBolt(), 2)
						.shuffleGrouping("kafkaspout");
				
				//set the mongo bolt class
				builder.setBolt("MongoBolt", new MongoBolt(),1)
					.globalGrouping("FilterText");
				
				// create an instance of LocalCluster class
				// for executing topology in local mode.
				 LocalCluster cluster = new LocalCluster();

				Config conf = new Config();
				// Set the number of workers for this topologyy
				conf.setNumWorkers(3);

				  // Submit topology for execution
				  cluster.submitTopology("twitterPOCToplogy", conf,
				  builder.createTopology());
				  try { 
					  // Wait for some time before exiting
				  System.out.println("Waiting to consume from kafka");
				  Thread.sleep(35000); 
				  }
				  catch (Exception exception) {
					  
				  System.out.println("Thread interrupted exception : " + exception); 
				  }
		
	}

}
