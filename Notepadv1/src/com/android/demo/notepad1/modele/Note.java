/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     ybonnel - initial API and implementation
 */
package com.android.demo.notepad1.modele;

import java.io.Serializable;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Column.TypeColumn;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;

@Entity( name = "notes" )
public class Note implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@PrimaryKey( autoIncrement = true )
	@Column(type = TypeColumn.INTEGER)
	public Long id;
	
	@Column( notNull = true )
	public String title;

	@Column( notNull = true )
	public String body;
}
