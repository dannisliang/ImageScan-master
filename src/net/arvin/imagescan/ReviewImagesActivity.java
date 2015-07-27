package net.arvin.imagescan;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ReviewImagesActivity extends FragmentActivity {
	public static final String SELECTED_IMAGES = "selected_images";
	public static final String CLICKED_POSITION = "clicked_position";
	private TextView title;
	private Button chooseOk;
	private ArrayList<String> reviewImages;
	private int currentPosition = 0;
	private ViewPager reviewPager;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_review_images);
		initView();
		initData();
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		chooseOk = (Button) findViewById(R.id.choose_ok);
		reviewPager = (ViewPager) findViewById(R.id.scalePager);
	}

	private void initData() {
		reviewImages = new ArrayList<String>();
		reviewImages = getIntent().getStringArrayListExtra(SELECTED_IMAGES);
		currentPosition = Integer.parseInt(getIntent().getStringExtra(
				CLICKED_POSITION));
		title.setText((currentPosition + 1) + "/" + reviewImages.size());
	}
}
