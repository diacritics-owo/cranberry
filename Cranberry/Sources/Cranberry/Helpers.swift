import Cjni
import Foundation

public class Listener: NSObject {
  public let callback: () -> Void

  public init(callback: @escaping () -> Void) {
    self.callback = callback
  }

  public func listen() {
    NotificationCenter.default.addObserver(
      self,
      selector: #selector(listener),
      name: .mrMediaRemoteNowPlayingInfoDidChange,
      object: nil
    )
  }

  @objc func listener() {
    self.callback()
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
