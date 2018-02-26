package com.bobxie.multidownload.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bobxie.multidownload.R;
import com.bobxie.multidownload.entities.FileInfo;
import com.bobxie.multidownload.services.DownloadService;

import java.util.List;

/**
 * Created by bob on 2018/2/26.
 */
public class FileListAdapter extends BaseAdapter {
    private Context context = null;
    private List<FileInfo> mFileList = null;

    public FileListAdapter(Context context, List<FileInfo> mFileList) {
        this.context = context;
        this.mFileList = mFileList;
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final FileInfo fileInfo = mFileList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_download_item, null);
            holder = new ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.mBtnStart = (Button) convertView.findViewById(R.id.button);
            holder.mBtnStop = (Button) convertView.findViewById(R.id.button2);
            holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_info);
            holder.tvFileName = convertView.findViewById(R.id.tv_file_name);

            holder.mProgressBar.setMax(100);
            holder.tvFileName.setText(fileInfo.getFileName());
            holder.mBtnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_START);
                    intent.putExtra(DownloadService.INTENT_FILEINFO, fileInfo);
                    context.startService(intent);
                }
            });
            holder.mBtnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_STOP);
                    intent.putExtra(DownloadService.INTENT_FILEINFO, fileInfo);
                    context.startService(intent);
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mProgressBar.setProgress(fileInfo.getFinished());
        return convertView;
    }

    /**
     * 更新列表项中的进度条
     */
    public void updateProgressBar(int id, int progress) {
        FileInfo fileInfo = mFileList.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        private ProgressBar mProgressBar;
        private Button mBtnStart;
        private Button mBtnStop;
        private TextView tvInfo;
        private TextView tvFileName;
    }

}
