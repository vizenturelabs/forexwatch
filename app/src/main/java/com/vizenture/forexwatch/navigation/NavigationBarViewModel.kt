package com.vizenture.forexwatch.navigation

import androidx.lifecycle.ViewModel
import com.vizenture.forexwatch.domain.reviewprompt.IsReviewPromptEligibleUseCase
import com.vizenture.forexwatch.domain.reviewprompt.RecordReviewPromptAttemptedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationBarViewModel @Inject constructor(
    private val isReviewPromptEligibleUseCase: IsReviewPromptEligibleUseCase,
    private val recordReviewPromptAttemptedUseCase: RecordReviewPromptAttemptedUseCase,
) : ViewModel() {

    suspend fun isReviewPromptEligible(): Boolean = isReviewPromptEligibleUseCase()

    suspend fun recordReviewPromptAttempted() = recordReviewPromptAttemptedUseCase()
}
