#if canImport(Testing)
import Testing
import NumTraits

@Suite("NumTraits Swift Export Tests")
struct NumTraitsExportTests {
    @Test("NumTraits swift module imported cleanly")
    func testSwiftModuleLoads() throws {
        let clamped = clamp(input: Int32(10), min: Int32(-5), max: Int32(5))
        #expect(clamped == 5)
        let absVal = I32Num.shared.abs(value: -42)
        #expect(absVal == 42)
    }
}
#elseif canImport(XCTest)
import XCTest
import NumTraits

final class NumTraitsExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        let clamped = clamp(input: Int32(10), min: Int32(-5), max: Int32(5))
        XCTAssertEqual(clamped, 5)
        let absVal = I32Num.shared.abs(value: -42)
        XCTAssertEqual(absVal, 42)
    }
}
#endif
