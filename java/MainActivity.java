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
    private Service service;
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
    String website=" ";
    String Titles[] = {
		"",
   };


    String[] audio = {

	"",


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
                service.stop();
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
            service.MusicBinder mServiceBinder = (service.MusicBinder) iBinder;
            service = mServiceBinder.getService();
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
        for (int i = 0; i < audio.length; i++) {
            list_items.add(new AdapterListView(Titles[i]));
        }
        final ListAdapter listAdapter = new ListAdapter(list_items);
        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NumMusicSelected = i;

                    service.play(website+audio[i]);
                    tv_Title.setText(Titles[i]);

                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                playStopBtn.startAnimation(animFadein);
            }
        });
        playStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!service.isPlaying()) {
                    service.play(website+audio[NumMusicSelected]);
                    playStopBtn.setImageResource(R.drawable.ic_stop_black_24dp);
                } else {
                    service.stop();
                    playStopBtn.setImageResource(R.drawable.ic_play);

                }
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                playStopBtn.startAnimation(animFadein);
            }
        });
        btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.play(website+audio[NumMusicSelected]);
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumMusicSelected++;
                if (NumMusicSelected==audio.length){
                    NumMusicSelected=0;
                }
                service.play(website+audio[NumMusicSelected]);
                tv_Title.setText(Titles[NumMusicSelected]);
            }
        });
        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumMusicSelected--;
                if (NumMusicSelected<0)
                    NumMusicSelected=audio.length-1;
                service.play(website+audio[NumMusicSelected]);
                tv_Title.setText(Titles[NumMusicSelected]);
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
                        if (service.isPlaying()) {
                            service.stop();
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
        Intent intent = new Intent(MainActivity.this, service.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter("com.example.exoplayer.PLAYER_STATUS")
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (service != null && service.isPlaying()) {
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
        if (!service.isPlaying()) {
            service.play(streamUrl);
        } else {
            service.stop();
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
            return Titles.length;
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
