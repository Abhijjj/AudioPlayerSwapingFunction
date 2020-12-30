package com.geochem.audioplayerswapingfunction

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geochem.audioplayerswapingfunction.adapter.AudioAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    var i=0
    lateinit var mediaPlayer: MediaPlayer
    var lastPos: Int? = null
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1

    lateinit var linearLayoutManager: LinearLayoutManager
    var arrayListA:ArrayList<HashMap<String, String>> ?=null
    lateinit var vAdapter:AudioAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestRead()

        setUpRecyclerView()

        setData()
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    val itemTouchHelperCallback =
        object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or
                    ItemTouchHelper.END,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recycler_View: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                var fromPos=viewHolder.adapterPosition
                var targetPos=target.adapterPosition
                Collections.swap(vAdapter.audioList,fromPos,targetPos)

                recyclerView.adapter?.notifyItemMoved(fromPos,targetPos)

                return true
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {



                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                var pos=viewHolder.adapterPosition

                if(i!=0){
                    if(pos==lastPos){
                        mediaPlayer.pause()
                    }else{
                        mediaPlayer.stop()
                    }
                }
                i++

                if(pos!=lastPos){
                    mediaPlayer= MediaPlayer()
                }
                var path= arrayListA?.get(pos)?.get("songPath")
                try {
                    mediaPlayer.setDataSource(
                      path
                    )
                    mediaPlayer.prepare()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                when(direction){
                     ItemTouchHelper.LEFT ->{

                         if(mediaPlayer.isPlaying){
                          if(pos==lastPos){
                              mediaPlayer.stop()

                          }else{
                              mediaPlayer.start()
                          }
                         }else{

                            lastPos=pos
                             mediaPlayer.start()
                         }
                     }
                     ItemTouchHelper.RIGHT ->{
                         if(mediaPlayer.isPlaying){
                             if(pos==lastPos){
                                 mediaPlayer.stop()

                             }else{
                                 mediaPlayer.start()
                             }


                         }else{
                             mediaPlayer.start()
                             lastPos=pos
                         }

                     }
                     else ->{

                     }
                 }

                vAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }

        }



    fun requestRead() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            arrayListA= getAudioList()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                arrayListA= getAudioList()
            } else {
                // Permission Denied
                Toast.makeText(this
                    , "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }
    private fun setData() {

        vAdapter= AudioAdapter(this,getAudioList())
        recyclerView.adapter=vAdapter
    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.setLayoutManager(linearLayoutManager)
        recyclerView.setHasFixedSize(false)
    }



    fun getAudioList(): ArrayList<HashMap<String, String>>? {
        val mSongsList =
            ArrayList<HashMap<String, String>>()
        val mCursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA
            ), null, null, null
        )
        val count = mCursor!!.count
        println("total no of songs are=$count")
        var songMap: HashMap<String, String>
        while (mCursor.moveToNext()) {
            songMap = HashMap()
            songMap["songTitle"] = mCursor.getString(
                mCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            )
            songMap["songPath"] = mCursor.getString(
                mCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            )
            mSongsList.add(songMap)
        }
        mCursor.close()
        return mSongsList
    }
}