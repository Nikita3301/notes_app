package com.example.pmd2.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {
    @Query("SELECT * FROM tasks ORDER BY done ASC, date DESC")
    fun allTasks(): Flow<List<TasksEntity>>

    @Query("SELECT * FROM tasks WHERE taskId =:taskId")
    fun loadTaskById(taskId: Int): TasksEntity

    @Query("UPDATE tasks SET done=:done WHERE taskId=:taskId")
    fun updateTaskDone(done: Boolean, taskId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tasks: TasksEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTask(tasks: TasksEntity)

    @Update
    fun update(tasks: TasksEntity)

    @Delete
    fun deleteTask(tasks: TasksEntity)

}

