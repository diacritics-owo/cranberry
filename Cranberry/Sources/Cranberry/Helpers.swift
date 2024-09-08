import AppKit
import Cjni
import CoreImage.CIFilterBuiltins
import Foundation

extension Data {
  var ciImage: CIImage? {
    CIImage(data: self)
  }
}

extension CIImage {
  // TODO: the image is read as argb but is rendered as rgba, leading to discoloration (e.g. 1001 argb -> 1001 rgba == 1100 argb)
  func resized(_ size: NSSize) -> CIImage? {
    let scale = size.height / (self.extent.height)
    let aspectRatio = size.width / ((self.extent.width) * scale)

    let resize = CIFilter.lanczosScaleTransform()
    resize.inputImage = self
    resize.scale = Float(scale)
    resize.aspectRatio = Float(aspectRatio)

    return resize.outputImage
  }

  var nsImage: NSImage {
    let rep = NSCIImageRep(ciImage: self)
    let image = NSImage(size: rep.size)
    image.addRepresentation(rep)
    return image
  }
}

extension NSImage {
  var png: Data? {
    if let cgImage = self.cgImage(forProposedRect: nil, context: nil, hints: nil) {
      let rep = NSBitmapImageRep(cgImage: cgImage)
      rep.size = self.size
      return rep.representation(using: .png, properties: [:])
    }

    return nil
  }
}

extension UnsafeMutablePointer<JNIEnv> {
  public var jni: JNINativeInterface {
    return self.pointee.pointee
  }
}

extension String {
  public func javaString(_ env: UnsafeMutablePointer<JNIEnv>) -> JavaString {
    return env.jni.NewStringUTF(env, self)!
  }
}

extension Int {
  public var int32: Int32 {
    Int32(self)
  }
}

extension UInt8 {
  public var int32: Int32 {
    Int32(self)
  }
}

extension Bool {
  var intValue: Int {
    return self ? 1 : 0
  }
}
