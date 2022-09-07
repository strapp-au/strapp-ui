//
//  StrappUI.swift
//
//  Created by Bren Pearson on 19/2/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import Shared

func searchPathForStrappFolder(folder: String) -> Bool {
    let fm = FileManager.default
    print("Folder: " + folder)
    do {
        let children = try fm.contentsOfDirectory(atPath: folder)
        for child in children {
            if (child == ".strapp") {
                print("Matched: " + child)
                return true
            } else {
                print("Not Matched: " + child)
            }
        }
    } catch {
        return false
    }
    
    return false
}

func getStrappFolder(filePath: String) -> String? {
    var file = URL(fileURLWithPath: filePath)
    for _ in 0...5 {
        file.deleteLastPathComponent()
        if (searchPathForStrappFolder(folder: file.path)) {
            return file.appendingPathComponent(".strapp").path
        }
    }
    return nil
}

public class StrappComponent {
    let componentName: String
    let group: String
    let size: CGSize?
    let snapshotDirectory: URL?
    let configUrl: URL?
    
    public init(name: String, group: String = "", filePath: String = #filePath, size: CGSize? = nil) {
        self.componentName = name
        self.group = group
        self.size = size
        let strappDir = getStrappFolder(filePath: filePath)

        if strappDir != nil {
            self.snapshotDirectory = URL(string: strappDir! + "/snapshots/ios")
            self.configUrl = URL(fileURLWithPath: strappDir! + "/config.json", isDirectory: false)
            
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


    struct SnapshotContainer<Content: View>: View {
        var view: Content
      var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .top, spacing: 0){
                view
                Spacer()
            }
          
          Spacer()
        }
      }
    }
    
    public func snapshot<Content: View>(label: String = "Default", @ViewBuilder view: () -> Content) throws {
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
            
            let image = SnapshotContainer(view: view()).snapshot(size: size)
            let fileManager = FileManager.default
            let exists = fileManager.fileExists(atPath: (file.path))
            if exists {
               try image.pngData()!.write(to: URL(fileURLWithPath: file.path, isDirectory: false))
            } else {
               fileManager.createFile(atPath: file.path, contents: image.pngData()!, attributes: [:])
            }

            let configBuilder = StrappConfigBuilder()
            
            do {
                let config = try getConfig()

                if config != nil {
                    let newConfig: String = configBuilder.addSnapshot(
                        componentName: self.componentName,
                        componentGroup: self.group,
                        snapshotLabel: label,
                        snapshotPath: file.path,
                        configString: config!
                    )
                    writeConfig(config: newConfig)
                }
                
            } catch {
                let json = try JSONEncoder().encode(
                    StrappConfig(
                        components: [
                            Component(name: componentName, group: group, snapshots: [Snapshot(label: label, type: "png", src: file.path)])
                        ]
                    )
                )
                let config = String(decoding: json, as: UTF8.self).replacingOccurrences(of: "\\", with: "")
                writeConfig(config: config)
            }
            
            assert(fileManager.fileExists(atPath: (file.path)))
        }
    }
    
    private func getConfig() throws -> String? {
        return try String(decoding: Data(contentsOf: configUrl!), as: UTF8.self).replacingOccurrences(of: "\\", with: "")
    }
    
    private func writeConfig(config: String) {
        let url = URL(fileURLWithPath: configUrl!.path, isDirectory: false)
        do {
            try config.write(to: url, atomically: true, encoding: String.Encoding.utf8)
        } catch {
            
        }
    }
    
    struct StrappConfig: Codable {
        var components: Array<Component>
    }

    struct Component: Codable {
        var name: String
        var group: String
        var snapshots: Array<Snapshot>
    }

    struct Snapshot: Codable {
        var label: String
        var type: String
        var src: String
    }
}

extension UIViewController {
    public func snapshot(width: CGFloat, height: CGFloat) -> UIImage {
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
    public func toVC(width: CGFloat, height: CGFloat) -> UIViewController {
        let controller = UIHostingController(rootView: self)
        let view = controller.view
        let intrinsicBounds = view?.intrinsicContentSize
        view?.bounds = CGRect(origin: .zero, size: CGSize(width: width / 3, height: intrinsicBounds!.height))// height / 3))
        view?.backgroundColor = .white
        return controller
    }
    
    public func snapshot(size: CGSize? = nil, backgroundColor: UIColor = .white) -> UIImage {
        
        let controller = UIHostingController(rootView: self.edgesIgnoringSafeArea(.all))
        let view = controller.view

        let targetSize = CGSize(width: 296, height: 296)//controller.view.intrinsicContentSize
        view?.bounds = CGRect(origin: .zero, size: targetSize)
        view?.backgroundColor = backgroundColor
        view?.overrideUserInterfaceStyle = .light

        let renderer = UIGraphicsImageRenderer(size: targetSize)

        return renderer.image { _ in
            view?.drawHierarchy(in: controller.view.bounds, afterScreenUpdates: true)
        }
//
//        let targetSize = controller.view.intrinsicContentSize
//        let size = CGSize(width: targetSize.width, height: targetSize.height * 2)
////        let size = CGSize(width: CGFloat(720), height: CGFloat(1280))
//        view?.bounds = CGRect(origin: .zero, size: size)
//        view?.backgroundColor = .gray
//
//        let renderer = UIGraphicsImageRenderer(size: size)
//
//        return renderer.image { _ in
//            view?.drawHierarchy(in: controller.view.bounds, afterScreenUpdates: true)
//        }
    }
}
