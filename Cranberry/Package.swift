// swift-tools-version: 5.10

import PackageDescription

let package = Package(
  name: "Cranberry",
  products: [
    .library(
      name: "Cranberry",
      type: .dynamic,
      targets: ["Cranberry"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/PrivateFrameworks/MediaRemote",
      .upToNextMinor(from: "0.1.0")
    ),
    .package(
      url: "https://github.com/diacritics-owo/swift-jni",
      branch: "swiftpm"
    ),
  ],
  targets: [
    .target(
      name: "Cranberry",
      dependencies: [
        .product(name: "PrivateMediaRemote", package: "MediaRemote"),
        .product(name: "MediaRemote", package: "MediaRemote"),
      ]
    ),
    .testTarget(
      name: "CranberryTests",
      dependencies: ["Cranberry"]
    ),
  ]
)
