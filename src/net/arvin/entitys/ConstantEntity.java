package net.arvin.entitys;

public class ConstantEntity {
	public static final String SELECTED_IMAGES = "selected_images";
	public static final String CURRENT_IMAGES = "current_images";
	public static final String CLICKED_POSITION = "clicked_position";
	public static final String RESPONSE_KEY = "response_key";
	public static final String MAX_NUM = "max_num";
	public static final int REQUEST_CODE = 2001;
	public static final int IMAGE_REQUEST_TAKE_PHOTO = 1001;
	public static final int TYPE_CAMERA = 0;
	public static final int TYPE_NORMAL = 1;

	/**
	 * return a number,which is the default most selected images' number
	 */
	public static int getDefaultMaxSelectNum() {
		return 9;
	}
}
