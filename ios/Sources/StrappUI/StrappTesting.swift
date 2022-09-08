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

public enum ComponentLayout {
    case WRAP_CONTENT
    case MATCH_PARENT
}

public class StrappComponent {
    let componentName: String
    let group: String
    let snapshotDirectory: URL?
    let configUrl: URL?
    let layout: ComponentLayout
    
    public init(name: String, group: String = "", filePath: String = #filePath, layout: ComponentLayout = ComponentLayout.WRAP_CONTENT) {
        self.componentName = name
        self.group = group
        self.layout = layout
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

    public func snapshot<Content: View>(label: String = "Default", @ViewBuilder view: () -> Content) throws {
        try _snapshot(label: label, image: view().asImage(layout: self.layout))
    }

    public func snapshot(label: String = "Default", view: UIViewController) throws {
        try _snapshot(label: label, image: view.snapshot())
    }
    
    private func _snapshot(label: String, image: UIImage) throws {
    
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
    public func snapshot() -> UIImage {
        
        // locate far out of screen
        view?.frame = CGRect(x: 0, y: CGFloat(Int.max), width: 1, height: 1)

        let size = UIScreen.main.bounds.size
        view?.bounds = CGRect(origin: .zero, size: size)
        view?.sizeToFit()
        UIApplication.shared.windows.first?.rootViewController?.view?.addSubview(view!)

        let image = view!.asImage()
        view?.removeFromSuperview()
        return image
        
//        let targetSize = view!.intrinsicContentSize
//        let size = CGSize(width: 480, height: targetSize.height)
//        view?.bounds = CGRect(origin: .zero, size: size)
//        view?.backgroundColor = .white
//        view?.overrideUserInterfaceStyle = .light
//
//        let renderer = UIGraphicsImageRenderer(size: size)
//
//        return renderer.image { _ in
//            view?.drawHierarchy(in: view.bounds, afterScreenUpdates: true)
//        }
    }
}

extension UIView {
    func asImage() -> UIImage {
        let renderer = UIGraphicsImageRenderer(bounds: bounds)
        return renderer.image { rendererContext in
// [!!] Uncomment to clip resulting image
//             rendererContext.cgContext.addPath(
//                UIBezierPath(roundedRect: bounds, cornerRadius: 20).cgPath)
//            rendererContext.cgContext.clip()

// As commented by @MaxIsom below in some cases might be needed
// to make this asynchronously, so uncomment below DispatchQueue
// if you'd same met crash
//            DispatchQueue.main.async {
                 layer.render(in: rendererContext.cgContext)
//            }
        }
    }
}

extension SwiftUI.View {
    
    public func asImage(layout: ComponentLayout) -> UIImage {
        let controller = UIHostingController(rootView: self)
        
        // locate far out of screen
        controller.view.frame = CGRect(x: 0, y: CGFloat(Int.max), width: 1, height: 1)
        UIApplication.shared.windows.first!.rootViewController?.view.addSubview(controller.view)
        
        var size: CGSize = .zero
        var origin: CGPoint = .zero
        var height: CGFloat = 0
        switch (layout) {
            case .WRAP_CONTENT:
                size = controller.sizeThatFits(in: UIScreen.main.bounds.size)
                origin = CGPoint(x: 0, y: -(size.height * 0.5 / 2))
                height = size.height * 0.5
            case .MATCH_PARENT:
                size = UIScreen.main.bounds.size
                height = size.height
        }
        controller.view.bounds = CGRect(origin: origin, size: CGSize(width: size.width, height: height))
//        controller.view.sizeToFit()
        controller.view.backgroundColor = .white
        controller.view.overrideUserInterfaceStyle = .light
        let image = controller.view.asImage()
        controller.view.removeFromSuperview()
        return image
    }
    
    public func toVC() -> UIViewController {
        return UIHostingController(rootView: self.edgesIgnoringSafeArea(.all))
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
