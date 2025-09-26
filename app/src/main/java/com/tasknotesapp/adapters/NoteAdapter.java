package com.tasknotesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tasknotesapp.R;
import com.tasknotesapp.models.Note;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private OnNoteClickListener onNoteClickListener;
    private OnNoteLongClickListener onNoteLongClickListener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public interface OnNoteLongClickListener {
        void onNoteLongClick(Note note);
    }

    public NoteAdapter(List<Note> noteList, OnNoteClickListener onNoteClickListener, 
                      OnNoteLongClickListener onNoteLongClickListener) {
        this.noteList = noteList;
        this.onNoteClickListener = onNoteClickListener;
        this.onNoteLongClickListener = onNoteLongClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle, textViewContent, textViewCategory, textViewDate;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }

        public void bind(Note note) {
            textViewTitle.setText(note.getTitle());

            // Truncate content for preview
            String content = note.getContent();
            if (content != null && content.length() > 100) {
                content = content.substring(0, 97) + "...";
            }
            textViewContent.setText(content);

            // Show category if available
            if (note.getCategory() != null && !note.getCategory().isEmpty()) {
                textViewCategory.setText(note.getCategory());
                textViewCategory.setVisibility(View.VISIBLE);
            } else {
                textViewCategory.setVisibility(View.GONE);
            }

            // Format modified date
            if (note.getModifiedDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                textViewDate.setText("Modified: " + dateFormat.format(note.getModifiedDate()));
            } else if (note.getCreatedDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                textViewDate.setText("Created: " + dateFormat.format(note.getCreatedDate()));
            }

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (onNoteClickListener != null) {
                    onNoteClickListener.onNoteClick(note);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (onNoteLongClickListener != null) {
                    onNoteLongClickListener.onNoteLongClick(note);
                }
                return true;
            });
        }
    }
}