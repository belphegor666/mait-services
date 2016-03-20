package org.tmr.rest;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;

import org.bson.BSON;
import org.bson.Document;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Startup
@Singleton
public class MongoBean {
	
	final String ICM = "IncidentCreationMessages";
	final String IUM = "IncidentUpdateMessages";
	
	final String URN = "OrigIncidentURN";
	final String MSG_ID = "MessageId";
	
	private MongoClient mongoClient;
	
	private MongoCollection icmColl;
	private MongoCollection iumColl;
	
	public MongoBean() {
		super();
		setup();
	}
	
	public void setup() {
		String vcap_services = System.getenv("VCAP_SERVICES");
		String mongoURI = "";
		String dbname = "";
		
		JSONObject vcapJSON = new JSONObject();
		try{
			vcapJSON = new JSONObject(vcap_services);
			mongoURI = vcapJSON.getJSONArray("mongodb")
							   .getJSONObject(0)
							   .getJSONObject("credentials")
							   .getString("uri");
			dbname   = vcapJSON.getJSONArray("mongodb")
							   .getJSONObject(0)
							   .getJSONObject("credentials")
							   .getString("database");
		} catch (JSONException  e) {
			e.printStackTrace();
		}
		
		// Connect to Mongo
		MongoClientURI connectionString = new MongoClientURI(mongoURI);
		mongoClient = new MongoClient(connectionString);

		MongoDatabase db = mongoClient.getDatabase(dbname);
		if (db == null) {
			System.out.println("Error: MongoDB handle is null");
		}
		
		this.icmColl = db.getCollection(ICM);
		if (icmColl == null) {
			System.out.println("Error: ICM Collection handle is null");
		}
		
		this.iumColl = db.getCollection(IUM);
		if (iumColl == null) {
			System.out.println("Error: IUM Collection handle is null");
		}
	}
	
	public MongoCollection getIcmColl(){
		setup();
		return this.icmColl;
	}
	
	public MongoCollection getIumColl(){
		setup();
		return this.iumColl;
	}	
	
	public void addIcm(Document icm) {
		if (icm != null) {
			this.icmColl.insertOne(icm);
		}
	}
	
	public void addIum(Document ium) {
		if (ium != null) {
			this.iumColl.insertOne(ium);
		}
	}
	
	public JSONArray findAllIcmEvents() {
		JSONArray results = new JSONArray();
	    FindIterable<Document> search = this.icmColl.find();

		for( Document d : search ) {
			d.remove("_id");
			results.put(d);
		}
		return results;
	}

	public JSONArray findAllIcmEventsByUrn(String urn) {
		JSONArray results = new JSONArray();
	    FindIterable<Document> search = this.icmColl.find(new BasicDBObject(URN,urn));

		for( Document d : search ) {
			d.remove("_id");
			results.put(d);
		}
		return results;
	}

	public JSONArray findAllIcmEventsByMsgId(String msgId) {
		JSONArray results = new JSONArray();
	    FindIterable<Document> search = this.icmColl.find(new BasicDBObject(MSG_ID,msgId));

		for( Document d : search ) {
			d.remove("_id");
			results.put(d);
		}
		return results;
	}	
	
	public JSONArray findAllIumEvents() {
		JSONArray results = new JSONArray();
	    FindIterable<Document> search = this.iumColl.find();

		for( Document d : search ) {
			d.remove("_id");
			results.put(d);
		}
		return results;
	}
	
	public JSONArray findAllIumEventsByUrn(String urn) {
		JSONArray results = new JSONArray();
	    FindIterable<Document> search = this.iumColl.find(new BasicDBObject(URN,urn));

		for( Document d : search ) {
			d.remove("_id");
			results.put(d);
		}
		return results;
	}

	public JSONArray findAllIumEventsByMsgId(String msgId) {
		JSONArray results = new JSONArray();
	    FindIterable<Document> search = this.iumColl.find(new BasicDBObject(MSG_ID,msgId));

		for( Document d : search ) {
			d.remove("_id");
			results.put(d);
		}
		return results;
	}	
	
	@PreDestroy
	private void cleanup() {
		mongoClient.close();
	}
	
	public String getIcmEvents(String id) {
		return "{\"foo\":\"bar\"}";
	}


}
