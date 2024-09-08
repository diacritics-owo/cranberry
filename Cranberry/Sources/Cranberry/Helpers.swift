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
  func resized(_ size: NSSize) -> CIImage? {
    let scale = size.height / (self.extent.height)
    let aspectRatio = size.width / ((self.extent.width) * scale)

    let filter = CIFilter.lanczosScaleTransform()
    filter.inputImage = self
    filter.scale = Float(scale)
    filter.aspectRatio = Float(aspectRatio)

    return filter.outputImage
  }

  var nsImage: NSImage {
    let rep = NSCIImageRep(ciImage: self)
    let image = NSImage(size: rep.size)
    image.addRepresentation(rep)
    return image
  }
}

extension NSImage {
  // TODO: not a good solution (sending the png data results in discoloration while rendering even though the actual image is fine
  // (spent a couple hours on it, i wasn't able to figure out exactly how to fix it)
  // (what's even weirder is that other images work fine (see the circumflex testmod))
  var data: String? {
    if let cgImage = self.cgImage(forProposedRect: nil, context: nil, hints: nil) {
      var data = ""
      let rep = NSBitmapImageRep(cgImage: cgImage)

      for x in 0..<Int(self.size.width) {
        for y in 0..<Int(self.size.height) {
          let color = rep.colorAt(x: x, y: y)!
          let a = Int(Float(color.alphaComponent) * 0xff)
          let r = Int(Float(color.redComponent) * 0xff)
          let g = Int(Float(color.greenComponent) * 0xff)
          let b = Int(Float(color.blueComponent) * 0xff)

          data += String(a << 24 + r << 16 + g << 8 + b, radix: 16).leftPadding(
            toLength: 8, withPad: "0")
        }
      }

      return data
    }

    return nil
  }
}

extension String {
  func leftPadding(toLength: Int, withPad character: Character) -> String {
    let stringLength = self.count
    if stringLength < toLength {
      return String(repeatElement(character, count: toLength - stringLength)) + self
    } else {
      return String(self.suffix(toLength))
    }
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
