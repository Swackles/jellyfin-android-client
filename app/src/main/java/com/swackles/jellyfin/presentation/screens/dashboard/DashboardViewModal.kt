package com.swackles.jellyfin.presentation.screens.dashboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.swackles.jellyfin.util.BaseViewModel
import com.swackles.jellyfin.util.ViewState
import com.swackles.libs.jellyfin.LibraryClient
import com.swackles.libs.jellyfin.LibraryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

data class ButtonCard(val title: String, val color: Color, val libraryItem: LibraryItem)

enum class DashboardCarouselAction {
    DETAIL,
    PLAYER
}

interface UiSection {
    data class Carousel(val title: String, val action: DashboardCarouselAction, val items: List<LibraryItem>): UiSection
    data class ButtonCards(val cards: List<ButtonCard>): UiSection
}

data class UiState(
    val step: Step
): ViewState

sealed interface Step {
    data object Loading: Step
    data class Success(val sections: List<UiSection>): Step
}

@HiltViewModel
class DashboardViewModal @Inject constructor(
    private val libraryClient: LibraryClient
): BaseViewModel<UiState>() {
    override fun initialState(): UiState = UiState(step = Step.Loading)

    init { loadData() }

    private fun loadData() = viewModelScope.launch {
        val continueWatchingDeferred = async(Dispatchers.IO) { libraryClient.getContinueWatching() }
        val newlyAddedDeferred = async(Dispatchers.IO) { libraryClient.getNewlyAdded() }
        val recommendedDeferred = async(Dispatchers.IO) { libraryClient.getRecommended() }
        val favorites = async(Dispatchers.IO) { libraryClient.getFavorites() }.await()

        val genreLibraryItemMap = favorites
            .flatMap { item ->
                when(item) {
                    is LibraryItem.Movie -> item.genres
                    is LibraryItem.Series -> item.genres
                    else -> emptyList()
                }.map { genre -> genre to item }
            }.groupBy({ it.first }, { it.second })

        setStepSuccess(listOf(
            UiSection.Carousel(
                title = "Continue Watching",
                action = DashboardCarouselAction.PLAYER,
                items = continueWatchingDeferred.await()
            ),
            UiSection.Carousel(
                title = "New",
                action = DashboardCarouselAction.DETAIL,
                items = newlyAddedDeferred.await()
            ),
            UiSection.ButtonCards(cards = getMostPopularGenres(favorites).take(6).map {
                ButtonCard(title = it, color = it.toColor(), libraryItem = genreLibraryItemMap[it]!!.random())
            }),
            UiSection.Carousel(
                title = "Recommended",
                action = DashboardCarouselAction.DETAIL,
                items = recommendedDeferred.await()
            ),
            UiSection.Carousel(
                title = "Favorites",
                action = DashboardCarouselAction.DETAIL,
                items = favorites.take(10)
            )
        ))
    }

    private fun getMostPopularGenres(items: List<LibraryItem>): List<String> {
        val genres = items.map {
            when(it) {
                is LibraryItem.Movie -> it.genres
                is LibraryItem.Series -> it.genres
                else -> emptyList()
            }
        }.flatten()
        val genresFrequencyMap = genres.groupingBy { it }.eachCount()

        return genres.distinct().sortedByDescending { genresFrequencyMap[it] }
    }

    private fun setStepSuccess(sections: List<UiSection>) = setState { copy(step = Step.Success(sections = sections)) }

    private fun String.toColor(): Color {
        var hash = 0
        for (char in this) {
            hash = char.code + ((hash shl 5) - hash)
        }

        return Color.hsl(hash.absoluteValue.toFloat() % 260 + 100, CARD_SATURATION, CARD_LIGHTNESS)
    }

    private companion object {
        const val CARD_SATURATION = .8f
        const val CARD_LIGHTNESS = .5f
    }
}
