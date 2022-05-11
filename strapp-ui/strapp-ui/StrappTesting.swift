//
//  StrappTesting.swift
//  iosAppTests
//
//  Created by Bren Pearson on 19/2/2022.
//  Copyright © 2022 orgName. All rights reserved.
//
import Foundation
import SwiftUI


class StrappTesting {
    let componentName: String
    let projectDir = ProcessInfo.processInfo.environment["PROJECT_DIR"]
    fileprivate var packageRootPath = URL(fileURLWithPath: #filePath)
    let snapshotDirectory: URL?
    let configUrl: URL?
    
    init(componentName: String) {
        self.componentName = componentName
        if projectDir != nil {
            self.snapshotDirectory = URL(string: projectDir! + "/.strapp/snaps/ios")
            self.configUrl = URL(fileURLWithPath: projectDir! + "/.strapp/config.json", isDirectory: false)
            
//            do {
//                var config = try getConfig()
//
//                if config != nil {
//                    // Clean the
//                    config?.components.ios.removeAll()
//                    try writeConfig(config: config!)
//                }
//            } catch {
//                
//            }
        } else {
            self.snapshotDirectory = nil
            self.configUrl = nil
        }
    }
    
    func snap<Content: View>(label: String = "Default", @ViewBuilder view: () -> Content) throws {
        print(snapshotDirectory!.absoluteString)
        assert(snapshotDirectory != nil)
        if snapshotDirectory != nil {
            try FileManager.default.createDirectory(atPath: snapshotDirectory!.path, withIntermediateDirectories: true, attributes: nil)
            let componentNameUtf8 = String(data: componentName.lowercased().data(using: .utf8)!, encoding: .utf8)!
            let labelUtf8 = String(data: label.lowercased().data(using: .utf8)!, encoding: .utf8)!
            let file = snapshotDirectory!.appendingPathComponent(
                componentNameUtf8
                    .appending("_")
                    .appending(labelUtf8)
                    .appending(".png")
            )
            
            let image = view().snapshot()
            let fileManager = FileManager.default
            let exists = fileManager.fileExists(atPath: (file.path))
            if exists {
               try image.pngData()!.write(to: URL(fileURLWithPath: file.path, isDirectory: false))
            } else {
               fileManager.createFile(atPath: file.path, contents: image.pngData()!, attributes: [:])
            }
            
            let snap = StrappSnap(label: label, snap: file.path)

            do {
                var config = try getConfig()

                if config != nil {
                    var snaps = config?.components.ios.first(where: { component in
                        component.name == componentName
                    })?.snaps
                    
                    if snaps != nil {
                        snaps?.removeAll(where: { c in
                            c.label == label
                        })
                        snaps?.append(snap)
                        let component = StrappComponent(name: componentName, snaps: snaps!)
                        config?.components.ios.removeAll(where: { component in
                            component.name == componentName
                        })
                        config?.components.ios.append(component)
                        snaps = component.snaps
                    } else {
                        
                        let component = StrappComponent(name: componentName, snaps: [snap])
                        config?.components.ios.append(component)
                        snaps = component.snaps
                    }
                    try writeConfig(config: config!)
                }
                
            } catch {
                let config = StrappConfig(components: StrappComponents(android: [], ios: [StrappComponent(name: componentName, snaps: [snap])]))
                try writeConfig(config: config)
            }
            
            assert(fileManager.fileExists(atPath: (file.path)))
        }
    }
    
    private func getConfig() throws -> StrappConfig? {
        return try JSONDecoder().decode(StrappConfig.self, from: Data(contentsOf: configUrl!))
    }
    
    private func writeConfig(config: StrappConfig) throws {
        try JSONEncoder().encode(config).write(to: URL(fileURLWithPath: configUrl!.path, isDirectory: false))
    }
    
    struct StrappConfig: Codable {
        var components: StrappComponents
    }

    struct StrappComponents: Codable {
        var android: Array<StrappComponent>
        var ios: Array<StrappComponent>
    }

    struct StrappComponent: Codable {
        var name: String
        var snaps: Array<StrappSnap>
    }

    struct StrappSnap: Codable {
        var label: String
        var snap: String
    }
}

extension UIViewController {
    func snapshot(width: CGFloat, height: CGFloat) -> UIImage {
        let intrinsicBounds = view?.intrinsicContentSize
        let targetSize = CGSize(width: width / 3, height: intrinsicBounds!.height)// height / 3))
        view?.bounds = CGRect(origin: .zero, size: targetSize)
        view?.backgroundColor = .white

        let renderer = UIGraphicsImageRenderer(size: targetSize)

        return renderer.image { _ in
            view?.drawHierarchy(in: view.bounds, afterScreenUpdates: true)
        }
    }
}

extension SwiftUI.View {
    func toVC(width: CGFloat, height: CGFloat) -> UIViewController {
        let controller = UIHostingController(rootView: self)
        let view = controller.view
        let intrinsicBounds = view?.intrinsicContentSize
        view?.bounds = CGRect(origin: .zero, size: CGSize(width: width / 3, height: intrinsicBounds!.height))// height / 3))
        view?.backgroundColor = .white
        return controller
    }
    
    func snapshot() -> UIImage {
        let controller = UIHostingController(rootView: self)
        let view = controller.view

//        let targetSize = controller.view.intrinsicContentSize
        let size = CGSize(width: CGFloat(720), height: CGFloat(1280))
        view?.bounds = CGRect(origin: .zero, size: size)
        view?.backgroundColor = .white

        let renderer = UIGraphicsImageRenderer(size: size)

        return renderer.image { _ in
            view?.drawHierarchy(in: controller.view.bounds, afterScreenUpdates: true)
        }
    }
}
