package com.example.audio_player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static com.example.audio_player.MainActivity.musicFiles;
import static com.example.audio_player.MainActivity.repeatBoolean;
import static com.example.audio_player.MainActivity.shuffleBoolean;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    static ArrayList<MusicFiles> listOfSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    TextView songName, artistName, durationPlayed, durationTotal;
    ImageView covertArt, nextButton, previousButton, backButton, menuButton, shuffleButton, repeatButton, playPauseButton;
    SeekBar seekBar;
    int position = -1;
    private Handler handler = new Handler();
    private Thread playPauseThread, nextThread, previousThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initView();
        getIntentMethod();

        songName.setText(listOfSongs.get(position).getTitle());
        artistName.setText(listOfSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int pos, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(pos * 1000);
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
                if (mediaPlayer != null) {
                    int mCurrentPos = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPos);
                    durationPlayed.setText(formattedTime(mCurrentPos));
                }
                handler.postDelayed(this, 1000);
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBoolean) {
                    shuffleBoolean = false;
                    shuffleButton.setImageResource(R.drawable.ic_shuffle_off);
                } else {
                    shuffleBoolean = true;
                    shuffleButton.setImageResource(R.drawable.ic__shuffle_on);
                }
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatBoolean) {
                    repeatBoolean = false;
                    repeatButton.setImageResource(R.drawable.ic_repeat_off);
                } else {
                    repeatBoolean = true;
                    repeatButton.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });

    }

    @Override
    protected void onResume() {

        playPauseThreadButton();
        nextThreadButton();
        previousThreadButton();
        super.onResume();
    }

    private void playPauseThreadButton() {
        playPauseThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseButtonClicked();
                    }
                });
            }
        };
        playPauseThread.start();
    }

    private void playPauseButtonClicked() {
        if (mediaPlayer.isPlaying()) {

            playPauseButton.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPos = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {

            playPauseButton.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPos = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });

        }
    }

    private void nextThreadButton() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextButtonClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextButtonClicked() {
        if (mediaPlayer.isPlaying()) {

            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listOfSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1) % listOfSongs.size());
            }
            // else position will be position if repeat button is ON
            uri = Uri.parse(listOfSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPos = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            playPauseButton.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);

        } else {

            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listOfSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1) % listOfSongs.size());
            }
            // else position will be position if repeat button is ON
            uri = Uri.parse(listOfSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPos = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            playPauseButton.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }

    private void previousThreadButton() {
        previousThread = new Thread() {
            @Override
            public void run() {
                super.run();
                previousButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        previousButtonClicked();
                    }
                });
            }
        };
        previousThread.start();
    }

    private void previousButtonClicked() {
        if (mediaPlayer.isPlaying()) {

            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listOfSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position - 1) < 0 ? (listOfSongs.size() - 1) : (position - 1));
            }
            // else position will be position if repeat button is ON
            uri = Uri.parse(listOfSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPos = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            playPauseButton.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);

        } else {

            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listOfSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position - 1) < 0 ? (listOfSongs.size() - 1) : (position - 1));
            }
            // else position will be position if repeat button is ON
            uri = Uri.parse(listOfSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songName.setText(listOfSongs.get(position).getTitle());
            artistName.setText(listOfSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPos = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPos);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            playPauseButton.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    private String formattedTime(int mCurrentPos) {

        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPos % 60);
        String minutes = String.valueOf(mCurrentPos / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }

    }

    private void getIntentMethod() {

        position = getIntent().getIntExtra("position", -1);
        listOfSongs = musicFiles;

        if (listOfSongs != null) {

            playPauseButton.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listOfSongs.get(position).getPath());
        }
        if (mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration() / 1000);

        metaData(uri);

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

    private void metaData(Uri uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durTotal = Integer.parseInt(listOfSongs.get(position).getDuration()) / 1000;
        durationTotal.setText(formattedTime(durTotal));

        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null) {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this, covertArt, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = Objects.requireNonNull(palette).getDominantSwatch();
                    if (swatch != null) {
                        ImageView gradientImg = findViewById(R.id.imageViewGradient);
                        ConstraintLayout mContainer = findViewById(R.id.mContainer);
                        gradientImg.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), 0x00000000});
                        gradientImg.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        songName.setTextColor(swatch.getTitleTextColor());
                        artistName.setTextColor(swatch.getBodyTextColor());
                    } else {

                        ImageView gradientImg = findViewById(R.id.imageViewGradient);
                        ConstraintLayout mContainer = findViewById(R.id.mContainer);
                        gradientImg.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0x00000000});
                        gradientImg.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        songName.setTextColor(Color.WHITE);
                        artistName.setTextColor(Color.DKGRAY);
                    }
                }
            });
        } else {
            Glide.with(this).asBitmap().load(R.drawable.default_album_art).into(covertArt);

            ImageView gradientImg = findViewById(R.id.imageViewGradient);
            ConstraintLayout mContainer = findViewById(R.id.mContainer);
            gradientImg.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);

            songName.setTextColor(Color.WHITE);
            artistName.setTextColor(Color.DKGRAY);
        }
    }

    public void ImageAnimation(final Context context, final ImageView imageView, final Bitmap bitmap) {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        final Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextButtonClicked();
        /*
        if (mediaPlayer != null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
        */
    }
}
