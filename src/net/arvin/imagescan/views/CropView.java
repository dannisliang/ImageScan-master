package net.arvin.imagescan.views;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import net.arvin.imagescan.entitys.ConstantEntity;
import net.arvin.imagescan.entitys.CropRect;
import net.arvin.imagescan.entitys.Point;
import net.arvin.imagescan.utils.WindowUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class CropView extends View {
	private Context mContext;
	private CropRect mRect;
	private int touchWidth;
	private int touchHelpWidth;
	private int totalWidth;
	private int totalHeight;
	private int rectDefaultWidth;
	private int rectDefaultHeight;
	private Paint shadowPaint;
	private Paint anglePaint;
	private Point currentPoint;
	private Point oldPoint;
	private Point unChangingPoint;
	private Point changingPoint;
	private boolean canMove = false;

	public CropView(Context context) {
		this(context, null, 0);
		initData();
	}

	public CropView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CropRect getMRect() {
		return this.mRect;
	}

	public void setMRect(CropRect mRect) {
		this.mRect = mRect;
		invalidate();
	}

	public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
		initData();
		setBackgroundColor(Color.parseColor("#00000000"));
	}

	private void initData() {
		touchWidth = WindowUtils.dip2px(mContext, 30);
		touchHelpWidth = WindowUtils.dip2px(mContext, 2);
		totalWidth = WindowUtils.getWindowWidth(mContext);
		totalHeight = WindowUtils.getWindowHeight(mContext)
				- WindowUtils.dip2px(mContext, 48 + 24);
		rectDefaultWidth = WindowUtils.dip2px(mContext, 150);
		rectDefaultHeight = WindowUtils.dip2px(mContext, 150);
		initPoint();
		initPaint();
		initCropRect();
	}

	private void initPoint() {
		currentPoint = new Point();
		oldPoint = new Point();
		unChangingPoint = new Point();
		changingPoint = new Point();
	}

	private void initPaint() {
		shadowPaint = new Paint();
		shadowPaint.setColor(Color.parseColor("#80000000"));

		anglePaint = new Paint();
		anglePaint.setColor(Color.parseColor("#009501"));
		anglePaint.setStrokeWidth(touchHelpWidth);
	}

	private void initCropRect() {
		mRect = new CropRect();
		initLeftUp();
		initLeftBottom();
		initRightUp();
		initRightBottom();

		initTopCenter();
		initBottomCenter();
		initLeftCenter();
		initRightCenter();
	}

	private void initLeftUp() {
		mRect.leftUp = new Point();
		mRect.leftUp.x = (totalWidth - rectDefaultWidth) / 2;
		mRect.leftUp.y = (totalHeight - rectDefaultHeight) / 2;
	}

	private void initLeftBottom() {
		mRect.leftBottom = new Point();
		mRect.leftBottom.x = (totalWidth - rectDefaultWidth) / 2;
		mRect.leftBottom.y = (totalHeight + rectDefaultHeight) / 2;
	}

	private void initRightUp() {
		mRect.rightUp = new Point();
		mRect.rightUp.x = (totalWidth + rectDefaultWidth) / 2;
		mRect.rightUp.y = (totalHeight - rectDefaultHeight) / 2;
	}

	private void initRightBottom() {
		mRect.rightBottom = new Point();
		mRect.rightBottom.x = (totalWidth + rectDefaultWidth) / 2;
		mRect.rightBottom.y = (totalHeight + rectDefaultHeight) / 2;
	}

	private void initTopCenter() {
		mRect.topCenter = new Point();
		mRect.topCenter.x = (mRect.leftUp.x + mRect.rightUp.x) / 2;
		mRect.topCenter.y = mRect.leftUp.y;
	}

	private void initBottomCenter() {
		mRect.bottomCenter = new Point();
		mRect.bottomCenter.x = (mRect.leftBottom.x + mRect.rightBottom.x) / 2;
		mRect.bottomCenter.y = mRect.leftBottom.y;
	}

	private void initLeftCenter() {
		mRect.leftCenter = new Point();
		mRect.leftCenter.x = mRect.leftUp.x;
		mRect.leftCenter.y = (mRect.leftUp.y + mRect.leftBottom.y) / 2;
	}

	private void initRightCenter() {
		mRect.rightCenter = new Point();
		mRect.rightCenter.x = mRect.rightUp.x;
		mRect.rightCenter.y = (mRect.rightUp.y + mRect.rightBottom.y) / 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mRect.dealRect(totalHeight, totalWidth, 2 * touchWidth);
		drawShadow(canvas);
		drawTouchAngle(canvas);
	}

	/**
	 * 绘制各个角上的形状
	 * 
	 * @param canvas
	 */
	private void drawTouchAngle(Canvas canvas) {
		drawLeftTopAngle(canvas, mRect.leftUp);
		drawLeftCenterAngle(canvas, mRect.leftUp, mRect.leftBottom);
		drawLeftBottomAngle(canvas, mRect.leftBottom);
		drawRightTopAngle(canvas, mRect.rightUp);
		drawRightCenterAngle(canvas, mRect.rightUp, mRect.rightBottom);
		drawRightBottomAngle(canvas, mRect.rightBottom);
		drawTopCenterAngle(canvas, mRect.leftUp, mRect.rightUp);
		drawBottomCenterAngle(canvas, mRect.leftBottom, mRect.rightBottom);
	}

	/**
	 * 绘制左上角
	 * 
	 * @param canvas
	 * @param leftUp
	 */
	private void drawLeftTopAngle(Canvas canvas, Point leftUp) {
		float h_startX = leftUp.x - touchHelpWidth / 2;
		float v_startX = leftUp.x;
		float startY = leftUp.y;
		drawHorizontalLine(canvas, h_startX, startY);
		drawVerticalLine(canvas, v_startX, startY);
	}

	/**
	 * 绘制左中角
	 * 
	 * @param canvas
	 * @param leftUp
	 * @param leftBottom
	 */
	private void drawLeftCenterAngle(Canvas canvas, Point leftUp,
			Point leftBottom) {
		float startX = leftUp.x;
		float startY = (leftBottom.y + leftUp.y - touchWidth / 2) / 2;
		drawVerticalLine(canvas, startX, startY);
	}

	/**
	 * 绘制左下角
	 * 
	 * @param canvas
	 * @param leftBottom
	 */
	private void drawLeftBottomAngle(Canvas canvas, Point leftBottom) {
		float h_startX = leftBottom.x - touchHelpWidth / 2;
		float h_startY = leftBottom.y;
		float v_startX = leftBottom.x;
		float v_startY = leftBottom.y - touchWidth / 2;
		drawHorizontalLine(canvas, h_startX, h_startY);
		drawVerticalLine(canvas, v_startX, v_startY);
	}

	/**
	 * 绘制右上角
	 * 
	 * @param canvas
	 * @param rightUp
	 */
	private void drawRightTopAngle(Canvas canvas, Point rightUp) {
		float h_startX = rightUp.x - touchWidth / 2;
		float h_startY = rightUp.y;
		float v_startX = rightUp.x;
		float v_startY = rightUp.y - touchHelpWidth / 2;
		drawHorizontalLine(canvas, h_startX, h_startY);
		drawVerticalLine(canvas, v_startX, v_startY);
	}

	/**
	 * 绘制右中角
	 * 
	 * @param canvas
	 * @param rightUp
	 * @param rightBottom
	 */
	private void drawRightCenterAngle(Canvas canvas, Point rightUp,
			Point rightBottom) {
		float startX = rightUp.x;
		float startY = (rightBottom.y + rightUp.y - touchWidth / 2) / 2;
		drawVerticalLine(canvas, startX, startY);
	}

	/**
	 * 绘制右下角
	 * 
	 * @param canvas
	 * @param rightBottom
	 */
	private void drawRightBottomAngle(Canvas canvas, Point rightBottom) {
		float h_startX = rightBottom.x - touchWidth / 2;
		float h_startY = rightBottom.y;
		float v_startX = rightBottom.x;
		float v_startY = rightBottom.y - touchWidth / 2 + touchHelpWidth / 2;
		drawHorizontalLine(canvas, h_startX, h_startY);
		drawVerticalLine(canvas, v_startX, v_startY);
	}

	/**
	 * 绘制中上角
	 * 
	 * @param canvas
	 * @param leftUp
	 * @param rightUp
	 */
	private void drawTopCenterAngle(Canvas canvas, Point leftUp, Point rightUp) {
		float startX = (rightUp.x + leftUp.x - touchWidth / 2) / 2;
		float startY = leftUp.y;
		drawHorizontalLine(canvas, startX, startY);
	}

	/**
	 * 绘制中下角
	 * 
	 * @param canvas
	 * @param leftBottom
	 * @param rightBottom
	 */
	private void drawBottomCenterAngle(Canvas canvas, Point leftBottom,
			Point rightBottom) {
		float startX = (rightBottom.x + leftBottom.x - touchWidth / 2) / 2;
		float startY = rightBottom.y;
		drawHorizontalLine(canvas, startX, startY);
	}

	/**
	 * 划横线
	 * 
	 * @param canvas
	 * @param startX
	 * @param startY
	 */
	private void drawHorizontalLine(Canvas canvas, float startX, float startY) {
		canvas.drawLine(startX, startY, startX + touchWidth / 2, startY,
				anglePaint);
	}

	/**
	 * 画竖线
	 * 
	 * @param canvas
	 * @param startX
	 * @param startY
	 */
	private void drawVerticalLine(Canvas canvas, float startX, float startY) {
		canvas.drawLine(startX, startY, startX, startY + touchWidth / 2,
				anglePaint);
	}

	/**
	 * 绘制阴影
	 * 
	 * @param canvas
	 */
	private void drawShadow(Canvas canvas) {
		drawLeftShadow(canvas, mRect.leftUp, mRect.leftBottom);
		drawTopShadow(canvas, mRect.leftUp, mRect.rightUp);
		drawRightShadow(canvas, mRect.rightUp, mRect.rightBottom);
		drawBottomShadow(canvas, mRect.leftBottom, mRect.rightBottom);
	}

	/**
	 * 绘制左边阴影
	 * 
	 * @param canvas
	 * @param leftUp
	 * @param leftBottom
	 */
	private void drawLeftShadow(Canvas canvas, Point leftUp, Point leftBottom) {
		canvas.drawRect(0, 0, leftUp.x, totalHeight, shadowPaint);// 左边
		// canvas.drawRect(0, 0, leftUp.x, leftUp.y, shadowPaint);// 左上
		// canvas.drawRect(0, leftUp.y, leftBottom.x, leftBottom.y,
		// shadowPaint);// 左中
		// canvas.drawRect(0, leftBottom.y, leftBottom.x, totalHeight,
		// shadowPaint);// 左下
	}

	/**
	 * 绘制右边阴影
	 * 
	 * @param canvas
	 * @param rightUp
	 * @param rightBottom
	 */
	private void drawRightShadow(Canvas canvas, Point rightUp, Point rightBottom) {
		canvas.drawRect(rightUp.x, 0, totalWidth, totalHeight, shadowPaint);// 右边
		// canvas.drawRect(rightUp.x, 0, totalWidth, rightUp.y, shadowPaint);//
		// 右上
		// canvas.drawRect(rightUp.x, rightUp.y, totalWidth,
		// rightBottom.y,shadowPaint);// 右中
		// canvas.drawRect(rightBottom.x, rightBottom.y, totalWidth,
		// totalHeight,shadowPaint);// 右下
	}

	/**
	 * 绘制上中阴影
	 * 
	 * @param canvas
	 * @param leftUp
	 * @param rightUp
	 */
	private void drawTopShadow(Canvas canvas, Point leftUp, Point rightUp) {
		canvas.drawRect(leftUp.x, 0, rightUp.x, leftUp.y, shadowPaint);// 上中
	}

	/**
	 * 绘制下中阴影
	 * 
	 * @param canvas
	 * @param leftBottom
	 * @param rightBottom
	 */
	private void drawBottomShadow(Canvas canvas, Point leftBottom,
			Point rightBottom) {
		canvas.drawRect(leftBottom.x, rightBottom.y, rightBottom.x,
				totalHeight, shadowPaint);// 下中
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			oldPoint.setPoint(event.getX(), event.getY());
			currentPoint.setPoint(event.getX(), event.getY());
			if ((currentPoint.x >= 0 && currentPoint.x < mRect.leftUp.x)
					|| (currentPoint.x > mRect.rightUp.x && currentPoint.x <= totalWidth)
					|| (currentPoint.y >= 0 && currentPoint.y < mRect.leftUp.y)
					|| (currentPoint.y > mRect.leftBottom.y && currentPoint.y < totalHeight)) {
				canMove = false;
				return true;
			}
			if ((currentPoint.x > mRect.leftUp.x + touchWidth && currentPoint.x < mRect.rightUp.x
					- touchWidth)
					&& (currentPoint.y > mRect.leftUp.y + touchWidth && currentPoint.y < mRect.leftBottom.y
							- touchWidth)) {
				canMove = true;
			} else {
				canMove = false;
				ensurePoint(currentPoint.x, currentPoint.y);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			oldPoint.setPoint(currentPoint.x, currentPoint.y);
			currentPoint.setPoint(event.getX(), event.getY());
			float changeX = currentPoint.x - oldPoint.x;
			float changeY = currentPoint.y - oldPoint.y;
			if (canMove) {
				if ((changeX < 0 && mRect.leftUp.x <= 0)
						|| (changeX > 0 && mRect.rightBottom.x >= totalWidth)
						|| (changeY < 0 && mRect.leftUp.y <= 0)
						|| (changeY > 0 && mRect.rightBottom.y >= totalHeight)) {
					break;
				}
				reCalculateRect(changeX, changeY);
			} else {
				if (unChangingPoint.isTheSamePoint(mRect.leftUp)) {
					reCalculatePoint(mRect.leftBottom, 0, changeY);
					reCalculatePoint(mRect.rightUp, changeX, 0);
					reCalculatePoint(mRect.rightBottom, changeX, changeY);
				}
				if (unChangingPoint.isTheSamePoint(mRect.leftBottom)) {
					reCalculatePoint(mRect.leftUp, 0, changeY);
					reCalculatePoint(mRect.rightUp, changeX, changeY);
					reCalculatePoint(mRect.rightBottom, changeX, 0);
				}
				if (unChangingPoint.isTheSamePoint(mRect.rightUp)) {
					reCalculatePoint(mRect.leftUp, changeX, 0);
					reCalculatePoint(mRect.leftBottom, changeX, changeY);
					reCalculatePoint(mRect.rightBottom, 0, changeY);
				}
				if (unChangingPoint.isTheSamePoint(mRect.rightBottom)) {
					reCalculatePoint(mRect.leftUp, changeX, changeY);
					reCalculatePoint(mRect.leftBottom, changeX, 0);
					reCalculatePoint(mRect.rightUp, 0, changeY);
				}
				if (unChangingPoint.isTheSamePoint(mRect.topCenter)) {
					reCalculatePoint(mRect.leftBottom, 0, changeY);
					reCalculatePoint(mRect.rightBottom, 0, changeY);
				}
				if (unChangingPoint.isTheSamePoint(mRect.bottomCenter)) {
					reCalculatePoint(mRect.leftUp, 0, changeY);
					reCalculatePoint(mRect.rightUp, 0, changeY);
				}
				if (unChangingPoint.isTheSamePoint(mRect.leftCenter)) {
					reCalculatePoint(mRect.rightUp, changeX, 0);
					reCalculatePoint(mRect.rightBottom, changeX, 0);
				}
				if (unChangingPoint.isTheSamePoint(mRect.rightCenter)) {
					reCalculatePoint(mRect.leftUp, changeX, 0);
					reCalculatePoint(mRect.leftBottom, changeX, 0);
				}
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			unChangingPoint = new Point();
			changingPoint = new Point();
			break;
		}
		return true;
	}

	private void reCalculateRect(float changeX, float changeY) {
		reCalculatePoint(mRect.leftUp, changeX, changeY);
		reCalculatePoint(mRect.leftBottom, changeX, changeY);
		reCalculatePoint(mRect.rightUp, changeX, changeY);
		reCalculatePoint(mRect.rightBottom, changeX, changeY);
	}

	private void reCalculatePoint(Point point, float changeX, float changeY) {
		point.setPoint(point.x + changeX, point.y + changeY);
	}

	private void ensurePoint(float x, float y) {
		/**
		 * 按下的若是左上角
		 */
		if (isInRect(x, y, mRect.leftUp.x, mRect.leftUp.y, mRect.leftUp.x
				+ touchWidth, mRect.leftUp.y + touchWidth)) {
			unChangingPoint.setPoint(mRect.rightBottom);
			changingPoint.setPoint(mRect.leftUp);
			return;
		}
		/**
		 * 按下的若是左下角
		 */
		if (isInRect(x, y, mRect.leftBottom.x, mRect.leftBottom.y - touchWidth,
				mRect.leftBottom.x + touchWidth, mRect.leftBottom.y)) {
			unChangingPoint.setPoint(mRect.rightUp);
			changingPoint.setPoint(mRect.leftBottom);
			return;
		}
		/**
		 * 按下的若是右上角
		 */
		if (isInRect(x, y, mRect.rightUp.x - touchWidth, mRect.rightUp.y,
				mRect.rightUp.x, mRect.rightUp.y + touchWidth)) {
			unChangingPoint.setPoint(mRect.leftBottom);
			changingPoint.setPoint(mRect.rightUp);
			return;
		}
		/**
		 * 按下的若是右下角
		 */
		if (isInRect(x, y, mRect.rightBottom.x - touchWidth,
				mRect.rightBottom.y - touchWidth, mRect.rightBottom.x,
				mRect.rightBottom.y)) {
			unChangingPoint.setPoint(mRect.leftUp);
			changingPoint.setPoint(mRect.rightBottom);
			return;
		}
		/**
		 * 按下的是上方
		 */
		if (isInRect(x, y, mRect.leftUp.x + touchWidth, mRect.leftUp.y,
				mRect.rightUp.x - touchWidth, mRect.rightUp.y + touchWidth)) {
			unChangingPoint.setPoint(mRect.bottomCenter);
			changingPoint.setPoint(mRect.topCenter);
			return;
		}
		/**
		 * 按下的是下方
		 */
		if (isInRect(x, y, mRect.leftBottom.x + touchWidth, mRect.leftBottom.y
				- touchWidth, mRect.rightBottom.x - touchWidth,
				mRect.rightBottom.y)) {
			unChangingPoint.setPoint(mRect.topCenter);
			changingPoint.setPoint(mRect.bottomCenter);
			return;
		}
		/**
		 * 按下的是左方
		 */
		if (isInRect(x, y, mRect.leftUp.x, mRect.leftUp.y + touchWidth,
				mRect.leftBottom.x + touchWidth, mRect.leftBottom.y
						- touchWidth)) {
			unChangingPoint.setPoint(mRect.rightCenter);
			changingPoint.setPoint(mRect.leftCenter);
			return;
		}
		/**
		 * 按下的是右方
		 */
		if (isInRect(x, y, mRect.rightUp.x - touchWidth, mRect.leftUp.y
				+ touchWidth, mRect.rightBottom.x, mRect.rightBottom.y
				- touchWidth)) {
			unChangingPoint.setPoint(mRect.leftCenter);
			changingPoint.setPoint(mRect.rightCenter);
			return;
		}
	}

	/**
	 * 
	 * @param x
	 *            触摸点的x
	 * @param y
	 *            触摸点的y
	 * @param x1
	 *            左上角的x
	 * @param y1左上角的y
	 * @param x2右下角的x
	 * @param y2右下角的y
	 * @return 是否在这个矩形里，左上角和右下角就能确定这个矩形
	 */
	public boolean isInRect(float x, float y, float x1, float y1, float x2,
			float y2) {
		if ((x >= x1 && x <= x2) && (y >= y1 && y <= y2)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public String getCropImagePath(ImageView imageView) throws Exception {
		Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		int x = (int) mRect.leftUp.x;
		int y = (int) mRect.leftUp.y;
		int cropWidth = (int) (mRect.rightUp.x - mRect.leftUp.x);
		int cropHeight = (int) (mRect.leftBottom.y - mRect.leftUp.y);
		if (x + cropWidth > bm.getWidth()) {
			x = x * bm.getWidth() / totalWidth;
			cropWidth = cropWidth * bm.getWidth() / totalWidth;
		}
		if (y + cropHeight > bm.getHeight()) {
			y = y * bm.getHeight() / totalHeight;
			cropHeight = cropHeight * bm.getHeight() / totalHeight;
		}
		bm = bm.createBitmap(bm, x, y, cropWidth, cropHeight);
		return savePic(mContext, bm, Calendar.getInstance().getTimeInMillis()
				+ "");
	}

	public String savePic(Context context, Bitmap bm, String fileName)
			throws Exception {
		String path = getPicPath();
		File dirFile = new File(path);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		String cropImageName = path + "/" + fileName + ".jpg";
		File myCaptureFile = new File(cropImageName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		if (bm.compress(Bitmap.CompressFormat.JPEG, 80, bos)) {
			bos.flush();
			bos.close();
			Log.i("TAG", "保存成功~");
			return cropImageName;
		}
		if (bm.isRecycled()) {
			bm.recycle();
		}
		return "";
	}

	public String getPicPath() {
		String fileDir = "";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			fileDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else {
			fileDir = Environment.getRootDirectory().getAbsolutePath();
		}
		return fileDir + "/" + ConstantEntity.SAVE_IMAGE_FILE_NAME;
	}
}
