import AppKit
import PrivateMediaRemote

public func x() {}

public class Cranberry {
  public var information: Information = Information.none()

  public init() {
    self.update()
  }

  public func update() {
    let group = DispatchGroup()
    group.enter()

    /*

    example data:

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
        self.information.title = information["kMRMediaRemoteNowPlayingInfoTitle"] as? String
        self.information.artist = information["kMRMediaRemoteNowPlayingInfoArtist"] as? String
        self.information.album = information["kMRMediaRemoteNowPlayingInfoAlbum"] as? String

        self.information.duration.elapsed =
          (information["kMRMediaRemoteNowPlayingInfoElapsedTime"] as? NSNumber)?.floatValue
        self.information.duration.total =
          (information["kMRMediaRemoteNowPlayingInfoDuration"] as? NSNumber)?.floatValue

        self.information.artwork.width =
          (information["kMRMediaRemoteNowPlayingInfoArtworkDataWidth"] as? NSNumber)?.intValue
        self.information.artwork.height =
          (information["kMRMediaRemoteNowPlayingInfoArtworkDataHeight"] as? NSNumber)?.intValue
        self.information.artwork.data =
          information["kMRMediaRemoteNowPlayingInfoArtworkData"] as? Data
        self.information.artwork.mime =
          information["kMRMediaRemoteNowPlayingInfoArtworkMIMEType"] as? String
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
    public var duration: Duration
    public var artwork: Artwork

    public static func none() -> Information {
      return Information(
        title: nil, artist: nil, album: nil, duration: Duration(elapsed: nil, total: nil),
        artwork: Artwork(width: nil, height: nil, data: nil, mime: nil))
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
