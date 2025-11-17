package com.example.todoapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todoapp.ui.screens.taskdetail.TaskDetailScreen
import com.example.todoapp.ui.screens.tasklist.TaskListScreen

sealed class Screen(val route: String) {
    object TaskList : Screen("taskList")
    object TaskDetail : Screen("taskDetail/{taskId}") {
        fun createRoute(taskId: Long) = "taskDetail/$taskId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.TaskList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.TaskList.route) {
            TaskListScreen(
                onAddTask = {
                    navController.navigate(Screen.TaskDetail.createRoute(0L))
                },
                onEditTask = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            TaskDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 