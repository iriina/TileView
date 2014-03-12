package com.qozix.tileview.markers;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import com.qozix.layouts.ZoomPanLayout;
import com.qozix.tileview.detail.DetailManager;

public class ScalableMarkerManager extends MarkerManager {

	private double mMinScale, mMaxScale;

	public ScalableMarkerManager(Context context, DetailManager detailManager) {
		super(context, detailManager);
	}

	public void setMarkerScaleLimits(double minScale, double maxScale) {
		mMinScale = minScale;
		mMaxScale = maxScale;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		double mapMin = ((ZoomPanLayout) getParent().getParent()).getMinScale();
		double mapMax = ((ZoomPanLayout) getParent().getParent()).getMaxScale();
		double markerScale = scale * (mMaxScale - mMinScale) / (mapMax - mapMin) +
				(mMinScale * mapMax - mMaxScale * mapMin) / (mapMax - mapMin);

		for (int i = getChildCount() - 1; i >= 0; i--) {
			View child = getChildAt(i);

			if (child.getVisibility() != GONE) {
				LayoutParams lp = (LayoutParams) child.getLayoutParams();
				// get sizes
				int w, h;
				w = (int) (child.getMeasuredWidth() * markerScale);
				h = (int) (child.getMeasuredHeight() * markerScale);

				// get offset position
				int scaledX = (int) (0.5 + (lp.x * scale));
				int scaledY = (int) (0.5 + (lp.y * scale));
				// user child's layout params anchor position if set, otherwise default to anchor position of layout
				float aX = (lp.anchorX == null) ? anchorX : lp.anchorX;
				float aY = (lp.anchorY == null) ? anchorY : lp.anchorY;
				// apply anchor offset to position
				int x = scaledX + (int) (w * aX);
				int y = scaledY + (int) (h * aY);
				// get and set the rect for the child
				Rect rect = markerMap.get(child);
				if (rect != null) {
					rect.set(x, y, x + w, y + h);
				}
				child.layout(x, y, x + w, y + h);
			}
		}
	}
}