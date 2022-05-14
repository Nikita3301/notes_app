package com.example.pmd2.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TasksEntity (
    @PrimaryKey var taskId: Int? = null,
    @ColumnInfo(name = "date") var taskDate: String? = null,
    @ColumnInfo(name = "title") var title: String? = null,
    @ColumnInfo(name = "description") var description: String? = null,
    @ColumnInfo(name = "done") var done: Boolean = false
)
