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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import fr.ybo.database.DataBaseException;
import fr.ybo.database.annotation.Indexed;
import fr.ybo.database.annotation.PrimaryKey;

/**
 * Represents a column of a table in the database definition.
 * 
 * @author ybonnel
 * 
 */
class Column {

	/**
	 * Type of the column.
	 */
	private final fr.ybo.database.annotation.Column.TypeColumn type;
	/**
	 * Field associated with the column.
	 */
	private final Field field;
	/**
	 * Name of the column.
	 */
	private final String name;
	/**
	 * Indicate if this column is a primary key.
	 */
	private final PrimaryKey primaryKey;
	/**
	 * Index.
	 */
	private final Indexed indexed;
	/**
	 * Table name.
	 */
	private String tableName;
	
	/**
	 * True if the column is not null.
	 */
	private final boolean notNull;

	/**
	 * Copy constructor.
	 * 
	 * @param column
	 *            copie.
	 */
	Column(Column column) {
		type = column.type;
		field = column.field;
		name = column.name;
		primaryKey = column.primaryKey;
		indexed = column.indexed;
		tableName = column.tableName;
		notNull = column.notNull;
	}

	/**
	 * Constructor.
	 * 
	 * @param field
	 *            field.
	 * @param tableName
	 *            table name.
	 */
	Column(Field field, String tableName) {
		this.field = field;
		this.tableName = tableName;
		fr.ybo.database.annotation.Column colonne = field.getAnnotation(fr.ybo.database.annotation.Column.class);
		type = colonne.type();
		primaryKey = field.getAnnotation(PrimaryKey.class);
		if (isAutoIncrement() && !field.getType().equals(Long.class)) {
			throw new DataBaseException("An autoIncrement column is not a Long for the table " + tableName);
		}
		name = "".equals(colonne.name()) ? field.getName() : colonne.name();
		indexed = field.getAnnotation(Indexed.class);
		notNull = colonne.notNull();
	}

	/**
	 * Add value to the entity.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param values
	 *            values.
	 * @param entity
	 *            entity.
	 */
	<Entity> void addValue(ContentValues values, Entity entity) {
		Object valeur = getValue(entity);
		if (valeur != null) {
			switch (type) {
				case BOOLEAN:
					values.put(name, (Boolean) valeur ? 1 : 0);
					break;
				case DATE:
					values.put(name, ((Date) valeur).getTime());
					break;
				case INTEGER:
					values.put(name, (Integer) valeur);
					break;
				case TEXT:
					values.put(name, (String) valeur);
					break;
				case NUMERIC:
					values.put(name, (Double) valeur);
					break;
			}
		}
	}

	/**
	 * Add a clause to the where if the field is not null.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param queryBuilder
	 *            query where append the where clause.
	 * @param entity
	 *            entity.
	 * @param selectionArgs
	 *            values of the query params.
	 */
	<Entity> void appendWhereIfNotNull(StringBuilder queryBuilder, Entity entity, Collection<String> selectionArgs) {
		String valeur = getValueToString(entity);
		if (valeur != null) {
			if (queryBuilder.length() > 0) {
				queryBuilder.append(" AND ");
			}
			queryBuilder.append(name);
			queryBuilder.append(" = :");
			queryBuilder.append(name);
			selectionArgs.add(valeur);
		}
	}

	/**
	 * Definition of the index.
	 * 
	 * @return the sql definition of the index.
	 */
	String getIndexSqlDef() {
		if (indexed == null) {
			return null;
		}
		StringBuilder requete = new StringBuilder("CREATE ");
		if (indexed.unique()) {
			requete.append("UNIQUE ");
		}
		requete.append("INDEX ");
		String nameIndex = "".equals(indexed.name()) ? new StringBuilder(tableName).append('_').append(name).toString() : indexed.name();
		requete.append(nameIndex);
		requete.append(" ON ");
		requete.append(tableName);
		requete.append(" (");
		requete.append(name);
		requete.append(" );");
		return requete.toString();
	}

