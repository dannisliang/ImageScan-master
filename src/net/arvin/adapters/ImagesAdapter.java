package net.arvin.adapters;

import java.util.List;

import net.arvin.entitys.ImageBean;
import net.arvin.imagescan.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ImagesAdapter extends BaseAdapter {
	private Context mContext;
	private List<ImageBean> images;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public ImagesAdapter(Context mContext, List<ImageBean> images) {
		super();
		this.mContext = mContext;
		this.images = images;
		initImageLoader(mContext);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public void initImageLoader(Context context) {

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(100 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object getItem(int position) {
		return images.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_edit_image, null);
			holder = new ViewHolder();
			holder.item_img = (ImageView) convertView
					.findViewById(R.id.item_img);
			holder.item_box = (CheckBox) convertView
					.findViewById(R.id.item_box);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		setData(holder, position);
		return convertView;
	}

	private void setData(ViewHolder holder, int position) {
		imageLoader
				.displayImage("file://" + images.get(position).getImagePath(),
						holder.item_img, options);
		holder.item_box.setChecked(images.get(position).isChecked());
	}

	private class ViewHolder {
		private ImageView item_img;
		private CheckBox item_box;
	}

}
