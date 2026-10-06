package com.vizenture.forexwatch.domain.reviewprompt

import com.vizenture.forexwatch.data.repository.reviewPrompt.ReviewPromptRepository
import javax.inject.Inject

class RecordReviewPromptAttemptedUseCase @Inject constructor(
    private val reviewPromptRepository: ReviewPromptRepository,
) {
    suspend operator fun invoke() {
        return recordReviewPromptAttempted()
    }

    private suspend fun recordReviewPromptAttempted() {
        return reviewPromptRepository.recordReviewPromptAttempted()
    }
}
