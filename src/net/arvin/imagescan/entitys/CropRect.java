package net.arvin.imagescan.entitys;

public class CropRect {
	public Point leftUp;
	public Point leftBottom;
	public Point rightUp;
	public Point rightBottom;
	public Point topCenter;
	public Point bottomCenter;
	public Point leftCenter;
	public Point rightCenter;

	public void dealRect(float totalHeight, float totalWidth, float minWidth) {
		dealRange(totalHeight, totalWidth);
		dealLineWidth(minWidth);
	}

	private void dealLineWidth(float minWidth) {
		if (rightUp.x - leftUp.x < minWidth) {
			rightUp.x = leftUp.x + minWidth;
		}
		if (leftBottom.y - leftUp.y < minWidth) {
			leftBottom.y = leftUp.y + minWidth;
		}
		if (rightBottom.x - leftBottom.x < minWidth) {
			rightBottom.x = leftBottom.x + minWidth;
		}
		if (rightBottom.y - rightUp.y < minWidth) {
			rightBottom.y = rightUp.y + minWidth;
		}
	}

	private void dealRange(float totalHeight, float totalWidth) {
		if (leftUp.x <= 0) {
			leftUp.x = 0;
		}
		if (leftUp.y <= 0) {
			leftUp.y = 0;
		}
		if (leftBottom.x <= 0) {
			leftBottom.x = 0;
		}
		if (leftBottom.y >= totalHeight) {
			leftBottom.y = totalHeight;
		}
		if (rightUp.x >= totalWidth) {
			rightUp.x = totalWidth;
		}
		if (rightUp.y <= 0) {
			rightUp.y = 0;
		}
		if (rightBottom.x >= totalWidth) {
			rightBottom.x = totalWidth;
		}
		if (rightBottom.y >= totalHeight) {
			rightBottom.y = totalHeight;
		}
	}
}
