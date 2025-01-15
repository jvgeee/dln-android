# DeepLinkNow Android SDK

DeepLinkNow (DLN) is a lightweight, powerful deep linking and attribution SDK for Android applications. Handle deep links, deferred deep links, and track user attribution with ease.

## Features

- 🔗 Deep link handling
- 📋 Clipboard deep link detection
- 🔒 Secure API communication
- 🚀 Easy integration
- ⚡️ Lightweight implementation
- 🎯 Custom parameters support

## Requirements

- Android API level 21+ (Android 5.0+)
- Kotlin 1.5+

## Installation

Add this to your project's `build.gradle`:

```groovy
dependencies {
    implementation 'com.deeplinknow:deeplinknow-android:0.1.0'
}
```

## Usage

### Initialize the SDK

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DeepLinkNow.initialize(this, "your-api-key-here")
    }
}
```

### Custom Parameters

The SDK supports passing and receiving custom parameters through deep links.

#### Creating Deep Links with Custom Parameters

```kotlin
val customParams = DLNCustomParameters().apply {
    this["referrer"] = "social_share"
    this["is_promo"] = true
    this["discount"] = 20
}

val deepLink = DeepLinkNow.getInstance().createDeepLink(
    path = "/product/123",
    customParameters = customParams
)
// Result: deeplinknow://app/product/123?referrer=social_share&is_promo=true&discount=20
```

#### Handling Deep Links with Custom Parameters

```kotlin
val router = DLNRouter()

router.register("product/:id") { uri, params ->
    val parsed = DeepLinkNow.getInstance().parseDeepLink(uri)

    // Access route parameters
    val productId = params["id"]

    // Access custom parameters
    val referrer = parsed.parameters.string("referrer")
    val isPromo = parsed.parameters.boolean("is_promo") ?: false
    val discount = parsed.parameters.int("discount")

    // Handle the deep link
    navigateToProduct(
        id = productId,
        referrer = referrer,
        isPromo = isPromo,
        discount = discount
    )
}
```

## Documentation

For detailed documentation, visit [docs.deeplinknow.com](https://docs.deeplinknow.com)

## License

DeepLinkNow is available under the MIT license. See the LICENSE file for more info.
