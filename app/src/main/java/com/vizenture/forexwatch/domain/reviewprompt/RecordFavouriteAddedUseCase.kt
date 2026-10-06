package com.vizenture.forexwatch.domain.reviewprompt

import com.vizenture.forexwatch.data.repository.reviewPrompt.ReviewPromptRepository
import javax.inject.Inject

class RecordFavouriteAddedUseCase @Inject constructor(
    private val reviewPromptRepository: ReviewPromptRepository,
) {
    suspend operator fun invoke() {
        return recordFavouriteAdded()
    }

    private suspend fun recordFavouriteAdded() {
        return reviewPromptRepository.recordFavouriteAdded()
    }
}
