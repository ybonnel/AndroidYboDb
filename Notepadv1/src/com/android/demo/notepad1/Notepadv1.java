/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.demo.notepad1;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.demo.notepad1.modele.Note;

/**
 * This is the adaptation of the notepad exemple of android, to show how to use AndroidYboDb.
 * original tutotial : http://developer.android.com/resources/tutorials/notepad/index.html
 * @author ybonnel
 *
 */
public class Notepadv1 extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
	
	private NotesDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_list);
        mDbHelper = new NotesDbAdapter(this);
        fillData();
        registerForContextMenu(getListView());
    }
    
    public static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = INSERT_ID + 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	 switch (item.getItemId()) {
         case INSERT_ID:
             createNote();
             return true;
         }
        
         return super.onOptionsItemSelected(item);
    }
    
    private void createNote() {
    	Intent i = new Intent(this, NoteEdit.class);
    	startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private ArrayAdapter<Note> notesAdapter = null;
    
    private void fillData() {
        // Now create an array adapter and set it to display using our row
    	// FIXME add selectAll feature.
    	notesAdapter = new NoteAdapter(this, mDbHelper.select(new Note()));
        setListAdapter(notesAdapter);
    }

    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

    @Override
	public boolean onContextItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
	    case DELETE_ID:
	        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	        mDbHelper.delete(notesAdapter.getItem(info.position));
	        fillData();
	        return true;
	    }
	    return super.onContextItemSelected(item);
	}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	Intent i = new Intent(this, NoteEdit.class);
    	i.putExtra("note", notesAdapter.getItem(position));
    	startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			Bundle extras = intent.getExtras();
			Note note;
			switch (requestCode) {
			case ACTIVITY_CREATE:
				note = (Note) extras.getSerializable("note");
				mDbHelper.insert(note);
				fillData();
				break;
			case ACTIVITY_EDIT:
				note = (Note) extras.getSerializable("note");
				// FIXME add an update feature.
				mDbHelper.delete(note);
				mDbHelper.insert(note);
				fillData();
				break;
			}
		}
        
    }
}
