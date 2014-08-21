package twitterData;

// Use Boon Parser for faster reading
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class converse {
	
	public static String result(LinkedHashMap<String, String> index, String key) {
		if(index.get(key) == null)
			return null;
//		return key;
		String ans;
		if(index.get(key).equals("null")){
			return key;
		}
		else{
			ans = key;
			String replies = index.get(key);
			String[] nodes = replies.split(":");
			int i=0;
			System.out.println("Length " + nodes.length);
			for(i=1;i<nodes.length;i++){
				String itKey = nodes[i];
				String sth = result(index, itKey);
				ans = ans + "(" + sth + ")";
			}
			return ans;
		}
	}
	public static void main(String[] args) throws IOException, java.text.ParseException {
		// TODO Auto-generated method stub
		File file = new File("yesData");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		
		TreeMap<Date, String> reply = new TreeMap<Date, String>();
		HashMap<String, String> idToTweet = new HashMap<String, String>();

		
		JSONParser parser = new JSONParser();
		while ((line = br.readLine()) != null) {
			// process the line.
			String id = null;
			String replyId = null;
			String time = null;
			String tweet = null;
		    ContainerFactory containerFactory = new ContainerFactory(){
		    	public List creatArrayContainer() {
					return new LinkedList();
				}
				public Map createObjectContainer() {
					return new LinkedHashMap();
				}                    
			};
			                
			try{
				Map tweetJson = (Map)parser.parse(line, containerFactory);
				Iterator iter = tweetJson.entrySet().iterator();
			    
//				System.out.println("==iterate result==");
			    
				while(iter.hasNext()){
			      Map.Entry entry = (Map.Entry)iter.next();
			      if(entry.getKey().toString().equals("Data")){
//			    	  System.out.println(entry.getKey() + "=>" + entry.getValue());
			    	  Map dataJson = (Map)parser.parse(entry.getValue().toString(), containerFactory);
			    	  Iterator diter = dataJson.entrySet().iterator();
			    
			    	  while(diter.hasNext()){
			    		  Map.Entry dentry = (Map.Entry)diter.next();
			    		  if(dentry.getKey().toString().equals("IdStr")){
//			    			  System.out.println(dentry.getKey() + "=>" + dentry.getValue());
			    			  id = dentry.getValue().toString();
//			    			  mapId = id;
			    		  }
			    		  if(dentry.getKey().toString().equals("InReplyToStatusIdStr")){
//			    			  System.out.println(dentry.getKey() + "=>" + dentry.getValue());
			    			  if(dentry.getValue() != null){
			    				  replyId = dentry.getValue().toString();  
			    			  } 
			    		  }
			    		  if(dentry.getKey().toString().equals("CreatedAt")){
//			    			  System.out.println(dentry.getKey() + "=>" + dentry.getValue());
			    			  if(dentry.getValue() != null){
			    				  time = dentry.getValue().toString();
			    			  }
			    		  }
			    		  if(dentry.getKey().toString().equals("Text")){
//			    			  System.out.println(dentry.getKey() + "=>" + dentry.getValue());
			    			  if(dentry.getValue() != null){
			    				  tweet = dentry.getValue().toString();
			    			  }
			    		  }
			    	   }
			        }
			    }
			}
			catch(ParseException pe){
//				System.out.println(pe);
			}
			//Mapping ID to TWEET
			idToTweet.put(id, tweet);
			
			String concat = id+":"+replyId;
			if(time != null){
//				Date date = time;
				Date date = new SimpleDateFormat("EEE MMM d H:m:s Z yyyy").parse(time);
				reply.put(date, concat);
			}
		}
		br.close();
//		sortedReply.putAll(reply);
		
//		Iterator<Date> keySetIterator = reply.keySet().iterator();
//
//		while(keySetIterator.hasNext()){
//		  String key = keySetIterator.next();
//		  if(reply.get(key)!= null){
//			  System.out.println("time: " + key + " reply: " + reply.get(key));
//		  }
//		}
		LinkedHashMap<String, String> index = new LinkedHashMap<String, String>();
		int counter = 0;
		int countNulls = 0;
		for(Map.Entry<Date,String> entry : reply.entrySet()) {
			  String key = entry.getKey().toString();
			  String value = entry.getValue();

//			  System.out.println(key + " => " + value);
			  String[] array = value.split(":");
//			  System.out.println(array[0] + " and " + array[1]);
			  
//			  index.put(array[0], array[1]);
			  if(!array[1].equals("null") && index.containsKey(array[1])){
				  counter++;
				  String replyValue = index.get(array[1]);
				  replyValue = replyValue + ":" + array[0];
				  if(replyValue != null){
					  index.put(array[1], replyValue);
				  }
				  index.put(array[0], "null");
				  System.out.println(index.get(array[1]));
//				  System.out.println("------------------------------");
			  }
			  System.out.println(array[1]);
			  if(array[1].equals("null")){
				  countNulls++;
//				  System.out.println("-------------YES-----------------");
				  index.put(array[0], array[1]);
			  }
			  
		}
		
		// Printing conversation tree
		for(Map.Entry<String, String> entry : index.entrySet()) {
			String IdStr = entry.getKey().toString();
			String ReplyIdStr = entry.getValue();
			
			System.out.println("Answer");
			String ans = result(index, IdStr);
			System.out.println(ans);
		}
		System.out.println(counter);
		System.out.println(countNulls);
		
		// ChatBot
//		Scanner in = new Scanner(System.in);
//		String s;
//		while((s = in.nextLine()) != null){
//			System.out.println("You entered: " + s);
//		}
	}
}
