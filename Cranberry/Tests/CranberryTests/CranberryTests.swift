import XCTest

@testable import Cranberry

final class CranberryTests: XCTestCase {
  func test() {
    let cranberry = Cranberry()
    print(cranberry.information)
  }
}
