package com.anhubo.anhubo.ui.impl;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.UnitAdapter;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.bean.MyPolygonBean;
import com.anhubo.anhubo.bean.SesameItemModel;
import com.anhubo.anhubo.bean.SesameModel;
import com.anhubo.anhubo.bean.UnitBean;
import com.anhubo.anhubo.protocol.RequestResultListener;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.unitDetial.MsgPerfectActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.QrScanActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.Unit2Study;
import com.anhubo.anhubo.ui.activity.unitDetial.UnitMenuActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.UnitMsgCenterActivity;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.NetUtil;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.MyPolygonView;
import com.anhubo.anhubo.view.SesameCreditPanel;
import com.anhubo.anhubo.view.ShowLoadingDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2016/10/8.
 */
public class UnitFragment extends BaseFragment {


    private ListView lvUnit;
    private RelativeLayout rlUnit01;
    private RelativeLayout rlUnit02;
    private RelativeLayout rlUnit;
    private TextView tvUnitFrag;
    private TextView tvUnitFragMsg;
    private TextView tvUnitFragInvite;
    private TextView tvUnitFragAdd;
    private Button btnStudy;
    private Button btnCheck;
    private Button btnDrill;
    private View dotStudy;
    private View dotCheck;
    private View dotDrill;
    private FrameLayout sesameCreditPanelLL;
    private MyPolygonView myPolygonView;
    private String datatime;
    private String grade;
    private int sumScore;
    private String subScore1;
    private String subScore2;
    private String subScore3;
    private String subScore4;
    private String subScore5;
    private String subScore6;
    private ArrayList<String> list;
    private int[] arrScores;
    private UnitBean.Data data;
    private SesameCreditPanel scp;
    private Dialog dialog;


    @Override
    public void initTitleBar() {
        //设置返回键隐藏
        iv_basepager_left.setVisibility(View.GONE);
        //设置菜单键和信息键显示
        ivTopBarleftUnitMenu.setVisibility(View.VISIBLE);
        ivTopBarRightUnitMsg.setVisibility(View.VISIBLE);

        tv_basepager_title.setText("五彩太平洋");
        // 设置title的背景颜色
        llTop.setBackgroundResource(R.color.unit_top);
    }

    @Override
    public Object getContentView() {
        return R.layout.fragment_unit;
    }

    @Override
    public void initView() {
        // 统计图
        rlUnit01 = findView(R.id.rl_unit_01);
        rlUnit02 = findView(R.id.rl_unit_02);
        rlUnit = findView(R.id.rl_unit);
        // 统计图下面的按钮
        tvUnitFrag = findView(R.id.tv_unit_frag_01);
        tvUnitFragMsg = findView(R.id.tv_unit_frag_02_msg);
        tvUnitFragInvite = findView(R.id.tv_unit_frag_02_invite);
        tvUnitFragAdd = findView(R.id.tv_unit_frag_02_add);

        // 圆形进度条
        sesameCreditPanelLL = findView(R.id.panel);
        // 多边形
        myPolygonView = findView(R.id.polygon);
        // 设置下划线 setUnderline里面的参数是可变参数
        Utils.setUnderline(tvUnitFrag, tvUnitFragMsg, tvUnitFragInvite, tvUnitFragAdd);
        // listView
         lvUnit = findView(R.id.lv_unit);

        View view = View.inflate(mActivity, R.layout.header_unit, null);

        // 安全提升级别的button
        btnStudy = (Button) view.findViewById(R.id.btn_study);
        btnCheck = (Button) view.findViewById(R.id.btn_check);
        btnDrill = (Button) view.findViewById(R.id.btn_drill);
        // button的小圆点
        dotStudy = view.findViewById(R.id.dot_study);
        dotCheck = view.findViewById(R.id.dot_check);
        dotDrill = view.findViewById(R.id.dot_drill);

        lvUnit.addHeaderView(view);
        UnitAdapter adapter = new UnitAdapter(this);
        lvUnit.setAdapter(adapter);

    }



