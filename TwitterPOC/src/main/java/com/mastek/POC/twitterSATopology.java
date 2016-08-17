package com.mastek.POC;

import java.util.Arrays;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
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
				ZkHosts zkHosts = new ZkHosts("172.16.210.27:5181"); //172.16.210.27 - MapR sandbox IP

				// Create the KafkaSpout configuration
				// Second argument is the topic name to read from
				// Third argument is the ZooKeeper root path for the spout to store the
				// consumer offsets
				// Fourth argument is consumer group id for storing the consumer offsets
				// in Zookeeper
				SpoutConfig kafkaConfig = new SpoutConfig(zkHosts,
						"kafkasink", "/kafkasink", "groupid");
				
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
				
				Config conf = new Config();
				
				// Set the number of workers for this topologyy
				conf.setNumWorkers(3);

			
				if(args.length > 0) //Pass argument to run on cluster
				{
					 try {
						 conf.put(Config.NIMBUS_HOST, "172.16.210.27");
						 conf.put(Config.NIMBUS_THRIFT_PORT,6627);
						 conf.put(Config.STORM_ZOOKEEPER_SERVERS,Arrays.asList(new String[]{"172.16.210.27"}));
						 conf.put(Config.STORM_ZOOKEEPER_PORT,5181);
						 
						 System.setProperty("storm.jar", "C:\\Users\\rohan440148\\.m2\\repository\\org\\apache\\storm\\storm-core\\0.9.2-incubating\\storm-core-0.9.2-incubating.jar");
							
						 StormSubmitter.submitTopology("twitterPOCToplogy", conf, builder.createTopology());
						} catch (AlreadyAliveException e) {
							
							System.out.println("Exception: "+ e.toString());
							e.printStackTrace();
							
						} catch (InvalidTopologyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
				else
				{
				// create an instance of LocalCluster class
				// for executing topology in local mode.
					LocalCluster cluster = new LocalCluster();
					  // Submit topology for execution
					  cluster.submitTopology("twitterPOCToplogy", conf,
					  builder.createTopology());
					
				}
				
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
