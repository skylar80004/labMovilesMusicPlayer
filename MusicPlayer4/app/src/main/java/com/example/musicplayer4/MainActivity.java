package com.example.musicplayer4;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {



    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    SeekBar seekBarVolume;
    SeekBar seekBarSong;
    Boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        try {
            this.InitSongList();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        //Volumen
        this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


        this.seekBarVolume = findViewById(R.id.seekBarVolume);
        this.seekBarVolume.setMax(maxVolume);
        this.seekBarVolume.setProgress(currentVolume);


        this.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

              audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        //List On Click Listener
        ListView listView = findViewById(R.id.listViewSongsList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }

                TextView textView = (TextView) view;
                String name = (String)textView.getText();
                int songID = getResources().getIdentifier(name,"raw",getPackageName());


                mediaPlayer = MediaPlayer.create(getApplicationContext(),songID);
                mediaPlayer.start();


                // Set Bar
                final SeekBar advanceSeekBar = findViewById(R.id.seekBarSong);
                int duration =  mediaPlayer.getDuration();
                int progress = mediaPlayer.getCurrentPosition();
                advanceSeekBar.setMax(duration);
                advanceSeekBar.setProgress(progress);

                Button button = findViewById(R.id.buttonPlayPause);
                button.setText("Pause");
                isPlaying = true;

                TextView textSongName = findViewById(R.id.textViewSongInfo);
                textSongName.setText(name);





            }
        });

        final SeekBar seekBarSong = findViewById(R.id.seekBarSong);
        seekBarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                
                if (fromUser){
                    this.mediaPlayer.seekTo(progress);
                       
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){

                if(mediaPlayer != null){
                   // Log.i("CHES","hola");
                    SeekBar seekBarSong = findViewById(R.id.seekBarSong);
                    seekBarSong.setProgress(mediaPlayer.getCurrentPosition());

                }

            }
        },0,1000);
    }




    public void OnClickButtonPlayPause(View view){


        if(mediaPlayer == null){
            return;
        }

        if(this.mediaPlayer.isPlaying()){
            this.mediaPlayer.pause();
            Button button = (Button)view;
            button.setText("Play");
        }
        else{
            this.mediaPlayer.start();
            Button button = (Button)view;
            button.setText("Pause");
        }


    }


    public void OnClickButtonNext(View view){

        if(this.mediaPlayer != null){

            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            TextView textSong = findViewById(R.id.textViewSongInfo);
            ListView listView = findViewById(R.id.listViewSongsList);
            ListAdapter listAdapter = listView.getAdapter();

            String item;
            String nameActualSong = (String) textSong.getText();
            int posNext = listAdapter.getCount() - 1;
            for(int i = 0 ; i < listAdapter.getCount();i++){
                item = (String)listAdapter.getItem(i);
                if(item.equals(nameActualSong)){
                    posNext = i + 1;
                }
                break;
            }

            if(posNext == listAdapter.getCount()){
                posNext--;
            }

            String previousSong = (String)listAdapter.getItem(posNext);
            int songID = getResources().getIdentifier(previousSong,"raw",getPackageName());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),songID);
            mediaPlayer.start();
            textSong.setText(previousSong);


        }
    }

    public void OnClickButtonPrevious(View view){

        if(this.mediaPlayer != null){

            if(mediaPlayer.isPlaying()){
                this.mediaPlayer.stop();
            }
            TextView textSong = findViewById(R.id.textViewSongInfo);
            ListView listViewSong = findViewById(R.id.listViewSongsList);
            ListAdapter listAdapter = listViewSong.getAdapter();


            String item ;
            String nameActualSong = (String)textSong.getText();
            int posPrevious = 0;
            for(int i = 0 ; i < listAdapter.getCount();i++){

                item =(String)listAdapter.getItem(i);
                Log.i("CHESITEM",item);
                if(item.equals(nameActualSong)){
                    posPrevious = i - 1;
                    break;
                }
            }

            if(posPrevious == - 1){
                posPrevious = 0 ;
            }



            String previousSong = (String)listAdapter.getItem(posPrevious);
            int songID = getResources().getIdentifier(previousSong,"raw",getPackageName());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),songID);
            mediaPlayer.start();
            textSong.setText(previousSong);

        }
    }

    public  ArrayList<String> GetFilesName(){

        Field[] fields=R.raw.class.getFields();
        ArrayList<String> fileNames = new ArrayList<>();
        for(int count=0; count < fields.length; count++) {
            fileNames.add(fields[count].getName());
           // Log.i("CHES", fields[count].getName());
        }
        return fileNames;
    }

    public void InitSongList() throws URISyntaxException {

        ListView listView = findViewById(R.id.listViewSongsList);
        final ArrayList<String> elements = new ArrayList<>();
        ArrayList<String> fileListNames = this.GetFilesName();
        String fileName;
        for(int i = 0 ; i< fileListNames.size();i++){

            fileName = fileListNames.get(i);
            elements.add(fileName);
        }


        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1
                        ,elements);
        listView.setAdapter(itemsAdapter);

    }


    // https://stackoverflow.com/questions/7499605/how-to-play-the-audio-files-directly-from-res-raw-folder



}
