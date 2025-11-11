package com.swackles.jellyfin.presentation.screens.auth

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootGraph

@NavGraph<RootGraph>
annotation class AuthGraph(
    val start: Boolean = false
)
