package net.arvin.imagescan.ui;

import java.util.ArrayList;

import net.arvin.imagescan.R;
import net.arvin.imagescan.entitys.ImageBean;
import net.arvin.imagescan.views.CropView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class CropFragment extends BaseFragment {

	private ImageView selectedImg;
	private CropView cropView;
	private String cropImagePath;

	@Override
	protected int setLayoutResId() {
		return R.layout.is_activity_crop;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initNormalData() {
		super.initNormalData();
		initView();
		initData();
	}
	
	private void initView() {
		selectedImg = (ImageView) rootView.findViewById(R.id.is_selectedImg);
		cropView = (CropView) rootView.findViewById(R.id.is_cropView);
		chooseOk.setEnabled(true);
	}

	private void initData() {
//		String path = getIntent().getStringExtra(ConstantEntity.CROP_IMAGE);
//		if (path == null) {
//			path = "";
//		}
		String path="";
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
					dismissProgressDialog();
					UIHandler.sendEmptyMessage(0);
				}
			}
		}).start();
	}

	@Override
	protected void onBackClicked() {
		popBackStack();
	}

	Handler UIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(getActivity(), "±ß«∏£¨≤√ºÙ ß∞‹¡À~", Toast.LENGTH_SHORT)
					.show();
		}
	};
}
