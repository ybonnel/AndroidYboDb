/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser GeneralGeneral Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See tGNU Lesser GeneralLesser General Public License for more details.
 *
 * You should have received GNU Lesser Generalhe GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.database.modele;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.ybo.database.DataBaseException;

/**
 * Represent a table in the database definition.
 * 
 * @author ybonnel
 * 
 */
public class Table {

	/** List of columns. */
	private final List<Column> columns = new ArrayList<Column>();
	/** Name of the table. */
	private String name;
	/** Where clause with the primary keys */
	private String primaryKeyWhere;
	/**
	 * Names of the columns.
	 */
	private String[] columnsNames;
	/**
	 * Constructor of the class associated with the entity.
	 */
	private final Constructor<?> constructor;

	/**
	 * Constructor.
	 * 
	 * @param clazz
	 *            the class associated with the table.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	Table(Class<?> clazz) throws DataBaseException {
		fr.ybo.database.annotation.Entity table = clazz.getAnnotation(fr.ybo.database.annotation.Entity.class);
		if (table == null) {
			throw new DataBaseException("The class " + clazz.getSimpleName() + " don't contains the annotation @Table");
		}
		name = table.name();
		if ("".equals(name)) {
			name = clazz.getSimpleName();
		}
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(fr.ybo.database.annotation.Column.class) != null) {
				columns.add(new Column(field, name));
			}
		}
		try {
			constructor = clazz.getConstructor((Class<?>[]) null);
		} catch (SecurityException e) {
			throw new DataBaseException(e);
		} catch (NoSuchMethodException e) {
			throw new DataBaseException(e);
		}
	}

	/**
	 * Copy constructor.
	 * 
	 * @param table
	 *            the copy.
	 */
	public Table(Table table) {
		for (Column colonne : table.columns) {
			columns.add(new Column(colonne));
		}
		name = table.name;
		primaryKeyWhere = table.primaryKeyWhere;
		columnsNames = table.columnsNames;
		constructor = table.constructor;
	}

	/**
	 * Add a suffix to the table name.
	 * 
	 * @param suffix
	 *            the suffix to add.
	 */
	public void addSuffixeToTableName(String suffix) {
		name = name + '_' + suffix;
		for (Column colonne : columns) {
			colonne.setTableName(name);
		}
	}

	/**
	 * Create the table.
	 * 
	 * @param db
	 *            the SQLite dataBase.
	 */
	public void createTable(SQLiteDatabase db) {
		StringBuilder requete = new StringBuilder();
		requete.append("CREATE TABLE ");
		requete.append(name);
		requete.append(" (");
		Collection<String> indexes = new ArrayList<String>(2);
		StringBuilder primaryKeys = new StringBuilder();
		boolean first = true;
		boolean hasAutoIncrement = false;
		for (Column colonne : columns) {
			if (!first) {
				requete.append(',');
			}
			requete.append(colonne.getSqlDefinition());
			if (colonne.isPrimaryKey()) {
				if (hasAutoIncrement) {
					throw new DataBaseException("Only one primary key with an autoIncrement by table");
				}
				if (colonne.isAutoIncrement()) {
					requete.append(" PRIMARY KEY AUTOINCREMENT ");
					hasAutoIncrement = true;
				} else {
					if (primaryKeys.length() == 0) {
						primaryKeys.append(",PRIMARY KEY (");
					} else {
						primaryKeys.append(',');
					}
					primaryKeys.append(colonne.getName());
				}
			}
			if (colonne.isNotNull()) {
				requete.append("NOT NULL");
			}
			if (colonne.isIndexed()) {
				indexes.add(colonne.getIndexSqlDef());
			}
			first = false;
		}
		if (primaryKeys.length() > 0) {
			if (hasAutoIncrement) {
				throw new DataBaseException("Only one primary key with an autoIncrement by table");
			}
			requete.append(primaryKeys.toString());
			requete.append(')');
		}
		requete.append(");");
		db.execSQL(requete.toString());
		for (String requeteIndex : indexes) {
			db.execSQL(requeteIndex);
		}
	}

	/**
	 * Delete all the rows of the table.
	 * 
	 * @param db
	 *            the SQLite database.
	 */
	void delete(SQLiteDatabase db) {
		db.delete(name, null, null);
	}

	/**
	 * Delete one entity.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param db
	 *            the SQLite database.
	 * @param entity
	 *            the entity to delete.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	<Entity> void delete(SQLiteDatabase db, Entity entity) throws DataBaseException {
		List<String> where = generatePrimaryKeyWhere(entity);
		db.delete(name, getPrimaryKeyWhere(), where.toArray(new String[where.size()]));
	}

	/**
	 * Update an entity.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param db
	 *            the SQLite database.
	 * @param entity
	 *            the entity to update.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	<Entity> void update(SQLiteDatabase db, Entity entity) throws DataBaseException {
		List<String> where = generatePrimaryKeyWhere(entity);
		ContentValues values = new ContentValues();
		for (Column colonne : columns) {
			if (!colonne.isPrimaryKey()) {
				colonne.addValue(values, entity);
			}
		}
		db.update(name, values, getPrimaryKeyWhere(), where.toArray(new String[where.size()]));
	}

	/**
	 * Drop the table.
	 * 
	 * @param db
	 *            the SQLite database.
	 */
	public void dropTable(SQLiteDatabase db) {
		StringBuilder requete = new StringBuilder();
		requete.append("DROP TABLE IF EXISTS ");
		requete.append(name);
		requete.append(';');
		db.execSQL(requete.toString());
	}

