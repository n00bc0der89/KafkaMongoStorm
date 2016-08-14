package com.mastek.POC;

import java.util.List;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;


import backtype.storm.spout.Scheme;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class ParsetoJSON implements Scheme {

	 String tweet_created_at;
	  String tweet_id;
	  String tweet_id_str;
	  String tweet_text;
	  String tweet_source;
	  
	

	public Fields getOutputFields() {
		// TODO Auto-generated method stub
		return new Fields("tweet_text");
	}



	public List<Object> deserialize(byte[] bytes) {
		
		try
		{
		String twitterEvent = new String(bytes, "UTF-8");
		   //JSONArray JSON = new JSONArray(twitterEvent);
		     // for(int i=0;i <  JSON.length();i++) {
		        JSONObject object_tweet=new JSONObject(twitterEvent);
		//Tweet status                  
		          try{
		            this.tweet_created_at=object_tweet.getString("created_at");
		            this.tweet_text=object_tweet.getString("text");
		            this.tweet_source=object_tweet.getString("source");
		          }catch(Exception e){}
		  //  } 
		}
		catch(Exception ex){
			System.out.println(ex.toString());
		}
		
		  return new Values(tweet_text);
		  
	}

}
