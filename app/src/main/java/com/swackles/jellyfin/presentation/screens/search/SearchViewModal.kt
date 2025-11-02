package com.swackles.jellyfin.presentation.screens.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.swackles.jellyfin.util.BaseViewModel
import com.swackles.jellyfin.util.ViewState
import com.swackles.libs.jellyfin.JellyfinFilters
import com.swackles.libs.jellyfin.LibraryClient
import com.swackles.libs.jellyfin.LibraryFilters
import com.swackles.libs.jellyfin.LibraryItem
import com.swackles.libs.jellyfin.MediaItemType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val step: Step
): ViewState

sealed interface Step {
    object Loading: Step
    data class ShowContent(
        val possibleFilters: JellyfinFilters,
        val activeFilters: LibraryFilters,
        val items: List<LibraryItem>
    ): Step

    data class LoadContent(
        val possibleFilters: JellyfinFilters,
        val activeFilters: LibraryFilters
    ): Step
}

@HiltViewModel
class SearchViewModal @Inject constructor(
    private val libraryClient: LibraryClient
): BaseViewModel<UiState>() {
    override fun initialState(): UiState = UiState(step = Step.Loading)

    fun updateQuery(newQuery: String) {
        val step = state.value.step

        when(step) {
            is Step.Loading -> Log.e("SearchViewModel", "No Query to update")
            is Step.ShowContent -> setStep(step.copy(activeFilters = step.activeFilters.copy(query = newQuery.ifEmpty { null })))
            is Step.LoadContent -> setStep(step.copy(activeFilters = step.activeFilters.copy(query = newQuery.ifEmpty { null })))

        }
    }

    fun search(filters: LibraryFilters) = viewModelScope.launch {
        val filters = filters.copy(
            mediaTypes =
                if (filters.mediaTypes.isNotEmpty()) filters.mediaTypes.filter { MEDIA_TYPE_WHITELIST.contains(it) }
                else MEDIA_TYPE_DEFAULT
        )

        Log.d("SearchViewModel", "Searching with filters \"$filters\"")

        val step = state.value.step

        when(step) {
            is Step.Loading -> {
                Log.d("SearchViewModel", "Loading inital configuration")

                val resultDeferred = async(Dispatchers.IO) { libraryClient.search(filters) }
                val possibleFiltersDeferred = async(Dispatchers.IO) { libraryClient.getFilters() }

                setStep(Step.ShowContent(
                    possibleFilters = possibleFiltersDeferred.await(),
                    activeFilters = filters,
                    items = resultDeferred.await()
                ))
            }
            is Step.LoadContent -> Log.e("SearchViewModel", "Content is already loading, not doing another search")
            is Step.ShowContent -> {
                Log.d("SearchViewModel", "Searching the library based on filters $filters")

                setStep(step.toLoadContent())

                setStep((state.value.step as Step.LoadContent).toShowContent(
                    items = libraryClient.search(filters),
                    filters = filters,
                ))
            }
        }

    }

    private fun Step.LoadContent.toShowContent(items: List<LibraryItem>, filters: LibraryFilters) =
        Step.ShowContent(
            possibleFilters = this.possibleFilters,
            activeFilters = filters,
            items = items
        )

    private fun Step.ShowContent.toLoadContent() =
        Step.LoadContent(
            possibleFilters = this.possibleFilters,
            activeFilters = this.activeFilters
        )

    private fun setStep(step: Step) = setState { copy(step = step) }

    private companion object {
        val MEDIA_TYPE_WHITELIST = listOf(MediaItemType.MOVIE, MediaItemType.SERIES)
        val MEDIA_TYPE_DEFAULT = listOf(MediaItemType.MOVIE, MediaItemType.SERIES)
    }
}
