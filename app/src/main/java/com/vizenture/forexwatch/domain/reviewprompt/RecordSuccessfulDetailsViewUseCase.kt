package com.vizenture.forexwatch.domain.reviewprompt

import com.vizenture.forexwatch.data.repository.reviewPrompt.ReviewPromptRepository
import javax.inject.Inject

class RecordSuccessfulDetailsViewUseCase @Inject constructor(
    private val reviewPromptRepository: ReviewPromptRepository,
) {
    suspend operator fun invoke() {
        return recordSuccessfulDetailsView()
    }

    private suspend fun recordSuccessfulDetailsView() {
        return reviewPromptRepository.recordSuccessfulDetailsView()
    }
}
