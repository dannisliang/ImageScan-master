package net.arvin.imagescan;

import java.util.ArrayList;

import net.arvin.entitys.ConstantEntity;
import net.arvin.entitys.ImageBean;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public abstract class BaseActivity extends FragmentActivity {
	private boolean isInitImageLoader = false;
	protected TextView title, review;
	protected Button chooseOk;
	protected CheckBox chooseBox;
	protected int maxNum;
	protected boolean isCrop;
	protected ProgressDialog mDialog;
	/**
	 * 当前图集
	 */
	protected ArrayList<ImageBean> currentImages;
	/**
	 * 选中的图集，避免在切换图片文件时被清除
	 */
	protected ArrayList<ImageBean> selectedImages;
	protected ImageLoader imageLoader;
	protected DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initImageLoader(this);
		setContentView(setLayoutResId());
		initNormalData();
		
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	private void initImageLoader(Context context) {
		if (isInitImageLoader) {
			return;
		}
		isInitImageLoader = true;
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(100 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	private void initNormalData() {
		title = (TextView) findViewById(R.id.is_title);
		chooseOk = (Button) findViewById(R.id.is_choose_ok);
		chooseOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onChooseOkBtnClicked();
			}

		});
		findViewById(R.id.is_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackClicked();
			}
		});
	}

	protected void onChooseOkBtnClicked(){
		setResultData();
		SelectMultImagesActivity.INSTANCE.finish();
	}

	protected void setResultData() {
		Intent data = new Intent();
		addSelectedImages(getCurrentSelectedImages());
		data.putParcelableArrayListExtra(ConstantEntity.RESPONSE_KEY,
				selectedImages);
		setResult(RESULT_OK, data);
		finish();
	}

	protected void cropImage(String path) {
		Intent intent = new Intent(this, CropActivity.class);
		intent.putExtra(ConstantEntity.CROP_IMAGE, path);
		startActivity(intent);
	}

	protected void setChooseOkStatus(int selectedImageNum) {
		if (selectedImageNum == 0) {
			chooseOk.setText("完成");
			chooseOk.setEnabled(false);
			return;
		}
		chooseOk.setText(getString(R.string.is_chooseOk, selectedImageNum, maxNum));
		chooseOk.setEnabled(true);
	}

	protected boolean isCropImage(){
		if(maxNum == 1&&isCrop){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取当前选中的图集
	 * 
	 * @return
	 */
	protected ArrayList<ImageBean> getCurrentSelectedImages() {
		if (currentImages == null) {
			return null;
		}
		ArrayList<ImageBean> images = new ArrayList<ImageBean>();
		for (ImageBean bean : currentImages) {
			if (bean.isChecked()) {
				images.add(bean);
			}
		}
		return images;
	}

	/**
	 * 获取当前选中的图集对应的路径
	 * 
	 * @return
	 */
	protected ArrayList<String> getSelectedImagePaths() {
		return ImageBean.Object2String(getCurrentSelectedImages());
	}

	/**
	 * 同步当前图集和选中图集的数据
	 * 
	 */
	protected void syncCurrentImageStatus() throws Exception {
		for (int i = 0; i < currentImages.size(); i++) {
			boolean isNeedChecked = false;
			for (int j = 0; j < selectedImages.size(); j++) {
				if (currentImages.get(i).getImagePath()
						.equals(selectedImages.get(j).getImagePath())) {
					isNeedChecked = true;
				}
			}
			currentImages.get(i).setChecked(isNeedChecked);
		}
	}

	/**
	 * 避免重复添加已选中的图片
	 */
	protected void addSelectedImages(ArrayList<ImageBean> currentSelectedImages) {
		if (selectedImages.size() == 0) {
			selectedImages.addAll(currentSelectedImages);
			return;
		}
		if(currentSelectedImages == null){
			return ;
		}
		for (int i = 0; i < currentSelectedImages.size(); i++) {
			boolean isNeedAdd = true;
			for (int j = 0; j < selectedImages.size(); j++) {
				if (currentSelectedImages.get(i).getImagePath()
						.equals(selectedImages.get(j).getImagePath())) {
					isNeedAdd = false;
				}
			}
			if (isNeedAdd) {
				selectedImages.add(currentSelectedImages.get(i));
			}
		}
	}
	
	protected void scanFile(String path) {
		MediaScannerConnection.scanFile(this, new String[] { path }, null,
				new OnScanCompletedListener() {
					@Override
					public void onScanCompleted(String path, Uri uri) {
						Log.i("scanFile", "刷新成功");

					}
				});
	}
	
	protected void showProgressDialog(String value){
		if(mDialog == null){
			mDialog = new ProgressDialog(this);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
	        mDialog.setCancelable(true);
	        mDialog.setCanceledOnTouchOutside(false);
	        if(value!=null){
	        	mDialog.setMessage(value);
	        }
		}
		mDialog.show();
	}
	
	protected void dismissProgressDialog(){
		if(mDialog!= null){
			mDialog.dismiss();
		}
	}
	
	@Override
	protected void onDestroy() {
		dismissProgressDialog();
		mDialog = null;
		super.onDestroy();
	}
	
	protected abstract int setLayoutResId();

	protected abstract void onBackClicked();
}
