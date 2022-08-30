import XCTest
@testable import StrappUI
import Shared

final class StrappUITests: XCTestCase {
    func testExample() throws {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct
        // results.
        XCTAssertEqual(Platform().platform, "iOS")
        
    }
}
