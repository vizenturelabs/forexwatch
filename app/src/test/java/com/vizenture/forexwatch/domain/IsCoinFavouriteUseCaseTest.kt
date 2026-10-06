package dev.shorthouse.coinwatch.domain

import dev.shorthouse.coinwatch.data.repository.favouriteCoinId.FavouriteForexPairIdRepository
import dev.shorthouse.coinwatch.data.source.local.database.model.FavouriteCoinId
import dev.shorthouse.coinwatch.domain.favourites.IsCoinFavouriteUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verifySequence
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class IsCoinFavouriteUseCaseTest {

    // Class under test
    private lateinit var isCoinFavouriteUseCase: IsCoinFavouriteUseCase

    @MockK
    private lateinit var favouriteForexPairIdRepository: FavouriteForexPairIdRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        isCoinFavouriteUseCase = IsCoinFavouriteUseCase(
            favouriteForexPairIdRepository = favouriteForexPairIdRepository
        )
    }

    @Test
    fun `When use case invoked should return if coin is favourite`() {
        // Arrange
        val favouriteCoinId = FavouriteCoinId(id = "Qwsogvtv82FCd")

        every {
            favouriteForexPairIdRepository.isCoinFavourite(favouriteCoinId = favouriteCoinId)
        } returns flowOf(Result.Success(true))

        // Act
        isCoinFavouriteUseCase(favouriteCoinId = favouriteCoinId)

        // Assert
        verifySequence {
            favouriteForexPairIdRepository.isCoinFavourite(favouriteCoinId = favouriteCoinId)
        }
    }
}
