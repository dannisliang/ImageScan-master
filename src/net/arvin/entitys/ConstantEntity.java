package net.arvin.entitys;

public class ConstantEntity {
	/**
	 * ����ͼƬ�ļ��е�����
	 */
	public static final String SAVE_IMAGE_FILE_NAME = "imagescan";
	public static final String SELECTED_IMAGES = "selected_images";
	public static final String CURRENT_IMAGES = "current_images";
	public static final String CLICKED_POSITION = "clicked_position";
	/**
	 * �������ݵĹؼ���
	 */
	public static final String RESPONSE_KEY = "response_key";
	/**
	 * ��ѡͼƬ����������Ĺؼ���
	 */
	public static final String MAX_NUM = "max_num";
	/**
	 * �Ƿ���Ҫ�ü��Ĺؼ���
	 */
	public static final String IS_CROP = "is_crop";
	public static final String CROP_IMAGE = "crop_image";
	public static final int REQUEST_CODE = 2001;
	public static final int IMAGE_REQUEST_TAKE_PHOTO = 1001;
	public static final int TYPE_CAMERA = 0;
	public static final int TYPE_NORMAL = 1;
	public static final int SCAN_OK = 0;

	/**
	 * return a number,which is the default most selected images' number
	 */
	public static int getDefaultMaxSelectNum() {
		return 1;
	}
}
