package net.arvin.imagescan.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.arvin.imagescan.R;
import net.arvin.imagescan.adapters.FileMenuAdapter;
import net.arvin.imagescan.adapters.ImagesAdapter;
import net.arvin.imagescan.entitys.ConstantEntity;
import net.arvin.imagescan.entitys.ImageBean;
import net.arvin.imagescan.entitys.ImageFileBean;
import net.arvin.imagescan.listeners.OnItemChecked;
import net.arvin.imagescan.utils.TakePhotoUtils;
import net.arvin.imagescan.utils.WindowUtils;
import net.arvin.imagescan.utils.TakePhotoUtils.SelectImageSuccessListener;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class SelectMultImagesActivity extends BaseActivity implements
		OnItemClickListener, OnItemChecked, OnClickListener,
		SelectImageSuccessListener {
	public static SelectMultImagesActivity INSTANCE;
	private GridView imageGrid;
	private ArrayList<ImageFileBean> imageFiles;
	private ImagesAdapter mAdapter;
	private TextView review;
	private PopupWindow fileMenu;
	private boolean showCamera = true;
	private TakePhotoUtils photoUtils;

	@Override
	protected int setLayoutResId() {
		return R.layout.is_activity_main;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		setGridView();
		loadData();
		INSTANCE = this;
	}

	private void initView() {
		imageGrid = (GridView) findViewById(R.id.is_image_grid);
		review = (TextView) findViewById(R.id.is_review);
		findViewById(R.id.is_file_menu).setOnClickListener(this);
		review.setOnClickListener(this);
	}

	private void setGridView() {
		currentImages = new ArrayList<ImageBean>();
		selectedImages = new ArrayList<ImageBean>();
		maxNum = getIntent().getIntExtra(ConstantEntity.MAX_NUM,
				ConstantEntity.getDefaultMaxSelectNum());
		isCrop = getIntent().getBooleanExtra(ConstantEntity.IS_CROP, true);
		mAdapter = new ImagesAdapter(this, currentImages, this, maxNum);
		mAdapter.setShowCamera(showCamera);
		mAdapter.setIsCrop(isCropImage());
		imageGrid.setAdapter(mAdapter);
		imageGrid.setOnItemClickListener(this);
		if(isCropImage()){
			chooseOk.setVisibility(View.GONE);
			review.setVisibility(View.GONE);
		}
	}

	private void loadData() {
		imageFiles = new ArrayList<ImageFileBean>();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Cursor externalCursor = getImageCursor();
					setData(externalCursor);
					UIHandler.sendEmptyMessage(ConstantEntity.SCAN_OK);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private void setData(Cursor externalCursor) throws Exception {
		if (externalCursor != null) {
			while (externalCursor.moveToNext()) {
				String path = externalCursor.getString(externalCursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				ImageBean bean = new ImageBean();
				bean.setImagePath(path);
				currentImages.add(bean);
				getParentFileInfo(path);
			}
		}
		externalCursor.close();
	}

	private void getParentFileInfo(String path) throws Exception {
		boolean isAdd = true;
		File parentFile = new File(path).getParentFile();
		for (int i = 0; i < imageFiles.size(); i++) {
			if (imageFiles.get(i).getImageFileName()
					.equals(parentFile.getName())) {
				imageFiles.get(i).setTotalNum(
						imageFiles.get(i).getTotalNum() + 1);
				imageFiles.get(i).getImageFiles().add(path);
				isAdd = false;
				break;
			}
		}
		if (isAdd) {
			ImageFileBean bean = new ImageFileBean();
			bean.setTotalNum(1);
			bean.setFirstImagePath(path);
			bean.setImageFileName(parentFile.getName());
			List<String> imagePaths = new ArrayList<String>();
			imagePaths.add(path);
			bean.setImageFiles(imagePaths);
			imageFiles.add(bean);
		}
	}

	private Cursor getImageCursor() throws Exception {
		Uri externalImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ContentResolver resolver = SelectMultImagesActivity.this
				.getContentResolver();
		Cursor externalCursor = resolver.query(externalImageUri, null,
				MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=?",
				new String[] { "image/jpeg", "image/png" },
				MediaStore.Images.Media.DATE_MODIFIED);
		return externalCursor;
	}

	Handler UIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConstantEntity.SCAN_OK:
				Collections.reverse(currentImages);
				addAllImageToFile();
				mAdapter.notifyDataSetChanged();
				break;
			}
		}
	};

	private void addAllImageToFile() {
		ImageFileBean imageFile = new ImageFileBean();
		imageFile.setChekced(true);
		imageFile.setFirstImagePath(currentImages.get(0).getImagePath());
		imageFile.setImageFileName("所有图片");
		List<String> temps = new ArrayList<String>();
		for (ImageBean bean : currentImages) {
			temps.add(bean.getImagePath());
		}
		imageFile.setImageFiles(temps);
		imageFile.setTotalNum(temps.size());
		imageFiles.add(0, imageFile);
	};

	@Override
	protected void onBackClicked() {
		setResult(RESULT_CANCELED);
		finish();
	}

	private ImageBean getItem(int position) {
		if (showCamera) {
			if (position == 0) {
				return null;
			}
			return currentImages.get(position - 1);
		} else {
			return currentImages.get(position);
		}
	}

	@Override
	public void onItemChecked(int position, boolean isChecked) {
		getItem(position).setChecked(isChecked);
		int size = getCurrentSelectedImages().size() + selectedImages.size();
		setChooseOkStatus(size);
		setReviewStatus(size);
	}

	protected void setReviewStatus(int selectedImageNum) {
		if (review == null) {
			review = (TextView) findViewById(R.id.is_review);
		}
		if (selectedImageNum == 0) {
			review.setText("预览");
			review.setEnabled(false);
			return;
		}
		review.setText(getString(R.string.is_review, selectedImageNum, maxNum));
		review.setEnabled(true);
	}

	@Override
	protected void onChooseOkBtnClicked() {
		setResultData();
	}

	@Override
	public void onClick(View v) {
		if(v == review){
			reviewImage(selectedImages, 0);
		}else if(v == findViewById(R.id.is_file_menu)){

			showFileMenu();
		}
		// switch (v.getId()) {
		// case R.id.review:
		// reviewImage(selectedImages, 0);
		// break;
		// case R.id.file_menu:
		// showFileMenu();
		// break;
		// }
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (showCamera) {
			if (position == 0) {
				if (photoUtils == null) {
					photoUtils = new TakePhotoUtils(this, this);
				}
				photoUtils.choosePhotoFromCamera();
				return;
			} else {
				position--;
			}
		}
		if (isCropImage()) {
			cropImage(currentImages.get(position).getImagePath());
		} else {
			reviewImage(currentImages, position);
		}
	}

	@Override
	public void onSelectImageSuccess(String path) {
		scanFile(path);
		addSelectedImages(getCurrentSelectedImages());
		ImageBean bean = new ImageBean(path, true);
		selectedImages.add(bean);
		currentImages.add(0, bean);
		syncData();
		if(isCropImage()){
			cropImage(path);
			return ;
		}
	}

	private void syncData() {
		try {
			syncCurrentImageStatus();
			setChooseOkStatus(selectedImages.size());
			setReviewStatus(selectedImages.size());
			mAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 预览图集
	 * 
	 * @param selectedImage
	 *            所需展示的图集
	 * @param position
	 *            当前图片是第几位
	 */
	private void reviewImage(ArrayList<ImageBean> currentImages, int position) {
		addSelectedImages(getCurrentSelectedImages());
		Intent intent = new Intent(this, ReviewImagesActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(ConstantEntity.SELECTED_IMAGES,
				selectedImages);
		bundle.putParcelableArrayList(ConstantEntity.CURRENT_IMAGES,
				currentImages);
		intent.putParcelableArrayListExtra(ConstantEntity.SELECTED_IMAGES,
				selectedImages);
		intent.putParcelableArrayListExtra(ConstantEntity.CURRENT_IMAGES,
				currentImages);
		intent.putExtra(ConstantEntity.CLICKED_POSITION, position);
		intent.putExtra(ConstantEntity.MAX_NUM, maxNum);
		startActivityForResult(intent, ConstantEntity.REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ConstantEntity.REQUEST_CODE:
				refreshViews(data);
				break;
			case ConstantEntity.IMAGE_REQUEST_TAKE_PHOTO:
				photoUtils
						.onActivityResult(requestCode, resultCode, data, this);
				break;
			}
		}
	}

	private void refreshViews(Intent data) {
		try {
			selectedImages.clear();
			ArrayList<ImageBean> temps = data
					.getParcelableArrayListExtra(ConstantEntity.RESPONSE_KEY);
			selectedImages.addAll(temps);
			setChooseOkStatus(selectedImages.size());
			setReviewStatus(selectedImages.size());
			syncCurrentImageStatus();
			mAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showFileMenu() {
		if (fileMenu == null) {
			fileMenu = new PopupWindow(this);
			View contentView = LayoutInflater.from(this).inflate(
					R.layout.is_layout_file_menu, null);
			ListView fileMenuList = (ListView) contentView
					.findViewById(R.id.is_fileMenuList);
			final FileMenuAdapter fileMenuAdapter = new FileMenuAdapter(this,
					imageFiles);
			fileMenuList.setAdapter(fileMenuAdapter);
			fileMenu = new PopupWindow(this);
			fileMenu.setBackgroundDrawable(new ColorDrawable(0x000000));
			fileMenu.setWidth(WindowUtils.getWindowWidth(this));
			fileMenu.setHeight(WindowUtils.getWindowHeight(this) - 3
					* WindowUtils.dip2px(this, 48)
					- WindowUtils.dip2px(this, 24));
			fileMenu.setOutsideTouchable(true);
			fileMenu.setFocusable(true);
			// fileMenu.setAnimationStyle(R.style.PopupAnimation);
			fileMenu.setContentView(contentView);
			fileMenuList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long arg3) {
					if (position == 0) {
						showCamera = true;
					} else {
						showCamera = false;
					}
					mAdapter.setShowCamera(showCamera);
					addSelectedImages(getCurrentSelectedImages());
					for (int i = 0; i < imageFiles.size(); i++) {
						imageFiles.get(i).setChekced(false);
					}
					imageFiles.get(position).setChekced(true);
					fileMenuAdapter.notifyDataSetChanged();
					List<ImageBean> temps = currentImages;
					currentImages.clear();
					try {
						currentImages.addAll(ImageBean.String2Object(imageFiles
								.get(position).getImageFiles(), false));
						syncCurrentImageStatus();
					} catch (Exception e) {
						currentImages.addAll(temps);
						e.printStackTrace();
					}
					mAdapter.notifyDataSetChanged();
					fileMenu.dismiss();
				}
			});
		}
		fileMenu.showAsDropDown(findViewById(R.id.is_bottom_bar), 0, 0);
	}
}
