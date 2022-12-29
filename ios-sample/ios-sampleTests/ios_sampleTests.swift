//
//  ios_sampleTests.swift
//  ios-sampleTests
//
//  Created by Bren Pearson on 29/12/2022.
//

import XCTest
@testable import ios_sample
import SwiftUI

final class ios_sampleTests: XCTestCase {
    
    let component = StrappComponent(name: "SwiftUI Button", group: "Buttons")

    func testExample() throws {
        try component.snapshot {
            Button(action: {}, label: { Label(title: { Text("Button") }, icon: {}) })
        }
    }

}
