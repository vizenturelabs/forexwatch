package dev.shorthouse.coinwatch.data.repository.searchResults

import com.google.common.truth.Truth.assertThat
import dev.shorthouse.coinwatch.rule.MainDispatcherRule
import dev.shorthouse.coinwatch.common.Result
import dev.shorthouse.coinwatch.data.mapper.ForexSearchResultsMapper
import dev.shorthouse.coinwatch.data.source.remote.model.CoinSearchResult
import dev.shorthouse.coinwatch.data.source.remote.model.CoinSearchResultsApiModel
import dev.shorthouse.coinwatch.data.source.remote.model.CoinSearchResultsData
import dev.shorthouse.coinwatch.model.SearchResult
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class ForexSearchResultsRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Class under test
    private lateinit var forexSearchResultsRepository: ForexSearchResultsRepository

    @MockK
    private lateinit var coinNetworkDataSource: CoinNetworkDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        forexSearchResultsRepository = ForexSearchResultsRepositoryImpl(
            coinNetworkDataSource = coinNetworkDataSource,
            coinSearchResultsMapper = ForexSearchResultsMapper(),
            ioDispatcher = mainDispatcherRule.testDispatcher
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When coin search results data is valid should return success`() = runTest {
        // Arrange
        val searchQuery = "bit"

        val expectedResult = Result.Success(
            listOf(
                SearchResult(
                    id = "Qwsogvtv82FCd",
                    symbol = "BTC",
                    name = "Bitcoin",
                    imageUrl = "https://cdn.coinranking.com/bOabBYkcX/bitcoin_btc.svg"
                ),
                SearchResult(
                    id = "ZlZpzOJo43mIo",
                    symbol = "BCH",
                    name = "Bitcoin Cash",
                    imageUrl = "https://cdn.coinranking.com/By8ziihX7/bch.svg"
                )
            )
        )

        coEvery {
            coinNetworkDataSource.getCoinSearchResults(searchQuery)
        } returns
            Response.success(
                CoinSearchResultsApiModel(
                    coinsSearchResultsData = CoinSearchResultsData(
                        coinSearchResults = listOf(
                            CoinSearchResult(
                                id = "Qwsogvtv82FCd",
                                symbol = "BTC",
                                name = "Bitcoin",
                                imageUrl =
                                    "https://cdn.coinranking.com/bOabBYkcX/bitcoin_btc.svg"
                            ),
                            CoinSearchResult(
                                id = "ZlZpzOJo43mIo",
                                symbol = "BCH",
                                name = "Bitcoin Cash",
                                imageUrl = "https://cdn.coinranking.com/By8ziihX7/bch.svg"
                            )
                        )
                    )
                )
            )

        // Act
        val result = forexSearchResultsRepository.getCoinSearchResults(searchQuery)

        // Assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(expectedResult.data)
    }

    @Test
    fun `When coin search results data has null values should populate these with default values and return success`() =
        runTest {
            // Arrange
            val searchQuery = ""

            val expectedResult = Result.Success(
                listOf(
                    SearchResult(
                        id = "Qwsogvtv82FCd",
                        symbol = "",
                        name = "",
                        imageUrl = ""
                    )
                )
            )

            coEvery {
                coinNetworkDataSource.getCoinSearchResults(searchQuery)
            } returns
                Response.success(
                    CoinSearchResultsApiModel(
                        coinsSearchResultsData = CoinSearchResultsData(
                            coinSearchResults = listOf(
                                CoinSearchResult(
                                    id = "Qwsogvtv82FCd",
                                    symbol = null,
                                    name = null,
                                    imageUrl = null
                                )
                            )
                        )
                    )
                )

            // Act
            val result = forexSearchResultsRepository.getCoinSearchResults(searchQuery)

            // Assert
            assertThat(result).isInstanceOf(Result.Success::class.java)
            assertThat((result as Result.Success).data).isEqualTo(expectedResult.data)
        }

    @Test
    fun `When coin id is null should filter from list and return success`() = runTest {
        // Arrange
        val searchQuery = ""

        val expectedResult = Result.Success<List<SearchResult>>(
            emptyList()
        )

        coEvery {
            coinNetworkDataSource.getCoinSearchResults(searchQuery)
        } returns
            Response.success(
                CoinSearchResultsApiModel(
                    coinsSearchResultsData = CoinSearchResultsData(
                        coinSearchResults = listOf(
                            CoinSearchResult(
                                id = null,
                                symbol = null,
                                name = null,
                                imageUrl = null
                            )
                        )
                    )
                )
            )

        // Act
        val result = forexSearchResultsRepository.getCoinSearchResults(searchQuery)

        // Assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(expectedResult.data)
    }

    @Test
    fun `When coin search results is empty should return success with empty list`() = runTest {
        // Arrange
        val searchQuery = ""

        val expectedResult = Result.Success<List<SearchResult>>(
            emptyList()
        )

        coEvery {
            coinNetworkDataSource.getCoinSearchResults(searchQuery)
        } returns
            Response.success(
                CoinSearchResultsApiModel(
                    coinsSearchResultsData = CoinSearchResultsData(
                        coinSearchResults = emptyList()
                    )
                )
            )

        // Act
        val result = forexSearchResultsRepository.getCoinSearchResults(searchQuery)

        // Assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(expectedResult.data)
    }

    @Test
    fun `When coin search results is null should return success with empty list`() = runTest {
        // Arrange
        val searchQuery = ""

        val expectedResult = Result.Success<List<SearchResult>>(
            emptyList()
        )

        coEvery {
            coinNetworkDataSource.getCoinSearchResults(searchQuery)
        } returns
            Response.success(
                CoinSearchResultsApiModel(
                    coinsSearchResultsData = CoinSearchResultsData(
                        coinSearchResults = null
                    )
                )
            )

        // Act
        val result = forexSearchResultsRepository.getCoinSearchResults(searchQuery)

        // Assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(expectedResult.data)
    }

    @Test
    fun `When coin search results data is null should return error`() = runTest {
        // Arrange
        val searchQuery = ""
        val errorMessage = "Unable to fetch coin search results"

        val expectedResult = Result.Error<List<SearchResult>>(
            message = errorMessage
        )

        coEvery {
            coinNetworkDataSource.getCoinSearchResults(searchQuery)
        } returns
            Response.success(
                CoinSearchResultsApiModel(
                    coinsSearchResultsData = null
                )
            )

        // Act
        val result = forexSearchResultsRepository.getCoinSearchResults(searchQuery)

        // Assert
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo(expectedResult.message)
    }

    @Test
    fun `When coin search results api model is null should return error`() = runTest {
        // Arrange
        val searchQuery = ""
        val errorMessage = "Unable to fetch coin search results"

        val expectedResult = Result.Error<List<SearchResult>>(
            message = errorMessage
        )

        coEvery {
            coinNetworkDataSource.getCoinSearchResults(searchQuery)
        } returns
            Response.success(
                null
            )

        // Act
        val result = forexSearchResultsRepository.getCoinSearchResults(searchQuery)

        // Assert
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo(expectedResult.message)
    }

    @Test
    fun `When coin search results has unsuccessful response should return error`() = runTest {
        // Arrange
        val searchQuery = ""
        val errorMessage = "Unable to fetch coin search results"

        val expectedResult = Result.Error<List<SearchResult>>(
            message = errorMessage
        )

        coEvery {
            coinNetworkDataSource.getCoinSearchResults(searchQuery)
        } returns
            Response.error(
                404,
                errorMessage.toResponseBody(null)
            )

        // Act
        val result = forexSearchResultsRepository.getCoinSearchResults(searchQuery)

        // Assert
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo(expectedResult.message)
    }

    @Test
    fun `When coin search results throws should return error`() = runTest {
        // Arrange
        val searchQuery = ""
        val errorMessage = "Unable to fetch coin search results"

        val expectedResult = Result.Error<List<SearchResult>>(
            message = errorMessage
        )

        coEvery {
            coinNetworkDataSource.getCoinSearchResults(searchQuery)
        } throws IllegalArgumentException(errorMessage)

        // Act
        val result = forexSearchResultsRepository.getCoinSearchResults(searchQuery)

        // Assert
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo(expectedResult.message)
    }

    @Test
    fun `When coin search results request is cancelled should rethrow cancellation`() = runTest {
        // Arrange
        val searchQuery = ""
        val cancellationException = CancellationException("Cancelled")

        coEvery {
            coinNetworkDataSource.getCoinSearchResults(searchQuery)
        } throws cancellationException

        // Act & Assert
        try {
            forexSearchResultsRepository.getCoinSearchResults(searchQuery)
            throw AssertionError("Expected CancellationException")
        } catch (e: CancellationException) {
            assertThat(e).isSameInstanceAs(cancellationException)
        }
    }
}
