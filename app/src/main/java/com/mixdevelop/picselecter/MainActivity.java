package com.mixdevelop.picselecter;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.mark.multiimage.core.ImageMainActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRv;
    private ShowSelectedPicsAdapter adapter;
    private List<String> icons = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;
  private static  int CAMERA_CODE = 10086;//相机
  private static  int PHOTO_CODE = 10010;//相册

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        icons.add("-1");
        adapter.setNewData(icons);
    }

    private void initView() {
        mRv = (RecyclerView) findViewById(R.id.rv);
        adapter = new ShowSelectedPicsAdapter(R.layout.show_selected_pic_item);
        GridLayoutManager managere = new GridLayoutManager(this, 4);
        mRv.setLayoutManager(managere);
        mRv.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                imageView = (ImageView) adapter.getViewByPosition(mPublishNoticeRv, position, R.id.mine_sugguest_icon_iv);
                List<String> arrays = reSortIconList();
                String icon_path = arrays.get(position);
                switch (view.getId()) {
                    case R.id.mine_sugguest_icon_iv:
                        if ("-1".equals(icon_path)) {
                            View bottomView = LayoutInflater.from(MainActivity.this).inflate(R.layout.select_pic_menue, null);
                            bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                            bottomSheetDialog.setCancelable(false);
                            bottomSheetDialog.setContentView(bottomView);
                            bottomSheetDialog.show();
                            bottomView.findViewById(R.id.mine_edit_cancel_pic_tv).setOnClickListener(MainActivity.this);
                            bottomView.findViewById(R.id.mine_edit_take_pic_tv).setOnClickListener(MainActivity.this);
                            bottomView.findViewById(R.id.mine_edit_select_pic_tv).setOnClickListener(MainActivity.this);
                            bottomView.findViewById(R.id.mine_edit_title_tv).setVisibility(View.GONE);
                            bottomView.findViewById(R.id.mine_edit_title_line_tv).setVisibility(View.GONE);
                        }
                        break;
                    case R.id.mine_sugguest_delete_iv:
                        arrays.remove(position);
                        icons.clear();
                        if (arrays.size() < 10) {
                            if (!arrays.contains("-1")) {
                                arrays.add("-1");
                            }
                        }
                        icons = arrays;
                        adapter.setNewData(arrays);
                        break;
                    default:
                        break;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                String imagePath = FileUtil.getHeadPicPath(this)
                        + FileUtil.SUGGESTION_SAVED_PICTURE_NAME;
//                Bitmap bitmap = ImageUtil.readPictureDegreeToForwordBitmap(imagePath);
//                String userHeadPic = ImageUtil.saveCroppedImage(FileUtil.getHeadPicPath(this), bitmap);
                icons.add(imagePath);
                adapter.setNewData(reSortIconList());

            } else if (requestCode == PHOTO_CODE) {
                String imagePath = "";
                ArrayList<Uri> images = data.getParcelableArrayListExtra("result");
                for (int i = 0; i < images.size(); i++) {
                    imagePath = images.get(i).getPath();
                    icons.add(imagePath);
//                    Bitmap bitmap = ImageUtil.readPictureDegreeToForwordBitmap(imagePath);
//                    String userHeadPic = ImageUtil.saveCroppedImage(FileUtil.getHeadPicPath(MainActivity.this), bitmap);
//                    icons.add(userHeadPic);
                }
                adapter.setNewData(reSortIconList());

            }
        }


    }

    /**
     * 对icons集合处理
     *
     * @return
     */
    private List<String> reSortIconList() {
        List<String> icons_new = new ArrayList<>();
        for (String icon : icons) {
            if (!"-1".equals(icon)) {
                icons_new.add(icon);
            }
        }
        if (icons.size() < 10) {
            icons_new.add("-1");
        }
        return icons_new;
    }
    /**
     * 相机权限获取
     */
    private void cameraPromissionAllowed() {
        if (bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                FileUtil.getHeadPicPath(this), FileUtil.SUGGESTION_SAVED_PICTURE_NAME)));
        startActivityForResult(intent, CAMERA_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_edit_cancel_pic_tv://替换头像中的取消按钮
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
                break;
            case R.id.mine_edit_take_pic_tv://替换头像中的拍照按钮
                if (isCameraCanUse()) {
                    cameraPromissionAllowed();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage("无法使用相机，请开启相机权限后重试")
                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
                break;
            case R.id.mine_edit_select_pic_tv:////替换头像中的从相册中选取按钮
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
                Intent intent = new Intent(MainActivity.this, ImageMainActivity.class);
                intent.putExtra("action-original", true);
                intent.putExtra("MAX_SEND",10-icons.size());
                startActivityForResult(intent, PHOTO_CODE);
                break;

            default:
                break;
        }
    }
    /**
     * 判断摄像头是否可用
     * 主要针对6.0 之前的版本，现在主要是依靠try...catch... 报错信息，感觉不太好，
     * 以后有更好的方法的话可适当替换
     *
     * https://blog.csdn.net/jm_beizi/article/details/51728495
     *
     * @return
     */
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            // setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
            // 对象不为null
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }
        if (mCamera != null) {
            mCamera.release();
        }
        return canUse;
    }

}
