package com.saas.sgm.ozuna_1;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private ImageView playStopBtn, gifImageView;
    private DrawableImageViewTarget imageViewTarget;
    private boolean mBound = false;
    private MusicService musicService;
    private String streamUrl = "  ";
    private static final int READ_PHONE_STATE_REQUEST_CODE = 22;
    private BroadcastReceiver broadcastReceiver;
    private Snackbar snackbar;
    private AudioManager audio;
    /////
    int NumMusicSelected;
    ListView listview;
    final static ArrayList<AdapterListView> list_items = new ArrayList<AdapterListView>();
    TextView   tv_Title, textView_item;
    ImageView  btn_replay,  btn_next, btn_pre;
   // Typeface typeface;
    Intent intent;
    String website="https://en.muzmo.org";
    String songTitles[] = {

            "Daddy Yankee, J. Balvin, Ozuna, Karol G, Anuel AA, Tainy - China",
            "Luis Fonsi, Ozuna - Imposible",
            "Nicky Jam, Ozuna, Darell, Nio Garcia, bad bunny, Casper Magico - Te Boté",
            "Daddy Yankee, Ozuna, Anuel AA, Darell, Brytiago - Asesina",
            "Ozuna, Karol G - Hello",
            "Ozuna, Natti Natasha - Criminal",
            "Nicky Jam, Ozuna - Cumpleaños",
            "Bad Bunny, Wisin, Ozuna, DJ Luian, Mambo Kingz, Almighty - Solita",
            "Daddy Yankee, Ozuna - La Rompe Corazones",
            "J. Balvin, Nicky Jam, Wisin, Ozuna, Yandel, Jowell &amp; Randy - Bonita",
            "Ozuna, Anuel AA - Cambio",
            "Ozuna, Romeo Santos - El Farsante",
            "Ozuna, Manuel Turizo - Vaina Loca",
            "Ozuna, Wisin, Yandel, Yampi, Hi Flow, Yancee - Unica",

   };


    String[] sounds = {


            "/get/music/20190720/muzmo_ru_Daddy_Yankee_J_Balvin_Ozuna_Karol_G_Anuel_AA_Tainy_-_China_65571412.mp3",

            "/get/music/20181020/muzmo_ru_Luis_Fonsi_Ozuna_-_Imposible_59804893.mp3",

            "/get/music/20180503/muzmo_ru_Nicky_Jam_Ozuna_Darell_Nio_Garcia_bad_bunny_Casper_Magico_-_Te_Bot_55725218.mp3",

            "/get/music/20181108/muzmo_ru_Daddy_Yankee_Ozuna_Anuel_AA_Darell_Brytiago_-_Asesina_60204435.mp3",

            "/get/music/20170907/muzmo_ru_Ozuna_Karol_G_-_Hello_48365425.mp3",

            "/get/music/20171127/muzmo_ru_Ozuna_Natti_Natasha_-_Criminal_50997821.mp3",

            "/get/music/20170830/muzmo_ru_Nicky_Jam_Ozuna_-_Cumpleaos_47826829.mp3",

            "/get/music/20180201/muzmo_ru_Bad_Bunny_Wisin_Ozuna_DJ_Luian_Mambo_Kingz_Almighty_-_Solita_53380347.mp3",

            "/get/music/20170907/muzmo_ru_Daddy_Yankee_Ozuna_-_La_Rompe_Corazones_48365380.mp3",

            "/get/music/20180213/muzmo_ru_J_Balvin_Nicky_Jam_Wisin_Ozuna_Yandel_Jowell_Randy_-_Bonita_53780412.mp3",

            "/get/music/20190716/muzmo_ru_Ozuna_Anuel_AA_-_Cambio_65458577.mp3",

            "/get/music/20180215/muzmo_ru_Ozuna_Romeo_Santos_-_El_Farsante_53830648.mp3",

            "/get/music/20180707/muzmo_ru_Ozuna_Manuel_Turizo_-_Vaina_Loca_57180699.mp3",

            "/get/music/20180825/muzmo_ru_Ozuna_Wisin_Yandel_Yampi_Hi_Flow_Yancee_-_Unica_58212175.mp3",


    };

    public void backPressed() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.back_pressed, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.MaDialog);
        alert.setView(alertLayout);
        alert.setCancelable(true);
        TextView yes = (TextView) alertLayout.findViewById(R.id.yes);
        TextView rate_app= (TextView) alertLayout.findViewById(R.id.rate_app);
        final AlertDialog dialog = alert.create();
        rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                startActivity(intent);
                dialog.cancel();

            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.super.onBackPressed();
                musicService.stop();
            }
        });
        dialog.show();
    }
    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        backPressed();
    }

    ////
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            MusicService.MusicBinder mServiceBinder = (MusicService.MusicBinder) iBinder;
            musicService = mServiceBinder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.exit(0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ///
//        typeface = Typeface.createFromAsset(getAssets(), "Mali-Medium.ttf");
     //   typeface = getResources().getFont(R.font.malimedium);
        tv_Title = (TextView) findViewById(R.id.tv_Title);
        btn_pre = (ImageView) findViewById(R.id.btn_pre);
        btn_next = (ImageView) findViewById(R.id.btn_next);
        playStopBtn = (ImageView) findViewById(R.id.playStopBtn);
        btn_replay = (ImageView) findViewById(R.id.btn_replay);
        listview = (ListView) findViewById(R.id.listview);
        for (int i = 0; i < sounds.length; i++) {
            list_items.add(new AdapterListView(songTitles[i]));
        }
        final ListAdapter listAdapter = new ListAdapter(list_items);
        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NumMusicSelected = i;

                    musicService.play(website+sounds[i]);
                    tv_Title.setText(songTitles[i]);

                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                playStopBtn.startAnimation(animFadein);
            }
        });
        playStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!musicService.isPlaying()) {
                    musicService.play(website+sounds[NumMusicSelected]);
                    playStopBtn.setImageResource(R.drawable.ic_stop_black_24dp);
                } else {
                    musicService.stop();
                    playStopBtn.setImageResource(R.drawable.ic_play);

                }
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                playStopBtn.startAnimation(animFadein);
            }
        });
        btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.play(website+sounds[NumMusicSelected]);
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumMusicSelected++;
                if (NumMusicSelected==sounds.length){
                    NumMusicSelected=0;
                }
                musicService.play(website+sounds[NumMusicSelected]);
                tv_Title.setText(songTitles[NumMusicSelected]);
            }
        });
        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumMusicSelected--;
                if (NumMusicSelected<0)
                    NumMusicSelected=sounds.length-1;
                musicService.play(website+sounds[NumMusicSelected]);
                tv_Title.setText(songTitles[NumMusicSelected]);
            }
        });



        ///
        //radioStationNowPlaying = findViewById(R.id.radioStationNowPlaying);
        //radioStationNowPlaying.setText("Now streaming   /ssmusic");

        gifImageView = findViewById(R.id.gifImageView);
        imageViewTarget = new DrawableImageViewTarget(gifImageView);

        snackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "No internet connection", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        //processPhoneListenerPermission();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
                        if (musicService.isPlaying()) {
                            musicService.stop();
                            playStopBtn.setImageResource(R.drawable.ic_play);
                        }
                    }
                }

                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                        if (snackbar.isShown()) {
                            snackbar.dismiss();
                        }
                        playStopBtn.setEnabled(true);
                    } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                        playStopBtn.setEnabled(false);
                        snackbar.show();
                    }
                }

                int playerState = intent.getIntExtra("state", 0);
                if (playerState == PlaybackStateCompat.STATE_BUFFERING) {
                    Glide.with(MainActivity.this).load(R.drawable.not_playing).into(imageViewTarget);
                    playStopBtn.setImageResource(R.drawable.ic_stop_black_24dp);
                } else if (playerState == PlaybackStateCompat.STATE_PLAYING) {
                    playStopBtn.setImageResource(R.drawable.ic_pause);
                    Glide.with(MainActivity.this).load(R.drawable.playing).into(imageViewTarget);
                    int musicVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (musicVolume == 0) {
                        Toast.makeText(MainActivity.this, "Volume is muted", Toast.LENGTH_LONG).show();
                    }
                } else if (playerState == PlaybackStateCompat.STATE_PAUSED) {
                    playStopBtn.setImageResource(R.drawable.ic_play);
                    Glide.with(MainActivity.this).load(R.drawable.not_playing).into(imageViewTarget);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);

        createNotificationChannel();

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter("com.example.exoplayer.PLAYER_STATUS")
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicService != null && musicService.isPlaying()) {
            playStopBtn.setImageResource(R.drawable.ic_pause);
            Glide.with(MainActivity.this).load(R.drawable.playing).into(imageViewTarget);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "activity destroy called");
