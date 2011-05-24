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
	
	// FIXME add not null feature.
	@Column
	public String title;

	// FIXME add not null feature.
	@Column
	public String body;

}
