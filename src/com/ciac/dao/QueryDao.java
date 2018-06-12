package com.ciac.dao;

import java.util.ArrayList;
import java.util.Date;

import org.bson.types.ObjectId;

import com.ciac.Image.Query;
import com.ciac.db.CiacConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
/**
 * @author Maxwell Ndognkon Manga 
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: QueryDao.java</p>
 *  Data Access Layer Implementation for Query Management
 * */
public class QueryDao extends Dao<Query> {

	public QueryDao(DB db) {
		super(db);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Query create(Query obj, String coll) {
		// TODO Auto-generated method stub
		
		Query q = null;
		DBCollection db_coll = db.getCollection(coll);
		BasicDBObject bsonQuery1 = new BasicDBObject("_id", new ObjectId(obj.getId()));
		BasicDBObject bsonQuery2 = new BasicDBObject("query", obj.getQuery());
		DBCursor cursor1 = db_coll.find(bsonQuery1);
		DBCursor cursor2 = db_coll.find(bsonQuery2);
		int nb_obj = cursor1.count();		
		//System.out.println("Insert: N. matched Obj_Query = "+nb_obj);		
		
		if(cursor1.count() == 0 && cursor2.count() == 0){
			ObjectId id = new ObjectId();
			BasicDBObject bsonObj = new BasicDBObject();
			bsonObj.append("_id", id);
			bsonObj.append("domain", obj.getDomain());
			bsonObj.append("person", obj.getPerson());
			bsonObj.append("query", obj.getQuery());
			bsonObj.append("created_on", new Date());
			bsonObj.append("udpated_on", null);

			db_coll.insert(bsonObj);
			//System.out.println("ObjectId: "+id.toString());	
			q = this.find(id.toString(), coll);
		}
		else{
			int i = 1;
			ObjectId id;
			while(cursor2.hasNext()) {
				BasicDBObject db_obj = (BasicDBObject) cursor2.next();
				id =  new ObjectId(db_obj.get("_id").toString());//id = new ObjectId(db_obj.get("_id"));
				//obj = this.find(id.toString(), coll);
				obj.setId(id.toString());
				if(i == 1)break;
			}
			q = this.update(obj, coll);		
		}		
		return q;
		
	}

	@Override
	public void delete(Query obj, String coll) {
		// TODO Auto-generated method stub
		DBCollection db_coll = db.getCollection(coll);
		BasicDBObject bsonQuery = new BasicDBObject("_id", new ObjectId(obj.getId()));
		DBCursor cursor = db_coll.find(bsonQuery);
		int nb_obj = cursor.count();
		System.out.println("Delete: N. matched Obj_Query = " + nb_obj);
		/*
		 * MongoClient mongoClient; mongoClient = new MongoClient(); Cia DB dbb
		 * = mongoClient.getDB(CiacConnection.db_MAIN);
		 */
		/*
		 * CiacConnection con = new CiacConnection(); if (con==null){
		 * System.out.println("Null connexion"); System.exit(-1); }
		 * con.initInstance(); DB dbb = con.getInstance(CiacConnection.db_MAIN);
		 */// mongoClient.getDB(CiacConnection.db_MAIN);
			// System.out.println("database = "+CiacConnection.db_MAIN);

		if (nb_obj > 0) {
			Dao<Query> DataPointDAO = DaoFactory.getQueryDAO(db);
			while (cursor.hasNext()) {
				BasicDBObject bsonJob = (BasicDBObject) cursor.next();
				db_coll.remove(bsonJob);
			}
		}
	}

	@Override
	public Query update(Query obj, String coll) {
		Query q = null;
		DBCollection db_coll = db.getCollection(coll);
		BasicDBObject bsonQuery = new BasicDBObject("_id", new ObjectId(obj.getId()));
		DBCursor cursor = db_coll.find(bsonQuery);
		int nb_obj = cursor.count();
		System.out.println("Update: N. matched Obj_Query = " + nb_obj);

		if (nb_obj == 0) {
			obj = this.create(obj, coll);
		} else {
			BasicDBObject bsonForUpdate = new BasicDBObject();

			BasicDBObject bsonObj = new BasicDBObject();
			bsonObj.append("domain", obj.getDomain());
			bsonObj.append("person", obj.getPerson());
			bsonObj.append("query", obj.getQuery());
			// bsonObj.append("created_on", obj.getDateCreate());
			bsonObj.append("udpated_on", new Date());

			bsonForUpdate.append("$set", bsonObj);
			db_coll.update(bsonQuery, bsonForUpdate);
			q = this.find(obj.getId(), coll);
		}
		return q;
	}

	@Override
	public Query find(String id, String coll) {
		Query q = null;

		DBCollection db_coll = db.getCollection(coll);
		BasicDBObject bsonQuery = new BasicDBObject("_id", new ObjectId(id));
		DBCursor cursor = db_coll.find(bsonQuery);
		try {
			int i = 1;
			ObjectId id_dset;
			while (cursor.hasNext()) {
				BasicDBObject db_obj = (BasicDBObject) cursor.next();

				q = new Query();
				q.setId(id);
				q.setDomain(db_obj.getString("domain").toString());
				q.setPerson(db_obj.getString("person").toString());
				q.setListQuery(db_obj.getString("query").toString());
				q.setDateCreate(db_obj.getDate("created_on"));
				q.setDateUpdate(db_obj.getDate("udpated_on"));

				/*
				 * job.setId(id);
				 * job.setThreadId(Integer.parseInt((db_obj.get("threadId").
				 * toString())));
				 * job.setExeId(Integer.parseInt((db_obj.get("exeId").toString()
				 * ))); job.setName(String.valueOf(db_obj.get("name")));
				 * job.setState(String.valueOf(db_obj.get("state")));
				 * job.setDate(db_obj.getDate("date"));
				 */
				// System.out.println("date_db =
				// "+db_obj.getDate("date").toString());

				if (i == 1)
					break;
			}
		} finally {
			cursor.close();
		}
		return q;
	}

	@Override
	public ArrayList<Query> findOnManyConstraints(String coll, ArrayList<String> al_fields, ArrayList<String> al_vals) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Query> findOnConstraint(String coll, String field, String val) {
		ArrayList<Query> al_queries = new ArrayList<Query>();
		DBCollection db_coll = db.getCollection(coll);
		BasicDBObject bsonQuery = new BasicDBObject(field, val);
		DBCursor cursor = db_coll.find(bsonQuery);
		Query q;
		ObjectId id;

		try {
			ObjectId id_dset;
			while (cursor.hasNext()) {
				q = new Query();
				BasicDBObject db_obj = (BasicDBObject) cursor.next();
				id = new ObjectId(db_obj.get("_id").toString());// ObjectId.massageToObjectId(db_obj.get("_id"));

				q.setId(id.toString());
				q.setDomain(db_obj.getString("domain").toString());
				q.setPerson(db_obj.getString("person").toString());
				q.setListQuery(db_obj.getString("query").toString());
				q.setDateCreate(db_obj.getDate("created_on"));
				q.setDateUpdate(db_obj.getDate("udpated_on"));

				al_queries.add(q);
			}
		} finally {
			cursor.close();
		}

		return al_queries;
	}

	@Override
	public ArrayList<Query> findAll(String coll) {
		ArrayList<Query> al_queries = new ArrayList<Query>();
		DBCollection db_coll = db.getCollection(coll);
		// BasicDBObject bsonQuery = new BasicDBObject(field, val);
		DBCursor cursor = db_coll.find();
		Query q;
		ObjectId id;

		try {
			ObjectId id_dset;
			while (cursor.hasNext()) {
				q = new Query();
				BasicDBObject db_obj = (BasicDBObject) cursor.next();
				id = new ObjectId(db_obj.get("_id").toString());// ObjectId.massageToObjectId(db_obj.get("_id"));

				q.setId(id.toString());
				q.setDomain(db_obj.getString("domain").toString());
				q.setPerson(db_obj.getString("person").toString());
				q.setListQuery(db_obj.getString("query").toString());
				q.setDateCreate(db_obj.getDate("created_on"));
				q.setDateUpdate(db_obj.getDate("udpated_on"));

				al_queries.add(q);
			}
		} finally {
			cursor.close();
		}

		return al_queries;
	}

}
