package com.anhubo.anhubo.ui.activity.MyDetial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MyAlterAgeBean;
import com.anhubo.anhubo.bean.MyAlterGenderBean;
import com.anhubo.anhubo.bean.MyAlterNameBean;
import com.anhubo.anhubo.bean.MyFragmentBean;
import com.anhubo.anhubo.bean.My_HeaderIconBean;
import com.anhubo.anhubo.bean.PersonMsgBindBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.impl.MyFragment;
import com.anhubo.anhubo.utils.DatePackerUtil;
import com.anhubo.anhubo.utils.ImageTools;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.PopBirthHelper;
import com.anhubo.anhubo.utils.PopGenderHelper;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.ConfirmPopWindow;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.google.gson.Gson;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/10/28.
 */
public class PersonMsgActivity extends BaseActivity {
    private static final int PICTURE = 0;
    private static final int CAMERA = 1;
    @InjectView(R.id.ll_psHeaderIcon)
    LinearLayout llPsHeaderIcon;
    @InjectView(R.id.ll_psUsername)
    LinearLayout llPsUsername;
    @InjectView(R.id.ll_psAge)
    LinearLayout llPsAge;
    @InjectView(R.id.ll_psGender)
    LinearLayout llPsGender;
    @InjectView(R.id.ll_psPhone)
    LinearLayout llPsPhone;
    @InjectView(R.id.ll_pspwd)
    LinearLayout llPspwd;
    @InjectView(R.id.ll_psCertification)
    LinearLayout llPsCertification;
    @InjectView(R.id.ll_psUnit)
    LinearLayout llPsUnit;
    @InjectView(R.id.ll_psWeChat)
    LinearLayout llPsWeChat;
    @InjectView(R.id.iv_headerIcon)
    ImageView ivHeaderIcon;
    @InjectView(R.id.et_my_username)
    EditText etMyUsername;
    @InjectView(R.id.tv_my_age)
    TextView tvMyAge;
    @InjectView(R.id.tv_my_gender)
    TextView tvMyGender;
    @InjectView(R.id.tv_my_pwd)
    TextView tvMyPwd;
    @InjectView(R.id.tv_my_unit)
    TextView tvMyUnit;
    @InjectView(R.id.tv_my_wechat)
    TextView tvMyWechat;
    @InjectView(R.id.tv_my_phone)
    TextView tvMyPhone;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private boolean isClick = false;
    private File filePhoto01;
    private File filePhoto02;
    private boolean isShow;
    private String age;
    private String sex;
    private String img;
    private String name;
    private String businessName;
    private String phone;
    private String qqName;
    private String weiboName;
    private String weixinName;
    private String uid;
    private Handler handler = new Handler();
    private PopBirthHelper popBirthHelper;
    private InputMethodManager imm;
    private String newName;
    private PopGenderHelper popGenderHelper;
    private String newGender;
    private String newAge;
    private String screenname;
    public static boolean isSetHeadIcon = false;// 记录自己是否设置过头像

    @Override
    protected void initConfig() {
        super.initConfig();
        Intent intent = getIntent();
        MyFragmentBean bean = (MyFragmentBean) intent.getSerializableExtra(Keys.MYBEAN);
        if (bean != null) {
            age = bean.data.age;
            sex = bean.data.sex;
            img = bean.data.img;
            name = bean.data.name;
            businessName = bean.data.business_name;
            phone = bean.data.phone;
            qqName = bean.data.qq_name;
            weiboName = bean.data.weibo_name;
            weixinName = bean.data.weixin_name;
        }
        // 进来页面后取出微信昵称
        screenname = SpUtils.getStringParam(mActivity, Keys.SCREENNAME, null);


    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personmsg;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("个人信息");
        if (!TextUtils.isEmpty(screenname)) {
            tvMyWechat.setText(screenname);
        }
    }


    @Override
    protected void onLoadDatas() {

    }

