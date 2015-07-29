package net.arvin.imagescan;

import java.util.ArrayList;

import net.arvin.entitys.ConstantEntity;
import net.arvin.entitys.ImageBean;
import net.arvin.views.CropView;
import android.os.Bundle;
import android.widget.ImageView;

public class CropActivity extends BaseActivity {

	private ImageView selectedImg;
	private CropView cropView;
	private String cropImagePath;

	@Override
	protected int setLayoutResId() {
		return R.layout.activity_crop;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initView() {
		selectedImg = (ImageView) findViewById(R.id.selectedImg);
		cropView = (CropView) findViewById(R.id.cropView);
		chooseOk.setEnabled(true);
	}

	private void initData() {
		String path = getIntent().getStringExtra(ConstantEntity.CROP_IMAGE);
		if (path == null) {
			path = "";
		}
		imageLoader.displayImage("file://" + path, selectedImg, options);
	}

	@Override
	protected void onChooseOkBtnClicked() {
		showProgressDialog("≤√ºÙ÷–...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					cropImagePath = cropView.getCropImagePath(selectedImg);
					scanFile(cropImagePath);
					dismissProgressDialog();
					selectedImages = new ArrayList<ImageBean>();
					selectedImages.add(new ImageBean(cropImagePath, true));
					setResultData();
					SelectMultImagesActivity.INSTANCE.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	protected void onBackClicked() {
		onBackPressed();
	}
}
