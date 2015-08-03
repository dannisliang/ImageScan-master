package net.arvin.imagescan.uis;

import java.util.ArrayList;

import net.arvin.imagescan.R;
import net.arvin.imagescan.adapters.ReviewPagerAdapter;
import net.arvin.imagescan.entitys.ConstantEntity;
import net.arvin.imagescan.entitys.ImageBean;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

public class ReviewImagesFragment extends BaseFragment implements
		OnClickListener, OnPageChangeListener {
	private int currentPosition = 0;
	private ViewPager reviewPager;
	private CheckBox chooseBox;
	private int selectedNum;

	@Override
	protected int contentLayoutRes() {
		return R.layout.is_fragment_review_images;
	}

	@Override
	protected void init() {
		initView();
		setListener();
		initData(getArguments());
	}

	private void initView() {
		reviewPager = (ViewPager) root.findViewById(R.id.is_scalePager);
		chooseBox = (CheckBox) root.findViewById(R.id.is_choose_box);
	}

	private void setListener() {
		root.findViewById(R.id.is_choose_layout).setOnClickListener(this);
		chooseBox.setOnClickListener(this);
	}

	@Override
	protected void updata(Bundle bundle) {
		initData(bundle);
	}
	
	private void initData(Bundle bundle) {
		getData(bundle);
		{
			selectedNum = selectedImages.size();
			setTitleText(currentPosition);
			setChooseStatus(currentImages.get(currentPosition).isChecked());
			setChooseOkStatus(selectedNum);
		}
		reviewPager.setAdapter(new ReviewPagerAdapter(getActivity(),
				currentImages));
		reviewPager.setCurrentItem(currentPosition, false);
		reviewPager.setOnPageChangeListener(this);
	}

	private void getData(Bundle bundle) {
		if(bundle == null){
			bundle = new Bundle();
		}
		maxNum = bundle.getInt(ConstantEntity.MAX_NUM,
				ConstantEntity.getDefaultMaxSelectNum());
		isCrop = bundle.getBoolean(ConstantEntity.IS_CROP, false);
		selectedImages = bundle
				.getParcelableArrayList(ConstantEntity.SELECTED_IMAGES);
		currentImages = bundle
				.getParcelableArrayList(ConstantEntity.CURRENT_IMAGES);
		if (currentImages == null) {
			currentImages = new ArrayList<ImageBean>();
		}
		currentPosition = bundle.getInt(ConstantEntity.CLICKED_POSITION, 0);

		try {
			syncCurrentImageStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setTitleText(int currentNum) {
		title.setText((currentNum + 1) + "/" + currentImages.size());
	}

	protected void setChooseStatus(boolean isChecked) {
		chooseBox.setChecked(isChecked);
	}

	@Override
	public void onClick(View v) {
		if (v == root.findViewById(R.id.is_choose_layout)
				|| v == root.findViewById(R.id.is_choose_box)) {
			chooseImage();
		}
	}

	private void chooseImage() {
		if (selectedNum >= maxNum) {
			chooseBox.setChecked(false);
			Toast.makeText(getActivity(), R.string.is_error_limit, Toast.LENGTH_SHORT)
					.show();
		} else {
			boolean checked = currentImages.get(currentPosition).isChecked();
			if (checked) {
				selectedNum--;
				remove();
			} else {
				selectedNum++;
			}
			setChooseOkStatus(selectedNum);
			chooseBox.setChecked(!checked);
			currentImages.get(currentPosition).setChecked(!checked);
		}
	}

	public void remove() {
		for (ImageBean bean : selectedImages) {
			if (bean.getImagePath().equals(
					currentImages.get(currentPosition).getImagePath())) {
				selectedImages.remove(bean);
				break;
			}
		}
	}

	@Override
	protected void onBackClicked() {
		addSelectedImages(getCurrentSelectedImages());
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(ConstantEntity.RESPONSE_KEY, selectedImages);
		switchFragment(0, bundle);
	}
	
	@Override
	public void onPageSelected(int position) {
		currentPosition = position;
		setChooseStatus(currentImages.get(position).isChecked());
		setTitleText(position);
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}


}
