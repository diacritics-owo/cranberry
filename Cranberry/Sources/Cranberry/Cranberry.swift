import Cjni
import PrivateMediaRemote

/* @_silgen_name("Java_diacritics_owo_util_Media_getRaw")
public func getRaw(
  env: UnsafeMutablePointer<JNIEnv>,
  class: JavaObject
) -> JavaString {
  let jni = env.jni
  return "hello!!!! i'm so happy i'm going to cry oml jni is working".javaString(env)
} */
// yes it did take a while to figure out

@_silgen_name("Java_diacritics_owo_util_Media_track")
public func track(
  env: UnsafeMutablePointer<JNIEnv>,
  class: JavaObject
) -> JavaObject {
  let jni = env.jni
  let data = Cranberry().information

  let informationClass = jni.FindClass(env, "diacritics/owo/util/Media$Track")!
  let durationClass = jni.FindClass(env, "diacritics/owo/util/Media$Duration")!

  let titleField = jni.GetFieldID(env, informationClass, "title", "Ljava/lang/String;")!
  let artistField = jni.GetFieldID(env, informationClass, "artist", "Ljava/lang/String;")!
  let albumField = jni.GetFieldID(env, informationClass, "album", "Ljava/lang/String;")!
  let idField = jni.GetFieldID(env, informationClass, "id", "Ljava/lang/String;")!
  let playingField = jni.GetFieldID(env, informationClass, "playing", "Z")!
  let playbackRateField = jni.GetFieldID(env, informationClass, "playbackRate", "F")!
  let durationField = jni.GetFieldID(
    env, informationClass, "duration", "Ldiacritics/owo/util/Media$Duration;")!

  let elapsedField = jni.GetFieldID(env, durationClass, "elapsed", "F")!
  let totalField = jni.GetFieldID(env, durationClass, "total", "F")!

  let duration = jni.AllocObject(env, durationClass)!
  jni.SetFloatField(env, duration, elapsedField, data.duration.elapsed ?? 0)
  jni.SetFloatField(env, duration, totalField, data.duration.total ?? 0)

  let information = jni.AllocObject(env, informationClass)!
  jni.SetObjectField(env, information, titleField, data.title?.javaString(env))
  jni.SetObjectField(env, information, artistField, data.artist?.javaString(env))
  jni.SetObjectField(env, information, albumField, data.album?.javaString(env))
  jni.SetObjectField(env, information, idField, data.id?.description.javaString(env))
  jni.SetBooleanField(env, information, playingField, data.playing ? 1 : 0)
  jni.SetFloatField(env, information, playbackRateField, data.playbackRate ?? 0)
  jni.SetObjectField(env, information, durationField, duration)

  return information
}

@_silgen_name("Java_diacritics_owo_util_Artwork_artwork")
public func artwork(
  env: UnsafeMutablePointer<JNIEnv>,
  class: JavaObject,
  width: JavaInt,
  height: JavaInt
) -> JavaObject {
  let jni = env.jni
  let data = Cranberry().information
  let size = NSSize(width: Int(width), height: Int(height))

  let artworkClass = jni.FindClass(env, "diacritics/owo/util/Artwork")!

  let widthField = jni.GetFieldID(env, artworkClass, "width", "I")!
  let heightField = jni.GetFieldID(env, artworkClass, "height", "I")!
  let dataField = jni.GetFieldID(env, artworkClass, "data", "Ljava/lang/String;")!

  let image = data.artwork.data?.ciImage

  let artwork = jni.AllocObject(env, artworkClass)!
  jni.SetIntField(env, artwork, widthField, width)
  jni.SetIntField(env, artwork, heightField, height)
  jni.SetObjectField(
    env, artwork, dataField,
    image?.resized(size)?.nsImage
      .data?.javaString(env))

  return artwork
}

@_silgen_name("Java_diacritics_owo_util_Media_play")
public func play(
  env: UnsafeMutablePointer<JNIEnv>,
  class: JavaObject
) {
  MRMediaRemoteSendCommand(
    MRMediaRemoteCommand(rawValue: MRMediaRemoteCommandPlay.rawValue),
    [:]
  )
}

@_silgen_name("Java_diacritics_owo_util_Media_pause")
public func pause(
  env: UnsafeMutablePointer<JNIEnv>,
  class: JavaObject
) {
  MRMediaRemoteSendCommand(
    MRMediaRemoteCommand(rawValue: MRMediaRemoteCommandPause.rawValue),
    [:]
  )
}

@_silgen_name("Java_diacritics_owo_util_Media_toggle")
public func toggle(
  env: UnsafeMutablePointer<JNIEnv>,
  class: JavaObject
) {
  MRMediaRemoteSendCommand(
    MRMediaRemoteCommand(rawValue: MRMediaRemoteCommandTogglePlayPause.rawValue),
    [:]
  )
}

public class Cranberry {
  public var information: Information = Information.none()

  public init() {
    self.update()
  }

  public func update() {
    let group = DispatchGroup()

    group.enter()
    MRMediaRemoteGetNowPlayingInfo(.global(qos: .default)) { information in
      if let information = information {
        self.information.title = information[kMRMediaRemoteNowPlayingInfoTitle] as? String
        self.information.artist = information[kMRMediaRemoteNowPlayingInfoArtist] as? String
        self.information.album = information[kMRMediaRemoteNowPlayingInfoAlbum] as? String
        self.information.playbackRate =
          information[kMRMediaRemoteNowPlayingInfoPlaybackRate] as? Float
        self.information.id = information[kMRMediaRemoteNowPlayingInfoUniqueIdentifier] as? Int

        self.information.duration.elapsed =
          (information[kMRMediaRemoteNowPlayingInfoElapsedTime] as? NSNumber)?.floatValue
        self.information.duration.total =
          (information[kMRMediaRemoteNowPlayingInfoDuration] as? NSNumber)?.floatValue

        self.information.artwork.width =
          (information["kMRMediaRemoteNowPlayingInfoArtworkDataWidth"] as? NSNumber)?.intValue
        self.information.artwork.height =
          (information["kMRMediaRemoteNowPlayingInfoArtworkDataHeight"] as? NSNumber)?.intValue
        self.information.artwork.data =
          information[kMRMediaRemoteNowPlayingInfoArtworkData] as? Data
      } else {
        self.information = Information.none()
      }

      group.leave()
    }

    group.enter()
    MRMediaRemoteGetNowPlayingApplicationIsPlaying(.global(qos: .default)) { playing in
      self.information.playing = playing
      group.leave()
    }

    group.wait()
  }

  public struct Information {
    public var title: String?
    public var artist: String?
    public var album: String?
    public var playbackRate: Float?
    public var playing: Bool
    public var id: Int?
    public var duration: Duration
    public var artwork: Artwork

    public static func none() -> Information {
      return Information(playing: false, duration: Duration(), artwork: Artwork())
    }

    public struct Artwork {
      public var width: Int?
      public var height: Int?
      public var data: Data?
    }

    public struct Duration {
      public var elapsed: Float?
      public var total: Float?
    }
  }
}
