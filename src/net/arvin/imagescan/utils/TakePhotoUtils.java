package net.arvin.imagescan.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import net.arvin.imagescan.entitys.ConstantEntity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

@SuppressLint("InlinedApi")
public class TakePhotoUtils {
	private Activity mActivity;
	private String fileDir;
	private String imagePath;
	private File mCurrentFile;
	private SelectImageSuccessListener imageSuccess;
	private static final int RESULT_OK = -1;

	public TakePhotoUtils(Activity mActivity,
			SelectImageSuccessListener imageSuccess) {
		this.mActivity = mActivity;
		this.imageSuccess = imageSuccess;
		initFileDir();
	}

	private void initFileDir() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			fileDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else {
			fileDir = Environment.getRootDirectory().getAbsolutePath();
		}
		fileDir += "/" + ConstantEntity.SAVE_IMAGE_FILE_NAME;
	}

	public String getImagePath() {
		if (imagePath != null && !imagePath.equals("")) {
			return imagePath;
		}
		return null;
	}

	public void choosePhotoFromCamera() {
		File photoDir = new File(fileDir);
		if (!photoDir.exists()) {
			photoDir.mkdirs();
		}
		mCurrentFile = new File(fileDir, getPhotoName());
		Log.i("URI", mCurrentFile.toString());
		imagePath = mCurrentFile.toString();
		final Intent intent = getCameraIntent(mCurrentFile);
		mActivity.startActivityForResult(intent,
				ConstantEntity.IMAGE_REQUEST_TAKE_PHOTO);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data,
			Activity mActivity) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ConstantEntity.IMAGE_REQUEST_TAKE_PHOTO:
				imageSuccess.onSelectImageSuccess(imagePath);
				break;
			}
		}
	}

	private String getPhotoName() {
		return Calendar.getInstance().getTimeInMillis() + ".jpg";
	}

	private Intent getCameraIntent(File f) {
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	public interface SelectImageSuccessListener {
		public void onSelectImageSuccess(String path);
	}

	public interface SelectMultImageListener {
		public void onSelectMultImageListener(ArrayList<String> paths);
	}

}
