package net.arvin.imagescan;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.arvin.adapters.FileMenuAdapter;
import net.arvin.adapters.ImagesAdapter;
import net.arvin.entitys.ImageBean;
import net.arvin.entitys.ImageFileBean;
import net.arvin.listeners.OnItemChecked;
import net.arvin.utils.WindowUtils;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

@SuppressLint("HandlerLeak")
public class SelectMultImagesActivity extends FragmentActivity implements
		OnItemClickListener, OnItemChecked, OnClickListener {

	private GridView imageGrid;
	private List<ImageBean> images;
	private List<ImageFileBean> imageFiles;
	private ArrayList<String> selectedImages;
	private ImagesAdapter mAdapter;
	private Button chooseOk;
	private int selectedMaxNum = 9;
	private TextView review;
	private PopupWindow fileMenu;
	public static String RESPONSE_KEY = "response_key";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initImageLoader(this);
		initView();
		setGridView();
		initNormalData();
		setListener();
		loadData();
	}

	public void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(100 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	private void initView() {
		imageGrid = (GridView) findViewById(R.id.image_grid);
		chooseOk = (Button) findViewById(R.id.choose_ok);
		review = (TextView) findViewById(R.id.review);
	}

	private void initNormalData() {
		selectedImages = new ArrayList<String>();
		imageFiles = new ArrayList<ImageFileBean>();
	}

	private void setListener() {
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.file_menu).setOnClickListener(this);
		chooseOk.setOnClickListener(this);
		review.setOnClickListener(this);
	}

	private void setGridView() {
		images = new ArrayList<ImageBean>();
		mAdapter = new ImagesAdapter(this, images, this, selectedMaxNum);
		imageGrid.setAdapter(mAdapter);
		imageGrid.setOnItemClickListener(this);
	}

	private void loadData() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Cursor externalCursor = getImageCursor();
					setData(externalCursor);
					UIHandler.sendEmptyMessage(SCAN_OK);
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
				ImageBean img = new ImageBean();
				img.setImagePath(path);
				images.add(img);
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
			ImageFileBean imageFile = new ImageFileBean();
			imageFile.setTotalNum(1);
			imageFile.setFirstImagePath(path);
			imageFile.setImageFileName(parentFile.getName());
			List<String> imagePaths = new ArrayList<String>();
			imagePaths.add(path);
			imageFile.setImageFiles(imagePaths);
			imageFiles.add(imageFile);
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

	public static final int SCAN_OK = 0;

	Handler UIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCAN_OK:
				Collections.reverse(images);
				addAllImageToFile();
				mAdapter.notifyDataSetChanged();
				break;
			}
		}

	};

	private void addAllImageToFile() {
		ImageFileBean imageFile = new ImageFileBean();
		imageFile.setChekced(true);
		imageFile.setFirstImagePath(images.get(0).getImagePath());
		imageFile.setImageFileName("所有图片");
		List<String> temps = new ArrayList<String>();
		for (ImageBean bean : images) {
			temps.add(bean.getImagePath());
		}
		imageFile.setImageFiles(temps);
		imageFile.setTotalNum(temps.size());
		imageFiles.add(0, imageFile);
	};

	@Override
	public void onClick(View v) {
		Intent data = null;
		switch (v.getId()) {
		case R.id.choose_ok:
			data = new Intent();
			data.putStringArrayListExtra(RESPONSE_KEY, selectedImages);
			setResult(RESULT_OK, data);
			finish();
			break;
		case R.id.back:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.review:
			reviewImage(selectedImages, 0);
			break;
		case R.id.file_menu:
			showFileMenu();
			break;
		}
	}

	/**
	 * @param position
	 */
	private void reviewImage(ArrayList<String> data, int position) {
		Intent intent = new Intent(this, ReviewImagesActivity.class);
		intent.putStringArrayListExtra(ReviewImagesActivity.SELECTED_IMAGES,
				data);
		intent.putExtra(ReviewImagesActivity.CLICKED_POSITION, position);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ArrayList<String> allImages = new ArrayList<String>();
		for (ImageBean bean : images) {
			allImages.add(bean.getImagePath());
		}
		reviewImage(allImages, position);
	}

	@Override
	public void onItemChecked(int position, boolean isChecked) {
		if (isChecked) {
			selectedImages.add(images.get(position).getImagePath());
		} else {
			selectedImages.remove(images.get(position).getImagePath());
		}
		setSelectedNum(selectedImages.size());
	}

	private void setSelectedNum(int num) {
		if (num > 0) {
			chooseOk.setEnabled(true);
			chooseOk.setText(getString(R.string.chooseOk, num, selectedMaxNum));
			review.setEnabled(true);
			review.setText(getString(R.string.review, num));
		} else {
			chooseOk.setEnabled(false);
			chooseOk.setText("完成");
			review.setEnabled(false);
			review.setText("预览");
		}
	}

	public int getSelectedMaxNum() {
		return selectedMaxNum;
	}

	public void setSelectedMaxNum(int selectedMaxNum) {
		this.selectedMaxNum = selectedMaxNum;
	}

	private void showFileMenu() {
		if (fileMenu == null) {
			fileMenu = new PopupWindow(this);
			View contentView = LayoutInflater.from(this).inflate(
					R.layout.layout_file_menu, null);
			ListView fileMenuList = (ListView) contentView
					.findViewById(R.id.fileMenuList);
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
					for (int i = 0; i < imageFiles.size(); i++) {
						imageFiles.get(i).setChekced(false);
					}
					imageFiles.get(position).setChekced(true);
					fileMenuAdapter.notifyDataSetChanged();
					List<ImageBean> temps = images;
					images.clear();
					try {
						List<String> childImageFiles = imageFiles.get(position)
								.getImageFiles();
						for (int i = 0; i < childImageFiles.size(); i++) {
							ImageBean temp = new ImageBean();
							temp.setChecked(false);
							temp.setImagePath(childImageFiles.get(i));
							images.add(temp);
						}
					} catch (Exception e) {
						images.addAll(temps);
						e.printStackTrace();
					}
					mAdapter.notifyDataSetChanged();
					fileMenu.dismiss();
				}
			});
		}
		fileMenu.showAsDropDown(findViewById(R.id.bottom_bar), 0, 0);
	}

}
