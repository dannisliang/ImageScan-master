package net.arvin.imagescan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.arvin.adapters.ImagesAdapter;
import net.arvin.entitys.ImageBean;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

@SuppressLint("HandlerLeak")
public class MainActivity extends FragmentActivity implements
		OnItemClickListener {

	private GridView imageGrid;
	private List<ImageBean> images;
	private ImagesAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		setGridView();
		loadData();
	}

	private void initView() {
		imageGrid = (GridView) findViewById(R.id.image_grid);
	}

	private void setGridView() {
		images = new ArrayList<ImageBean>();
		mAdapter = new ImagesAdapter(this, images);
		imageGrid.setAdapter(mAdapter);
		imageGrid.setOnItemClickListener(this);
	}

	private void loadData() {
		try {

			new Thread(new Runnable() {

				@Override
				public void run() {
					Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					ContentResolver resolver = MainActivity.this
							.getContentResolver();
					Cursor cursor = resolver.query(imageUri, null,
							MediaStore.Images.Media.MIME_TYPE + "=? or "
									+ MediaStore.Images.Media.MIME_TYPE + "=?",
							new String[] { "image/jpg", "image/png" },
							MediaStore.Images.Media.DATE_MODIFIED);
					if (cursor == null) {
						return;
					}

					while (cursor.moveToNext()) {
						String path = cursor.getString(cursor
								.getColumnIndex(MediaStore.Images.Media.DATA));
						ImageBean img = new ImageBean();
						img.setImagePath(path);
						images.add(img);
						// String parentName = new File(path).getParentFile()
						// .getName();
					}
					UIHandler.sendEmptyMessage(SCAN_OK);
					cursor.close();
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO show the bigger image
	}

}
