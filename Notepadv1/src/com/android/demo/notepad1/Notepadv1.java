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

import com.android.demo.notepad1.modele.Note;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

/**
 * This is the adaptation of the notepad exemple of android, to show how to use AndroidYboDb.
 * original tutotial : http://developer.android.com/resources/tutorials/notepad/index.html
 * @author ybonnel
 *
 */
public class Notepadv1 extends ListActivity {
	
	private NotesDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_list);
        mDbHelper = new NotesDbAdapter(this);
        fillData();
    }
    
    public static final int INSERT_ID = Menu.FIRST;

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
    
    private int mNoteNumber = 1;
    
    private void createNote() {
    	Note note = new Note();
    	note.title = "Note " + mNoteNumber++;
    	note.body = "";
        mDbHelper.insert(note);
        fillData();
    }
    
    private void fillData() {
        // Now create an array adapter and set it to display using our row
    	// FIXME add selectAll feature.
        setListAdapter(new ArrayAdapter<Note>(this, R.layout.notes_row, mDbHelper.select(new Note())));
    }
}
