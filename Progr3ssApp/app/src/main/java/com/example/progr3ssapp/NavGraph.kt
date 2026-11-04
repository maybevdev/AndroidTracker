package com.example.progr3ssapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(route = Home, label = "Home"),
        BottomNavItem(route = Profile(), label = "Profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Bottom bar only on screens inside MainGraph (so never on Login)
    val showBottomBar = currentDestination?.hierarchy?.any {
        it.hasRoute(MainGraph::class)
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo<MainGraph> { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(text = item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Login,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Login> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(MainGraph) {
                            popUpTo<Login> { inclusive = true } // Back won't return to Login
                        }
                    }
                )
            }

            navigation<MainGraph>(startDestination = Home) {
                composable<Home> {
                    HomeScreen(
                        onNavigateToProfile = { userId, userName ->
                            navController.navigate(Profile(userId, userName))
                        }
                    )
                }

                composable<Profile> { backStackEntry ->
                    val profile: Profile = backStackEntry.toRoute()
                    ProfileScreen(profile.userId, profile.userName)
                }
            }
        }
    }
}

private data class BottomNavItem(val route: Any, val label: String)