    @OnClick({R.id.ll_psHeaderIcon, R.id.ll_psUsername, R.id.et_my_username, R.id.ll_psAge, R.id.ll_psGender, R.id.ll_psPhone, R.id.ll_pspwd, R.id.ll_psCertification, R.id.ll_psUnit, R.id.ll_psWeChat})
    public void onClick(View view) {
        // 获取uid
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        //System.out.println("111uid是+++===" + uid);
        switch (view.getId()) {
            case R.id.ll_psHeaderIcon:
                /**弹出拍照对话框*/
                showDialog();
                break;
            case R.id.ll_psUsername:
                /**修改用户名*/
                alterName();
                break;
            case R.id.et_my_username:
                break;
            case R.id.ll_psAge:
                /**年龄弹窗*/
                popBirthHelper.show(llPsAge);
                break;
            case R.id.ll_psGender:
                /**性别弹窗*/
                popGenderHelper.show(llPsGender);
                break;
            case R.id.ll_psPhone:
                //手机号码
                break;
            case R.id.ll_pspwd:
                // 密码修改
                alterPwd();
                break;
            case R.id.ll_psCertification:
                // 实名认证
                certification();
                break;
            case R.id.ll_psUnit:
                // 所属单位
                break;
            case R.id.ll_psWeChat:
                //微信
                UMShareAPI mShareAPI = UMShareAPI.get(mActivity);
                //mShareAPI.doOauthVerify(mActivity, SHARE_MEDIA.WEIXIN, umAuthListener);//授权
                mShareAPI.getPlatformInfo(mActivity, SHARE_MEDIA.WEIXIN, umAuthListener1);//获取用户信息
                break;
            case R.id.btn_popDialog_takephoto:
                // 拍照
                isClick = true;
                takePhoto();
                break;
            case R.id.btn_popDialog_photo:
                // 相册
                isClick = false;
                getPhoto();
                break;
        }
    }


