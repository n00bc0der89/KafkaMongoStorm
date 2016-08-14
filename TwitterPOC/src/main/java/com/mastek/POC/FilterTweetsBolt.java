package com.mastek.POC;

import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class FilterTweetsBolt extends BaseBasicBolt{

	public void execute(Tuple input, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		// Get the message from the tuple
				String sentimentres = null;
				String message = input.getString(0);
				System.out.println("Tweets Text: "+ message);
				
				int score = NLP.findSentiment(message);
				
				//class is an integer number 0:very negative, 1:negative 2:neutral 3:positive and 4:very positive. 
				if(score < 2)
				{
					sentimentres = "Negative";
				}
				else if(score > 2)
				{
					sentimentres = "Positive";
				}
				else
				{
					sentimentres = "Neutral";
				}
			 System.out.println("SCore : " + message + " : " + sentimentres);
			 
			 collector.emit(new Values(message,sentimentres));
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		try
		{
			NLP.init();
		}
		catch(Exception ex)
		{
			System.out.println(ex.toString());
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("tweetmessage", "tweetsentiment"));
	}

}
