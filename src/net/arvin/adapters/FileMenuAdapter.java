package net.arvin.adapters;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.arvin.entitys.ImageFileBean;
import net.arvin.imagescan.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileMenuAdapter extends BaseAdapter {
	private Context context;
	private List<ImageFileBean> imageFiles;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public FileMenuAdapter(Context context, List<ImageFileBean> imageFiles) {
		this.context = context;
		this.imageFiles = imageFiles;

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		return imageFiles.size();
	}

	@Override
	public Object getItem(int position) {
		return imageFiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_file_menu, null);
			holder = new ViewHolder();
			holder.firstImage = (ImageView) convertView
					.findViewById(R.id.firstImage);
			holder.isChecked = (ImageView) convertView
					.findViewById(R.id.isChecked);
			holder.fileName = (TextView) convertView
					.findViewById(R.id.fileName);
			holder.imgCount = (TextView) convertView
					.findViewById(R.id.imageCount);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		setData(holder, position);
		return convertView;
	}

	private void setData(ViewHolder holder, int position) {
		ImageFileBean imageFile = imageFiles.get(position);
		imageLoader.displayImage("file://"+imageFile.getFirstImagePath(), holder.firstImage, options);
		holder.fileName.setText(imageFile.getImageFileName());
		holder.imgCount.setText(imageFile.getTotalNum()+"уе");
		if(imageFile.isChekced()){
			holder.isChecked.setSelected(true);
		}else{
			holder.isChecked.setSelected(false);
		}
	}

	private class ViewHolder {
		public ImageView firstImage,isChecked;
		public TextView fileName, imgCount;
	}

}
