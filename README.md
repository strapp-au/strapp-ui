# StrappUI

https://strapp.au

StrappUI is a Kotlin and Swift library, where you can write unit tests to take snapshots of your native Android and iOS components and view them in a convenient web interface.

## Getting Started

The first step for both Android and iOS is to install the Strapp CLI tool. This allows you to run the commands to take snapshots and run the local server.
```
npm install -g strapp-cli
```

Now you can run commands like;

To take snapshots
```
strapp build
```

To run the local server after building
```
strapp run 
```

### Android
Apply the strapp-ui gradle plugin
```
plugins {
    id("au.strapp.strapp-ui").version("0.1.1")
}
```

Now you can write a unit test for each UI component state you want to capture in your builds.

Jetpack Compose
```
    @get:Rule
    val strapp = StrappTesting(
        componentName = "Example"
    )

    @Test
    fun default() {
        strapp.snap(label = "Default") {
            CustomButton(text = "This is my default button")
        }
    }
```

### iOS 
iOS Swift package coming soon...
