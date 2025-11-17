package com.example.todoapp.ui.screens.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.domain.model.Task
import com.example.todoapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            taskRepository.getAllTasks()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update { it.copy(
                                tasks = result.data,
                                error = null,
                                isLoading = false
                            ) }
                        }
                        is Result.Error -> {
                            _uiState.update { it.copy(
                                error = result.exception.message,
                                isLoading = false
                            ) }
                        }
                        is Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
        }
    }

    fun onTaskChecked(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isDone = !task.isDone))
        }
    }

    fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
) 