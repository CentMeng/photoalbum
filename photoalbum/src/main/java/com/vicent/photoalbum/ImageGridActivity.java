package com.vicent.photoalbum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 相册里面选择图片页面
 *
 * @author 孟祥程
 */
public class ImageGridActivity extends Activity {


    List<ImageItem> dataList;
    GridView gridView;
    ImageGridAdapter adapter;
    AlbumHelper helper;
    Button submit;

    //总选择数
    private int totalCount;
    //已经选中的个数
    private int selectCount;

    //获取内容
    private Thread dataThread;

    private static final int REFRESH_VIEW = 1;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case REFRESH_VIEW:
                    //刷新页面
                    initView();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);
        getIntents();
        getData();
    }

    @Override
    protected void onDestroy() {
        recycle();
        super.onDestroy();
    }


    private void getIntents() {
        Intent intent = this.getIntent();
        if (intent != null) {
            totalCount = intent.getIntExtra(PicAlbumActivity.EXTRA_TOTALCOUNT, 1);
            selectCount = intent.getIntExtra(PicAlbumActivity.EXTRA_SELECT_COUNT, 0);
        }
    }

    private void getData() {
        dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                helper = AlbumHelper.getHelper();
                helper.init(getApplicationContext());
                dataList = (List<ImageItem>) getIntent().getSerializableExtra(
                        PicAlbumActivity.EXTRA_IMAGE_LIST);
                Message message = Message.obtain(mHandler, REFRESH_VIEW);
                message.sendToTarget();
            }
        });
        dataThread.start();
    }

    /**
     * 刷新页面
     */
    private void initView() {
        submit = (Button) findViewById(R.id.bt);
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,
                mHandler, totalCount, selectCount);
        gridView.setAdapter(adapter);
        adapter.setTextCallback(new ImageGridAdapter.TextCallback() {
            public void onListen(int count) {
                submit.setText("完成" + "(" + count + ")");
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.notifyDataSetChanged();
            }

        });

    }


    public void submit(View view) {
        ArrayList<String> list = new ArrayList<String>();
        Collection<String> c = adapter.map.values();
        if (c.size() > 0) {
            Iterator<String> it = c.iterator();
            for (; it.hasNext(); ) {
                list.add(it.next());
            }

            //完成按钮
            Intent data = new Intent();
            Bundle bundle = new Bundle();
//				String picture = list.get(0);
            bundle.putStringArrayList(PicAlbumActivity.EXTRA_CHOOSE_PICTURE, list);
            data.putExtras(bundle);
            setResult(RESULT_OK, data);
            finish();
        } else {
            finish();
        }
    }


    /**
     *
     * @param context
     * @param totalCount  最多的选择个数
     * @param selectCount  已经选择的个数
     * @param imageList
     * @param requestCode
     */
    public static void startAcForResult(Context context,int totalCount,int selectCount, List<ImageItem> imageList,int requestCode) {//跳到选择图片的ListView
        Intent i = new Intent(context, ImageGridActivity.class);
        i.putExtra(PicAlbumActivity.EXTRA_TOTALCOUNT, totalCount);
        i.putExtra(PicAlbumActivity.EXTRA_SELECT_COUNT,selectCount);
        i.putExtra(PicAlbumActivity.EXTRA_IMAGE_LIST, (Serializable) imageList);
        ((Activity) context).startActivityForResult(i, requestCode);
    }

    public void onback(View v) {
        finish();
    }

    private void recycle() {
        if (!dataThread.isInterrupted()) {
            dataThread.interrupt();
        }
        helper = null;
        dataList = null;
        adapter = null;
        System.gc();
    }

}
