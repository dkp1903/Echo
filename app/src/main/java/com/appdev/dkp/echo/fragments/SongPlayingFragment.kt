package com.appdev.dkp.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.appdev.dkp.echo.R
import com.appdev.dkp.echo.CurrentSongHelper
import com.appdev.dkp.echo.Songs


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//Hopefully it finaaly loaded.
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SongPlayingFragment : Fragment() {

    object Statified{
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null

        /*The different variables defined will be used for their respective purposes*/
        /*Depending on the task they do we name the variables as such so that it gets easier to identify the task they perform*/
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null

        /*The current song helper is used to store the details of the current song being played*/
        var currentSongHelper: CurrentSongHelper? = null
        var audioVisualization : AudioVisualization?= null
        var glView = GLAudioVisualizationView?=null
        var fab:ImageButton?= null
        var favoriteContent:EchoDatabase?=null

        var mSensorManager: SensorManager?=null
        //listener is an interface recieves notfis from sensor manager
        var mSensorListener: SensorEventListener?= null
        var MY_PREFS_NAME = "ShakeFeature"
        var updateSongTime = object : Runnable{
            override fun run(){
                val getcurrent = mediaplayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong{} as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong() as Long)-
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong{} as Long))))
                Handler().postDelayed(this , 1000)
            }
        }


    }//statified ends here


    object Staticated{
         var MY_PREFS_SHUFFLE = "Shuffle feature"
         var MY_PREFS_LOOP = "Loop feature"

        fun onSongComplete() {

            /*If shuffle was on then play a random next song*/
            if (currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                currentSongHelper?.isPlaying = true
            } else {

                /*If loop was ON, then play the same ong again*/
                if (currentSongHelper?.isLoop as Boolean) {
                    currentSongHelper?.isPlaying = true
                    var nextSong = fetchSongs?.get(currentPosition)
                    currentSongHelper?.currentPosition = currentPosition
                    currentSongHelper?.songPath = nextSong?.songData
                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songArtist = nextSong?.artist
                    currentSongHelper?.songId = nextSong?.songID as Long
                    updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)


                    mediaPlayer?.reset()
                    try {
                        mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                        processInformation(mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {

                    /*If loop was OFF then normally play the next song*/
                    playNext("PlayNextNormal")
                    currentSongHelper?.isPlaying = true
                }
            }//else ends here



        }//fun onsongcomplete ends here
        fun updateTextViews(songtitle: String, songArtist: String){
            songTitleView?.setText(songtitle)
            songArtistView?.setText(songArtist)
        }
        fun processInformation(mediaPlayer;MediaPlayer){
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            seekbar?.max = finalTime
            startTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong())-
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MINUTES.toMinutes(getCurrent.toLong()))))

            endTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong())-
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MINUTES.toMinutes(getCurrent.toLong()))))


            seekBar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime , 1000)
        }//processinfo ends here
        fun playNext(check: String) {

            /*Here we check the value of the string parameter passed*/
            if (check.equals("PlayNextNormal", true)) {

                /*If the check string was PlayNextNormal, we normally move on to the next song*/
                currentPosition = currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {

                /*If the check string was PlayNextLikeNormalShuffle, we then randomly select a song and play it*/
                /*The next steps are used to choose the select a random number
                * First we declare a variable and then initialize it to a random object*/
                var randomObject = Random()

                /*Now here we calculate the random number
                * The nextInt(val n: Int) is used to get a random number between 0(inclusive) and the number passed in this argument(n), exclusive.
                * Here we pass the paramter as the length of the list of the songs fetched
                * We add 1 to the size as the length will be one more than the size. For example if the size of arraylist is 10, then it has items from 0 to 10, which gives the length as 11*/

                var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
                /*Now put the current position i.e the position of the song to be played next equal to the random position*/
                currentPosition = randomPosition
            }

            /*Now if the current position equals the length of the i.e the current position points to the end of the list
            * we then make the current position to 0 as no song will be there*/
            if (currentPosition == fetchSongs?.size) {
                currentPosition = 0
            }
            currentSongHelper?.isLoop = false

            /*Here we get the details of the song which is played as the next song
            * and update the contents of the current song helper*/
            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songId = nextSong?.songID as Long
            updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
            /*Before playing the song we reset the media player*/
            mediaPlayer?.reset()
            try {

                /*Similar steps which were done when we started the music*/
                mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInformation(mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }//object staticated ends here

    var mAccelaration : Float = 0f
    var mAccelarationCurrent: Float = 0f
    var mAccelarationLast: Float = 0f

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)

        /*Linking views with their ids*/
        Statified.seekBar = view?.findViewById(R.id.seekBar)
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.previousImageButton = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fab=view?.findViewById(R.id.favoriteIcon)
        Statified.fab?.alpha = 0.8f


        return view
    }
    override fun onViewCreated(view : View? , savedInstanceState : Bundle?){
        super.onViewCreated(view , savedInstanceState)
        Statified.audioVisualization = glView as AudioVisualization

    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }
    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }
    override fun onResume(){
        super.onResume()
        Statified.audioVisualization?.onResume()
        Statified.mSensorManager.registerListener(Statified.mSensorListener , Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) , SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onPause(){
        super.onPause()
        Statified.audioVisualization?.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }
    override fun onDestroyView(){
        Statified.audioVisualization?.release()
        super.onDestroyView()
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelaration = 0.0f
        mAccelarationCurrent = SensorManager.GRAVITY_EARTH
        mAccelarationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()

    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?){
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onPrepareOptionsMenu(menu: Menu?)
    {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem?= menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Statified.FavoriteContent = EchoDatabase (myActivity)

        /*Initialising the params of the current song helper object*/
        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false
        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0

        try {
            path = arguments.getString("path")
            _songTitle = arguments.getString("songTitle")
            _songArtist = arguments.getString("songArtist")
            songId = arguments.getInt("songId").toLong()

            /*Here we fetch the received bundle data for current position and the list of all songs*/
            Statified.currentPosition = arguments.getInt("position")
            Statified.fetchSongs = arguments.getParcelableArrayList("songData")

            /*Now store the song details to the current song helper object so that they can be used later*/
            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.currentPosition = currentPosition
            Staticated.updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromFavBottomBar = arguments.get("FavBottomBar") as? String
        if(fromFavBottomBar != null){
            Statified.mediaplayer = FavoriteFragment.Statified.mediaPlayer
        }else {
            Statified.mediaPlayer = MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaPlayer?.setDataSource(myActivity, Uri.parse(path))
                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer?.start()
        }

        processInformation(mediaPlayer as MediaPlayer)
        if (currentSongHelper?.isPlaying as Boolean) {
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        mediaplayer?.setOnCompletionListener{
            onSongComplete()

        }
        clickHandler()
        var vizualizationHandler = DbmHandler.Factory.newVizualizerHandler(myActivity as Context , 0)
        Statified.audioVizualization?.linkTo(vizualizationHandler)

        var prefsForShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE , Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature" , false)
        if(isShuffleAllowed as Boolean){
            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        else{
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var prefsForLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE , Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature" , false)
        if(isLoopAllowed as Boolean){
            Statified.currentSongHelper?.isLoop = true
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        }
        else{
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
    if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int)){
        Statified.fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_on))
    }
    }


    /*A new click handler function is created to handle all the click functions in the song playing fragment*/
    fun clickHandler() {
        fab?.setOnClickListener({
            if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean){
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
                Statified.favoriteContent?.deleteFavourite(currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(myActivity , "Removed from Favorites" , Toast.LENGTH_SHORT)
            }else{
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
                Statified.favoriteContent?.storeAsFavorite(currentSongHelper?.SongId?.toInt() , currentSongHelper?.songArtist,currentSongHelper?.songTitle , currentSongHelper?.songPath)
                Toast.makeText(myActivity , "Added to favorites" , Toast.LENGTH_SHORT).show()
            }
        })

        /*The implementation will be taught in the coming topics*/
        shuffleImageButton?.setOnClickListener({
            var editorShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if(currentSongHelper?.isShuffle as Boolean){
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature" , false)
                editorShuffle?.apply()
            }else{
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isLoop = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature" , true)
                editorShuffle.apply()
                editorLoop?.putBoolean("feature" , false)
                editorLoop?.apply()

            }
        })

        /*Here we set the click listener to the next button*/
        nextImageButton?.setOnClickListener({

            /*We set the player to be playing by setting isPlaying to be true*/
            currentSongHelper?.isPlaying = true

            /*First we check if the shuffle button was enabled or not*/
            if (currentSongHelper?.isShuffle as Boolean) {

                /*If yes, then  we play next song randomly
                * The check string is passed as the PlayNextLikeNormalShuffle which plays the random next song*/
                playNext("PlayNextLikeNormalShuffle")
            } else {

                /*If shuffle was not enabled then we normally play the next song
                * The check string passed is the PlayNextNormal which serves the purpose*/
                playNext("PlayNextNormal")
            }
        })

        /*Here we set the click listener to the next button*/
        previousImageButton?.setOnClickListener({

            /*We set the player to be playing by setting isPlaying to be true*/
            currentSongHelper?.isPlaying = true

            /*First we check if the loop is on or not*/
            if (currentSongHelper?.isLoop as Boolean) {

                /*If the loop was on we turn it off*/
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }

            /*After all of the above is done we then play the previous song using the playPrevious() function*/
            playPrevious()
        })

        /*Here we handle the click on the loop button*/
        loopImageButton?.setOnClickListener({
            var editorShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            /*if loop was enabled, we turn it off and vice versa*/
            if (currentSongHelper?.isLoop as Boolean) {

                /*Making the isLoop false*/
                currentSongHelper?.isLoop = false

                /*We change the color of the icon*/
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature" , false)
                editorLoop?.apply()
            } else {

                /*If loop was not enabled when tapped, we enable if and make the isLoop to true*/
                currentSongHelper?.isLoop = true

                /*Loop and shuffle won't work together so we put shuffle false irrespectve of the whether it was on or not*/
                currentSongHelper?.isShuffle = false

                /*Loop button color changed to mark it ON*/
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)

                /*Changing the shuffle button to white, no matter which color it was earlier*/
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature" , false)
                editorShuffle.apply()
                editorLoop?.putBoolean("feature" , true)
                editorLoop?.apply()
            }
        })

        /*Here we handle the click event on the play/pause button*/
        playPauseImageButton?.setOnClickListener({

            /*if the song is already playing and then play/pause button is tapped
            * then we pause the media player and also change the button to play button*/
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
                currentSongHelper?.isPlaying = false
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)

                /*If the song was not playing the, we start the music player and
                * change the image to pause icon*/
            } else {
                mediaPlayer?.start()
                currentSongHelper?.isPlaying = true
                playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    /*The playNext() function is used to play the next song
    * The playNext() function is called when we tap on the next button*/

    /*The function playPrevious() is used to play the previous song again*/
    fun playPrevious() {

        /*Decreasing the current position by 1 to get the position of the previous song*/
        currentPosition = currentPosition - 1

        /*If the current position becomes less than 1, we make it 0 as there is no index as -1*/
        if (currentPosition == -1) {
            currentPosition = 0
        }
        if (currentSongHelper?.isPlaying as Boolean) {
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        currentSongHelper?.isLoop = false

        /*Similar to the playNext() function defined above*/
        var nextSong = fetchSongs?.get(currentPosition)
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songArtist = nextSong?.artist
        currentSongHelper?.songId = nextSong?.songID as Long
        updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
        mediaPlayer?.reset()
        try {
            mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            processInformation(mediaPlayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateTextViews(songtitle: String, songArtist: String){
        songTitleView?.setText(songtitle)
        songArtistView?.setText(songArtist)
    }
    fun processInformation(mediaPlayer;MediaPlayer){
        val finalTime = mediaPlayer.duration
        val startTime = mediaPlayer.currentPosition
        seekbar?.max = finalTime
        startTimeText?.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(startTime.toLong())-
                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MINUTES.toMinutes(getCurrent.toLong()))))

        endTimeText?.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong())-
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MINUTES.toMinutes(getCurrent.toLong()))))


            seekBar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime , 1000)
    }
    fun bindShakeListener(){
        Statified.mSensorListener = object : SensorEventListener{
            override fun onAccuracyChanged(p0: Sensor?, p1: Int){

            }
            override fun onSensorChanged(p0: SensorEvent){
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]

                mAccelarationLast = mAccelarationCurrent
                mAccelarationCurrent = java.lang.Math.sqrt(((x*x + y*y + z*z).toDouble())).toFloat()
                val delta = mAccelarationCurrent - mAccelarationLast
                mAccelaration = mAccelaration* 0.9f + delta

                if(mAccelaration > 12){
                    val prefs = Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature" , false)
                    if(isAllowed as Boolean){
                        Statified.playNext("PlayBextNormal")
                    }

                }
            }
        }
    }
}//everything ends