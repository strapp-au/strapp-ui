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


## Android
Apply the strapp-ui gradle plugin
```
plugins {
    id("au.strapp.strapp-ui").version("22.9.2")
}
```

Add Jitpack repository to your module
```
 repositories {
   // ...
   maven {
     url 'https://jitpack.io'
   }
 }
 ```

Now you can write a unit test for each UI component state you want to capture in your builds.

```
class ExampleTests {

    @get:Rule
    val strapp = StrappComponent(
        componentName = "Example",
        group = "My group",
        layout = ComponentLayout.MATCH_PARENT // defaults to WRAP_CONTENT
    )

    // Jetpack Compose
    @Test
    fun composeView() {
        strapp.snapshot(label = "Default") {
            CustomButton(text = "This is my default button")
        }
    }

    // Layout Resource View
    @Test
    fun resourceView() {
        strapp.snapshot(label = "Default", layout = R.layout.example) { view ->
            view.findViewById<TextView>(R.id.my_text_view).text = "Set the text of something in the view"
        }
    }
    
}
```

Add a `strapp.yml` file to your project root directory.
```
android:
  module: app:components
```

In terminal within your project root directory run;
`strapp build` to take your snapshots and prepare the local data,
`strapp run` to run to local server - accessible at http://localhost:3001

Strapp currently can only be added to an Android Library module. We recommend that you create a new module in Android Studio for your UI components if you have not already.


## iOS 
Add this git repository as a Swift package in your iOS project
In XCode: File -> Add packages...
```
git@github.com:strapp-au/strapp-ui.git
```

Now you can add a unit test for each UI component state which you would like to take snapshots of.
```
import StrappUI

class MyButtonTest: XCTestCase {
    
    let strapp = StrappTesting(
        componentName: "My Button",
        group: "My group",
        layout: .MATCH_PARENT // defaults to .WRAP_CONTENT
    )
    
    // SwiftUI
    func testMyButtonSwiftUI() throws {
        try strapp.snapshot(label: "With SwiftUI") {
          MyButton(text: "Testing")
        }
    }
    
    // UIViewController
    func testMyButtonViewController() throws {
        try strapp.snapshot(label: "With View Controller", view: MyViewController())
    }
    
}
```

Add a `strapp.yml` file to your project root directory.
```
ios:
  project: StrappExample.xcodeproj
  scheme: StrappExample
  target: StrappExampleTests
  simulator: iPhone 13 Pro
  OS: 15.4
```

In terminal within your project root directory run;

`strapp build` to take your snapshots and prepare the local data,

`strapp run` to run to local server - accessible at http://localhost:3001


