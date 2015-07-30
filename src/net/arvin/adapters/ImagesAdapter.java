package net.arvin.adapters;

import java.util.List;

import net.arvin.entitys.ConstantEntity;
import net.arvin.entitys.ImageBean;
import net.arvin.imagescan.R;
import net.arvin.listeners.OnClickWithObject;
import net.arvin.listeners.OnItemChecked;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImagesAdapter extends BaseAdapter {
	private Context mContext;
	private List<ImageBean> images;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private OnItemChecked itemChecked;
	private int maxNum, selectedNum;
	private boolean showCamera = true;
	private boolean isCrop = true;

	public boolean isShowCamera() {
		return showCamera;
	}

	public void setShowCamera(boolean showCamera) {
		this.showCamera = showCamera;
	}
	
	public void setIsCrop(boolean isCrop){
		this.isCrop = isCrop;
	}

	public ImagesAdapter(Context mContext, List<ImageBean> images,
			OnItemChecked itemChecked, int maxNum) {
		super();
		this.mContext = mContext;
		this.images = images;
		this.itemChecked = itemChecked;
		this.maxNum = maxNum;
		this.selectedNum = 0;
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		return showCamera ? images.size() + 1 : images.size();
	}

	@Override
	public ImageBean getItem(int position) {
		if (showCamera) {
			if (position == 0) {
				return null;
			}
			return images.get(position - 1);
		} else {
			return images.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (showCamera) {
			return position == 0 ? ConstantEntity.TYPE_CAMERA
					: ConstantEntity.TYPE_NORMAL;
		}
		return ConstantEntity.TYPE_NORMAL;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		if (type == ConstantEntity.TYPE_CAMERA) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.is_item_take_photo, null);
			convertView.setTag(null);
			return convertView;
		}
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.is_item_edit_image, null);
			holder = new ViewHolder(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
			if (holder == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.is_item_edit_image, null);
				holder = new ViewHolder(convertView);

			}
		}
		if(isCrop){
			holder.item_box.setVisibility(View.GONE);
		}else{
			holder.item_box.setVisibility(View.VISIBLE);
		}
		setData(holder, getItem(position));
		holder.item_box.setOnClickListener(new OnClickWithObject(holder) {

			@Override
			public void onClick(View v, Object[] objs) {
				if (getItem(position).isChecked()) {
					selectedNum--;
					getItem(position).setChecked(false);
					itemChecked.onItemChecked(position, false);
				} else {
					if (selectedNum >= maxNum) {
						ViewHolder holder = (ViewHolder) objs[0];
						holder.item_box.setChecked(false);
						Toast.makeText(mContext, R.string.is_error_limit,
								Toast.LENGTH_SHORT).show();
						return;
					}
					selectedNum++;
					getItem(position).setChecked(true);
					itemChecked.onItemChecked(position, true);
				}
			}
		});
		return convertView;
	}

	private void setData(ViewHolder holder, ImageBean item) {
		imageLoader.displayImage("file://" + item.getImagePath(),
				holder.item_img, options);
		holder.item_box.setChecked(item.isChecked());
	}

	private class ViewHolder {
		private ImageView item_img;
		private CheckBox item_box;

		public ViewHolder(View convertView) {
			item_img = (ImageView) convertView.findViewById(R.id.is_item_img);
			item_box = (CheckBox) convertView.findViewById(R.id.is_item_box);
			convertView.setTag(this);
		}
	}

}
