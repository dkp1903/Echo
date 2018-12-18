package com.appdev.dkp.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.appdev.dkp.echo.R
import com.appdev.dkp.echo.Songs


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FavoriteFragment : Fragment() {

    var myActivity: Activity?=null
    var getSongsList: java.util.ArrayList<Songs>?= null
    var noFavorites: TextView?=null
    var nowPlayingBottomBar : RelativeLayout?= null
    var playPauseButton: ImageButton?= null
    var songTitle: TextView?= null
    var recyclerView: RecyclerView?= null
    var trackPosition: Int = 0
//    var favoriteContent: EchoDatabase?= null

    var refreshList: ArrayList<Songs>?= null
    var getListfromDatabase: ArrayList<Songs>?= null


    object Statified{
        var mediaPlayer: MediaPlayer?= null

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        noFavorites = view?.findViewById(R.id.noFavourites)
        nowPlayingBottomBar = view.findViewById(R.id.hiddenBarFavScreen)
        songTitle = view.findViewById(R.id.songTitle)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        recyclerView = view.findViewById(R.id.favoriteRecycler)
            return view
    }

    override fun onAttach(context: Context?){
        super.onAttach(context)
        myActivity = context as Activity
    }
    override fun onAttach(activity: Activity?){
        super.onAttach(activity)
        myActivity = activity
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?){
        super.onActivityCreated(savedInstanceState)
        favoriteContent = EchoDatabase(myActivity)
        getSongsList = getSongsFromPhone()
        if(getSongsList == null){
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE
        }else{
                var favoriteAdapter = FavoriteAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = favoriteAdapter
            recyclerView?.setHasFixedSize(true)
        }

    }
    override fun onResume(){
        super.onResume()
    }
    override fun onPrepareOptionsMenu(menu: Menu?){
        super.onPrepareOptionsMenu(menu)
    }
    fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songID)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songID)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))

            }
        }
        return arrayList
    }
    fun bottomBarSetup(){
        try{
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaplayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPLayingFragment.Staticated.onSongComplete()
            })
            if(SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean){
                nowPlayingBottomBar?.visibility = View.VISIBLE
            }else{
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        }catch(e : Exception){
            e.printStackTrace()
        }
    }
    fun bottomBarClickHandler(){
        nowPlayingBottomBar?.setOnClickListener({
            Statified.mediaPlayer = SongPlayongFragment. Statified.mediaplayer
            //when the user clicks on the bar..they should get redirected to the songplaying fragment screen
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt("SongID", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
            args.putString("FavBottomBar" , "success")
            songPlayingFragment.arguments = args
            fragmentManager.beginTransaction().replace(R.id.details_fragment , songPlayingFragment).addToBackStack("SongPlayingFragment").commit

        })
        playPauseButton?.setOnClickListener({
            if(SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean){
                SongPlayingFragment.Statified.mediapplayer?.pause()
                trackposition = SongPlayingFragment.Statified.mediaplayer?.getCurrentPosition() as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }//if ends here
            else{
                SongPlayingFragment.Statified.mediaplayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaplayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })

    }

    fun display_favorites_by_searching() {
/*Checking if database has any entry or not*/
        if (favoriteContent?.checkSize() as Int > 0) {
/*New list for storing the favorites*/
            refreshList = ArrayList<Songs>()
/*Getting the list of songs from database*/
            getListfromDatabase = favoriteContent?.queryDBList()
/*Getting list of songs from phone storage*/
            val fetchListfromDevice = getSongsFromPhone()
/*If there are no songs in phone then there cannot be any favorites*/
            if (fetchListfromDevice != null) {
/*Then we check all the songs in the phone*/
                for (i in 0..fetchListfromDevice?.size - 1) {
/*We iterate through every song in database*/
                    for (j in 0..getListfromDatabase?.size as Int - 1) {
/*While iterating through all the songs we check for the songs
which are in both the lists
* i.e. the favorites songs*/
                        if (getListfromDatabase?.get(j)?.songID ===
                                fetchListfromDevice?.get(i)?.songID) {
/*on getting the favorite songs we add them to the refresh
list*/
                            refreshList?.add((getListfromDatabase as ArrayList<Songs>)
                                    [j])
                        }
                    }
                }
            } else {
            }
/*If refresh list is null we display that there are no favorites*/
            if (refreshList == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            } else {
/*Else we setup our recycler view for displaying the favorite songs*/
                val favoriteAdapter = FavoriteAdapter(refreshList as ArrayList<Songs>,
                        myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favoriteAdapter
                recyclerView?.setHasFixedSize(true)
            }
        } else {
/*If initially the checkSize() function returned 0 then also we display the no
favorites present message*/
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE
        }
    }
}//class ends here