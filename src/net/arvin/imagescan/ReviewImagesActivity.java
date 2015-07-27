package net.arvin.imagescan;

import java.util.ArrayList;

import net.arvin.adapters.ReviewPagerAdapter;
import net.arvin.entitys.ConstantEntity;
import net.arvin.entitys.ImageBean;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

public class ReviewImagesActivity extends BaseActivity implements
		OnClickListener, OnPageChangeListener {
	private int currentPosition = 0;
	private ViewPager reviewPager;
	private CheckBox chooseBox;
	private int selectedNum;

	@Override
	protected int setLayoutResId() {
		return R.layout.activity_review_images;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		setListener();
		initData();
	}

	private void initView() {
		reviewPager = (ViewPager) findViewById(R.id.scalePager);
		chooseBox = (CheckBox) findViewById(R.id.choose_box);
	}

	private void setListener() {
		findViewById(R.id.choose_layout).setOnClickListener(this);
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
		switch (v.getId()) {
		case R.id.choose_layout:
		case R.id.choose_box:
			chooseImage();
			break;
		case R.id.choose_ok:
			break;
		}
	}

	private void chooseImage() {
		if (selectedNum >= maxNum) {
			chooseBox.setChecked(false);
			Toast.makeText(this, R.string.error_limit, Toast.LENGTH_SHORT)
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
