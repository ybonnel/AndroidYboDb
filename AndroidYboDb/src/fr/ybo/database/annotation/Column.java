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

package fr.ybo.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define a column of an entity.
 * 
 * @author ybonnel
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	enum TypeColumn {
		INTEGER("INTEGER"), TEXT("TEXT"), NUMERIC("NUMERIC"), BOOLEAN("INTEGER(1)"), DATE("INTEGER");

		private final String sqlType;

		TypeColumn(String sqlType) {
			this.sqlType = sqlType;
		}

		public String getSqlType() {
			return sqlType;
		}
	}

	/**
	 * Name of the column, use a field name if the name is not defined.
	 */
	String name() default "";

	/**
	 * Type of the column. TEXT by default.
	 */
	Column.TypeColumn type() default Column.TypeColumn.TEXT;
	
	/**
	 * True if the column must be not null. FALSE by default.
	 */
	boolean notNull() default false;

}