	/**
	 * @return the column's name.
	 */
	String getName() {
		return name;
	}

	/**
	 * @return the sql definition of the column.
	 */
	String getSqlDefinition() {
		StringBuilder requete = new StringBuilder(name);
		requete.append(' ');
		requete.append(type.getSqlType());
		return requete.toString();
	}

	/**
	 * Get the field value in the object.
	 * 
	 * @param object
	 *            the object.
	 * @return the value of the value.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	private Object getValue(Object object) throws DataBaseException {
		try {
			boolean isAccessible = field.isAccessible();
			field.setAccessible(true);
			Object valeur = field.get(object);
			field.setAccessible(isAccessible);
			return valeur;
		} catch (IllegalArgumentException e) {
			throw new DataBaseException(e);
		} catch (IllegalAccessException e) {
			throw new DataBaseException(e);
		}
	}

	/**
	 * Get the value of the field in String format.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param entity
	 *            entity.
	 * @return the field in String format.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	<Entity> String getValueToString(Entity entity) throws DataBaseException {
		Object valeur = getValue(entity);
		if (valeur == null) {
			return null;
		}
		String retour;
		switch (type) {
			case BOOLEAN:
				retour = (Boolean) valeur ? "1" : "0";
				break;
			case DATE:
				retour = Long.toString(((Date) valeur).getTime());
				break;
			case INTEGER:
			case NUMERIC:
				retour = valeur.toString();
				break;
			case TEXT:
				retour = (String) valeur;
				break;
			default:
				throw new DataBaseException("Column type unknown [" + type + ']');
		}
		return retour;
	}

	/**
	 * @return true if the column is indexed.
	 */
	boolean isIndexed() {
		return indexed != null;
	}

	/**
	 * @return true if the column is not null.
	 */
	boolean isNotNull() {
		return notNull;
	}

	/**
	 * @return true if the column is a primary key.
	 */
	boolean isPrimaryKey() {
		return primaryKey != null;
	}

	/**
	 * @return true if the column is a primary key and autoIncrement.
	 */
	boolean isAutoIncrement() {
		return primaryKey != null && primaryKey.autoIncrement();
	}

	/**
	 * Complete an entity with a cursor.
	 * 
	 * @param <Entity>
	 *            entity.
	 * @param cursor
	 *            cursor.
	 * @param entity
	 *            the entity to complete.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	<Entity> void completeEntity(Cursor cursor, Entity entity) throws DataBaseException {
		int index = cursor.getColumnIndex(name);
		if (!cursor.isNull(index)) {
			Object value;
			switch (type) {
				case INTEGER:
					if (field.getType().equals(Long.class)) {
						value = cursor.getLong(index);
					} else {
						value = cursor.getInt(index);
					}
					break;
				case NUMERIC:
					value = cursor.getDouble(index);
					break;
				case TEXT:
					value = cursor.getString(index);
					break;
				case BOOLEAN:
					value = cursor.getInt(index) == 1;
					break;
				case DATE:
					value = new Date(cursor.getLong(index));
					break;
				default:
					throw new DataBaseException("Column type unknown [" + type + ']');
			}
			setValue(entity, value);
		}
	}

	/**
	 * Use to change the table name.
	 * 
	 * @param tableName
	 *            tableName.
	 */
	void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Set the value to the field in the entity.
	 * 
	 * @param <Entity>
	 *            entity
	 * @param entity
	 *            the entity where the field must be set.
	 * @param value
	 *            the value to set.
	 * @throws DataBaseException
	 *             if there is a problem (may be a development problem).
	 */
	protected <Entity> void setValue(Entity entity, Object value) throws DataBaseException {
		try {
			boolean isAccessible = field.isAccessible();
			field.setAccessible(true);
			field.set(entity, value);
			field.setAccessible(isAccessible);
		} catch (IllegalArgumentException e) {
			throw new DataBaseException(e);
		} catch (IllegalAccessException e) {
			throw new DataBaseException(e);
		}
	}

}
