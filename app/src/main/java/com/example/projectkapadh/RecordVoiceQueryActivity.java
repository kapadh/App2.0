package com.example.projectkapadh;

import static android.Manifest.permission.RECORD_AUDIO;

import static com.example.projectkapadh.RecordingActivity.AudioFilePath;
import static com.example.projectkapadh.RecordingActivity.recordQueryBtn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import timerx.Stopwatch;
import timerx.StopwatchBuilder;
import timerx.Timer;
import timerx.TimerBuilder;

public class RecordVoiceQueryActivity extends AppCompatActivity {

    private static int MICROPHONE_PERMISSION_CODE = 200;

    private FloatingActionButton start_stopBTN;
    private boolean isStopEnable = true;
    private TextView textViewStopwatch, recordAgainText, playerPosition, playerDuration;
    private Stopwatch stopwatch;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private boolean isPause = false;
    private boolean playing = false;
    private CardView audioPlayerCard;
    private SeekBar playerSeekbar;
    private int duration;
    Handler handler = new Handler();
    Runnable runnable;
    String SpName;
    ImageView playBtn, pauseBtn;
    String sDuration, sFileName, recordingDuration;
    TextView fileName;
    String filePath;

    AppCompatButton recordingDoneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_voice_query);

        playerPosition = findViewById(R.id.playerPosition);
        playerDuration = findViewById(R.id.playerDuration);
        playerSeekbar = findViewById(R.id.playerSeekbar);
        audioPlayerCard = findViewById(R.id.audioPlayerCard);
        recordingDoneBtn = findViewById(R.id.recordingDone);
        fileName = findViewById(R.id.fileName);
        playBtn = findViewById(R.id.playBtn);
        pauseBtn = findViewById(R.id.pauseBtn);
        textViewStopwatch = findViewById(R.id.timeTextView);
        recordAgainText = findViewById(R.id.recordAgainText);
        start_stopBTN = findViewById(R.id.floatingActionButton);
        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);

        if (isMicrophonePresent()) {
            getMicrophonePermission();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if (getIntent() != null) {
            SpName = getIntent().getStringExtra("SpName");
            filePath = getIntent().getStringExtra("FilePath");

            File file = new File(filePath);
            if(file.exists()){
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                getRecordingFilePath();
                fileName.setText(sFileName);
                int duration = mediaPlayer.getDuration();
                sDuration = convertFormat(duration);
                playerDuration.setText(sDuration);
                textViewStopwatch.setVisibility(View.INVISIBLE);
                recordAgainText.setVisibility(View.VISIBLE);
                audioPlayerCard.setVisibility(View.VISIBLE);
            }
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 50);
            }
        };

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseBtn.setVisibility(View.VISIBLE);
                playBtn.setVisibility(View.INVISIBLE);

                mediaPlayer.start();

                playerSeekbar.setMax(mediaPlayer.getDuration());
                handler.postDelayed(runnable, 50);
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseBtn.setVisibility(View.INVISIBLE);
                playBtn.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                handler.removeCallbacks(runnable);
            }
        });

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        pauseBtn.setVisibility(View.INVISIBLE);
                        playBtn.setVisibility(View.VISIBLE);
                        mediaPlayer.seekTo(0);

                    }
                });

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        start_stopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStopEnable) {

                    isStopEnable = false;
                    start_stopBTN.setImageResource(R.drawable.ic_baseline_stop_24);
                    rippleBackground.startRippleAnimation();
                    recordBtnPress();
                } else {

                    isStopEnable = true;
                    start_stopBTN.setImageResource(R.drawable.ic_baseline_mic_24);
                    rippleBackground.stopRippleAnimation();
                    stopBtnPress();
                }
            }
        });

        stopwatch = new StopwatchBuilder()
                // Set the initial format
                .startFormat("MM:SS")
                // Set the tick listener for displaying time
                .onTick(time -> textViewStopwatch.setText(time))
                // When time is equal to one hour, change format to "HH:MM:SS"
                .changeFormatWhen(1, TimeUnit.HOURS, "HH:MM:SS")
                .build();


        recordingDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioPlayerCard.getVisibility() == View.VISIBLE){
                    recordQueryBtn.setText(sFileName);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    finish();
                }else{
                    if(isPause){
                        Toast.makeText(RecordVoiceQueryActivity.this, "Use the same button to stop recording", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Please record your query", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {

        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration)
                , TimeUnit.MILLISECONDS.toSeconds(duration)
                , TimeUnit.MINUTES.toSeconds(TimeUnit.MICROSECONDS.toMinutes(duration)));

    }

    private void recordBtnPress() {

        isPause = true;
        stopwatch.start();
        textViewStopwatch.setVisibility(View.VISIBLE);
        recordAgainText.setVisibility(View.INVISIBLE);
        audioPlayerCard.setVisibility(View.INVISIBLE);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setAudioEncodingBitRate(16*44100);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            String loc=getExternalCacheDir().getAbsolutePath()+ "/" + "AudioRecording.3gp";
            mediaRecorder.setOutputFile(loc);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void stopBtnPress() {

        isPause = false;
        stopwatch.stop();
        stopwatch.reset();
        textViewStopwatch.setVisibility(View.INVISIBLE);
        recordAgainText.setVisibility(View.VISIBLE);

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getRecordingFilePath());
            mediaPlayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }

        fileName.setText(sFileName);

        int duration = mediaPlayer.getDuration();
        sDuration = convertFormat(duration);
        playerDuration.setText(sDuration);

        audioPlayerCard.setVisibility(View.VISIBLE);

    }

    private boolean isMicrophonePresent() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        sFileName = "Query_to_" + SpName + ".mp3";
        AudioFilePath = sFileName;
        File file = new File(musicDirectory, sFileName);
        return file.getPath();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        return super.onKeyDown(keyCode, event);
    }
}