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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_list);
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
		notesAdapter = new NoteAdapter(this, NoteApplication.getNotesDbAdapter().selectAll(Note.class));
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
	        NoteApplication.getNotesDbAdapter().delete(notesAdapter.getItem(info.position));
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
		fillData();
    }
}
