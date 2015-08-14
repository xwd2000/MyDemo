package com.example.mydemos.net.downloadqueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mydemos.R;
import com.example.mydemos.net.downloadqueue.DownloadService.ServiceBinder;
import com.example.mydemos.net.downloadqueue.bean.Job;

/**
 * @author xuweidong
 */
public class DownloadActicity extends Activity implements OnClickListener,
		ServiceConnection {
	public static final String TAG="DownloadActicity";
	private ListView processList;
	private Button button;
	private DownloadService service;
	private boolean binded;
	private List<Job> jobList;
	private ProgressListAdapter progressListAdapter;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Job.STATUS_INITED:
				
				break;
			case Job.STATUS_STARTED:

				break;
				
			case Job.STATUS_RUNNING:

				break;
			case Job.STATUS_STOP:

				break;
			case Job.STATUS_FINISHED:

				break;
		
			case Job.STATUS_ERROR:

				break;

			default:
				break;
			}
			progressListAdapter.notifyDataSetChanged();
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.download_activity);
		initInstance();
		initView();
		Intent intent = new Intent(DownloadActicity.this, DownloadService.class);
		intent.putExtra(DownloadService.OPERATE_KEY,
				DownloadService.OPERATE_STAET_DOWNLOAD_SERVICE);
		startService(intent);
		binded = bindService(new Intent(DownloadActicity.this,
				DownloadService.class), this, BIND_AUTO_CREATE);
		super.onCreate(savedInstanceState);
	}

	private void initInstance() {

		processList = (ListView) findViewById(R.id.lv_process_list);
		jobList=new ArrayList<Job>();
		progressListAdapter=new ProgressListAdapter(jobList, this);
	}

	private void initView() {
		processList.setAdapter(progressListAdapter);
		
	}

	public int i=0;
	@Override
	public void onClick(View v) {
		String downloadUrl[] = {
				"http://10.20.34.109:8080/WebModule/photo.rar",
				"http://10.20.34.109:8080/WebModule/photo1.rar",
				"http://10.20.34.109:8080/WebModule/photo2.rar",
				"http://10.20.34.109:8080/WebModule/photo3.rar",
				"http://10.20.34.109:8080/WebModule/photo4.rar",
				"http://10.20.34.109:8080/WebModule/photo5.rar"
		};
		if (binded) {
			Job job = service.addNewDownLoad(downloadUrl[i++%6], handler);
			progressListAdapter.addJobProcess(job);
			
		}
	}
	


	
	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		this.service = ((ServiceBinder) binder).getService();
		List<Job> jobs =service.getUnfinishedJob();
		for(Job job:jobs){
			service.setJobHandler(job, handler);
		}
		progressListAdapter.addJobProcess(jobs);
		service.setActivityReady();
		Log.d(TAG, "onServiceConnected");
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.d(TAG, "onServiceDisconnected");
	}

	@Override
	protected void onDestroy() {
		unbindService(this);
		super.onDestroy();
	}

	class ProgressListAdapter extends BaseAdapter {
		private List<Job> jobList;
		private Context context;
		public ProgressListAdapter(List<Job> jobList,Context context) {
			super();
			this.jobList = jobList;
			this.context=context;
		}
		
		@Override
		public int getCount() {
			return jobList.size();
		}

		@Override
		public Job getItem(int position) {
			return jobList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void addJobProcess(Job job){
			jobList.add(job);
			notifyDataSetChanged();
		}
		public void addJobProcess(Collection<Job> jobCol){
			jobList.addAll(jobCol);
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
	        if (convertView == null) {
	            viewHolder = new ViewHolder();

	            // 获取list_item布局文件的视图
	            convertView = LayoutInflater.from(context).inflate(R.layout.download_activity_item, null);
	            viewHolder.tvProcess=(TextView)convertView.findViewById(R.id.tv_download_process);
	            viewHolder.pbProgress=(ProgressBar)convertView.findViewById(R.id.pb_download_process);
	            viewHolder.btOperate=(Button)convertView.findViewById(R.id.bt_download_process);
	            convertView.setTag(viewHolder);
	        }else{
	        	viewHolder = (ViewHolder) convertView.getTag();
	        }
	        Job job = getItem(position);
	        viewHolder.tvProcess.setText(job.getDownloadedSize()+"/"+job.getTotalSize());
	        viewHolder.pbProgress.setMax(job.getTotalSize());
	        viewHolder.pbProgress.setProgress(job.getDownloadedSize());
	        return convertView;
		}
		public class ViewHolder{
			public TextView tvProcess;
			public ProgressBar pbProgress;
			public Button btOperate;
			
		}
	}

}
