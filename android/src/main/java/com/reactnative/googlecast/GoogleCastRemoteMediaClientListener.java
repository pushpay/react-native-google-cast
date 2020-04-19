package com.reactnative.googlecast;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;

public class GoogleCastRemoteMediaClientListener
    implements RemoteMediaClient.Listener, RemoteMediaClient.ProgressListener {
  private GoogleCastModule module;
  private boolean playbackStarted;
  private boolean playbackEnded;
  private int currentItemId;

  public GoogleCastRemoteMediaClientListener(GoogleCastModule module) {
    this.module = module;
  }

  @Override
  public void onStatusUpdated() {
    module.runOnUiQueueThread(new Runnable() {
      @Override
      public void run() {
        MediaStatus mediaStatus = module.getMediaStatus();
        if (mediaStatus == null) {
          return;
        }

        if (currentItemId != mediaStatus.getCurrentItemId()) {
          // reset item status
          currentItemId = mediaStatus.getCurrentItemId();
          playbackStarted = false;
          playbackEnded = false;
        }

        module.emitMessageToRN(GoogleCastModule.MEDIA_STATUS_UPDATED,
            WritableMapUtils.fromMediaStatus(mediaStatus));

        if (!playbackStarted &&
            mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING) {
          module.emitMessageToRN(GoogleCastModule.MEDIA_PLAYBACK_STARTED,
              WritableMapUtils.fromMediaStatus(mediaStatus));
          playbackStarted = true;
        }

        if (!playbackEnded &&
            mediaStatus.getIdleReason() == MediaStatus.IDLE_REASON_FINISHED) {
          module.emitMessageToRN(GoogleCastModule.MEDIA_PLAYBACK_ENDED,
              WritableMapUtils.fromMediaStatus(mediaStatus));
          playbackEnded = true;
        }
      }
    });
  }

  @Override
  public void onMetadataUpdated() {}

  @Override
  public void onQueueStatusUpdated() {}

  @Override
  public void onPreloadStatusUpdated() {}

  @Override
  public void onSendingRemoteMediaRequest() {}

  @Override
  public void onAdBreakStatusUpdated() {}

  @Override
  public void onProgressUpdated(final long progressMs, final long durationMs) {
    module.runOnUiQueueThread(new Runnable() {
      @Override
      public void run() {
        MediaStatus mediaStatus = module.getMediaStatus();
        if (mediaStatus == null) {
          return;
        }

        if (mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING) {
          module.emitMessageToRN(
              GoogleCastModule.MEDIA_PROGRESS_UPDATED,
              prepareProgressMessage(progressMs, durationMs));
        }
      }
    });
  }

  @NonNull
  private WritableMap prepareProgressMessage(long progressMs, long durationMs) {
    // needs to be constructed for every message from scratch because reusing a
    // message fails with "Map already consumed"
    WritableMap map = Arguments.createMap();
    map.putInt("progress", (int)progressMs / 1000);
    map.putInt("duration", (int)durationMs / 1000);

    WritableMap message = Arguments.createMap();
    message.putMap("mediaProgress", map);
    return message;
  }
}
