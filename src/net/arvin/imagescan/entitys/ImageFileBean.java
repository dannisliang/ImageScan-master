package net.arvin.imagescan.entitys;

import java.util.List;

public class ImageFileBean {
	private String firstImagePath;
	private int totalNum;
	private String imageFileName;
	private List<String> imageFiles;
	private boolean isChekced;

	public String getFirstImagePath() {
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public List<String> getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(List<String> imageFiles) {
		this.imageFiles = imageFiles;
	}

	public boolean isChekced() {
		return isChekced;
	}

	public void setChekced(boolean isChekced) {
		this.isChekced = isChekced;
	}

	@Override
	public String toString() {
		return "ImageFileBean [firstImagePath=" + firstImagePath
				+ ", totalNum=" + totalNum + ", imageFileName=" + imageFileName
				+ ", imageFiles=" + imageFiles + ", isChekced=" + isChekced
				+ "]";
	}
}
