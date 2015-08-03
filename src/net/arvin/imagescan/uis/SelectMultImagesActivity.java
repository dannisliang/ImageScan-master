package net.arvin.imagescan.uis;

import net.arvin.imagescan.R;
import net.arvin.imagescan.entitys.ConstantEntity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

@SuppressLint("Recycle")
public class SelectMultImagesActivity extends FragmentActivity {
	public static SelectMultImagesActivity INSTANCE;
	private SelectMultImagesFragment selectMultImagesFragment;
	private ReviewImagesFragment reviewImagesFragment;
	private CropFragment cropFragment;
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.is_activity_main);
		initImageLoader(this);
		INSTANCE = this;
		fragmentManager = getSupportFragmentManager();
		Bundle bundle = getIntent().getExtras();
		setPageSelection(0, bundle);
	}

	private void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(100 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * @param index 0表示首页，1表示预览页，2表示裁剪页
	 * @param bundle
	 */
	public void setPageSelection(int index, Bundle bundle) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case 0:
			if (selectMultImagesFragment == null) {
				selectMultImagesFragment = new SelectMultImagesFragment();
				selectMultImagesFragment.setArguments(bundle);
				transaction.add(R.id.is_content, selectMultImagesFragment);
			} else {
				selectMultImagesFragment.updata(bundle);
				transaction.show(selectMultImagesFragment);
			}
			break;
		case 1:
			if (reviewImagesFragment == null) {
				reviewImagesFragment = new ReviewImagesFragment();
				reviewImagesFragment.setArguments(bundle);
				transaction.add(R.id.is_content, reviewImagesFragment);
			} else {
				reviewImagesFragment.updata(bundle);
				transaction.show(reviewImagesFragment);
			}
			break;
		case 2:
			if (cropFragment == null) {
				cropFragment = new CropFragment();
				cropFragment.setArguments(bundle);
				transaction.add(R.id.is_content, cropFragment);
			} else {
				cropFragment.updata(bundle);
				transaction.show(cropFragment);
			}
			break;
		}
		transaction.commit();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (selectMultImagesFragment != null) {
			transaction.hide(selectMultImagesFragment);
		}
		if (reviewImagesFragment != null) {
			transaction.hide(reviewImagesFragment);
		}
		if (cropFragment != null) {
			transaction.hide(cropFragment);
		}
	}

	protected void popBackStack() {
		if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {

		} else {
			getSupportFragmentManager().popBackStack();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == ConstantEntity.RESULT_OK) {
			switch (requestCode) {
			case ConstantEntity.IMAGE_REQUEST_TAKE_PHOTO:
				selectMultImagesFragment.onActivityResult(requestCode, resultCode, data);
				break;
			}
		}
	}

}
