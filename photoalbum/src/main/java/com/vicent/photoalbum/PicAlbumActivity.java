package com.vicent.photoalbum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.util.List;

/**
 * 模仿相册，自写相册
 *
 * @author 孟祥程
 */
public class PicAlbumActivity extends Activity {
    //已经选择的图片
    public static final String EXTRA_IMAGE_LIST = "imagelist";

    //新选择的图片
    public static final String EXTRA_CHOOSE_PICTURE = "Picture";

    //已经选择的个数
    public static final String EXTRA_SELECT_COUNT = "selectCount";

    //可以选择的总个数
    public static final String EXTRA_TOTALCOUNT = "totalCount";

    public static final int CHOOSE_PIC_REQUEST = 100;

    // ArrayList<Entity> dataList;//用来装载数据源的列表
    List<ImageBucket> dataList;
    GridView gridView;
    ImageBucketAdapter adapter;// 自定义的适配器
    AlbumHelper helper;
    public static Bitmap bimap;


    //总个数
    private int totalCount;

    //已经选择的数量
    private int selectCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_bucket);
        getIntents();
        initTools();
        initView();
    }

    private void getIntents() {
        Intent intent = this.getIntent();
        if (intent != null) {
            totalCount = intent.getIntExtra(EXTRA_TOTALCOUNT, 1);
            selectCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 0);
        }
    }

    private void initTools() {
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {

        dataList = helper.getImagesBucketList(false);
        bimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_addpic_unfocused);
    }

    /**
     * 初始化view视图
     */
    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        adapter = new ImageBucketAdapter(PicAlbumActivity.this, dataList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /**
                 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
                 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
                 */
                // if(dataList.get(position).isSelected()){
                // dataList.get(position).setSelected(false);
                // }else{
                // dataList.get(position).setSelected(true);
                // }
                /**
                 * 通知适配器，绑定的数据发生了改变，应当刷新视图
                 */
                // adapter.notifyDataSetChanged();
                ImageGridActivity.startAcForResult(PicAlbumActivity.this, totalCount, selectCount, dataList.get(position).imageList, CHOOSE_PIC_REQUEST);

            }

        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_PIC_REQUEST:
                    Bundle b = data.getExtras();
//                    List<String> pictures = (List<String>) b.getStringArrayList(EXTRA_CHOOSE_PICTURE);
                    if (b.getStringArrayList(EXTRA_CHOOSE_PICTURE) != null && b.getStringArrayList(EXTRA_CHOOSE_PICTURE).size() > 0) {
                        PicAlbumActivity.this.setResult(RESULT_OK,
                                data); // 就是在这里把返回所选择的图片setResult给FirstActivity
                        finish(); // finish应该写到这个地方
                    }
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 传递值给第一个Activity
    public static void startAcForResult(Activity context, int totalCount, int selectCount,int requestCode) {
        Intent i = new Intent(context, PicAlbumActivity.class);
        i.putExtra(EXTRA_TOTALCOUNT, totalCount);
        i.putExtra(EXTRA_SELECT_COUNT, selectCount);
        context.startActivityForResult(i,
                requestCode);
    }

    public static void startFragmentForResult(android.support.v4.app.Fragment context, int totalCount, int selectCount,int requestCode) {
        Intent i = new Intent(context.getActivity(), PicAlbumActivity.class);
        i.putExtra(EXTRA_TOTALCOUNT, totalCount);
        i.putExtra(EXTRA_SELECT_COUNT, selectCount);
        context.startActivityForResult(i,
                requestCode);
    }

    public void onback(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        recycle();
        super.onDestroy();
    }

    private void recycle() {
//		adapter.recycle();
        destoryBimap();
        helper = null;
        if (dataList != null) {
            dataList.clear();
            dataList = null;
        }
        adapter = null;
        System.gc();
    }

    private void destoryBimap() {
        if (bimap != null && !bimap.isRecycled()) {
            try {
                bimap.recycle();
            } catch (Exception e) {
                System.out.println("回收异常2");
            } finally {
                bimap = null;
                System.gc();
            }
        }
    }


}
