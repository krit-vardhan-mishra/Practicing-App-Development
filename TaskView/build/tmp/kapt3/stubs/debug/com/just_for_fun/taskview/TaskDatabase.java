package com.just_for_fun.taskview;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000eJ\u0010\u0010\u000f\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\fJ\u000e\u0010\u0014\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\f\u0010\u0015\u001a\u00020\f*\u00020\u0016H\u0002J\f\u0010\u0017\u001a\u00020\u0016*\u00020\fH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/just_for_fun/taskview/TaskDatabase;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "db", "Lcom/just_for_fun/taskview/AppDatabase;", "taskDao", "Lcom/just_for_fun/taskview/TaskDao;", "deleteTask", "", "task", "Lcom/just_for_fun/taskview/Task;", "getAllTask", "", "getTaskById", "id", "", "insertTask", "", "updateTask", "toTask", "Lcom/just_for_fun/taskview/TaskEntity;", "toTaskEntity", "taskview_debug"})
public final class TaskDatabase {
    @org.jetbrains.annotations.NotNull()
    private final com.just_for_fun.taskview.AppDatabase db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.just_for_fun.taskview.TaskDao taskDao = null;
    
    public TaskDatabase(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.just_for_fun.taskview.Task> getAllTask() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.just_for_fun.taskview.Task getTaskById(int id) {
        return null;
    }
    
    public final long insertTask(@org.jetbrains.annotations.NotNull()
    com.just_for_fun.taskview.Task task) {
        return 0L;
    }
    
    public final void updateTask(@org.jetbrains.annotations.NotNull()
    com.just_for_fun.taskview.Task task) {
    }
    
    public final void deleteTask(@org.jetbrains.annotations.NotNull()
    com.just_for_fun.taskview.Task task) {
    }
    
    private final com.just_for_fun.taskview.Task toTask(com.just_for_fun.taskview.TaskEntity $this$toTask) {
        return null;
    }
    
    private final com.just_for_fun.taskview.TaskEntity toTaskEntity(com.just_for_fun.taskview.Task $this$toTaskEntity) {
        return null;
    }
}