    /**
     * 设置监听
     */
    @Override
    public void initListener() {
        // 统计图
        rlUnit01.setOnClickListener(this);
        rlUnit02.setOnClickListener(this);
        // 统计图下面的TextView
        tvUnitFragMsg.setOnClickListener(this);
        tvUnitFragInvite.setOnClickListener(this);
        tvUnitFragAdd.setOnClickListener(this);
        // 安全提升级别的button
        btnStudy.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        btnDrill.setOnClickListener(this);

    }

    private boolean isLoading = false;// 页面已经加载过
    private boolean isSucceed = false;// 记录数据是否获取成功


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 每次界面可见的时候请求网络获取数据
        getDataInternet(isVisibleToUser);
    }


    private void getDataInternet(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            //每次请求网络之前（控件是在网络获取成功后动态添加上去的）先把上次的控件对象移除，否则会重复
            if (sesameCreditPanelLL != null) {
                sesameCreditPanelLL.removeView(scp);
            }

            //只有当该Fragment被用户可见的时候,才加载网络数据
            String businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
            // 获取网络数据
            String url = Urls.Url_Unit;
            HashMap<String, String> params = new HashMap<>();
            params.put("business_id", businessid);
            MyRequestResultListener requestResultListener = new MyRequestResultListener();
            NetUtil.requestData(url, params, UnitBean.class, requestResultListener, 0);
            isLoading = true;
        } else {
            //否则不加载网络数据
        }
    }


    @Override
    public void initData() {
    }

    class MyRequestResultListener implements RequestResultListener<UnitBean> {

        @Override
        public void onRequestFinish(UnitBean bean, int what) {
            list = new ArrayList<>();
            // 创建一个数组
            arrScores = new int[6];
            list.clear();
            if (bean != null) {
                // 表示获取到了真是的数据且不为空
                isSucceed = true;


                data = bean.data;
                // 获取到数据
                datatime = data.datatime;
                grade = data.grade;
                sumScore = Integer.parseInt(data.sum_score.replace(".00", ""));
                if (isLoading) {
                    // 动态的添加自定义控件，设置数据
                    scp = new SesameCreditPanel(getContext());
                    scp.setDataModel(getData(sumScore, grade, datatime));
                    scp.setEnabled(true);
                    scp.setClickable(true);
                    sesameCreditPanelLL.addView(scp);
                    isLoading = false;
                }


                subScore1 = data.sub_score1;
                subScore2 = data.sub_score2;
                subScore3 = data.sub_score3;
                subScore4 = data.sub_score4;
                subScore5 = data.sub_score5;
                subScore6 = data.sub_score6;
                list.add(0, subScore4);
                list.add(1, subScore5);
                list.add(2, subScore6);
                list.add(3, subScore1);
                list.add(4, subScore2);
                list.add(5, subScore3);
                // 遍历集合，把元素添加到数组里面
                for (int i = 0; i < list.size(); i++) {
                    arrScores[i] = Integer.parseInt(list.get(i)) * 7 / 100;
                }
                if (arrScores.length == 6) {
                    myPolygonView.setDataModel(getPolygonData());
                }
            } else {
                System.out.println("UnitFragment界面+++===没拿到bean对象");
            }
        }

        @Override
        public void showJsonStr(String str, int what) {
//            System.out.println("UnitFragment界面+++===" + str);
        }
    }


    @Override
    public void processClick(View v) {
        switch (v.getId()) {

            case R.id.ivTopBarleft_unit_menu:// 执行记录
                startActivity(new Intent(mActivity, UnitMenuActivity.class));
                break;

            case R.id.ivTopBarRight_unit_msg://消息中心
                startActivity(new Intent(mActivity, UnitMsgCenterActivity.class));
                break;

            case R.id.tv_unit_frag_02_msg: //信息完善
                startActivity(new Intent(mActivity, MsgPerfectActivity.class));
                break;
            case R.id.tv_unit_frag_02_invite://邀请加入
                ToastUtils.showLongToast(mActivity, "邀请加入");
                break;
            case R.id.tv_unit_frag_02_add:  //新增设备
                Intent intent = new Intent(mActivity, QrScanActivity.class);
                intent.putExtra(Keys.NEWDEVICE, "newDevice");
                startActivity(intent);
                break;
            case R.id.btn_study:// 学习
                startActivity(new Intent(mActivity, Unit2Study.class));
                break;
            case R.id.btn_check://检查
                Intent intentCheck = new Intent(mActivity, QrScanActivity.class);
                intentCheck.putExtra(Keys.CHECK, "Check");
                startActivity(intentCheck);
                break;
            case R.id.btn_drill: // 演练
                Intent intentExercise = new Intent(mActivity, QrScanActivity.class);
                intentExercise.putExtra(Keys.EXERCISE, "Exercise");
                startActivity(intentExercise);
                break;
            case R.id.rl_unit_01:
                rlUnit.setVisibility(View.GONE);
                rlUnit02.setVisibility(View.VISIBLE);
                tvUnitFragMsg.setVisibility(View.VISIBLE);
                //tvUnitFragInvite.setVisibility(View.VISIBLE);
                tvUnitFragAdd.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_unit_02:
                rlUnit.setVisibility(View.VISIBLE);
                rlUnit02.setVisibility(View.GONE);
                tvUnitFragMsg.setVisibility(View.GONE);
                tvUnitFragInvite.setVisibility(View.GONE);
                tvUnitFragAdd.setVisibility(View.GONE);
                break;
            default:
                break;
        }

    }

    /**
     * 弹出加载的动画
     */
    private void showDialog() {
        View view = View.inflate(mActivity, R.layout.loading_layout, null);
        View btnLoading = view.findViewById(R.id.btn_loading);
        ImageView ivLoading = (ImageView) view.findViewById(R.id.iv_loading);

        Animation operatingAnim = AnimationUtils.loadAnimation(mActivity, R.anim.progress_loading);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivLoading.startAnimation(operatingAnim);
        //ivLoading.clearAnimation();清除动画
        ShowLoadingDialog loadingDialog = new ShowLoadingDialog((Activity) mActivity, view, btnLoading);
        dialog = loadingDialog.show();
    }

    /**
     * 获取多边形的数据
     */
    private MyPolygonBean getPolygonData() {
        MyPolygonBean myPolygonBean = new MyPolygonBean();
        String[] arrText = new String[]{"参与人数", "行政审批", "设施设备", "知识水平", "管理行为", "疏散逃生"};
        myPolygonBean.setText(arrText);
        myPolygonBean.setArea(arrScores);
        return myPolygonBean;
    }

    /**
     * 获取圆形统计图的数据
     */
    private SesameModel getData(int userTotal, String level, String time) {

        SesameModel model = new SesameModel();
        model.setUserTotal(userTotal);
        model.setFirstText("安全级别 " + level);
        model.setFourText("评估时间:" + time);


        /**上面这些。。。。。。。。。。。。。。。。。。。。。。。*/


        // 区间最小值
        model.setTotalMin(0);
        // 区间最大值
        model.setTotalMax(100);
        ArrayList<SesameItemModel> sesameItemModels = new ArrayList<SesameItemModel>();
        // 添加级别
        SesameItemModel ItemModel0 = new SesameItemModel();
        //ItemModel0.setArea("较差");
        ItemModel0.setMin(0);
        ItemModel0.setMax(20);
        sesameItemModels.add(ItemModel0);

        SesameItemModel ItemModel60 = new SesameItemModel();
        //ItemModel60.setArea("中等");
        ItemModel60.setMin(20);
        ItemModel60.setMax(40);
        sesameItemModels.add(ItemModel60);

        SesameItemModel ItemModel70 = new SesameItemModel();
        //ItemModel70.setArea("良好");
        ItemModel70.setMin(40);
        ItemModel70.setMax(60);
        sesameItemModels.add(ItemModel70);

        SesameItemModel ItemModel80 = new SesameItemModel();
        //ItemModel80.setArea("较好");
        ItemModel80.setMin(60);
        ItemModel80.setMax(80);
        sesameItemModels.add(ItemModel80);

        SesameItemModel ItemModel90 = new SesameItemModel();
        //ItemModel90.setArea("优秀");
        ItemModel90.setMin(80);
        ItemModel90.setMax(100);
        sesameItemModels.add(ItemModel90);

        model.setSesameItemModels(sesameItemModels);
        return model;
    }


}
