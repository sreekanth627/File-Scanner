package assesment.srikanth.macys.filescanner;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * This service scans all the directories in external storage.
 * It finds top 10 largest files and top 5 last modified files
 * It sends broadcast whenever it has something to publish.
 */


public class FileScanningService extends IntentService {

    private int totalFilesCount = 0, processedFileCount = 0, publishedProgress = 0;
    private long totalMemoryInKB;

    public static String STOP_SERVICE_ACTION = "STOP_SERVICE_ACTION";

    private ArrayList<File> largeFileList = new ArrayList<File>();
    private ArrayList<File> recentFileList = new ArrayList<File>();
    private HashMap<String, Integer> fileExtensionMap = new HashMap<String, Integer>();

    private boolean isServiceStopped = false;

    private StopServiceReceiver stopReceiver;

    public FileScanningService() {
        super("FileScanningService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        resetAllValues();

        stopReceiver = new StopServiceReceiver();
        registerReceiver(stopReceiver, new IntentFilter(STOP_SERVICE_ACTION));

        //finding total file count is helpful to update progress bar.
        countTotalFilesInDirectory(new File(dirPath));
        //real work starts in this method. it process every file in the sd card.
        scanExternalStorage(new File(dirPath));
        //sending scan finished event to activity by following line.
        publishProgress(Constants.SCAN_FINISHED_VALUE);
    }

    private void scanExternalStorage(File parentDir) {
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if(!isServiceStopped) {
                if (file.isDirectory()) {
                    scanExternalStorage(file);
                } else {
                    handleNewLargeListEntry(file);
                    handleNewRecentListEntry(file);
                    handleNewEntry(file);
                    processedFileCount++;
                    calculateProgress();
                }
            }
        }
    }

    private void calculateProgress() {
       double percentage = (processedFileCount * 1.0) / totalFilesCount;
        int roundedPercentage = (int)(percentage * 100);
        if(roundedPercentage > publishedProgress) {
            publishedProgress = roundedPercentage;
            publishProgress(publishedProgress);
        }
    }

    private void publishProgress(int progress) {
        if(!isServiceStopped) {
            Intent intent = new Intent(new Intent(FileScannerActivity.PROGRESS_BROADCAST_ACTION));
            intent.putExtra(Constants.PROGRESS_UPDATE_KEY, progress);
            if (progress >= Constants.SCAN_FINISHED_VALUE) {
                ArrayList<FileInfo> largeList = createFileInfoList(largeFileList);
                ArrayList<FileInfo> recentList = createFileInfoList(recentFileList);

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.TOTAL_FILES_KEY, totalFilesCount);
                bundle.putLong(Constants.TOTAL_MEMORY_KEY, totalMemoryInKB);
                bundle.putParcelableArrayList(Constants.LARGEST_FILES_KEY, largeList);
                bundle.putParcelableArrayList(Constants.RECENT_FILES_KEY, recentList);
                bundle.putSerializable(Constants.FREQUENT_EXTENSIONS_KEY, fileExtensionMap);
                intent.putExtras(bundle);
            }
            sendBroadcast(intent);
        }
    }

    private void handleNewLargeListEntry(File file) {
        totalMemoryInKB = (long)(totalMemoryInKB + (file.length() * 1.0)/1024);
        if(largeFileList.size() < Constants.LARGE_FILES_COUNT_NEEDED) {
            largeFileList.add(file);
            if(largeFileList.size() == Constants.LARGE_FILES_COUNT_NEEDED) {
                sortLargestList();
            }
            return;
        }

        if(file.length() > largeFileList.get(0).length()) {
            largeFileList.set(0, file);
            sortLargestList();
        }

    }

    private void handleNewRecentListEntry(File file) {
        if(recentFileList.size() < Constants.RECENT_FILES_COUNT_NEEDED) {
            recentFileList.add(file);
            if(recentFileList.size() == Constants.RECENT_FILES_COUNT_NEEDED) {
                sortRecentList();
            }
            return;
        }

        if(file.lastModified() > recentFileList.get(0).lastModified()) {
            recentFileList.set(0, file);
            sortRecentList();
        }

    }

    private void handleNewEntry(File file) {
        String extension = "";
        try {
            extension = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toURL().toString() );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if(fileExtensionMap.containsKey(extension)) {
            Integer count = fileExtensionMap.get(extension);
            fileExtensionMap.put(extension, ++count);
        } else {
            fileExtensionMap.put(extension, 1);
        }
    }

    private void sortLargestList() {
        Collections.sort(largeFileList, new Comparator<File>() {
            public int compare( File a, File b ) {
                if(a.length() >= b.length()) {
                    return 1;
                }
                return -1;
            }
        } );

    }

    private void sortRecentList() {
        Collections.sort(recentFileList, new Comparator<File>() {
            public int compare( File a, File b ) {
                if(a.lastModified() >= b.lastModified()) {
                    return 1;
                }
                return -1;
            }
        } );

    }

    private ArrayList<FileInfo> createFileInfoList(ArrayList<File> fileList) {

        ArrayList<FileInfo> fileInfoList = new ArrayList<FileInfo>();

        for(int i = fileList.size() - 1; i >= 0; i--) {
            File file = fileList.get(i);
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(file.getName());
            long size = (long)(file.length() * 1.0 /1024);
            fileInfo.setFileSize(size);
            fileInfo.setStorageLocation(file.getAbsolutePath());
            fileInfo.setLastOpened(file.lastModified());
            fileInfoList.add(fileInfo);
        }

        return fileInfoList;
    }

    private void countTotalFilesInDirectory(File parentDir) {
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                countTotalFilesInDirectory(file);
            } else {
                totalFilesCount++;
            }
        }
    }

    private void resetAllValues() {
        totalFilesCount = 0;
        processedFileCount = 0;
        publishedProgress = 0;
        totalMemoryInKB = 0;
        isServiceStopped = false;
        largeFileList = new ArrayList<File>();
        recentFileList = new ArrayList<File>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stopReceiver);
    }

    private class StopServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isServiceStopped = true;
        }
    }



}
