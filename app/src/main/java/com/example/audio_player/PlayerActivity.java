package com.example.audio_player;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.example.audio_player.MainActivity.musicFiles;

public class PlayerActivity extends AppCompatActivity {

    TextView songName, artistName, durationPlayed, durationTotal;
    ImageView covertArt, nextButton, previousButton, backButton, menuButton, shuffleButton, repeatButton, playPauseButton;
    SeekBar seekBar;

    int position = -1;
    static ArrayList<MusicFiles> listOfSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initView();
        getIntentMethod();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b){
                    mediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null){
                    int mCurrentPos = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPos);
                    durationPlayed.setText(formattedTime(mCurrentPos));
                }
                handler.postDelayed(this, 1000);
            }
        });

    }

    private String formattedTime(int mCurrentPos) {

        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPos % 60);
        String minutes = String.valueOf(mCurrentPos / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1){
            return totalNew;
        }else {
            return totalOut;
        }

    }

    private void getIntentMethod() {

        position = getIntent().getIntExtra("position", -1);
        listOfSongs = musicFiles;

        if (listOfSongs != null){

            playPauseButton.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listOfSongs.get(position).getPath());
        }
        if (mediaPlayer != null){

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }else {

            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);

    }

    private void initView() {

        songName = findViewById(R.id.song_name);
        artistName = findViewById(R.id.song_artist);
        durationPlayed = findViewById(R.id.duration_played);
        durationTotal = findViewById(R.id.duration_total);
        covertArt = findViewById(R.id.cover_art);
        nextButton = findViewById(R.id.next_button);
        previousButton = findViewById(R.id.previous_button);
        backButton = findViewById(R.id.back_button);
        menuButton = findViewById(R.id.menu_button);
        shuffleButton = findViewById(R.id.shuffle_button);
        repeatButton = findViewById(R.id.repeat_button);
        seekBar = findViewById(R.id.seek_bar);
        playPauseButton = findViewById(R.id.play_pause_button);
    }
}
