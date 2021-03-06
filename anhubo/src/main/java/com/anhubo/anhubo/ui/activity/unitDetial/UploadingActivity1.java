package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MsgPerfectLicenseBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.ImageFactory;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/17.
 */
public class UploadingActivity1 extends BaseActivity {

    private static final int CAMERA = 0;
    private static final int PICTURE = 1;
    private static final String TAG = "UploadingActivity1";
    @InjectView(R.id.ll_takePhoto01)
    LinearLayout llTakePhoto01;
    @InjectView(R.id.btn_unloading01)
    Button btnUnloading01;
    @InjectView(R.id.iv_showPhoto01)
    ImageView ivShowPhoto01;

    private Button btnTakephoto;
    private Button btnPhoto;
    private Dialog dialog;
    private Dialog showDialog;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_uploading1;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("营业执照");

    }

    @Override
    protected void onLoadDatas() {

    }


    @OnClick({R.id.ll_takePhoto01, R.id.btn_unloading01})
    public void onClick(View view) {
        //
        switch (view.getId()) {
            case R.id.ll_takePhoto01:
                // 底部弹出对话框
                showDialog();
                break;
            case R.id.btn_unloading01:
                // 提交按钮，走网络
                upLoading();
                break;
            case R.id.btn_popDialog_takephoto:
                // 拍照
                takePhoto();
                break;
            case R.id.btn_popDialog_photo:
                // 相册
                getPhoto();
                break;
        }
    }

    /**
     * 拿到拍到的照片去上传
     */
    private File newFile = null;

    private void upLoading() {
        // 获取
        String businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);


        if (newFile == null) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("亲，请拍取营业执照照片")
                    .setCancelable(true).show();
            return;
        }

        showDialog = loadProgressDialog.show(mActivity, "正在上传...");
        String url = Urls.Url_UpLoading01;
        Map<String, String> params = new HashMap<>();
        params.put("business_id", businessid);

        OkHttpUtils.post()//
                .addFile("file", "file01.png", newFile)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());


    }

    private Handler handler = new Handler();

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            LogUtils.e(TAG, ":upLoading:", e);
            showDialog.dismiss();
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();

        }

        @Override
        public void onResponse(String response) {
            showDialog.dismiss();
            LogUtils.eNormal(TAG + ":upLoading:", response);
            MsgPerfectLicenseBean licenseBean = JsonUtil.json2Bean(response, MsgPerfectLicenseBean.class);
            if (licenseBean != null) {
                int code = licenseBean.code;
                final String msg = licenseBean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, "上传失败");
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(mActivity, "上传成功");
                            Intent intent = new Intent();
                            intent.putExtra(Keys.ISCLICK1, true);
                            setResult(1, intent);
                            finish();
                        }
                    }, 500);
                }
            }

        }
    }

    private void getPhoto() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICTURE);

        dialog.dismiss();

    }

    private void takePhoto() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA);
        dialog.dismiss();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && null != data) {
            switch (requestCode) {
                case CAMERA:
                    showPhoto01(data);
                    break;
                case PICTURE:
                    showPhoto02(data);
                    break;
            }
        }

    }

    private void showPhoto02(Intent data) {

        Uri selectedImage = data.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String imagePath = c.getString(columnIndex);
        Bitmap photo = ImageFactory.ratio(imagePath, 120f, 240f);

        try {
            if (photo != null) {

                llTakePhoto01.setVisibility(View.GONE);
                //显示图片
                ivShowPhoto01.setImageBitmap(photo);
                // 把本文件压缩后缓存到本地文件里面
                savePicture(photo, "photo02");
                File filePhoto02 = new File(Environment.getExternalStorageDirectory() + "/" + "photo02");
                newFile = filePhoto02;


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            c.close();
        }
    }


    private void showPhoto01(Intent data) {
        String sdState = Environment.getExternalStorageState();
        if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showLongToast(mActivity, "sd card unmount");
            return;
        }
        new DateFormat();
        String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        Bundle bundle = data.getExtras();
        //获取相机返回的数据，并转换为图片格式
        Bitmap bitmap = (Bitmap) bundle.get("data");
        FileOutputStream fout = null;
        File file = new File("/sdcard/photo_anhubo/");
        file.mkdirs();
        String filename = file.getPath() + name;
        try {
            fout = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fout.flush();
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        llTakePhoto01.setVisibility(View.GONE);
        //显示图片
        ivShowPhoto01.setImageBitmap(bitmap);
        // 把本文件压缩后缓存到本地文件里面
        savePicture(bitmap, "photo01");
        File filePhoto01 = new File(Environment.getExternalStorageDirectory() + "/" + "photo01");
        newFile = filePhoto01;
    }

    /**
     * 保存图片到本应用下
     **/
    private void savePicture(Bitmap bitmap, String fileName) {

        FileOutputStream fos = null;
        try {//直接写入名称即可，没有会被自动创建；私有：只有本应用才能访问，重新写入内容会被覆盖
            //fos = mActivity.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStream stream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);// 把图片写入指定文件夹中

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                    fos = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 弹出对话框
     */
    private void showDialog() {
        // 创建一个对象
        View view = View.inflate(mActivity, R.layout.dialog_layout, null);
        View btnCancel = view.findViewById(R.id.btn_popDialog_cancel);//取消按钮
        //显示对话框
        ShowBottonDialog showBottonDialog = new ShowBottonDialog(mActivity, view, btnCancel);
        dialog = showBottonDialog.show();
        //拍照按钮
        btnTakephoto = (Button) view.findViewById(R.id.btn_popDialog_takephoto);
        //相册获取
        btnPhoto = (Button) view.findViewById(R.id.btn_popDialog_photo);
        // 设置监听
        btnTakephoto.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
