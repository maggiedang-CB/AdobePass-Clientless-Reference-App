package com.example.android.adobepassclientlessrefapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AbstractActivity {

    public static String TAG = "PlayerActivity";

    @BindView(R.id.player_view)
    PlayerView playerView;

    Uri tokenizedUri;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        ButterKnife.bind(this);

        // Parse String of tokenizedUrl to a Uri object
        this.tokenizedUri = Uri.parse(getTokenizedUrl());
        Log.d(TAG, "Tokenized Uri = " + tokenizedUri);

        createAndLaunchExoPlayer();
    }

    /**
     * Initializes Exo Player, binds it to the player view, and play tokenized Url.
     */
    private void createAndLaunchExoPlayer() {
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this);
        // Bind player to player view
        playerView.setPlayer(player);

        // DataSource to load media data
        DataSource.Factory dataSource = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)));
        // Media Source containing tokenized Url
        MediaSource mediaSource = new HlsMediaSource.Factory(dataSource).createMediaSource(tokenizedUri);
        // Set mediaSource to player
        player.prepare(mediaSource);
        // Automatically play video when launched
        player.setPlayWhenReady(true);
        // TODO: Load spinner when buffering


    }

    /**
     * Return Tokenized Url saved from shared preferences. The URL will be played on the player.
     * @return
     */
    private String getTokenizedUrl() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        String tokenKey = MainActivity.sharedPrefKeys.TOKENIZED_URL.toString();

        if (sharedPreferences.contains(tokenKey)) {
            return sharedPreferences.getString(tokenKey, "");
        }
        return "";
    }


}
