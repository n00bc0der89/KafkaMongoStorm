package com.mastek.POC;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class MongoBolt extends BaseBasicBolt{

	private MongoClient mongoClient;
	private DB imdbMongo;
	private DBCollection movienameMongo;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context) {

		try {
			mongoClient = new MongoClient("localhost", 27017);
			imdbMongo = mongoClient.getDB("imdb");
			movienameMongo = imdbMongo.getCollection("moviename"); //Check mongo collection name here
		} catch (UnknownHostException e) {
			System.out
					.println("###############$$$$ -- Didn't get the connection to MongoDB");
		}

	}
	
	public void execute(Tuple input, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		String tweetmessage = input.getStringByField("tweetmessage");
		String sentimentRes = input.getStringByField("tweetsentiment");
		
		if(movienameMongo != null)
		{
			//Create Base object with values.
			
			BasicDBObject mongoObj = new BasicDBObject("Tweets", tweetmessage)
			.append("Sentiment", sentimentRes)
			.append("timestamp", new Date());
			
			//Add Object into collection
		 WriteResult writeresult =	movienameMongo.insert(mongoObj,WriteConcern.UNACKNOWLEDGED);
		 
		 System.out
			.println("Status of object inserted in Mongo Collection: "
					+ writeresult.getN());
			
		}
		
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

}
