package com.swackles.jellyfin.presentation.dashboard

import androidx.compose.runtime.State
import com.swackles.jellyfin.domain.models.MediaSection
import com.swackles.jellyfin.domain.repository.MediaRepositoryPreview
import com.swackles.jellyfin.domain.useCase.GetDashboardUseCase
import com.swackles.jellyfin.presentation.common.BaseScreenViewModal
import com.swackles.jellyfin.presentation.common.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class DashboardViewModal @Inject constructor(
    getDashboardUseCase: GetDashboardUseCase
) : BaseScreenViewModal<List<MediaSection>>(getDashboardUseCase)

private class StatePreview<T>(override val value: T) : State<T>

class PreviewDashboardViewModal constructor(
    _state: StateHolder<List<MediaSection>>
) : DashboardViewModal(GetDashboardUseCase(MediaRepositoryPreview())) {
    override val state: State<StateHolder<List<MediaSection>>> = StatePreview(_state)
}