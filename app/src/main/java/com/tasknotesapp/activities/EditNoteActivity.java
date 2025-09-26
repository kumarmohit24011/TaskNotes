package com.tasknotesapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tasknotesapp.R;
import com.tasknotesapp.models.Note;

import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextContent, editTextCategory;
    private Button buttonUpdateNote;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Get note from intent
        note = (Note) getIntent().getSerializableExtra("note");
        if (note == null) {
            Toast.makeText(this, "Error: Note not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Note");

        // Initialize views
        initViews();
        populateFields();
    }

    private void initViews() {
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        editTextCategory = findViewById(R.id.editTextCategory);
        buttonUpdateNote = findViewById(R.id.buttonUpdateNote);
        progressBar = findViewById(R.id.progressBar);

        buttonUpdateNote.setOnClickListener(v -> updateNote());
    }

    private void populateFields() {
        editTextTitle.setText(note.getTitle());
        editTextContent.setText(note.getContent());
        if (note.getCategory() != null) {
            editTextCategory.setText(note.getCategory());
        }
    }

    private void updateNote() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Title is required");
            editTextTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(content)) {
            editTextContent.setError("Content is required");
            editTextContent.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonUpdateNote.setEnabled(false);

        // Update note fields
        note.setTitle(title);
        note.setContent(content);
        note.setCategory(category);
        note.setModifiedDate(new Date());

        db.collection("notes").document(note.getId())
                .set(note)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditNoteActivity.this, "Note updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    buttonUpdateNote.setEnabled(true);
                    Toast.makeText(EditNoteActivity.this, "Error updating note: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}