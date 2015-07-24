package net.arvin.imagescan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.arvin.adapters.ImagesAdapter;
import net.arvin.entitys.ImageBean;
import net.arvin.listeners.OnItemChecked;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class SelectMultImagesActivity extends FragmentActivity implements
		OnItemClickListener, OnItemChecked, OnClickListener {

	private GridView imageGrid;
	private List<ImageBean> images;
	private ArrayList<String> selectedImages;
	private ImagesAdapter mAdapter;
	private Button chooseOk;
	private int selectedMaxNum = 9;
	private TextView review;
	public static String RESPONSE_KEY = "response_key";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		setGridView();
		initNormalData();
		setListener();
		loadData();
	}

	private void initView() {
		imageGrid = (GridView) findViewById(R.id.image_grid);
		chooseOk = (Button) findViewById(R.id.choose_ok);
		review = (TextView) findViewById(R.id.review);
	}

	private void initNormalData() {
		selectedImages = new ArrayList<String>();
	}

	private void setListener() {
		findViewById(R.id.back).setOnClickListener(this);
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
		try {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Uri externalImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					ContentResolver resolver = SelectMultImagesActivity.this
							.getContentResolver();
					Cursor externalCursor = resolver.query(externalImageUri,
							null, MediaStore.Images.Media.MIME_TYPE + "=? or "
									+ MediaStore.Images.Media.MIME_TYPE + "=?",
							new String[] { "image/jpeg", "image/png" },
							MediaStore.Images.Media.DATE_MODIFIED);
					if (externalCursor != null) {
						while (externalCursor.moveToNext()) {
							String path = externalCursor.getString(externalCursor
									.getColumnIndex(MediaStore.Images.Media.DATA));
							ImageBean img = new ImageBean();
							img.setImagePath(path);
							images.add(img);
							// String parentName = new
							// File(path).getParentFile()
							// .getName();
						}
					}
					UIHandler.sendEmptyMessage(SCAN_OK);
					externalCursor.close();
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final int SCAN_OK = 0;

	Handler UIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCAN_OK:
				Collections.reverse(images);
				mAdapter.notifyDataSetChanged();
				break;
			}
		};
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
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO show the bigger image
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
			chooseOk.setTextColor(Color.WHITE);
			chooseOk.setText(getString(R.string.chooseOk, num, selectedMaxNum));
			review.setText(getString(R.string.review, num));
		} else {
			chooseOk.setEnabled(false);
			chooseOk.setTextColor(getResources().getColor(R.color.text_color));
			chooseOk.setText("ÕÍ≥…");
			review.setText("‘§¿¿");
		}
	}

	public int getSelectedMaxNum() {
		return selectedMaxNum;
	}

	public void setSelectedMaxNum(int selectedMaxNum) {
		this.selectedMaxNum = selectedMaxNum;
	}
}
