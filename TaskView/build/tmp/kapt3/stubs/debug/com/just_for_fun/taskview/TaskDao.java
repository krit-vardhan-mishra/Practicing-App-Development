package com.just_for_fun.taskview;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u000e\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007H\'J\u0012\u0010\b\u001a\u0004\u0018\u00010\u00052\u0006\u0010\t\u001a\u00020\nH\'J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0010\u0010\r\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'\u00a8\u0006\u000e"}, d2 = {"Lcom/just_for_fun/taskview/TaskDao;", "", "deleteTask", "", "task", "Lcom/just_for_fun/taskview/TaskEntity;", "getAllTasks", "", "getTaskById", "id", "", "insertTask", "", "updateTask", "taskview_debug"})
@androidx.room.Dao()
public abstract interface TaskDao {
    
    @androidx.room.Query(value = "SELECT * FROM tasks")
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.just_for_fun.taskview.TaskEntity> getAllTasks();
    
    @androidx.room.Query(value = "SELECT * FROM tasks WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract com.just_for_fun.taskview.TaskEntity getTaskById(int id);
    
    @androidx.room.Insert()
    public abstract long insertTask(@org.jetbrains.annotations.NotNull()
    com.just_for_fun.taskview.TaskEntity task);
    
    @androidx.room.Update()
    public abstract void updateTask(@org.jetbrains.annotations.NotNull()
    com.just_for_fun.taskview.TaskEntity task);
    
    @androidx.room.Delete()
    public abstract void deleteTask(@org.jetbrains.annotations.NotNull()
    com.just_for_fun.taskview.TaskEntity task);
}