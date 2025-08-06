# Quest1 Demo POS

[Click here to view codebase](https://github.com/Quest1Codes/ditto-poc) | [Click here to view demo video](https://www.youtube.com/watch?v=reocbJzh4jM)


## Overview

The Quest1 Demo POS is an Android Point-of-Sale (POS) application built with modern Android development tools and practices. It showcases a complete, albeit simplified, POS system with features like user authentication, a product catalog, a shopping cart, and a dynamic payment processing system. The application leverages the Ditto platform for real-time data synchronization, enabling features like live analytics and peer-to-peer data sharing.

## Core Technologies

* **Kotlin**: The primary programming language.
* **Jetpack Compose**: For building the user interface.
* **Hilt**: For dependency injection.
* **Retrofit**: For making network requests to authentication and payment APIs.
* **Ditto**: For real-time data synchronization and peer-to-peer communication.
* **Jetpack ViewModel**: For managing UI-related data.
* **Coroutines & Flow**: For asynchronous programming.
* **Android Security (EncryptedSharedPreferences)**: For secure storage of sensitive data like authentication tokens.

## Application Entry Points

The entry points are the files that initialize and launch the application.

### `DittoPosApp.kt`

This is the main application class that is created when the app starts. It is annotated with `@HiltAndroidApp`, which sets up the Hilt dependency injection framework for the entire application.

### `MainActivity.kt`

This file contains the main screen of the application and is the primary entry point for the user interface. It sets up the overall theme and navigation for the app. It also handles requesting necessary permissions for the Ditto synchronization framework to function correctly.

## Dependency Injection

Dependency injection is a technique used to provide the necessary objects (dependencies) to a class. This project uses Hilt for dependency injection, and the configuration is organized into modules.

### `di/AppModule.kt`

This module provides application-wide dependencies that are available to all parts of the app. It is responsible for initializing and configuring:
- **DittoManager**: Manages the core functionalities of the Ditto platform.
- **EncryptedSharedPreferences**: Provides a secure way to store sensitive data.
- **DittoStoreManager**: Manages the data store for the Ditto platform.

### `di/AuthModule.kt`

This module provides dependencies related to authentication. It sets up **Retrofit**, a library for making network requests to an authentication service, and **Moshi**, a library for converting JSON data to and from Kotlin objects.

### `di/PaymentModule.kt`

Similar to `AuthModule`, this module is responsible for providing dependencies for payment-related network requests. It configures a separate Retrofit instance for the payment service.

## User Interface (UI)

The UI package contains all the screens and UI components of the application. The UI is built using Jetpack Compose, a modern toolkit for building native Android UI.

### `ui/view/AuthScreen.kt`

This file defines the user interface for user authentication, including both the login and registration screens. It captures user input for user ID and password and communicates with the `AuthViewModel` to handle the authentication logic.

### `ui/view/ShopScreen.kt`

This screen displays the list of products available for sale. It allows users to add items to their cart and view the total number of items and the total amount. It also includes a top app bar with settings and a bottom navigation bar.

### `ui/view/CartScreen.kt`

This screen displays the items that the user has added to their shopping cart. Users can view and adjust the quantity of each item, remove items from the cart, and proceed to payment.

### `ui/view/PaymentScreen.kt`

This screen handles the payment process, showing different states such as "initiating," "processing," "successful," or "failed".

### `ui/view/AnalyticsScreen.kt`

This screen displays analytics and performance data for the store, such as total transactions, revenue, and recent transactions. It also shows rankings of different payment gateways based on their performance.

### `ui/view/PaymentDashboardScreen.kt`

This screen provides a dashboard of all payment transactions, displaying details for each payment such as the acquirer, amount, and status.

### `ui/view/PresenceViewer.kt`

This screen displays the Ditto Presence Viewer, a tool that shows real-time information about other devices connected to the network.

### UI Components

- **`components/composables.kt`**: This file contains reusable UI components like `PrimaryActionButton` for buttons and `QuantityControlButton` for increasing or decreasing item quantities.
- **`components/TerminalId.kt`**: This component displays the terminal ID and provides additional information about the terminal's connection status through a tooltip.
- **`components/ConnectionStatusIndicator.kt`**: A simple visual indicator that shows whether the device is connected or disconnected.

## ViewModels

ViewModels are responsible for preparing and managing the data for the UI. They are designed to store and manage UI-related data in a lifecycle-conscious way.

### `ui/view/AuthViewModel.kt`

This ViewModel handles the logic for user authentication. It interacts with the `AuthRepository` to perform login and registration and manages the authentication state (e.g., idle, loading, success, error). It also manages the authentication token and provides it to the Ditto instance when required.

### `ui/view/ShopViewModel.kt`

This ViewModel manages the state for the `ShopScreen`. It fetches the list of available items from the `InventoryRepository` and keeps track of the items in the shopping cart.

### `ui/view/PaymentViewModel.kt`

This ViewModel handles the logic for the payment process. It interacts with use cases to select the best payment gateway and process the payment. It updates the UI with the current status of the payment.

### `ui/view/AnalyticsViewModel.kt`

This ViewModel provides the data for the `AnalyticsScreen`. It fetches store performance data, recent transactions, and acquirer rankings from the `AnalyticsRepository` and `TransactionUseCase`.

### `ui/view/ShopTopBarViewModel.kt`

This ViewModel provides the data for the top app bar in the `ShopScreen`, including the terminal ID and terminal information.

## Theming

The `ui/theme` directory contains files that define the visual appearance of the application.

- **`Color.kt`**: Defines the color palette used throughout the app.
- **`Theme.kt`**: Sets up the overall theme for the application, including the color schemes for light and dark modes and the typography.


This documentation provides an understanding of the Quest1 Demo POS application's structure and functionality. For more specific details, you can refer to the inline comments and the implementation within each file.
