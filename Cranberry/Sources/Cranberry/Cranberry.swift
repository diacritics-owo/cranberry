import Cjni
import CoreImage
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
  let artworkClass = jni.FindClass(env, "diacritics/owo/util/Media$Artwork")!

  let titleField = jni.GetFieldID(env, informationClass, "title", "Ljava/lang/String;")!
  let artistField = jni.GetFieldID(env, informationClass, "artist", "Ljava/lang/String;")!
  let albumField = jni.GetFieldID(env, informationClass, "album", "Ljava/lang/String;")!
  let idField = jni.GetFieldID(env, informationClass, "id", "Ljava/lang/String;")!
  let playbackRateField = jni.GetFieldID(env, informationClass, "playbackRate", "F")!
  let durationField = jni.GetFieldID(
    env, informationClass, "duration", "Ldiacritics/owo/util/Media$Duration;")!
  let artworkField = jni.GetFieldID(
    env, informationClass, "artwork", "Ldiacritics/owo/util/Media$Artwork;")!

  let elapsedField = jni.GetFieldID(env, durationClass, "elapsed", "F")!
  let totalField = jni.GetFieldID(env, durationClass, "total", "F")!

  let widthField = jni.GetFieldID(env, artworkClass, "width", "I")!
  let heightField = jni.GetFieldID(env, artworkClass, "height", "I")!
  let mimeField = jni.GetFieldID(env, artworkClass, "mime", "Ljava/lang/String;")!

  let artwork = jni.AllocObject(env, artworkClass)!
  jni.SetIntField(env, artwork, widthField, data.artwork.width?.int32 ?? 0)
  jni.SetIntField(env, artwork, heightField, data.artwork.height?.int32 ?? 0)
  // TODO: artwork data
  jni.SetObjectField(env, artwork, mimeField, data.artwork.mime?.javaString(env))

  let duration = jni.AllocObject(env, durationClass)!
  jni.SetFloatField(env, duration, elapsedField, data.duration.elapsed ?? 0)
  jni.SetFloatField(env, duration, totalField, data.duration.total ?? 0)

  let information = jni.AllocObject(env, informationClass)!
  jni.SetObjectField(env, information, titleField, data.title?.javaString(env))
  jni.SetObjectField(env, information, artistField, data.artist?.javaString(env))
  jni.SetObjectField(env, information, albumField, data.album?.javaString(env))
  jni.SetObjectField(env, information, idField, data.id?.description.javaString(env))
  jni.SetFloatField(env, information, playbackRateField, data.playbackRate ?? 0)
  jni.SetObjectField(env, information, durationField, duration)
  jni.SetObjectField(env, information, artworkField, artwork)

  return information
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

public class Cranberry {
  public var information: Information = Information.none()

  public init() {
    self.update()
  }

  public func update() {
    let group = DispatchGroup()
    group.enter()

    /*

    example data (please don't judge my taste in music):

    Optional([
      AnyHashable("kMRMediaRemoteNowPlayingInfoArtist"): Käärijä,
      AnyHashable("kMRMediaRemoteNowPlayingInfoShuffleMode"): 1,
      AnyHashable("kMRMediaRemoteNowPlayingInfoArtworkDataWidth"): 600,
      AnyHashable("kMRMediaRemoteNowPlayingInfoArtworkDataHeight"): 600,
      AnyHashable("kMRMediaRemoteNowPlayingInfoTotalTrackCount"): 1,
      AnyHashable("kMRMediaRemoteNowPlayingInfoGenre"): Finnish Pop,
      AnyHashable("kMRMediaRemoteNowPlayingInfoMediaType"): MRMediaRemoteMediaTypeMusic,
      AnyHashable("kMRMediaRemoteNowPlayingInfoTotalQueueCount"): 1,
      AnyHashable("kMRMediaRemoteNowPlayingInfoArtworkData"): <data>,
      AnyHashable("kMRMediaRemoteNowPlayingInfoAlbum"): Välikuolema,
      AnyHashable("kMRMediaRemoteNowPlayingInfoContentItemIdentifier"): -3_230_027_204_935_330_022,
      AnyHashable("kMRMediaRemoteNowPlayingInfoTimestamp"): 2024-09-05 12:55:40 +0000,
      AnyHashable("kMRMediaRemoteNowPlayingInfoPlaybackRate"): 0,
      AnyHashable("kMRMediaRemoteNowPlayingInfoUniqueIdentifier"): -3_230_027_204_935_330_022,
      AnyHashable("kMRMediaRemoteNowPlayingInfoIsMusicApp"): 1,
      AnyHashable("kMRMediaRemoteNowPlayingInfoTrackNumber"): 1,
      AnyHashable("kMRMediaRemoteNowPlayingInfoRepeatMode"): 3,
      AnyHashable("kMRMediaRemoteNowPlayingInfoTitle"): Välikuolema,
      AnyHashable("kMRMediaRemoteNowPlayingInfoElapsedTime"): 17.076,
      AnyHashable("kMRMediaRemoteNowPlayingInfoQueueIndex"): 0,
      AnyHashable("kMRMediaRemoteNowPlayingInfoDuration"): 192.768,
      AnyHashable("kMRMediaRemoteNowPlayingInfoArtworkMIMEType"): image/jpeg,
      AnyHashable("kMRMediaRemoteNowPlayingInfoArtworkIdentifier"):
        f7216811b3951f7e870c4715d9600dcd23325fb991e8261baa2f30c4ad3c72f0,
    ])

    */

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
        self.information.artwork.mime =
          information[kMRMediaRemoteNowPlayingInfoArtworkMIMEType] as? String
      } else {
        self.information = Information.none()
      }

      group.leave()
    }

    group.wait()
  }

  public struct Information {
    public var title: String?
    public var artist: String?
    public var album: String?
    public var playbackRate: Float?
    public var id: Int?
    public var duration: Duration
    public var artwork: Artwork

    public static func none() -> Information {
      return Information(duration: Duration(), artwork: Artwork())
    }

    public struct Artwork {
      public var width: Int?
      public var height: Int?
      public var data: Data?
      public var mime: String?
    }

    public struct Duration {
      public var elapsed: Float?
      public var total: Float?
    }
  }
}
