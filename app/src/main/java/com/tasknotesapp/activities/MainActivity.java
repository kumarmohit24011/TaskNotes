package com.tasknotesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tasknotesapp.R;
import com.tasknotesapp.adapters.TaskAdapter;
import com.tasknotesapp.adapters.NoteAdapter;
import com.tasknotesapp.models.Task;
import com.tasknotesapp.models.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTasks, recyclerViewNotes;
    private TaskAdapter taskAdapter;
    private NoteAdapter noteAdapter;
    private List<Task> taskList;
    private List<Note> noteList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewEmptyTasks, textViewEmptyNotes;
    private FloatingActionButton fabAddTask, fabAddNote;
    private BottomNavigationView bottomNavigation;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;

    private boolean showingTasks = true; // Track current view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        // Initialize views
        initViews();
        setupRecyclerViews();
        setupBottomNavigation();
        setupSwipeRefresh();

        // Load initial data
        loadTasks();
        showTasksView();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        textViewEmptyTasks = findViewById(R.id.textViewEmptyTasks);
        textViewEmptyNotes = findViewById(R.id.textViewEmptyNotes);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        fabAddTask = findViewById(R.id.fabAddTask);
        fabAddNote = findViewById(R.id.fabAddNote);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupRecyclerViews() {
        // Initialize lists
        taskList = new ArrayList<>();
        noteList = new ArrayList<>();

        // Setup Task RecyclerView
        taskAdapter = new TaskAdapter(taskList, this::onTaskClick, this::onTaskLongClick);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        // Setup Note RecyclerView
        noteAdapter = new NoteAdapter(noteList, this::onNoteClick, this::onNoteLongClick);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotes.setAdapter(noteAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_tasks) {
                showTasksView();
                return true;
            } else if (itemId == R.id.nav_notes) {
                showNotesView();
                return true;
            }
            return false;
        });

        // Setup FABs
        fabAddTask.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
        });

        fabAddNote.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddNoteActivity.class));
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (showingTasks) {
                loadTasks();
            } else {
                loadNotes();
            }
        });
    }

    private void showTasksView() {
        showingTasks = true;
        recyclerViewTasks.setVisibility(View.VISIBLE);
        recyclerViewNotes.setVisibility(View.GONE);
        textViewEmptyNotes.setVisibility(View.GONE);
        fabAddTask.setVisibility(View.VISIBLE);
        fabAddNote.setVisibility(View.GONE);

        updateEmptyState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tasks");
        }
    }

    private void showNotesView() {
        showingTasks = false;
        recyclerViewTasks.setVisibility(View.GONE);
        recyclerViewNotes.setVisibility(View.VISIBLE);
        textViewEmptyTasks.setVisibility(View.GONE);
        fabAddTask.setVisibility(View.GONE);
        fabAddNote.setVisibility(View.VISIBLE);

        loadNotes(); // Load notes if not already loaded
        updateEmptyState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notes");
        }
    }

    private void loadTasks() {
        swipeRefreshLayout.setRefreshing(true);

        db.collection("tasks")
                .whereEqualTo("userId", currentUserId)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (task.isSuccessful()) {
                        taskList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Task taskItem = document.toObject(Task.class);
                            taskItem.setId(document.getId());
                            taskList.add(taskItem);
                        }
                        taskAdapter.notifyDataSetChanged();
                        updateEmptyState();
                    } else {
                        Toast.makeText(MainActivity.this, 
                                "Error loading tasks", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadNotes() {
        swipeRefreshLayout.setRefreshing(true);

        db.collection("notes")
                .whereEqualTo("userId", currentUserId)
                .orderBy("modifiedDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (task.isSuccessful()) {
                        noteList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Note noteItem = document.toObject(Note.class);
                            noteItem.setId(document.getId());
                            noteList.add(noteItem);
                        }
                        noteAdapter.notifyDataSetChanged();
                        updateEmptyState();
                    } else {
                        Toast.makeText(MainActivity.this, 
                                "Error loading notes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmptyState() {
        if (showingTasks) {
            textViewEmptyTasks.setVisibility(taskList.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            textViewEmptyNotes.setVisibility(noteList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    // Task click handlers
    private void onTaskClick(Task task) {
        Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
        intent.putExtra("task", task);
        startActivity(intent);
    }

    private void onTaskLongClick(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTask(task))
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Note click handlers
    private void onNoteClick(Note note) {
        Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
        intent.putExtra("note", note);
        startActivity(intent);
    }

    private void onNoteLongClick(Note note) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> deleteNote(note))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTask(Task task) {
        db.collection("tasks").document(task.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    taskList.remove(task);
                    taskAdapter.notifyDataSetChanged();
                    updateEmptyState();
                    Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error deleting task", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteNote(Note note) {
        db.collection("notes").document(note.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    noteList.remove(note);
                    noteAdapter.notifyDataSetChanged();
                    updateEmptyState();
                    Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error deleting note", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (showingTasks) {
            loadTasks();
        } else {
            loadNotes();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            logout();
            return true;
        } else if (itemId == R.id.action_refresh) {
            if (showingTasks) {
                loadTasks();
            } else {
                loadNotes();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}