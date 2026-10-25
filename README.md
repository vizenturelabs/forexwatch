<div align="center">
<img width="128" height="128" alt="ic_launcher-playstore" src="https://github.com/user-attachments/assets/e66dd330-8229-492c-8496-b24a68ead3a6" />
</div>

# ForexWatch - Open-Source Android Forex App

ForexWatch is providing real-time market prices, price histories, and market data. 
The app is built using the latest [Android architecture components](https://developer.android.com/topic/architecture/recommendations) - resulting in a scalable, modularised and testable app.

## 📥 **Download**

<div style="display: flex; align-items: center; gap: 15px;">
  <a href="https://github.com/vizenture/forexwatch/releases">
    <img src="https://raw.githubusercontent.com/rubenpgrady/get-it-on-github/refs/heads/main/get-it-on-github.png" alt="Get it on GitHub" height="96" />
  </a>
</div>

## 🛠️ **Built with (love)**

- Kotlin
- Jetpack Compose
- Retrofit
- Coil
- OkHttp
- Coroutines
- Flow
- Hilt
- ViewModel
- Timber logging
- Room
- Material Design 3
- Static code analysis using Ktlint

# 🧱 **Architecture**

ForexWatch is designed around modern Android architecture principles, ensuring scalability and seamless data handling for high-frequency Forex updates.

1. Unidirectional Data Flow (UDF) - UI events trigger actions that update the state, which is then observed by the UI components.
2. Single Source of Truth (SSOT) - The local Room database acts as the primary source for the UI, while the repository handles syncing with the Alpha Vantage API.
3. Drive UI from immutable data models - Application state is represented by immutable data classes and exposed via Kotlin Flow, preventing race conditions during rapid market updates.

The app follows Google's official architecture guidance, utilizing a layered approach:
- UI Layer: Built with Jetpack Compose, strictly observing state changes and dispatching events.
- Domain Layer: Contains use cases that orchestrate business logic, such as transforming Alpha Vantage time-series data.
- Data Layer: Repositories abstract the data sources, seamlessly combining remote REST API calls with Room for offline caching.

## ⭐ **Features**

- Monitor real-time bid/ask prices and percentage changes for major Forex currency pairs.
- Visualize historical price action with animated charts and technical indicators.
- Integrate seamlessly with the twelvedata API for comprehensive historical and intraday market data.
- Search and filter through hundreds of Forex pairs by symbol or name.
- Create and manage personalized watchlists of favorite currency pairs.

<!-- # 🤝 Contributing -->

<!-- # 📥 Contact -->

<!-- # ❤️ Acknowledgements -->

## 🔖 **License**
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
