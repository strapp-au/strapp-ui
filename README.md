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

Add a `strapp.yml` file to your project root directory.
```
version: 0.1

android:
  module: app:components
```

In terminal within your project root directory run;
`strapp build` to take your snapshots and prepare the local data,
`strapp run` to run to local server - accessible at http://localhost:3001


## iOS 
Add this git repository as a Swift package in your iOS project
In XCode: File -> Add packages...
```
git@github.com:strapp-au/strapp-ui.git
```

Now you can add a unit test for each UI component state which you would like to take snapshots of.
```
@testable import StrappUI

class MyButtonTest: XCTestCase {
    
    let strapp = StrappTesting(componentName: "My Button")
    
    func testMyButtonDefault() throws {
        try strapp.snap(label: "Default") {
          MyButton(text: "Testing")
        }
    }
}
```

Add a `strapp.yml` file to your project root directory.
```
version: 0.1

ios:
  project: StrappExample.xcodeproj
  scheme: StrappExample
  target: StrappExampleTests
  simulator: iPhone 13 Pro
  OS: 15.4
```

Set a PROJECT_DIR environment variable to your local path for the repository;

In XCode; Scheme -> Edit scheme... -> Test -> Arguments -> Environment Variables

`PROJECT_DIR` | `/Users/myuser/path/to/project`

In terminal within your project root directory run;

`strapp build` to take your snapshots and prepare the local data,

`strapp run` to run to local server - accessible at http://localhost:3001