    @Override
    protected void initEvents() {
        super.initEvents();

        //设置etMyUsername的点击事件
        etMyUsername.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            int touch_flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch_flag++;
                if (touch_flag == 2) {
                    alterName();
                }
                return false;
            }
        });

        // 给每个控件先设置初始内容
        setInitialData();

        Bitmap mBitmap = MyFragment.mBitmap;
        if (mBitmap != null) {
            ivHeaderIcon.setImageBitmap(mBitmap);
        }
        /**获取我的界面传过来的姓名，显示*/
        String name_new = MyFragment.name_new;
        if (!TextUtils.isEmpty(name_new)) {
            etMyUsername.setText(name_new);
        }
        /**获取我的界面传过来的年龄，显示*/
        String age_new = MyFragment.age_new;
        if (!TextUtils.isEmpty(age_new)) {
            tvMyAge.setText(age_new);
        }
        /**获取我的界面传过来的性别，显示*/
        String gender_new = MyFragment.gender_new;
        if (!TextUtils.isEmpty(gender_new)) {
            tvMyGender.setText(gender_new);
        }
        /**年龄弹窗的显示*/
        alterAge();
        /**性别弹窗*/
        alterGender();
    }

    private String unionid;
    private String profileImageUrl;
    private String screenName;
    /**
     * 微信绑定
     */
    private UMAuthListener umAuthListener1 = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if (map != null) {
                //转换为set

                Set<String> keySet = map.keySet();

                //遍历循环，得到里面的key值----用户名，头像....

                for (String string : keySet) {
                    //打印下
                    //System.out.println("==========11111111111=="+string);
                    // 打印完获取到的信息在下面
                   /* unionid profile_image_url country screen_name access_token city gender province
                    language expires_in refresh_token openid*/
                    //我需要的 uid unionid 头像 profile_image_url   姓名  screen_name
                }
                unionid = map.get("unionid");
                profileImageUrl = map.get("profile_image_url");
                screenName = map.get("screen_name");

            }

            /**微信授权后走的微信登录接口*/
            String url = Urls.Url_BindWEIXIN;
            // 封装请求参数
            HashMap<String, String> params = new HashMap<String, String>();

            params.put("uid", uid);
            params.put("third_type", 2 + "");
            params.put("unique_name", screenName);
            params.put("unique_id", unionid);
            params.put("pic_url", profileImageUrl);

            OkHttpUtils.post()//
                    .url(url)//
                    .params(params)//
                    .build()//
                    .execute(new MyStringCallback5());

        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            ToastUtils.showToast(mActivity, "授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            ToastUtils.showToast(mActivity, "授权取消");
        }
    };




    class MyStringCallback5 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");

            System.out.println("PersonMsgActivity+++界面绑定微信===" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
//            System.out.println("绑定微信"+response);
            PersonMsgBindBean bean = new Gson().fromJson(response, PersonMsgBindBean.class);
            if (bean != null) {
                int code = bean.code;
                if (code == 0) {
                    if (!isSetHeadIcon) {
                        // 代表用户没设置过自己的头像，因此显示自己的微信头像
                        setHeaderIcon(profileImageUrl);


                        // 设置完后通知我的界面也改变显示内容
                        Intent intent = new Intent();
                        intent.putExtra(Keys.HEADERICON_WEIXIN, profileImageUrl);
                        setResult(5, intent);
                    }
                    tvMyWechat.setText(screenName);
                    // 把微信名也要记录下来，下次进来的时候显示
                    SpUtils.putParam(mActivity, Keys.SCREENNAME, screenName);


                }

            }
        }
    }


    /**
     * ****************************************************************************
     * 实名认证
     */
    private void certification() {
        Intent intent = new Intent(mActivity, CertificationActivity.class);
        startActivity(intent);

    }

    /**
     * ****************************************************************************
     * 密码修改
     */
    private void alterPwd() {
        Intent intent = new Intent(mActivity, AlterPwdActivity.class);
        startActivity(intent);
    }

    /**
     * ****************************************************************************
     * 性别弹窗
     */
    private void alterGender() {
        popGenderHelper = new PopGenderHelper(this);
        popGenderHelper.setListItem(DatePackerUtil.getListGender());
        popGenderHelper.setOnClickOkListener(new PopGenderHelper.OnClickOkListener() {

            @Override
            public void onClickOk(String str) {
                tvMyGender.setText(str);
                newGender = str;
                // 走网络，提交性别
                String url = Urls.Url_My_Gender;
                HashMap<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("sex", str);
                OkHttpUtils.post()//
                        .url(url)//
                        .params(params)//
                        .build()//
                        .execute(new MyStringCallback4());
            }
        });
    }

    /**
     * 上传性别
     */
    class MyStringCallback4 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");

            System.out.println("PersonMsgActivity+++界面上传性别===" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("PersonMsgActivity+++界面上传性别"+response);
            MyAlterGenderBean bean = new Gson().fromJson(response, MyAlterGenderBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, msg);
                } else {
                    //ToastUtils.showToast(mActivity,"性别修改成功");
                    // 每次更改成功后要通知我的界面也要改变显示内容
                    Intent intent = new Intent();
                    intent.putExtra(Keys.NEWGENDER, newGender);
                    setResult(4, intent);
                }
            }
        }
    }

    /**
     * ****************************************************************************
     * 年龄弹窗的显示
     */
    private void alterAge() {
        popBirthHelper = new PopBirthHelper(mActivity);
        popBirthHelper.setOnClickOkListener(new PopBirthHelper.OnClickOkListener() {
            @Override
            public void onClickOk(String time) {

                try {

                    //此处是获得的年龄
                    //由出生日期获得年龄***
                    newAge = String.valueOf(getAge(parse(time)));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Integer.parseInt(newAge) >= 0) {
                    tvMyAge.setText(newAge);
                    // 拿到年龄,上传到网络
                    uploadAge(time);
                } else {
                    //new ConfirmPopWindow(mActivity).showDialog(llPsAge,"年龄","您所选日期大于当前时间");
                    ToastUtils.showToast(mActivity, "您所选日期大于当前时间");
                }

            }


        });
    }

    /**
     * 把年龄上传到网络
     *
     * @param time
     */
    private void uploadAge(String time) {
        // 走网络，提交性别
        String url = Urls.Url_My_Age;
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("birthday", time);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback3());
    }

    class MyStringCallback3 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");
            System.out.println("PersonMsgActivity+++界面上传年龄===" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            MyAlterAgeBean bean = new Gson().fromJson(response, MyAlterAgeBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, msg);
                } else {

                    //ToastUtils.showToast(mActivity, "年龄修改成功");
                    Intent intent = new Intent();
                    intent.putExtra(Keys.NEWAGE, newAge);
                    setResult(3, intent);
                }
            }
        }
    }

    //出生日期字符串转化成Date对象
    public Date parse(String strDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.parse(strDate);
    }


    //由出生日期获得年龄
    public int getAge(Date birthDay) {
        //获取当前系统时间
        Calendar cal = Calendar.getInstance();
        //如果出生日期大于当前时间，则抛出异常

        try {

            //取出系统当前时间的年、月、日部分
            int yearNow = cal.get(Calendar.YEAR);
            int monthNow = cal.get(Calendar.MONTH);
            int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

            //将日期设置为出生日期
            cal.setTime(birthDay);
            //取出出生日期的年、月、日部分
            int yearBirth = cal.get(Calendar.YEAR);
            int monthBirth = cal.get(Calendar.MONTH);
            int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            //当前年份与出生年份相减，初步计算年龄
            int age = yearNow - yearBirth;
            //当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
            if (monthNow <= monthBirth) {
                //如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
                if (monthNow == monthBirth) {
                    if (dayOfMonthNow < dayOfMonthBirth) age--;
                } else {
                    age--;
                }
            }
            return age;
        } catch (Exception e) {
            if (cal.before(birthDay)) {
                ToastUtils.showToast(mActivity, "您所选日期大于当前时间");
            }
            return 0;
        }
    }


    /**
     * 给每个控件先设置初始内容
     */
    private void setInitialData() {
        if (!TextUtils.isEmpty(img)) {
            isSetHeadIcon = true;
        }
        if (!TextUtils.isEmpty(name)) {
            etMyUsername.setText(name);
        }
        if (!TextUtils.isEmpty(age)) {
            tvMyAge.setText(age);
        }
        if (!TextUtils.isEmpty(sex)) {
            tvMyGender.setText(sex);
        }
        if (!TextUtils.isEmpty(phone)) {
            tvMyPhone.setText(phone);
        }
        if (!TextUtils.isEmpty(businessName)) {
            tvMyUnit.setText(businessName);
        }
        if (!TextUtils.isEmpty(weixinName)) {
            tvMyWechat.setText(weixinName);
        }
    }


    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            // 上传更改后的姓名
            uploadName();
        }
    };

    /**
     * ****************************************************************************
     * 修改用户名
     */
    private void alterName() {
        // 先弹出键盘,让焦点在输入框上
        etMyUsername.requestFocus();// 获取焦点
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        if (imm.isActive()) {
            // 获取焦点,先设置光标遇到最后，然后监听输入框的动态变化
            etMyUsername.setSelection(etMyUsername.length());
            listenetMyUsername();
        }
    }

    /**
     * 监听输入框的动态变化
     */
    private void listenetMyUsername() {
        etMyUsername.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                newName = s.toString();
                //延迟2s，如果不再输入字符，则执行该线程的run方法，取消输入框的焦点
                handler.postDelayed(delayRun, 2000);
            }
        });
    }

    /**
     * 上传名字
     */
    private void uploadName() {
        String url = Urls.Url_My_Name;
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("name", newName);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback2());
    }

    /**
     * 上传用户
     */
    class MyStringCallback2 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");
            System.out.println("PersonMsgActivity+++界面上传name===" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("PersonMsgActivity+界面修改用户名" + response);
            MyAlterNameBean bean = new Gson().fromJson(response, MyAlterNameBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, msg);
                } else {
                    //ToastUtils.showToast(mActivity, "修改成功");
                    // 每次更改成功后要通知我的界面也要改变显示内容
                    Intent intent = new Intent();
                    intent.putExtra(Keys.NEWNAME, newName);
                    setResult(2, intent);

                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && null != data) {
            switch (requestCode) {
                case CAMERA:
                    isShow = showPhoto01(data);
                    break;
                case PICTURE:
                    isShow = showPhoto02(data);
                    break;
            }
        }
        if (isShow) {
            // 记录头像已经设置过
            isSetHeadIcon = true;

            // 说明图片已经显示，上传头像到网络
            upLoading();
            isShow = false;
        } else {
            // 图片没显示
            isShow = false;
        }

    }

    /**
     * 拿到拍到的照片去上传
     */
    private void upLoading() {


        // 获取
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        File file = null;
        if (isClick) {
            file = filePhoto01;//相机照片
        } else {
            file = filePhoto02;//相册照片
        }
        if (file == null || !file.exists()) {
            ToastUtils.showLongToast(mActivity, "请先拍照或者获取图库图片");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);


        String url = Urls.Url_UpLoadingHeaderIcon;

        OkHttpUtils.post()//
                .addFile("file", "file01.png", file)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }

    /**
     * 上传头像
     */
    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            System.out.println("PersonMsgActivity+++界面上传头像===" + e.getMessage());

            ToastUtils.showToast(mActivity, "网络有问题，请检查");

        }

        @Override
        public void onResponse(String response) {
            //System.out.println("PersonMsgActivity个人信息页面+" + response);
            My_HeaderIconBean bean = new Gson().fromJson(response, My_HeaderIconBean.class);
            if (bean != null) {
                int code = bean.code;
                final String img1 = bean.data.img;
                String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, msg);
                } else {
                    // code = 0，保存成功

                    ToastUtils.showToast(mActivity, "保存成功");
                    Intent intent = new Intent();
                    intent.putExtra(Keys.HEADERICON, img1);
                    setResult(1, intent);

                }
            }

        }
    }

    /**
     * 展示相册图片
     */
    private boolean showPhoto02(Intent data) {

        Uri selectedImage = data.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String imagePath = c.getString(columnIndex);
        filePhoto02 = new File(imagePath);
        Bitmap photo = BitmapFactory.decodeFile(imagePath);
        try {

            /*imgName = createPhotoFileName();
            //写一个方法将此文件保存到本应用下面啦
            savePicture(imgName, photo);*/


            if (photo != null) {
                //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                Bitmap bitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / 5, photo.getHeight() / 5);

                //显示图片
                ivHeaderIcon.setImageBitmap(bitmap);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            c.close();
        }
        return false;
    }

    /**
     * 展示照相机图片
     */
    private boolean showPhoto01(Intent data) {
        String sdState = Environment.getExternalStorageState();
        if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showLongToast(mActivity, "sd card unmount");
            return false;
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
        filePhoto01 = new File(filename);//图片的文件
        try {
            fout = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);

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
        if (bitmap != null) {
            //显示图片
            ivHeaderIcon.setImageBitmap(bitmap);
            return true;
        }
        return false;
    }

    /**
     * 打开相册获取图片
     */
    private void getPhoto() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICTURE);

        dialog.dismiss();

    }

    /**
     * 打开相机拍照
     */
    private void takePhoto() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA);
        dialog.dismiss();

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

    /**
     * 设置头像的方法
     */
    private void setHeaderIcon(String imgurl) {
        OkHttpUtils
                .get()//
                .url(imgurl)//
                .tag(this)//
                .build()//
                .connTimeOut(15000)
                .readTimeOut(15000)
                .writeTimeOut(15000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                        System.out.println("MyFragment获取头像+++===" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ivHeaderIcon.setImageBitmap(bitmap);
                    }
                });
    }

}