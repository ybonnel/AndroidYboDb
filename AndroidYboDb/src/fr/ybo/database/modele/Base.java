/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.database.modele;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import fr.ybo.database.DataBaseException;

/**
 * Represent the database definition.
 * 
 * @author ybonnel
 * 
 */
public class Base {

	/**
	 * Map use to get a table with the class.
	 */
	private final Map<Class<?>, Table> mapClassTable = new HashMap<Class<?>, Table>(10);

	/**
	 * Constructor.
	 * 
	 * @param classes
	 *            the classes represents the database definition.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	public Base(Iterable<Class<?>> classes) throws DataBaseException {
		for (Class<?> clazz : classes) {
			mapClassTable.put(clazz, new Table(clazz));
		}
	}

	/**
	 * Create a database.
	 * 
	 * @param db
	 *            the SQLite database.
	 */
	public void createDataBase(SQLiteDatabase db) {
		for (Table table : mapClassTable.values()) {
			table.createTable(db);
		}
	}

	/**
	 * Update an entity.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param db
	 *            the SQLite database.
	 * @param entity
	 *            entity to update.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	public <Entity> void update(SQLiteDatabase db, Entity entity) throws DataBaseException {
		Class<?> clazz = entity.getClass();
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("The class " + clazz.getSimpleName() + " is not defined in the database.");
		}
		mapClassTable.get(clazz).update(db, entity);
	}

	/**
	 * Delete an entity.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param db
	 *            the SQLite database.
	 * @param entity
	 *            entity to delete.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	public <Entity> void delete(SQLiteDatabase db, Entity entity) throws DataBaseException {
		Class<?> clazz = entity.getClass();
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("The class " + clazz.getSimpleName() + " is not defined in the database.");
		}
		mapClassTable.get(clazz).delete(db, entity);
	}

	/**
	 * Delete all entities.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param db
	 *            the SQLite database.
	 * @param clazz
	 *            entity to delete.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	public <Entity> void deleteAll(SQLiteDatabase db, Class<Entity> clazz) throws DataBaseException {
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("The class " + clazz.getSimpleName() + " is not defined in the database.");
		}
		mapClassTable.get(clazz).delete(db);
	}

	/**
	 * Drop all tables defined in the database.
	 * 
	 * @param db
	 *            SQLite database.
	 */
	public void dropDataBase(SQLiteDatabase db) {
		for (Table table : mapClassTable.values()) {
			db.execSQL("DROP TABLE IF EXISTS " + table.getName());
		}
	}

	/**
	 * Get a table associated with a class.
	 * 
	 * @param clazz
	 *            the class.
	 * @return the table.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	public Table getTable(Class<?> clazz) throws DataBaseException {
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("The class " + clazz.getSimpleName() + " is not defined in the database.");
		}
		return new Table(mapClassTable.get(clazz));
	}

	/**
	 * Insert an entity.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param db
	 *            SQLite database.
	 * @param entity
	 *            entity to insert.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	public <Entity> void insert(SQLiteDatabase db, Entity entity) throws DataBaseException {
		Class<?> clazz = entity.getClass();
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("The class " + clazz.getSimpleName() + " is not defined in the database.");
		}
		mapClassTable.get(clazz).insert(db, entity);
	}

	/**
	 * Select entities.
	 * 
	 * @param <Entite>
	 *            entity.
	 * @param db
	 *            SQLite database.
	 * @param entite
	 *            entity example.
	 * @param selection
	 *            supplement clause of the select.
	 * @param selectionArgs
	 *            args of the selection.
	 * @param orderBy
	 *            orderBy condition.
	 * @return entities. if there is a problem (may be a development problem).
	 */
	public <Entite> List<Entite> select(SQLiteDatabase db, Entite entite, String selection, Collection<String> selectionArgs, String orderBy)
			throws DataBaseException {
		Class<?> clazz = entite.getClass();
		if (!mapClassTable.containsKey(clazz)) {
			throw new DataBaseException("The class " + clazz.getSimpleName() + " is not defined in the database.");
		}
		return mapClassTable.get(clazz).select(db, entite, selection, selectionArgs, orderBy);
	}

}
