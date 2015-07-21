package net.arvin.entitys;

public class ImageFileBean {
	private String firstImagePath;
	private int totalNum;
	private String imageFileName;
	private String imageFilePath;

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

	public String getImageFilePath() {
		return imageFilePath;
	}

	public void setImageFilePath(String imageFilePath) {
		this.imageFilePath = imageFilePath;
	}

	@Override
	public String toString() {
		return "ImageFileBean [firstImagePath=" + firstImagePath
				+ ", totalNum=" + totalNum + ", imageFileName=" + imageFileName
				+ ", imageFilePath=" + imageFilePath + "]";
	}
}
