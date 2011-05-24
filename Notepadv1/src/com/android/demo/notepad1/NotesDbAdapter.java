/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.demo.notepad1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.demo.notepad1.modele.Note;

import fr.ybo.database.DataBaseHelper;

/**
 * This is the database support with the integration of AndroidYboDb.
 */
public class NotesDbAdapter extends DataBaseHelper {

	/**
	 * Name of the database.
	 */
	private static final String DATABASE_NAME = "data";
	/**
	 * Current version.
	 */
	private static final int DATABASE_VERSION = 2;
	
	/**
	 * List of classes managed by AndroidYboDb.
	 */
	private static final List<Class<?>> DATABASE_CLASSES = new ArrayList<Class<?>>();
	static {
		DATABASE_CLASSES.add(Note.class);
	}

	/**
	 * Default Constructor.
	 * @param pContext the Android context.
	 */
	public NotesDbAdapter(Context pContext) {
		super(pContext, DATABASE_CLASSES, DATABASE_NAME, DATABASE_VERSION);
	}

	/**
	 * The database upgrade system.
	 * Just a map with an UpagradeDatabase implementation for a database version.
	 * AndroidYboDb will apply all upgrade methods from oldVersion + 1 to the newVersion of database. 
	 */
	private Map<Integer, UpgradeDatabase> mapUpgrades;
	@Override
	protected Map<Integer, UpgradeDatabase> getUpgrades() {
		if (mapUpgrades == null) {
			mapUpgrades = new HashMap<Integer, DataBaseHelper.UpgradeDatabase>();
			mapUpgrades.put(2, new UpgradeDatabase() {
				@Override
				public void upgrade(SQLiteDatabase pArg0) {
					Log.w("NotesDbAdapter",
							"Upgrading database from version 1 to 2, which will destroy all old data");
					getBase().dropDataBase(pArg0);
					getBase().createDataBase(pArg0);
				}
			});
		}
		return mapUpgrades;
	}
}
