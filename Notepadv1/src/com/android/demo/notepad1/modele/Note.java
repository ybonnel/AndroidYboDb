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
	public Integer id;
	
	@Column( notNull = true )
	public String title;

	@Column( notNull = true )
	public String body;
}
