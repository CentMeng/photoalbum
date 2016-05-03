package com.vicent.photoalbum;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImageGridAdapter extends BaseAdapter {

	private TextCallback textcallback = null;
	final String TAG = getClass().getSimpleName();
	Activity act;
	List<ImageItem> dataList;
	Map<String, String> map = new HashMap<String, String>();
	BitmapCache cache;
	private Handler mHandler;
	//当前所选的照片个数
	private int currentSelectCount = 0;
	//总共可选的个数
	private int totalCount = 1;
	//已经选过的个数
	private int selectCount = 0;

	BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};

	public static interface TextCallback {
		public void onListen(int count);
	}

	public void setTextCallback(TextCallback listener) {
		textcallback = listener;
	}

	public ImageGridAdapter(Activity act, List<ImageItem> list, Handler mHandler,int totalCount,int selectCount) {
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
		this.mHandler = mHandler;
		this.totalCount = totalCount;
		this.selectCount = selectCount;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class Holder {
		private MyImageView iv;
		private MyImageView selected;
		private TextView text;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_image_grid, null);
			holder.iv = (MyImageView) convertView.findViewById(R.id.image);
			holder.selected = (MyImageView) convertView
					.findViewById(R.id.isselected);
			holder.text = (TextView) convertView
					.findViewById(R.id.item_image_grid_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final ImageItem item = dataList.get(position);

		holder.iv.setTag(item.imagePath);
		cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,
				callback);
		if (item.isSelected) {
			holder.selected.setImageResource(R.drawable.icon_data_select);
			holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
		} else {
			holder.selected.setImageResource(-1);
			holder.text.setBackgroundColor(0x00000000);
		}
		holder.iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String path = dataList.get(position).imagePath;

				if ((currentSelectCount+selectCount) < totalCount ){
					item.isSelected = !item.isSelected;
					if (item.isSelected) {
						holder.selected
								.setImageResource(R.drawable.icon_data_select);
						holder.text
								.setBackgroundResource(R.drawable.bgd_relatly_line);
						currentSelectCount++;
						if (textcallback != null)
							textcallback.onListen(currentSelectCount);
						map.put(path, path);

					} else if (!item.isSelected) {
						holder.selected.setImageResource(-1);
						holder.text.setBackgroundColor(0x00000000);
						currentSelectCount--;
						if (textcallback != null)
							textcallback.onListen(currentSelectCount);
						map.remove(path);
					}
				} else if ((currentSelectCount+selectCount) >= totalCount) {
					if (item.isSelected == true) {
						item.isSelected = !item.isSelected;
						holder.selected.setImageResource(-1);
						currentSelectCount--;
						map.remove(path);

					} else {
						Message message = Message.obtain(mHandler, 0);
						message.sendToTarget();
					}
				}

				// 下面是可以选择9张图片判断
				// if (( selectTotal) < 9) {
				// item.isSelected = !item.isSelected;
				// if (item.isSelected) {
				// holder.selected
				// .setImageResource(R.drawable.icon_data_select);
				// holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
				// selectTotal++;
				// if (textcallback != null)
				// textcallback.onListen(selectTotal);
				// map.put(path, path);
				//
				// } else if (!item.isSelected) {
				// holder.selected.setImageResource(-1);
				// holder.text.setBackgroundColor(0x00000000);
				// selectTotal--;
				// if (textcallback != null)
				// textcallback.onListen(selectTotal);
				// map.remove(path);
				// }
				// } else if ((selectTotal) >= 9) {
				// if (item.isSelected == true) {
				// item.isSelected = !item.isSelected;
				// holder.selected.setImageResource(-1);
				// selectTotal--;
				// map.remove(path);
				//
				// } else {
				// Message message = Message.obtain(mHandler, 0);
				// message.sendToTarget();
				// }
				// }
			}

		});

		return convertView;
	}

}
