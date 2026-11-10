package com.example.progr3ssapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.progr3ssapp.utils.SessionManager

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // Auto-login: if a token is saved, skip Login and open the main screens
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val startDestination: Any = remember {
        if (sessionManager.isLoggedIn) MainGraph else Login
    }

    val bottomNavItems = listOf(
        BottomNavItem(route = Home, label = "Home"),
        BottomNavItem(route = Profile, label = "Profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Bottom bar only on screens inside MainGraph (so never on Login or Register)
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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Login> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(MainGraph) {
                            popUpTo<Login> { inclusive = true } // Back won't return to Login
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Register) { launchSingleTop = true }
                    }
                )
            }

            composable<Register> {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(MainGraph) {
                            // Removes both Register and Login, so Back won't return to them
                            popUpTo<Login> { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            navigation<MainGraph>(startDestination = Home) {
                composable<Home> {
                    HomeScreen()
                }

                composable<Profile> {
                    ProfileScreen(
                        onLoggedOut = {
                            navController.navigate(Login) {
                                // Remove every logged-in screen so Back can't re-enter them
                                popUpTo<MainGraph> { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}

private data class BottomNavItem(val route: Any, val label: String)