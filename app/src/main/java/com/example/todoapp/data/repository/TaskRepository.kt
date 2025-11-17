package com.example.todoapp.data.repository

import com.example.todoapp.data.local.dao.TaskDao
import com.example.todoapp.data.local.entity.TaskEntity
import com.example.todoapp.domain.model.Task
import com.example.todoapp.util.Result
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<Result<List<Task>>> {
        return taskDao.getAllTasks()
            .map { entities ->
                try {
                    Result.Success(entities.map { it.toDomain() })
                } catch (e: Exception) {
                    Result.Error(e)
                }
            }
            .catch { e ->
                emit(Result.Error(e as Exception))
            }
    }

    suspend fun getTaskById(id: Long): Result<Task> {
        return try {
            val task = taskDao.getTaskById(id)?.toDomain()
            if (task != null) {
                Result.Success(task)
            } else {
                Result.Error(Exception("Task not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun insertTask(task: Task): Result<Long> {
        return try {
            val id = taskDao.insertTask(task.toEntity())
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            taskDao.updateTask(task.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteTask(task: Task): Result<Unit> {
        return try {
            taskDao.deleteTask(task.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun TaskEntity.toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            dueDate = dueDate,
            isDone = isDone,
            createdAt = createdAt
        )
    }

    private fun Task.toEntity(): TaskEntity {
        return TaskEntity(
            id = id,
            title = title,
            description = description,
            dueDate = dueDate,
            isDone = isDone,
            createdAt = createdAt
        )
    }
}
