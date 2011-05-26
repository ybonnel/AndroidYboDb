package com.android.demo.notepad1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.demo.notepad1.modele.Note;

public class NoteEdit extends Activity {
	
	private EditText mTitleText;
	private EditText mBodyText;
	private Note currentNote;
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		setContentView(R.layout.note_edit);
		setTitle(R.string.edit_note);
		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		Button confirmButton = (Button) findViewById(R.id.confirm);
		currentNote = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			currentNote = (Note) extras.getSerializable("note");

		    if (currentNote.title != null) {
		        mTitleText.setText(currentNote.title);
		    }
		    if (currentNote.body != null) {
		        mBodyText.setText(currentNote.body);
		    }
		}
		
		confirmButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View view) {
		    	Intent mIntent = new Intent();
		    	if (currentNote == null) {
		    		currentNote = new Note();
		    	}
		    	currentNote.title = mTitleText.getText().toString();
		    	currentNote.body = mBodyText.getText().toString();
		    	mIntent.putExtra("note", currentNote);
		    	setResult(RESULT_OK, mIntent);
		    	finish();
		    }
		});
	}
}
