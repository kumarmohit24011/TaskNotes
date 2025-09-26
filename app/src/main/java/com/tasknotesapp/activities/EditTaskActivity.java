package com.tasknotesapp.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tasknotesapp.R;
import com.tasknotesapp.models.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextDueDate;
    private Spinner spinnerPriority;
    private Button buttonUpdateTask;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private Task task;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Get task from intent
        task = (Task) getIntent().getSerializableExtra("task");
        if (task == null) {
            Toast.makeText(this, "Error: Task not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Task");

        // Initialize views
        initViews();
        setupPrioritySpinner();
        setupDatePicker();
        populateFields();
    }

    private void initViews() {
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDueDate = findViewById(R.id.editTextDueDate);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        buttonUpdateTask = findViewById(R.id.buttonUpdateTask);
        progressBar = findViewById(R.id.progressBar);

        buttonUpdateTask.setOnClickListener(v -> updateTask());
    }

    private void setupPrioritySpinner() {
        String[] priorities = {"Low", "Medium", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
    }

    private void setupDatePicker() {
        selectedDate = Calendar.getInstance();

        editTextDueDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditTaskActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        editTextDueDate.setText(dateFormat.format(selectedDate.getTime()));
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }

    private void populateFields() {
        editTextTitle.setText(task.getTitle());
        editTextDescription.setText(task.getDescription());

        // Set priority spinner
        String[] priorities = {"Low", "Medium", "High"};
        for (int i = 0; i < priorities.length; i++) {
            if (priorities[i].equals(task.getPriority())) {
                spinnerPriority.setSelection(i);
                break;
            }
        }

        // Set due date
        if (task.getDueDate() != null) {
            selectedDate.setTime(task.getDueDate());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            editTextDueDate.setText(dateFormat.format(task.getDueDate()));
        }
    }

    private void updateTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();
        String dueDateText = editTextDueDate.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Title is required");
            editTextTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Description is required");
            editTextDescription.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonUpdateTask.setEnabled(false);

        Date dueDate = null;
        if (!TextUtils.isEmpty(dueDateText)) {
            dueDate = selectedDate.getTime();
        }

        // Update task fields
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDueDate(dueDate);

        db.collection("tasks").document(task.getId())
                .set(task)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditTaskActivity.this, "Task updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    buttonUpdateTask.setEnabled(true);
                    Toast.makeText(EditTaskActivity.this, "Error updating task: " + e.getMessage(), Toast.LENGTH_LONG).show();
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