package com.inc.miki.minimax.Utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inc.miki.minimax.R;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.inc.miki.minimax.Objects.FeedItem;
import com.squareup.picasso.Picasso;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static com.inc.miki.minimax.R.id.seekBar;


public class PlayerService extends Service {

    private static PlayerService sInstance;

    public static PlayerService get() {
        return sInstance;
    }

    MediaPlayer mediaPlayer = null;
    SeekBar mSeekBar;
    Handler mHandler = new Handler();
    TextView currentTimeText;
    TextView totalTimeTV;
    TextView playerEpisodeDesc;
    FloatingActionButton playpauseButton;
    ImageButton forwardButton;
    ImageButton backButton;

    public static final String ACTION_DATA_UPDATED = "com.inc.miki.minimax.ACTION_DATA_UPDATED";
    public static final String ACTION_PAUSE = "com.inc.miki.minimax.ACTION_PAUSE";
    Activity mainActivity;
    AudioManager am;
    AudioManager.OnAudioFocusChangeListener afChangeListener;

    MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            episodeEnding();
        }
    };

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    if (mediaPlayer.isPlaying()) {
                        Pause();
                    }
                } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                    if (mediaPlayer.isPlaying()) {
                        Pause();
                    }
                } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(0.2f, 0.2f);
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId != 1) {
            Pause();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void LoadUrl(FeedItem feedItem, Activity activity) {

        int result = am.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(feedItem.getAudioUrl());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mCompletionListener);

            mainActivity = activity;

            mSeekBar = activity.findViewById(seekBar);
            mSeekBar.setMax(100);

            totalTimeTV = activity.findViewById(R.id.totalTime);
            totalTimeTV.setText(feedItem.getLength());

            playerEpisodeDesc = activity.findViewById(R.id.playerEpisodeDesc);
            playerEpisodeDesc.setText(feedItem.getDescription());

            currentTimeText = activity.findViewById(R.id.currentTime);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        long mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        long maxPosition = mediaPlayer.getDuration() / 1000;

                        BigDecimal max = new BigDecimal(maxPosition);
                        BigDecimal mult = new BigDecimal(100);
                        BigDecimal current = new BigDecimal(mCurrentPosition);
                        BigDecimal progress = current.divide(max, 3, RoundingMode.CEILING);
                        BigDecimal percent = progress.multiply(mult);

                        mSeekBar.setProgress(percent.intValue());

                        currentTimeText.setText(getTimeString(mCurrentPosition * 1000));

                    }
                    mHandler.postDelayed(this, 1000);
                }
            });

            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mediaPlayer != null) {
                        int i = seekBar.getProgress();
                        double maxPosition = mediaPlayer.getDuration() * .01;
                        double newPercentage = maxPosition * i;
                        int newPosition = (int) newPercentage;
                        mediaPlayer.seekTo(newPosition);
                    }
                }
            });

            playpauseButton = activity.findViewById(R.id.playpause);
            playpauseButton.setImageResource(R.drawable.pause);
            playpauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Pause();
                }
            });

            forwardButton = activity.findViewById(R.id.seekForwardButton);
            forwardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipForward();
                }
            });

            backButton = activity.findViewById(R.id.seekBackButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipBack();
                }
            });

            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            dataUpdatedIntent.putExtra("showTitle", feedItem.getTitle());
            dataUpdatedIntent.putExtra("show", feedItem.getShow());
            activity.sendBroadcast(dataUpdatedIntent);
        }
    }

    public void Pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playpauseButton.setImageResource(R.drawable.play);
            } else {
                mediaPlayer.start();
                playpauseButton.setImageResource(R.drawable.pause);
            }
        }
    }

    public void SkipForward() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();

            int newPosition = currentPosition + 30000;

            mediaPlayer.seekTo(newPosition);
        }
    }

    public void SkipBack() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();

            int newPosition = currentPosition - 10000;

            mediaPlayer.seekTo(newPosition);
        }
    }

    //calculate time from millis
    @SuppressLint("DefaultLocale")
    private String getTimeString(long millis) {
        StringBuilder buf = new StringBuilder();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        if (hours != 0) {
            buf
                    .append(hours)
                    .append(":")
                    .append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds));
        } else {
            buf
                    .append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds));
        }
        return buf.toString();
    }

    private void episodeEnding() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            playpauseButton.setImageResource(R.drawable.play);
        }
    }

}
