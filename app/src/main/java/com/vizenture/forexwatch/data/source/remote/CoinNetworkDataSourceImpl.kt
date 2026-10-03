package dev.shorthouse.coinwatch.data.source.remote

import dev.shorthouse.coinwatch.data.source.local.datastore.common.PairSort
import dev.shorthouse.coinwatch.data.source.local.datastore.global.Currency
import dev.shorthouse.coinwatch.data.source.remote.model.CoinChartApiModel
import dev.shorthouse.coinwatch.data.source.remote.model.CoinDetailsApiModel
import dev.shorthouse.coinwatch.data.source.remote.model.CoinSearchResultsApiModel
import dev.shorthouse.coinwatch.data.source.remote.model.CoinsApiModel
import dev.shorthouse.coinwatch.data.source.remote.model.FavouriteCoinsApiModel
import dev.shorthouse.coinwatch.data.source.remote.model.FearGreedApiModel
import dev.shorthouse.coinwatch.data.source.remote.model.GlobalMarketCoinStatsApiModel
import dev.shorthouse.coinwatch.data.source.remote.model.GlobalStatsApiModel
import dev.shorthouse.coinwatch.data.source.remote.model.MarketStatsApiModel
import dev.shorthouse.coinwatch.data.source.remote.model.TrendingCoinsApiModel
import retrofit2.Response
import javax.inject.Inject

class CoinNetworkDataSourceImpl @Inject constructor(
    private val coinApi: CoinApi,
) : CoinNetworkDataSource {
    override suspend fun getCoins(
        coinSort: PairSort,
        currency: Currency,
    ): Response<CoinsApiModel> {
        return coinApi.getCoins(
            orderBy = coinSort.getOrderBy(),
            orderDirection = coinSort.getOrderDirection(),
            currencyUUID = currency.toCurrencyUUID()
        )
    }

    override suspend fun getFavouriteCoins(
        coinIds: List<String>,
        coinSort: PairSort,
        currency: Currency,
    ): Response<FavouriteCoinsApiModel> {
        return coinApi.getFavouriteCoins(
            coinIds = coinIds,
            orderBy = coinSort.getOrderBy(),
            orderDirection = coinSort.getOrderDirection(),
            currencyUUID = currency.toCurrencyUUID()
        )
    }

    override suspend fun getCoinDetails(
        coinId: String,
        currency: Currency,
    ): Response<CoinDetailsApiModel> {
        return coinApi.getCoinDetails(
            coinId = coinId,
            currencyUUID = currency.toCurrencyUUID()
        )
    }

    override suspend fun getCoinChart(
        coinId: String,
        chartPeriod: String,
        currency: Currency,
    ): Response<CoinChartApiModel> {
        return coinApi.getCoinChart(
            coinId = coinId,
            chartPeriod = chartPeriod,
            currencyUUID = currency.toCurrencyUUID()
        )
    }

    override suspend fun getCoinSearchResults(
        searchQuery: String,
    ): Response<CoinSearchResultsApiModel> {
        return coinApi.getCoinSearchResults(searchQuery = searchQuery)
    }

    override suspend fun getMarketStats(): Response<MarketStatsApiModel> {
        return coinApi.getMarketStats()
    }

    override suspend fun getGlobalStats(currency: Currency): Response<GlobalStatsApiModel> {
        return coinApi.getGlobalStats(currencyUUID = currency.toCurrencyUUID())
    }

    override suspend fun getGlobalMarketCoinStats(
        currency: Currency,
    ): Response<GlobalMarketCoinStatsApiModel> {
        return coinApi.getGlobalMarketCoinStats(currencyUUID = currency.toCurrencyUUID())
    }

    override suspend fun getFearGreed(): Response<FearGreedApiModel> {
        return coinApi.getFearGreed()
    }

    override suspend fun getTrendingCoins(
        currency: Currency,
    ): Response<TrendingCoinsApiModel> {
        return coinApi.getTrendingCoins(currencyUUID = currency.toCurrencyUUID())
    }
}

private fun Currency.toCurrencyUUID(): String {
    return when (this) {
        Currency.USD -> "yhjMzLPhuIDl"
        Currency.GBP -> "Hokyui45Z38f"
        Currency.EUR -> "5k-_VTxqtCEI"
    }
}

private fun PairSort.getOrderBy(): String {
    return when (this) {
        PairSort.MarketCap -> "marketCap"
        PairSort.Popular -> "24hVolume"
        PairSort.Gainers -> "change"
        PairSort.Losers -> "change"
        PairSort.Newest -> "listedAt"
    }
}

private fun PairSort.getOrderDirection(): String {
    return when (this) {
        PairSort.MarketCap -> "desc"
        PairSort.Popular -> "desc"
        PairSort.Gainers -> "desc"
        PairSort.Losers -> "asc"
        PairSort.Newest -> "desc"
    }
}
