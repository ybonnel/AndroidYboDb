package com.android.demo.notepad1;

import android.app.Activity;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_edit);
		setTitle(R.string.edit_note);
		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		Button confirmButton = (Button) findViewById(R.id.confirm);
		currentNote = null;
		
		currentNote = (savedInstanceState == null) ? null :
            (Note) savedInstanceState.getSerializable("note");
		if (currentNote == null) {
			Bundle extras = getIntent().getExtras();
			currentNote = (Note) (extras != null ? extras
					.getSerializable("note") : null);
		}
		
		populateFields();
		
		confirmButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View view) {
		        setResult(RESULT_OK);
		        finish();
		    }
		});
	}
	
	private void populateFields() {
	    if (currentNote != null) {
	        mTitleText.setText(currentNote.title);
	        mBodyText.setText(currentNote.title);
	    }
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable("note", currentNote);
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
	
	private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        currentNote.title = title;
        currentNote.body = body;
		if (currentNote.id == null) {
			currentNote = NoteApplication.getNotesDbAdapter().insert(currentNote);
		} else {
			NoteApplication.getNotesDbAdapter().update(currentNote);
		}
    }
}
