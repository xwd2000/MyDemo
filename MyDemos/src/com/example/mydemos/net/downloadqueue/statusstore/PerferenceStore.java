package com.example.mydemos.net.downloadqueue.statusstore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.bean.Task;
import com.example.util.EmptyUtil;
import com.example.util.StringUtils;

/**
 * jobs存储所有job的url
 * j+url存储job
 * jp+url存储job下载位置
 * t_+url存储任务当前位置和结束位置
 * 
 * @author xuweidong
 */
public class PerferenceStore implements StatusStore{

	private SharedPreferences spf;
	
	
	public PerferenceStore(Context context) {
		super();
		//this.context = context;
		spf=context.getSharedPreferences("perferenceStore", 0);
	}

	@Override
	public void storeTask(Job job, Task task) {
		Editor ed = spf.edit();
		ed.putString("t"+task.getByteStart()+job.getUrl(), task.getCurrentPos()+"_"+task.getByteEnd());
		ed.commit();
	}

	@Override
	public void removeTask(Job job, Task task) {
		Editor ed = spf.edit();
		ed.remove("t"+task.getByteStart()+job.getUrl());
		ed.commit();
	}

	@Override
	public List<Job> getUnFinishJob() {
		String jobs=spf.getString("jobs", "");
		List<Job> jobList= new ArrayList<Job>();
		if(!EmptyUtil.isEmpty(jobs)){
			List<String> jobStrList = new ArrayList<String>(Arrays.asList(jobs.split(",")));
			for(String jobUrl:jobStrList){
				String jobStr = spf.getString("j"+jobUrl, null);
				if(jobStr==null){
					return null;
				}
				Job job=stringToJob(jobStr);
				int jobPos = spf.getInt("jp"+jobUrl, 0);
				job.setDownloadedSize(jobPos);
				jobList.add(job);
			}
		}
		return jobList;
	}

	@Override
	public List<Task> getJobTasks(Job job) {
		List<Task> taskList=new ArrayList<Task>();
		String taskStr = spf.getString("t0"+job.getUrl(), null);
		Integer byteStart=0;
		while(!EmptyUtil.isEmpty(taskStr)){
			Task task = new Task();
			String[] splits=taskStr.split("_");
			Integer pos=Integer.parseInt(splits[0]);
			Integer endPos = Integer.parseInt(splits[1]);
			task.setByteStart(byteStart);
			task.setByteEnd(endPos);
			task.setCurrentPos(pos);
			task.setJob(job);
			taskList.add(task);
			byteStart=endPos+1;
			taskStr = spf.getString("t"+byteStart+job.getUrl(), null);
		}
		return taskList;
	}

	@Override
	public void storeJob(Job job) {
		Editor ed = spf.edit();
		
		
		Map<String,Object> keyValue=new HashMap<String,Object>();
		keyValue.put("url", job.getUrl());
		keyValue.put("savePath", job.getSavePath());
		keyValue.put("fileName", job.getFileName());
		keyValue.put("status", job.getStatus());
		keyValue.put("totalSize", job.getTotalSize());
		keyValue.put("downloadedSize", job.getDownloadedSize());
		keyValue.put("taskNum", job.getTaskNum());
		ed.putString("j"+job.getUrl(), mapToString(keyValue));
		
		
		String jobs=spf.getString("jobs", "");
		List<String> jobList = null;
		if(EmptyUtil.isEmpty(jobs)){
			jobList=new ArrayList<String>();
		}else{
			jobList = new ArrayList<String>(Arrays.asList(jobs.split(",")));
		}
		
		if(!jobList.contains(job.getUrl())){
			jobList.add(job.getUrl());
			ed.putString("jobs",StringUtils.join(jobList));
		}
		
		ed.putInt("jp"+job.getUrl(), job.getDownloadedSize());//单独存储下载位置，方便更新
		ed.commit();
	}

	@Override
	public void removeJob(Job job) {
		Editor ed = spf.edit();
		ed.remove("j"+job.getUrl());
		
		String jobs=spf.getString("jobs", "");
		if(!EmptyUtil.isEmpty(jobs)){
			List<String> jobList = new ArrayList<String>( Arrays.asList(jobs.split(",")));
			jobList.remove(job.getUrl());
			ed.putString("jobs",StringUtils.join(jobList));
		}
		ed.remove("jp"+job.getUrl());
		ed.commit();
		
	}
	
	@Override
	public void removeJobTasks(Job job) {
		Editor ed = spf.edit();
		List<String> taskKeys=new ArrayList<String>();
		Integer byteStart=0;
		String key="t"+byteStart+job.getUrl();
		String taskStr = spf.getString(key, null);
		
		while(!EmptyUtil.isEmpty(taskStr)){
			taskKeys.add(key);
			String[] splits=taskStr.split("_");
			Integer endPos = Integer.parseInt(splits[1]);
			byteStart=endPos+1;
			key="t"+byteStart+job.getUrl();
			taskStr = spf.getString(key, null);
		}
		for(int i=taskKeys.size()-1;i>=0;i--){
			ed.remove(taskKeys.get(i));
		}
		ed.commit();
	}

	
	private String mapToString(Map<String,Object> map){
		StringBuilder sb=new StringBuilder();
		for(String key:map.keySet()){
			sb.append(",");
			sb.append(key);
			sb.append(":");
			sb.append(map.get(key));
		}
		if(!map.keySet().isEmpty()){
			return sb.substring(1);
		}
		return "";
	}
	
	@Override
	public void updateTask(Job job, Task task, int currentPos) {
		Editor ed = spf.edit();
		ed.putString("t"+task.getByteStart()+job.getUrl(), currentPos+"_"+task.getByteEnd());
		ed.commit();
	}
	
	@Override
	public void updateJob(Job job, int currentPos) {
		Editor ed = spf.edit();
		ed.putInt("jp"+job.getUrl(), currentPos);
		ed.commit();
	}
	
	private Job stringToJob(String str){
		String[] keyVals=str.split(",");
		Job job=new Job();
		for(String keyVal:keyVals){
			int indexMaohao=keyVal.indexOf(':');
			String key=keyVal.substring(0,indexMaohao);
			String val=keyVal.substring(indexMaohao+1,keyVal.length());
			if(key.equals("url")){
				job.setUrl(val);
			}else if(key.equals("savePath")){
				job.setSavePath(val);
			}
			else if(key.equals("fileName")){
				job.setFileName(val);
			}
			else if(key.equals("status")){
				job.setStatus(Integer.parseInt(val));
			}
			else if(key.equals("totalSize")){
				job.setTotalSize(Integer.parseInt(val));
			}
			else if(key.equals("downloadedSize")){
				job.setDownloadedSize(Integer.parseInt(val));
			}
			else if(key.equals("taskNum")){
				job.setTaskNum(Integer.parseInt(val));
			}
			
		}
		return job;
	}

	


	


}
