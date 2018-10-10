/*
 * Copyright (C) 2015 zhenjin ma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.mark.multiimage.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.gyf.barlibrary.ImmersionBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagesBaseActivity extends FragmentActivity{

	protected static int sPosotion;
	public static  int MAX_SEND = 9;
	protected static List<ImageEntity> sResult = new ArrayList<ImageEntity>();
	protected static boolean isOriginal;
	protected ImmersionBar mImmersionBar;

	protected Context mContext;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mContext = this;
		if(PreferencesUtils.getContext()==null);{
			PreferencesUtils.setContext(mContext);
		}
		mImmersionBar = ImmersionBar.with(this);
		mImmersionBar
				.statusBarDarkFont(false, 0.2f) //设置状态栏字体颜色是否为黑色，不设置透明度，则默认透明
				.fitsSystemWindows(true)
				.statusBarColor(R.color.colorPrimary,0.7f)//设置状态栏颜色，不设置透明度，则默认透明
				.init();
	}
	
	public void submit(){

		ArrayList<Uri> respUris = new ArrayList<Uri>();
		if(isOriginal){
			for(int i=0;i<sResult.size();i++){
				ImageEntity data = sResult.get(i);
				Uri uri = Uri.parse("file:///"+data.getUrl());
				respUris.add(uri);
			}
		}else{
			for(int i=0;i<sResult.size();i++){
				ImageEntity data = sResult.get(i);
				Bitmap bm = BitmapUtils.getThumbnail(this, data.getId());
				String path = getCachePath();
				if(saveCacheDir(path, bm)){
					Uri uri = Uri.parse("file:///"+path);
					respUris.add(uri);
				}
			}
		}
		sResult.clear();

		if(respUris!=null && respUris.size()>0){
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("result", respUris);
			setResult(RESULT_OK, intent);
		}else{
			setResult(RESULT_CANCELED);
		}

		finish();
	}
	
	public boolean saveCacheDir(String filename, Bitmap bitmap) {
		File f = new File(filename);
		try {
			if(f.exists()){
				f.deleteOnExit();
			}
			f.createNewFile();
			
		    FileOutputStream out = new FileOutputStream(f);
		    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		    out.flush();
		    out.close();
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private String getCachePath(){
		String fileName = String.valueOf(System.currentTimeMillis())+".jpg";
		String folder = this.getCacheDir().getPath();
		String fullPath = folder+"/"+fileName;
		return fullPath;
	}
	
	protected void finishAndClear(){
		//isOriginal = false;
		sPosotion = 0;
		sResult.clear();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		if (mImmersionBar != null)
//			mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态


	}
}
