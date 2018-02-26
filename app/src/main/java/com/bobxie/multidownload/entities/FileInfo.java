package com.bobxie.multidownload.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 文件信息
 * Created by bob on 2018/2/25.
 */
@DatabaseTable(tableName = "tb_file_info")
public class FileInfo implements Parcelable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "url")
    private String url;
    @DatabaseField(columnName = "file_name")
    private String fileName;
    @DatabaseField(columnName = "length")
    private long length;
    @DatabaseField(columnName = "finished")
    private int finished;

    public FileInfo() {
    }

    public FileInfo(int id, String url, String fileName, int length, int finished) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length='" + length + '\'' +
                ", finished=" + finished +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.url);
        dest.writeString(this.fileName);
        dest.writeLong(this.length);
        dest.writeLong(this.finished);
    }

    protected FileInfo(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.fileName = in.readString();
        this.length = in.readInt();
        this.finished = in.readInt();
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel source) {
            return new FileInfo(source);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}
