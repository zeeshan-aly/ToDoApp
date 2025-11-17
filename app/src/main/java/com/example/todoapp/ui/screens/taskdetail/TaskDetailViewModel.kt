package com.example.todoapp.ui.screens.taskdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long = checkNotNull(savedStateHandle["taskId"])

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            taskRepository.getTaskById(taskId)?.let { task ->
                _uiState.update {
                    it.copy(
                        title = task.title,
                        description = task.description ?: "",
                        dueDate = task.dueDate,
                        isDone = task.isDone
                    )
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateDueDate(dueDate: Date?) {
        _uiState.update { it.copy(dueDate = dueDate) }
    }

    fun toggleTaskStatus() {
        _uiState.update { it.copy(isDone = !it.isDone) }
    }

    fun saveTask() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val task = Task(
                id = taskId,
                title = currentState.title,
                description = currentState.description.takeIf { it.isNotBlank() },
                dueDate = currentState.dueDate,
                isDone = currentState.isDone
            )
            taskRepository.updateTask(task)
        }
    }
}

data class TaskDetailUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: Date? = null,
    val isDone: Boolean = false
) 