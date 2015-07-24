package net.arvin.listeners;

public interface OnItemChecked {
	/**
	 * 
	 * @param position
	 * @param isChecked false means need remove it,true means add it
	 */
	public void onItemChecked(int position,boolean isChecked);
}