//        if (mBound) {
//            unbindService(serviceConnection);
//            mBound = false;
//        }
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        Log.i(TAG, "activity destroyed");
        super.onDestroy();
    }
    public void playStop(View view) {
        if (!musicService.isPlaying()) {
            musicService.play(streamUrl);
        } else {
            musicService.stop();
        }
        Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        playStopBtn.startAnimation(animFadein);
    }

    private void processPhoneListenerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_PHONE_STATE_REQUEST_CODE) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(), "Permission not granted.\nThe player will not be able to pause music when phone is ringing.", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = "Songs of "+getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("radio_playback_channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //class AdapterListView
    public class AdapterListView {

        String titles;

        public AdapterListView(String titles) {
            this.titles = titles;
        }

    }
    class ListAdapter extends BaseAdapter {
        ArrayList<AdapterListView> songAdapters = new ArrayList<AdapterListView>();

        ListAdapter(ArrayList<AdapterListView> arrayListObjectOfSongAdapter) {
            this.songAdapters = arrayListObjectOfSongAdapter;
        }


        @Override
        public int getCount() {
            return songTitles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.listview_item, null);
            textView_item = (TextView) view.findViewById(R.id.title_item);
            textView_item.setText(songAdapters.get(i).titles);
            //textView_item.setTypeface(typeface);
            return view;
        }
    }
}
