package com.swackles.jellyfin.presentation.dashboard

import com.swackles.jellyfin.domain.models.MediaSection
import com.swackles.jellyfin.domain.useCase.GetDashboardUseCase
import com.swackles.jellyfin.presentation.common.BaseScreenViewModal
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModal @Inject constructor(
    getDashboardUseCase: GetDashboardUseCase
) : BaseScreenViewModal<List<MediaSection>>(getDashboardUseCase)