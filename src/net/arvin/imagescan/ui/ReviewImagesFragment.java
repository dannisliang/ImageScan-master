package net.arvin.imagescan.ui;

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

public class ReviewImagesFragment extends BaseActivity implements
		OnClickListener, OnPageChangeListener {
	private int currentPosition = 0;
	private ViewPager reviewPager;
	private CheckBox chooseBox;
	private int selectedNum;

	@Override
	protected int setLayoutResId() {
		return R.layout.is_activity_review_images;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		setListener();
		initData();
	}

	private void initView() {
		reviewPager = (ViewPager) findViewById(R.id.is_scalePager);
		chooseBox = (CheckBox) findViewById(R.id.is_choose_box);
	}

	private void setListener() {
		findViewById(R.id.is_choose_layout).setOnClickListener(this);
		chooseBox.setOnClickListener(this);
	}

	private void initData() {
		getData();
		{
			setTitleText(currentPosition);
			setChooseStatus(currentImages.get(currentPosition).isChecked());
			selectedNum = selectedImages.size();
			setChooseOkStatus(selectedNum);
		}
		reviewPager.setAdapter(new ReviewPagerAdapter(this, currentImages));
		reviewPager.setCurrentItem(currentPosition, false);
		reviewPager.setOnPageChangeListener(this);
	}

	private void getData() {
		maxNum = getIntent().getIntExtra(ConstantEntity.MAX_NUM,
				ConstantEntity.getDefaultMaxSelectNum());
		isCrop = getIntent().getBooleanExtra(ConstantEntity.IS_CROP, false);
		selectedImages = getIntent().getParcelableArrayListExtra(
				ConstantEntity.SELECTED_IMAGES);
		currentImages = getIntent().getParcelableArrayListExtra(
				ConstantEntity.CURRENT_IMAGES);
		if (currentImages == null) {
			currentImages = new ArrayList<ImageBean>();
		}
		currentPosition = getIntent().getIntExtra(
				ConstantEntity.CLICKED_POSITION, 0);

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
		if (v == findViewById(R.id.is_choose_layout)
				|| v == findViewById(R.id.is_choose_box)) {
			chooseImage();
		}
	}

	private void chooseImage() {
		if (selectedNum >= maxNum) {
			chooseBox.setChecked(false);
			Toast.makeText(this, R.string.is_error_limit, Toast.LENGTH_SHORT)
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
		setResultData();
	}

	@Override
	protected void onChooseOkBtnClicked() {
		setResultData();
		SelectMultImagesActivity.INSTANCE.finish();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		currentPosition = position;
		setChooseStatus(currentImages.get(position).isChecked());
		setTitleText(position);
	}
}
