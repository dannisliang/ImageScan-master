package net.arvin.imagescan.entitys;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageBean implements Parcelable {
	private String imagePath;
	private boolean isChecked;

	public ImageBean() {
	}
	
	public ImageBean(String imagePath,boolean isChecked) {
		this.imagePath = imagePath;
		this.isChecked = isChecked;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public static ArrayList<ImageBean> String2Object(List<String> data,
			boolean allStatus) {
		if (data == null) {
			return null;
		}
		ArrayList<ImageBean> lists = new ArrayList<ImageBean>();
		for (String temp : data) {
			ImageBean bean = new ImageBean();
			bean.setChecked(allStatus);
			bean.setImagePath(temp);
			lists.add(bean);
		}
		return lists;
	}

	public static ArrayList<String> Object2String(List<ImageBean> data) {
		if (data == null) {
			return null;
		}
		ArrayList<String> lists = new ArrayList<String>();
		for (ImageBean temp : data) {
			lists.add(temp.getImagePath());
		}
		return lists;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(imagePath);
		dest.writeByte((byte) (isChecked ? 1 : 0));
	}

	public static final Parcelable.Creator<ImageBean> CREATOR = new Parcelable.Creator<ImageBean>() {
		public ImageBean createFromParcel(Parcel in) {
			return new ImageBean(in);
		}

		public ImageBean[] newArray(int size) {
			return new ImageBean[size];
		}
	};

	private ImageBean(Parcel in) {
		imagePath = in.readString();
		isChecked = in.readByte() != 0;
	}
}
