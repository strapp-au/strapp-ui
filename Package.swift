// swift-tools-version:5.5

import PackageDescription

let package = Package(
    name: "StrappUI",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "StrappUI",
            targets: ["StrappUI"]),
    ],
    targets: [
        .target(
            name: "StrappUI",
            path: "ios/Sources"),
        .testTarget(
            name: "StrappUITests",
            dependencies: ["StrappUI"],
            path: "ios/Tests/StrappUITests")
    ]
)
