package assesment.srikanth.macys.filescanner;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by srikanthsanagapalli on 6/8/16.
 * This is a model class. It contains filename, size, location and last modified time.
 * while showing results in list, this is useful.
 */
public class FileInfo implements Parcelable {
    private String fileName;
    private long fileSize;
    private long lastOpened;
    private String storageLocation;

    FileInfo() {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }


    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public long getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(long lastOpened) {
        this.lastOpened = lastOpened;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public FileInfo(Parcel in) {
        fileName = in.readString();
        fileSize = in.readLong();
        lastOpened = in.readLong();
        storageLocation = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeLong(fileSize);
        dest.writeLong(lastOpened);
        dest.writeString(storageLocation);

    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}
