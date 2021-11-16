package konradtercjak.daznplayer.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.*
import com.google.android.material.snackbar.Snackbar
import konradtercjak.daznplayer.databinding.PlayerBinding
import org.xmlpull.v1.XmlPullParserException


class PlayerActivity : Activity() {
    companion object {
        val URL = "URL"
    }

    private var url: String? = null //"https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        PlayerBinding.inflate(layoutInflater)
    }
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        url = bundle?.getString(URL)
        setContentView(binding.root)
    }

    private fun initPlayer() {
        if (url != null) {
            val trackSelector = DefaultTrackSelector(this).apply {
                setParameters(buildUponParameters().setMaxVideoSizeSd())
            }

//            val mediaItem = MediaItem.Builder()
//                .setUri(url)
//                .setMimeType(MimeTypes.APPLICATION_MPD)
//                .build()

            val mediaItem = MediaItem.fromUri(url!!)

            player = ExoPlayer.Builder(this).setTrackSelector(trackSelector)
                .build()
                .also { exoPlayer ->
                    binding.videoView.player = exoPlayer
                }

            try {
                player!!.setMediaItem(mediaItem)
                player!!.playWhenReady = playWhenReady
                player!!.seekTo(currentWindow, playbackPosition)
                player!!.prepare()

            }  catch (throwable: Throwable) {
                showErrorSnackbar("Stream error!")
            }

        }
    }

    private fun showErrorSnackbar(message: String): Snackbar {
        val snackbar = Snackbar.make(this, binding.root, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.view.setOnClickListener {
            snackbar.dismiss()
        }
        snackbar.show()
        return snackbar
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if ((Util.SDK_INT < 24 || player == null)) {
            initPlayer()
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.videoView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }


    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        url = savedInstanceState.getString(URL, null)
        initPlayer()

    }
}