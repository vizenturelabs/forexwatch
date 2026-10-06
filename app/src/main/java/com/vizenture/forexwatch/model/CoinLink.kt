package com.vizenture.forexwatch.model

data class CoinLink(
    val type: CoinLinkType,
    val url: String,
)

enum class CoinLinkType {
    Website,
    Whitepaper,
    Discord,
    GitHub,
    Reddit,
    Telegram,
    X,
    YouTube
}
