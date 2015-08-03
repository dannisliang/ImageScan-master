package net.arvin.imagescan.listeners;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class OnClickWithObject implements OnClickListener {
	private Object[] objs;

	public OnClickWithObject(Object... objs) {
		this.objs = objs;
	}

	@Override
	public void onClick(View v) {
		onClick(v, objs);
	}

	/**
	 * @param v
	 * @param objs
	 */
	public abstract void onClick(View v, Object[] objs);

}
