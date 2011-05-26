package com.android.demo.notepad1;

import android.app.Application;

public class NoteApplication extends Application {
	

	private static NotesDbAdapter notesDbAdapter;
	
	public static NotesDbAdapter getNotesDbAdapter() {
		return notesDbAdapter;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		notesDbAdapter = new NotesDbAdapter(this);
	}

}
