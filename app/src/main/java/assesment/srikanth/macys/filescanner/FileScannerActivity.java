package assesment.srikanth.macys.filescanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * This activity is a launcher activity of the application.
 * It provides scan functionality.
 */

public class FileScannerActivity extends AppCompatActivity {


    public static final String PROGRESS_BROADCAST_ACTION = "PROGRESS_BROADCAST_ACTION";

    private Button startScanBtn;
    private Button stopScanBtn;
    private ProgressDialog progressDialog;
    private Intent service;
    private ProgressReceiver receiver;
    private int notificationId = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_scanner);

        //register a broadcast receiver. service uses this broadcast receiver to send updates about the progress.
        receiver = new ProgressReceiver();
        registerReceiver(receiver, new IntentFilter(PROGRESS_BROADCAST_ACTION));

        startScanBtn = (Button)findViewById(R.id.scanBtn);
        stopScanBtn = (Button)findViewById(R.id.stopBtn);

        if(savedInstanceState != null) {
            //means activity is recreated. so loading progress if it stopped in middle.
            int progress = savedInstanceState.getInt(Constants.PROGRESS_UPDATE_KEY);
            if(progress > 0 && progress < 100) {
                showProgressDialog();
                progressDialog.setProgress(savedInstanceState.getInt(Constants.PROGRESS_UPDATE_KEY));
            }
        }

    }

    public void startScan(View v) {
        if(isExternalStorageReadable()) {
            showProgressDialog();
            service = new Intent(this, FileScanningService.class);
            startService(service);
            showNotification(null, Constants.SCANNING_STARTED, "");
            startScanBtn.setEnabled(false);
        }
    }

    public void stopScan(View v) {
        if(service != null) {
            //sending stop signal to running service using broadcast receiver
            Intent intent = new Intent(new Intent(FileScanningService.STOP_SERVICE_ACTION));
            sendBroadcast(intent);
            NotificationManager nMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nMgr.cancel(notificationId);
            startScanBtn.setEnabled(true);
            Toast.makeText(this, Constants.SCANNING_STOPPED, Toast.LENGTH_LONG).show();
        }
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void showProgressDialog(){
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage(Constants.SCANNING_EXTERNAL_STORAGE);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    public void updateProgress(Bundle bundle) {
        int progress = bundle.getInt(Constants.PROGRESS_UPDATE_KEY, 0);
        if(progress >= Constants.SCAN_FINISHED_VALUE) {
            startScanBtn.setEnabled(true);
            progressDialog.cancel();
            showNotification(bundle, Constants.SCANNING_COMPLETED, "Total Files Scanned : " + bundle.getInt(Constants.TOTAL_FILES_KEY));
            Intent intent = new Intent(this, ViewScanResultsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        progressDialog.setProgress(progress);
    }

    public void showNotification(Bundle bundle, String title, String content) {
        // Build notification
         Notification noti = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(content).setSmallIcon(android.R.drawable.stat_notify_sdcard)
                .build();

        if(bundle != null) {
            Intent intent = new Intent(this, ViewScanResultsActivity.class);
            intent.putExtras(bundle);
            PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
            noti.contentIntent = pIntent;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(notificationId, noti);

    }


    public void onDestroy() {
        super.onDestroy();
        progressDialog = null;
        service = null;
        unregisterReceiver(receiver);
        receiver = null;
    }

    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        if(progressDialog != null && progressDialog.isShowing()) {
            outState.putInt(Constants.PROGRESS_UPDATE_KEY, progressDialog.getProgress());
        }
    }


    private class ProgressReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateProgress(intent.getExtras());
        }
    }
}

