package net.arvin.imagescan.uis;

import java.util.ArrayList;

import net.arvin.imagescan.R;
import net.arvin.imagescan.entitys.ConstantEntity;
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
	protected int contentLayoutRes() {
		return R.layout.is_fragment_crop;
	}

	@Override
	protected void init() {
		initView();
		initData(getArguments());
	}

	private void initView() {
		selectedImg = (ImageView) root.findViewById(R.id.is_selectedImg);
		cropView = (CropView) root.findViewById(R.id.is_cropView);
		chooseOk.setEnabled(true);
	}

	@Override
	protected void updata(Bundle bundle) {
		initData(bundle);
	}

	private void initData(Bundle bundle) {
		String path = bundle.getString(ConstantEntity.CROP_IMAGE);
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
				} catch (Exception e) {
					e.printStackTrace();
					dismissProgressDialog();
					UIHandler.sendEmptyMessage(0);
				}
			}
		}).start();
	}

	protected void onBackClicked() {
		switchFragment(0, null);
	}

	Handler UIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(getActivity(), "±ß«∏£¨≤√ºÙ ß∞‹¡À~", Toast.LENGTH_SHORT)
					.show();
		}
	};
}
