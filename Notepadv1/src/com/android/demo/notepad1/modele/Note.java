package com.android.demo.notepad1.modele;

import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Column.TypeColumn;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;

@Entity( name = "notes" )
public class Note {
	
	@PrimaryKey( autoIncrement = true )
	@Column(type = TypeColumn.INTEGER)
	public Integer id;
	
	@Column( notNull = true )
	public String title;

	@Column( notNull = true )
	public String body;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Note [title=" + this.title + ", body=" + this.body + "]";
	}
}
