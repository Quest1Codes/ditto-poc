# Quest1 Demo POS

[Click here to view codebase](https://github.com/Quest1Codes/ditto-poc) | [Click here to view demo video](https://www.youtube.com/watch?v=reocbJzh4jM)

## Overview

The Quest1 Demo POS is an Android Point-of-Sale (POS) application built with modern Android development tools and practices. It showcases a complete, albeit simplified, POS system with features like user authentication, a product catalog, a shopping cart, and a dynamic payment processing system. The application leverages the Ditto platform for real-time data synchronization, enabling features like live analytics and peer-to-peer data sharing.

## Core Technologies

- **Kotlin**: The primary programming language
- **Jetpack Compose**: For building the user interface
- **Hilt**: For dependency injection
- **Retrofit**: For making network requests to authentication and payment APIs
- **Ditto**: For real-time data synchronization and peer-to-peer communication
- **Jetpack ViewModel**: For managing UI-related data
- **Coroutines & Flow**: For asynchronous programming
- **Android Security (EncryptedSharedPreferences)**: For secure storage of sensitive data like authentication tokens

## File-by-File Documentation

## 1. Application & Main Activity

#### DittoPosApp.kt
This is the main application class. The `@HiltAndroidApp` annotation enables Hilt for dependency injection throughout the application.

#### MainActivity.kt
This is the single activity in the application.

- It's annotated with `@AndroidEntryPoint` to allow Hilt to inject dependencies
- The `onCreate` method sets up the UI using Jetpack Compose by calling `setContent`
- `Quest1P0STheme` applies the application's theme
- `AppNavigation()` sets up the navigation graph for the entire app
- It also includes a `requestPermissions()` function to handle the necessary permissions for Ditto's synchronization features

## 2. Dependency Injection (DI)

The `di` package contains Hilt modules for providing dependencies throughout the app.

#### AppModule.kt
Provides application-wide singletons:

- **provideDittoManager**: Initializes and provides the DittoManager, which is the central point of interaction with the Ditto SDK. It's configured with the App ID and authentication URLs from BuildConfig
- **provideMasterKeyAlias & provideEncryptedSharedPreferences**: Set up and provide EncryptedSharedPreferences for securely storing sensitive data like authentication tokens
- **provideDittoStoreManager**: Provides a manager for interacting with the Ditto data store

#### AuthModule.kt
Provides dependencies related to authentication:

- **provideMoshi**: Provides a Moshi instance for JSON serialization/deserialization
- **provideRetrofit**: Provides a Retrofit instance configured for the authentication service
- **provideAuthApiService**: Provides an implementation of the AuthApiService interface for making authentication-related API calls

#### PaymentModule.kt
Provides dependencies for the payment functionality:

- **providePaymentRetrofit**: Provides a separate Retrofit instance for the payment service. It's annotated with `@Named("PaymentRetrofit")` to distinguish it from the authentication Retrofit instance
- **providePaymentApiService**: Provides an implementation of the PaymentApiService interface

## 3. User Interface (UI)

The UI is built entirely with Jetpack Compose. The `ui/view` package contains the screens and their corresponding ViewModels.

#### AuthScreen.kt & AuthViewModel.kt

**Screen**: Provides the UI for user login and registration. It switches between LoginUI and RegisterUI based on user actions. It observes the authState from the AuthViewModel to display loading indicators, error messages, or navigate on success.

**ViewModel**: Manages the state for the authentication screen. It handles the logic for login, register, and logout. It uses EncryptedSharedPreferences to persist the authentication token and user role. It interacts with the DittoManager to provide the authentication token when required for synchronization. It validates the JWT token to check for expiration.

#### ShopScreen.kt & ShopViewModel.kt

**Screen**: This is the main screen of the POS, displaying a list of products. It includes a top app bar with settings and a bottom navigation bar to switch between the shop and the cart. A floating action button (FloatingCheckoutBar) appears when items are added to the cart.

**ViewModel**: Manages the state for the shop. It fetches the list of products and observes the active order using use cases. It combines data from the inventory and the current order to create the ShopUiState. It handles updating the quantity of items in the cart and removing items.

#### CartScreen.kt

**Screen**: Displays the items that the user has added to the cart. Users can adjust quantities or remove items. It shows the total amount and a "Proceed to Payment" button. It shares the ShopViewModel to access cart data.

#### PaymentScreen.kt & PaymentViewModel.kt

**Screen**: Manages the payment flow, showing different UI states like "Selecting Gateway," "Initiating," "Processing," "Successful," or "Failed."

**ViewModel**: Orchestrates the payment process. It uses a MabGatewaySelector (Multi-Armed Bandit) to choose the optimal payment gateway. It simulates the payment process with delays. It updates the UI state based on the payment progress and result.

#### AnalyticsScreen.kt & AnalyticsViewModel.kt

**Screen**: A dashboard that displays store analytics, including live gateway rankings, store performance metrics (total transactions, revenue), and a list of recent transactions.

**ViewModel**: Fetches and combines data from different repositories and use cases to provide a unified AnalyticsUiState.

#### PresenceViewer.kt & PresenceViewModel.kt

**Screen**: Integrates Ditto's DittoPresenceViewer, a pre-built UI component that displays information about other devices on the network that are part of the same Ditto mesh.

**ViewModel**: Provides the Ditto instance required by the DittoPresenceViewer.

#### PaymentDashboardScreen.kt & PaymentDashboardViewModel.kt

**Screen**: Displays a grid of all payment transactions, showing details like the acquirer, amount, status, and timestamp.

**ViewModel**: Loads the list of all transactions.

#### PaymentGatewayScreen.kt & PaymentGatewayViewModel.kt

**Screen**: This screen is the final step before payment. It displays the total order amount, a mock credit card UI, and a summary of the order.

**ViewModel**: Fetches the active order and the optimal payment gateway to prepare for the final payment confirmation.

## 4. UI Components

The `ui/components` package contains reusable Compose functions.

#### components.kt

- **PrimaryActionButton**: A standardized button for primary actions
- **QuantityControlButton**: A component with "+" and "-" buttons to control the quantity of an item
- **ProductItemCard**: A card to display a single product in the shop list

#### TerminalId.kt
A component that displays the current terminal's ID and shows a tooltip with more detailed connection information (like the Ditto peer key and connection status).

#### ConnectionStatusIndicator.kt
A simple dot that changes color (green/red) to indicate the connection status.

## 5. Theming

#### Color.kt
Defines the color palette for the application.

#### Theme.kt
Sets up the MaterialTheme for the application. It defines light and dark color schemes, custom typography (using the "Inter" font), and configures the system status bar color.

---

This documentation provides an understanding of the Quest1 Demo POS application's structure and functionality. For more specific details, you can refer to the inline comments and the implementation within each file.
