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
import com.swackles.libs.jellyfin.Pagination
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
        val items: Map<Int, List<LibraryItem>>,
        val totalRecordCount: Int,
        val hasMoreContent: Boolean,
        val limit: Int
    ): Step

    data class LoadContent(
        val possibleFilters: JellyfinFilters,
        val activeFilters: LibraryFilters
    ): Step
}

private const val LIMIT = 3 * 7

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

                val possibleFiltersDeferred = async(Dispatchers.IO) { libraryClient.getFilters() }
                val result = async(Dispatchers.IO) { libraryClient.search(filters, Pagination(page = 0, limit = LIMIT)) }.await()

                setStep(Step.ShowContent(
                    possibleFilters = possibleFiltersDeferred.await(),
                    activeFilters = filters,
                    items = mapOf(result.page to result.items),
                    totalRecordCount = result.totalRecordCount,
                    hasMoreContent = result.totalRecordCount > (result.page + 1) * result.limit,
                    limit = result.limit
                ))
            }
            is Step.LoadContent -> Log.e("SearchViewModel", "Content is already loading, not doing another search")
            is Step.ShowContent -> {
                Log.d("SearchViewModel", "Searching the library based on filters $filters")

                setStep(Step.ShowContent(
                    items = mapOf(0 to emptyList()),
                    possibleFilters = (state.value.step as Step.LoadContent).possibleFilters,
                    activeFilters = filters,
                    totalRecordCount = -1,
                    hasMoreContent = false,
                    limit = LIMIT
                ))

                val result = libraryClient.search(filters, Pagination(page = 0, limit = LIMIT))

                setStep(Step.ShowContent(
                    items = mapOf(result.page to result.items),
                    possibleFilters = (state.value.step as Step.LoadContent).possibleFilters,
                    activeFilters = filters,
                    totalRecordCount = result.totalRecordCount,
                    hasMoreContent = result.totalRecordCount > (result.page + 1) * result.limit,
                    limit = result.limit
                ))
            }
        }
    }

    fun loadPage(page: Int) = viewModelScope.launch {
        val step = state.value.step
        if (step !is Step.ShowContent || !step.hasMoreContent) {
            Log.e("SearchViewModel", "Trying to load more pages while not in ShowContent step or there is nothing more to load")
            Log.d("SearchViewModel", step.toString())

            return@launch
        }
        Log.d("SearchViewModel", "Loading page $page")

        setStep(step.copy(
            items = step.items.plus(page to emptyList()),
            hasMoreContent = step.totalRecordCount > (page + 1) * step.limit
        ))

        val searchDeferred =
            async(Dispatchers.IO) { libraryClient.search(step.activeFilters, Pagination(page = page, limit = LIMIT)) }

        val result = searchDeferred.await()

        val newStep = state.value.step

        if (newStep !is Step.ShowContent) {
            Log.e("SearchViewModel", "Abandoning load page since state has changed")

            return@launch
        }

        val items = newStep.items.toMutableMap()
        items[page] = result.items

        setStep(newStep.copy(items = items))
    }

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
