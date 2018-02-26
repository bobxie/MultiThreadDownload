package com.bobxie.multidownload.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bob on 2018/2/25.
 */
@DatabaseTable(tableName = "tb_thread_info")
public class ThreadInfo {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "url")
    private String url;
    @DatabaseField(columnName = "start")
    private long start;
    @DatabaseField(columnName = "end")
    private long end;
    @DatabaseField(columnName = "finished")
    private long finished;

    public ThreadInfo() {

    }

    public ThreadInfo(int id, String url, long start, long end, long finished) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
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

    public long getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", finished=" + finished +
                '}';
    }
}
