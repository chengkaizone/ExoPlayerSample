package com.tony.exoplayersample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.tony.exoplayersample.R;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.AUDIO_SERVICE;
import static com.google.android.exoplayer2.ExoPlayer.STATE_ENDED;
import static com.google.android.exoplayer2.ExoPlayer.STATE_READY;
import static com.google.android.exoplayer2.SimpleExoPlayer.EXTENSION_RENDERER_MODE_OFF;

/**
 * Created by tony on 2017/7/19.
 * 视频播放组件
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class AVPlayerView extends FrameLayout {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private final View surfaceView;

    private final ComponentListener componentListener;
    private final AspectRatioFrameLayout layout;

    // 加载出视频的时候显示第一帧
    private final ImageView frameCover;

    /// 简单的播放器
    private SimpleExoPlayer player;

    private EventLogger eventLogger;
    private Handler mainHandler;
    private MappingTrackSelector trackSelector;
    private DataSource.Factory mediaDataSourceFactory;
    private Timeline.Window window;

    private boolean isTimelineStatic;

    private int playerWindow;
    private long playerPosition;

    // 是否是用户主动暂停
    private boolean isPauseFromUser = false;

    private boolean isLooping = true;

    private final AudioManager audioManager;
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
                resume();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // audioManager.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                audioManager.abandonAudioFocus(afChangeListener);
                // Stop playback
                stop();

            }
        }
    };

    private OnAVPlayerViewListener mOnPlayerListener;

    public interface OnAVPlayerViewListener {

        /**
         * 返回0~1000的值
         * @param progress
         */
        public void onProgress(int progress);

    }

    public void setOnPlayerListener(OnAVPlayerViewListener listener) {
        this.mOnPlayerListener = listener;
    }

    public AVPlayerView(Context context) {
        this(context, null);
    }

    public AVPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("WrongConstant")
    public AVPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        boolean useTextureView = true;
        @AspectRatioFrameLayout.ResizeMode
        int resizeMode = AspectRatioFrameLayout.RESIZE_MODE_NONE;

        LayoutInflater.from(getContext()).inflate(R.layout.layout_avplayer, this);

        componentListener = new ComponentListener();

        layout = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
        layout.setResizeMode(resizeMode);

        frameCover = (ImageView) findViewById(R.id.frame_cover);

        View view = useTextureView ? new TextureView(context) : new SurfaceView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        surfaceView = view;
        layout.addView(surfaceView, 0);

        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        audioManager.abandonAudioFocus(afChangeListener);
    }

    /**
     * Returns the player currently set on this view, or null if no player is set.
     */
    public SimpleExoPlayer getPlayer() {
        return player;
    }

    private static final int PROGRESS_BAR_MAX = 1000;

    private long positionValue(int progress) {
        long duration = player == null ? C.TIME_UNSET : player.getDuration();
        long result = duration == C.TIME_UNSET ? 0 : ((duration * progress) / PROGRESS_BAR_MAX);

        return result;
    }

    public void seekTo(int progress) {
        player.seekTo(positionValue(progress));
    }

    /**
     * Set the {@link SimpleExoPlayer} to use. The {@link SimpleExoPlayer#setTextOutput} and
     * {@link SimpleExoPlayer#setVideoListener} method of the player will be called and previous
     * assignments are overridden.
     *
     * @param player The {@link SimpleExoPlayer} to use.
     */
    public void setPlayer(SimpleExoPlayer player) {
        setPlayer(player, false);
    }

    private void setPlayer(SimpleExoPlayer player, boolean isFromSelf) {
        if (this.player == player && !isFromSelf) {
            return;
        }
        if (this.player != null) {
            this.player.setTextOutput(null);
            this.player.setVideoListener(null);
            this.player.removeListener(componentListener);
            this.player.setVideoSurface(null);
        }

        this.player = player;
        if (player != null) {
            if (surfaceView instanceof TextureView) {
                player.setVideoTextureView((TextureView) surfaceView);
            } else if (surfaceView instanceof SurfaceView) {
                player.setVideoSurfaceView((SurfaceView) surfaceView);
            }
            player.setVideoListener(componentListener);
            player.addListener(componentListener);
            player.setTextOutput(componentListener);

            Log.i("setPlayer", "设置好了监听");
        }
    }

    /**
     * Sets the resize mode which can be of value {@link AspectRatioFrameLayout#RESIZE_MODE_FIT},
     * {@link AspectRatioFrameLayout#RESIZE_MODE_FIXED_HEIGHT} ,
     * {@link AspectRatioFrameLayout#RESIZE_MODE_FIXED_WIDTH} or
     * {@link AspectRatioFrameLayout#RESIZE_MODE_NONE}
     *
     * @param resizeMode The resize mode.
     */
    public void setResizeMode(@AspectRatioFrameLayout.ResizeMode int resizeMode) {
        layout.setResizeMode(resizeMode);
    }

    /**
     * Get the view onto which video is rendered. This is either a {@link SurfaceView} (default)
     * or a {@link TextureView} if the {@code use_texture_view} view attribute has been set to true.
     *
     * @return either a {@link SurfaceView} or a {@link TextureView}.
     */
    public View getVideoSurfaceView() {
        return surfaceView;
    }

    private boolean requestAudioFocus() {
        // Request audio focus for playback
        int result = audioManager.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void initSelfPlayer() {

        if (mainHandler == null) {
            mainHandler = new Handler();
        }

        if (mediaDataSourceFactory == null) {
            mediaDataSourceFactory = buildDataSourceFactory(true);
        }

        if (window == null) {
            window = new Timeline.Window();
        }

        if (trackSelector == null) {
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        }


        eventLogger = new EventLogger(trackSelector);

        // TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, new DefaultLoadControl(),
                null, EXTENSION_RENDERER_MODE_OFF);
        player.addListener(eventLogger);
        player.setAudioDebugListener(eventLogger);
        player.setVideoDebugListener(eventLogger);
        player.setMetadataOutput(eventLogger);
        setPlayer(player, true);
    }


    public void releaseSelfPlayer() {

        if (player != null) {
            playerWindow = player.getCurrentWindowIndex();
            playerPosition = C.TIME_UNSET;
            player.release();
            player = null;
            trackSelector = null;
            eventLogger = null;
        }
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    // 传递的是http或者本地文件路径
    public void play(int rawId) {
        play(rawId, true, C.TIME_UNSET);
    }

    public void play(int rawId, boolean playWhenReady) {
        play(rawId, playWhenReady, C.TIME_UNSET);
    }

    public void play(int rawId, boolean playWhenReady, long where) {
        if (player == null) {
            initSelfPlayer();
        }

        // 显示预览视频
        getFrameCover(rawId);

        MediaSource mediaSource = buildMediaSource(rawId);

        if (mediaSource == null) {
            return;
        }

        player.setPlayWhenReady(requestAudioFocus() && playWhenReady);

        player.prepare(mediaSource, !isTimelineStatic, !isTimelineStatic);
        if (where == C.TIME_UNSET) {
            player.seekToDefaultPosition(playerWindow);
        } else {
            player.seekTo(playerWindow, where);
        }
    }


    // 传递的是http或者本地文件路径
    public void play(String uri) {
        play(uri, true, C.TIME_UNSET);
    }

    public void play(String uri, long where) {
        play(uri, true, where);
    }

    public void play(String uri, boolean playWhenReady) {
        play(uri, playWhenReady, C.TIME_UNSET);
    }

    public void play(String uri, boolean playWhenReady, long where) {
        if (player == null) {
            initSelfPlayer();
        }

        // 显示预览视频
        getFrameCover(uri);

        MediaSource mediaSource = buildMediaSource(Uri.parse(uri), null);

        if (isLooping) {
            mediaSource = new LoopingMediaSource(mediaSource);
        }

        player.setPlayWhenReady(requestAudioFocus() && playWhenReady);
        player.prepare(mediaSource, !isTimelineStatic, !isTimelineStatic);
        if (where == C.TIME_UNSET) {
            player.seekToDefaultPosition(playerWindow);
        } else {
            player.seekTo(playerWindow, where);
        }

    }

    public void pause() {
        if (player == null) {
            return;
        }

        if (player.getPlayWhenReady()) {

            Timeline currentTimeline = player.getCurrentTimeline();
            boolean haveNonEmptyTimeline = currentTimeline != null && !currentTimeline.isEmpty();
            if (haveNonEmptyTimeline && currentTimeline.getWindow(playerWindow, window).isSeekable) {
                playerPosition = player.getCurrentPosition();
            }

            player.setPlayWhenReady(false);

            isPauseFromUser = false;

        } else {
            isPauseFromUser = true;
        }
    }

    public void resume() {
        if (player == null) {
            return;
        }

        if (!player.getPlayWhenReady() && !isPauseFromUser) {
            player.seekTo(playerPosition - 500 < 0 ? 0 : playerPosition - 500);
            player.setPlayWhenReady(true);
//            removeCallbacks(resumeAction);
//            postDelayed(resumeAction,1500);
        }

    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    // 提取预览图片
    private void getFrameCover(Object dataSource) {
        frameCover.setVisibility(VISIBLE);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            if (dataSource instanceof String) {
                retriever.setDataSource((String)dataSource, new HashMap<String, String>());
            } else if (dataSource instanceof Integer) {

                final String uriPath = "android.resource://" + getContext().getPackageName() + "/raw/" + dataSource;
                final Uri uri = Uri.parse(uriPath);
                retriever.setDataSource(getContext(), uri);
            }

            Bitmap bitmap = retriever.getFrameAtTime();
            frameCover.setImageBitmap(bitmap);
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {

            try {
                retriever.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
    }

    // 提取预览图片
    private void getFrameCover(String dataSource) {
        frameCover.setVisibility(VISIBLE);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(dataSource, new HashMap<String, String>());
            Bitmap bitmap = retriever.getFrameAtTime();
            frameCover.setImageBitmap(bitmap);
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {

            try {
                retriever.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
    }

    // raw目录下的原始资源
    private MediaSource buildMediaSource(int rawId) {

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory selectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(selectionFactory);

        try {
            DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(rawId));
            final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(getContext());
            rawResourceDataSource.open(dataSpec);
            DataSource.Factory factory = new DataSource.Factory() {
                @Override
                public DataSource createDataSource() {
                    return rawResourceDataSource;
                }
            };

            MediaSource mediaSource = new ExtractorMediaSource(rawResourceDataSource.getUri(), factory, new DefaultExtractorsFactory(), null, null);

            if (isLooping) {
                mediaSource = new LoopingMediaSource(mediaSource);
            }

            return mediaSource;
        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }

        return null;
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension
                : uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    /**
     * Returns a new DataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *                          DataSource factory.
     * @return A new DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }


    DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(getContext().getApplicationContext(), bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(getContext().getApplicationContext(), "AVPlayerView"), bandwidthMeter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            // 固定毫秒刷新
            updateProgress();
        }
    };

    private void updateProgress() {

        if (mOnPlayerListener == null) {
            return;
        }

        long position = player == null ? 0 : player.getCurrentPosition();
        // 缓冲进度
        long bufferedPosition = player == null ? 0 : player.getBufferedPosition();

        long duration = player == null ? C.TIME_UNSET : player.getDuration();
        int result = duration == C.TIME_UNSET || duration == 0 ? 0 : (int) ((position * PROGRESS_BAR_MAX) / duration);

        mOnPlayerListener.onProgress(result);

        removeCallbacks(updateProgressAction);

        // Schedule an update if necessary.
        int playbackState = player == null ? ExoPlayer.STATE_IDLE : player.getPlaybackState();
        if (playbackState != ExoPlayer.STATE_IDLE && playbackState != STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            postDelayed(updateProgressAction, delayMs);
        }
    }

    private final class ComponentListener implements SimpleExoPlayer.VideoListener,
            TextRenderer.Output, ExoPlayer.EventListener {

        // TextRenderer.Output implementation
        @Override
        public void onCues(List<Cue> cues) {

            Log.i("ComponentListener",  "onCues");
        }


        // SimpleExoPlayer.VideoListener implementation
        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            layout.setAspectRatio(height == 0 ? 1 : (width * pixelWidthHeightRatio) / height);

            Log.i("ComponentListener",  "onVideoSizeChanged");
        }

        @Override
        public void onRenderedFirstFrame() {
            Log.i("ComponentListener",  "onRenderedFirstFrame");
        }


        // ExoPlayer.EventListener implementation
        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
            Log.i("ComponentListener",  "onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            if (playbackState == ExoPlayer.STATE_IDLE || playbackState == ExoPlayer.STATE_BUFFERING) {

            } else if (playbackState == STATE_READY && player.getPlayWhenReady() || playbackState == STATE_ENDED) {

            }

            if (playWhenReady && STATE_READY == playbackState) {
                frameCover.setVisibility(GONE);
            }

            if (playbackState == STATE_ENDED) {
                playerPosition = C.TIME_UNSET;
            }

            updateProgress();

            Log.i("ComponentListener",  "onPlayerStateChanged");
        }

        @Override // 播放器错误
        public void onPlayerError(ExoPlaybackException e) {
            // Do nothing.

            Log.i("ComponentListener",  "onPlayerError");
        }

        @Override // 位置不连续
        public void onPositionDiscontinuity() {
            // Do nothing.

            Log.i("ComponentListener",  "onPositionDiscontinuity");

            updateProgress();
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            isTimelineStatic = !timeline.isEmpty()
                    && !timeline.getWindow(timeline.getWindowCount() - 1, window).isDynamic;
            Log.i("ComponentListener",  "onTimelineChanged");
            updateProgress();
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            Log.i("ComponentListener",  "onTracksChanged");
        }

    }
}
