package com.android.demo.notepad1;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.demo.notepad1.modele.Note;

public class NoteAdapter extends ArrayAdapter<Note> {
	private final List<Note> notes;
	private final LayoutInflater inflater;

	public NoteAdapter(Context context, List<Note> objects) {
		super(context, R.layout.notes_row, objects);
		notes = objects;
		inflater = LayoutInflater.from(getContext());
	}

	private static class ViewHolder {
		private TextView title;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View convertView1 = convertView;
		Note note = notes.get(position);
		NoteAdapter.ViewHolder holder;
		if (convertView1 == null) {
			convertView1 = inflater.inflate(R.layout.notes_row, null);
			holder = new NoteAdapter.ViewHolder();
			holder.title = (TextView) convertView1.findViewById(R.id.text1);
			convertView1.setTag(holder);
		} else {
			holder = (NoteAdapter.ViewHolder) convertView1.getTag();
		}

		holder.title.setText(note.title);
		return convertView1;
	}

}