	/**
	 * Generate the where args with all fields witch is primary key.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param entity
	 *            the entity.
	 * @return the where args.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	private <Entity> List<String> generatePrimaryKeyWhere(Entity entity) throws DataBaseException {
		List<String> whereArgs = new ArrayList<String>(3);
		for (Column colonne : columns) {
			if (colonne.isPrimaryKey()) {
				whereArgs.add(colonne.getValueToString(entity));
			}
		}
		return whereArgs;
	}

	/**
	 * 
	 * @return the columns names.
	 */
	private String[] getColumns() {
		if (columnsNames == null) {
			columnsNames = new String[columns.size()];
			for (int count = 0; count < columns.size(); count++) {
				columnsNames[count] = columns.get(count).getName();
			}
		}
		return columnsNames;
	}

	/**
	 * 
	 * @return the table name.
	 */
	String getName() {
		return name;
	}

	/**
	 * @return a new entity.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	private Object getNewEntite() throws DataBaseException {
		try {
			return constructor.newInstance((Object[]) null);
		} catch (IllegalArgumentException e) {
			throw new DataBaseException(e);
		} catch (InstantiationException e) {
			throw new DataBaseException(e);
		} catch (IllegalAccessException e) {
			throw new DataBaseException(e);
		} catch (InvocationTargetException e) {
			throw new DataBaseException(e);
		}
	}

	/**
	 * True if the table has an auto-increment column.
	 */
	private Boolean autoIncrement = null;
	/**
	 * If the table has an auto-increment column, this is the one.
	 */
	private Column autoIncrementColumn = null;

	/**
	 * 
	 * @return true is the table has an auto-increment column.
	 */
	private boolean hasAutoIncrement() {
		if (autoIncrement == null) {
			autoIncrement = false;
			for (Column column : columns) {
				if (column.isAutoIncrement()) {
					autoIncrement = true;
					autoIncrementColumn = column;
					break;
				}
			}
		}
		return autoIncrement;
	}

	/**
	 * Set the rowId to the auto-increment field if it exists.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param entity
	 *            the entity which must be set the rowId.
	 * @param rowId
	 *            the rowId.
	 */
	private <Entity> void setAutoIncrementColumn(Entity entity, long rowId) {
		if (hasAutoIncrement()) {
			autoIncrementColumn.setValue(entity, rowId);
		}
	}

	/**
	 * @return the where clause with primary keys.
	 */
	private String getPrimaryKeyWhere() {
		if (primaryKeyWhere == null) {
			StringBuilder where = new StringBuilder();
			boolean first = true;
			for (Column colonne : columns) {
				if (colonne.isPrimaryKey()) {
					if (!first) {
						where.append(" AND ");
					}
					where.append(colonne.getName());
					where.append(" = :");
					where.append(colonne.getName());
					first = false;
				}
			}
			primaryKeyWhere = where.toString();
		}
		return primaryKeyWhere;
	}

	/**
	 * Insert one entity.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param db
	 *            the SQLite database.
	 * @param entity
	 *            the entity to insert.
	 * @return
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	public <Entity> Entity insert(SQLiteDatabase db, Entity entity) throws DataBaseException {
		ContentValues values = new ContentValues();
		for (Column colonne : columns) {
			colonne.addValue(values, entity);
		}
		long rowId = db.insertOrThrow(name, null, values);
		setAutoIncrementColumn(entity, rowId);
		return entity;
	}

	/**
	 * Select entities from an example.
	 * 
	 * @param <Entite>
	 *            entity.
	 * @param db
	 *            the SQLite database.
	 * @param entite
	 *            the example entity.
	 * @param selectionPlus
	 *            where clause.
	 * @param selectArgsPlus
	 *            args of the where clause.
	 * @param orderBy
	 *            oder by clause.
	 * @return the entities found.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	@SuppressWarnings("unchecked")
	<Entite> List<Entite> select(SQLiteDatabase db, Entite entite, String selectionPlus,
			Collection<String> selectArgsPlus, String orderBy) throws DataBaseException {
		List<Entite> entites = new ArrayList<Entite>();
		StringBuilder whereClause = new StringBuilder();
		List<String> selectionArgsList = new ArrayList<String>(selectArgsPlus == null ? 0 : selectArgsPlus.size());
		if (entite != null) {
			for (Column colonne : columns) {
				colonne.appendWhereIfNotNull(whereClause, entite, selectionArgsList);
			}
		}
		if (selectionPlus != null) {
			whereClause.append(" AND (");
			whereClause.append(selectionPlus);
			whereClause.append(')');
		}
		String selection = whereClause.length() > 0 ? whereClause.toString() : null;
		if (selectArgsPlus != null) {
			selectionArgsList.addAll(selectArgsPlus);
		}
		String[] selectionArgs = selection == null ? null : selectionArgsList.toArray(new String[selectionArgsList
				.size()]);
		Cursor cursor = db.query(name, getColumns(), selection, selectionArgs, null, null, orderBy);
		while (cursor.moveToNext()) {
			Entite newEntite = (Entite) getNewEntite();
			for (Column colonne : columns) {
				colonne.completeEntity(cursor, newEntite);
			}
			entites.add(newEntite);
		}
		cursor.close();
		return entites;
	}

}
