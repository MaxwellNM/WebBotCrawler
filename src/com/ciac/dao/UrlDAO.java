package com.ciac.dao;

import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.ciac.Image.Image_cr;
import com.ciac.Image.Url;
import com.ciac.db.CiacConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * @author Maxwell Ndognkon Manga 
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: UrlDAO.java</p>
 *  Data Access Layer Implementation for Url Management
 * */
public class UrlDAO extends Dao<Url> {

	public UrlDAO(DB db) {
		super(db);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Url create(Url obj, String coll) {
		// TODO Auto-generated method stub
		Url img1 = null;
		DBCollection db_coll = db.getCollection(coll);
		BasicDBObject bsonQuery1 = new BasicDBObject("_id", new ObjectId(obj.getId()));
		BasicDBObject bsonQuery2 = new BasicDBObject("url", obj.getUrl());
		DBCursor cursor1 = db_coll.find(bsonQuery1);
		DBCursor cursor2 = db_coll.find(bsonQuery2);
		int nb_obj = cursor1.count();
		System.out.println("Insert: N. matched Obj_img1 = " + nb_obj);

		if (cursor1.count() == 0 && cursor2.count() == 0) {
			ObjectId id = new ObjectId();
			BasicDBObject bsonObj = new BasicDBObject();
			bsonObj.append("_id", id);
			bsonObj.append("url", obj.getUrl());
			bsonObj.append("score", obj.getScore());
			bsonObj.append("index", obj.getIndex());

			db_coll.insert(bsonObj);
			System.out.println("ObjectId: " + id.toString());
			img1 = this.find(id.toString(), coll);
		} else {
			int i = 1;
			ObjectId id;
			while (cursor2.hasNext()) {
				BasicDBObject db_obj = (BasicDBObject) cursor2.next();
				id = new ObjectId(db_obj.get("_id").toString());// id =
																// ObjectId.massageToObjectId(db_obj.get("_id"));
				// obj = this.find(id.toString(), coll);
				obj.setId(id.toString());
				if (i == 1)
					break;
			}
			img1 = this.update(obj, coll);
		}
		return img1;

		// return null;
	}

	@Override
	public void delete(Url obj, String coll) {
		// TODO Auto-generated method stub
		DBCollection db_coll = db.getCollection(coll);
		BasicDBObject bsonQuery = new BasicDBObject("_id", new ObjectId(obj.getId()));
		DBCursor cursor = db_coll.find(bsonQuery);
		int nb_obj = cursor.count();
		System.out.println("Delete: N. matched Obj_job = " + nb_obj);

		if (nb_obj > 0) {
			/*
			 * DAO<DataPoint> DataPointDAO =
			 * DAOFactory.getDataPointDAO(CiacConnection.getInstance(
			 * CiacConnection.db_EV)); while(cursor.hasNext()) { BasicDBObject
			 * bsonJob = (BasicDBObject) cursor.next();
			 * 
			 * 
			 * ArrayList<DataPoint> al_dp =
			 * DataPointDAO.findOnConstraint(CiacConnection.coll_Url, "url",
			 * obj.getUrl()); System.out.println("Delete: N. matched Obj_DP = "
			 * +al_dp.size());
			 * 
			 * if(al_dp.size() > 0){ DBCollection db_collEval =
			 * db.getCollection(CiacConnection.coll_Url); DataPoint dp =
			 * al_dp.get(0); BasicDBObject bsonQ_eval = new BasicDBObject("_id",
			 * new ObjectId(dp.getId())); DBCursor cur_eval =
			 * db_collEval.find(bsonQ_eval); while(cur_eval.hasNext()) {
			 * System.out.println("Delete: Obj_DP find"); BasicDBObject bsonDP =
			 * (BasicDBObject) cur_eval.next();
			 * 
			 * db_collEval.remove(bsonDP); db_coll.remove(bsonJob); } } else
			 * db_coll.remove(bsonJob); }
			 */
		}

	}

	@Override
	public Url update(Url obj, String coll) {
		// TODO Auto-generated method stub
		Url img = null;
		DBCollection db_coll = db.getCollection(coll);
		BasicDBObject bsonQuery = new BasicDBObject("_id", new ObjectId(obj.getId()));
		DBCursor cursor = db_coll.find(bsonQuery);
		int nb_obj = cursor.count();
		System.out.println("Update: N. matched Obj_job = " + nb_obj);

		if (nb_obj == 0) {
			obj = this.create(obj, coll);
		} else {
			BasicDBObject bsonForUpdate = new BasicDBObject();

			BasicDBObject bsonObj = new BasicDBObject();
			bsonObj.append("url", obj.getUrl());
			bsonObj.append("score", obj.getScore());
			bsonObj.append("index", obj.getIndex());

			bsonForUpdate.append("$set", bsonObj);
			db_coll.update(bsonQuery, bsonForUpdate);
			img = this.find(obj.getId(), coll);
		}
		return img;
		//
		// return null;
	}

	@Override
	public Url find(String id, String coll) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Url> findOnManyConstraints(String coll, ArrayList<String> al_fields, ArrayList<String> al_vals) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Url> findOnConstraint(String coll, String field, String val) {
		// TODO Auto-generated method stub
		ArrayList<Url> al_imgs = new ArrayList<Url>();
		DBCollection db_coll = db.getCollection(coll);
		BasicDBObject bsonQuery = new BasicDBObject(field, val);
		DBCursor cursor = db_coll.find(bsonQuery);
		Url img;
		ObjectId id;

		try {
			ObjectId id_dset;
			while (cursor.hasNext()) {
				// img = new Url();
				BasicDBObject db_obj = (BasicDBObject) cursor.next();
				id = new ObjectId(db_obj.get("_id").toString());// id =
																// ObjectId.massageToObjectId(db_obj.get("_id"));

				img = new Url();
				img.setId(String.valueOf(id));
				/*
				 * img.setThreadId(Integer.parseInt((db_obj.get("threadId").
				 * toString())));
				 * img.setExeId(Integer.parseInt((db_obj.get("exeId").toString()
				 * )));
				 */
				img.setUrl(String.valueOf(db_obj.get("url")));
				img.setScore(String.valueOf(db_obj.get("score")));
				// img.setImageSrcPage(db_obj.getDate("image_src_page"));
				img.setIndex(String.valueOf(db_obj.get("index")));

				al_imgs.add(img);
			}
		} finally {
			cursor.close();
		}

		return al_imgs;

		// return null;
	}

	@Override
	public ArrayList<Url> findAll(String coll) {
		// TODO Auto-generated method stub
		return null;
	}

}
