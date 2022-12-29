# StrappUI

https://strapp.au

StrappUI is a Kotlin and Swift library, where you can write unit tests to take snapshots of your native Android and iOS components and view them in a convenient web interface.

## Getting Started

The first step for both Android and iOS is to install the Strapp CLI tool. This allows you to run the commands to take snapshots and run the local server.
```
npm install -g strapp-cli
```

Now you can run commands like;

To run the local instance after building
```
strapp run 
```


## Android
Apply the strapp-ui gradle plugin
```
plugins {
    id("au.strapp.strapp-ui").version("22.12.2")
}
```

Add Jitpack repository to your module
```
 repositories {
   // ...
   maven {
     url = uri("https://jitpack.io")
   }
 }
 ```

Now you can write a unit test for each UI component state you want to capture in your builds.

```
class ExampleTests {

    @get:Rule
    val component = StrappComponent(
        name = "Example",
        group = "My group",
    )

    // Jetpack Compose
    @Test
    fun composeView() {
        component.snapshot(label = "With Jetpack Compose") {
            CustomButton(text = "This is my default button")
        }
    }

    // Layout Resource View
    @Test
    fun resourceView() {
        component.snapshot(label = "With XML Layout", layout = R.layout.example) { view ->
            view.findViewById<TextView>(R.id.my_text_view).text = "Set the text of something in the view"
        }
    }
    
}
```

Run the Strapp tests to record your snapshots
```
./gradlew strappConnectedDeviceRecord
```

To run in a Gradle Managed Device for consistency and speed (recommended), add a managed device named `strappDevice` to your projects build.gradle file
```
android {
    // ...
    
    testOptions {
        managedDevices {
            devices {
                maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("strappDevice").apply {
                    device = "Pixel 5"
                    apiLevel = 30
                    // To include Google services, use "google".
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }
    
    // ...
}
```
and then run the managed device record command
```
./gradlew strappManagedDeviceRecord
```

In terminal within your project root directory run;
`strapp run` to run to local Strapp instance to see your component snapshots - accessible at http://localhost:3001


## iOS 
Add this git repository as a Swift package in your iOS project
In XCode: File -> Add packages...
```
git@github.com:strapp-au/strapp-ui.git
```
and select the latest version `22.12.2`

Now you can add a unit test for each UI component state which you would like to take snapshots of.
```
import StrappUI

class MyButtonTest: XCTestCase {
    
    let component = StrappComponent(
        name: "My Button",
        group: "My group",
    )
    
    // SwiftUI
    func testMyButtonSwiftUI() throws {
        try component.snapshot(label: "With SwiftUI") {
          MyButton(text: "Testing")
        }
    }
    
    // UIViewController
    func testMyButtonViewController() throws {
        try component.snapshot(label: "With View Controller", view: MyViewController())
    }
    
}
```
Run these tests within XCode and your snapshots will be generated and ready to go.

Please note that Strapp currently relies on git for future features and creating the build folder. Make sure that your project is under git version control for compatibility.

In terminal within your project root directory run;

`strapp run` to run a local Strapp instance to see your snapshots - accessible at http://localhost:3001


