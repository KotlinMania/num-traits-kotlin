#if canImport(Testing)
import Testing
import NumTraits

@Suite("NumTraits Swift Export Tests")
struct NumTraitsExportTests {
    @Test("NumTraits swift module imported cleanly")
    func testSwiftModuleLoads() throws {
        #expect(Bool(true), "NumTraits swift module imported cleanly")
    }
}
#elseif canImport(XCTest)
import XCTest
import NumTraits

final class NumTraitsExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        XCTAssertTrue(true, "NumTraits swift module imported cleanly")
    }
}
#endif
