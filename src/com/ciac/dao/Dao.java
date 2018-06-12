package com.ciac.dao;

import java.util.ArrayList;

import com.mongodb.DB;

/**
 * @author Maxwell Ndognkon Manga 
 * <p>Mail: maxwellndognkong@gmail.com</p>
 * <p>Date: 17/09/2016</p>
 * @version 2.0
 * <p>file: Dao.java</p>
 *  Data Access Abstract Layer 
 * */
public abstract class Dao<T> {

	protected DB db = null;

	public Dao(DB db) {
		this.db = db;
	}

	/**
	 * Create method
	 * 
	 * @param obj
	 * @return T
	 */
	public abstract T create(T obj, String coll);

	/**
	 * Delete method
	 * 
	 * @param obj
	 * @return void
	 */
	public abstract void delete(T obj, String coll);

	/**
	 * Update method
	 * 
	 * @param obj
	 * @return T
	 */
	public abstract T update(T obj, String coll);

	/**
	 * Find method
	 * 
	 * @param id
	 * @return T
	 */
	public abstract T find(String id, String coll);

	public abstract ArrayList<T> findOnManyConstraints(String coll, ArrayList<String> al_fields,
			ArrayList<String> al_vals);

	public abstract ArrayList<T> findOnConstraint(String coll, String field, String val);

	public abstract ArrayList<T> findAll(String coll);
}
