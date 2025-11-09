package com.swackles.jellyfin.presentation.screens.settings

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootGraph

@NavGraph<RootGraph>
annotation class SettingsGraph(
    val start: Boolean = false
)