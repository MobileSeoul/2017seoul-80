package com.easysubway;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import kr.go.seoul.trafficsubway.Common.BaseActivity;
import kr.go.seoul.trafficsubway.Common.CustomProgressDialog;
import kr.go.seoul.trafficsubway.Common.FirstLastTimeInfo;
import kr.go.seoul.trafficsubway.Common.FontUtils;
import kr.go.seoul.trafficsubway.Common.RealtimeStationArrivalInfo;
import kr.go.seoul.trafficsubway.Common.StationInfo;

/**
 * Created by eunhye Lee on 2017-08-08.
 */

public class TrafficSubwayInfo extends BaseActivity
{
    private int heightPx;
    private String openAPIKey = "";
    private String subwayLocationAPIKey = "";
    private ImageView btnBackSubway;
    private String stationNM = "";
    private TextView stationNm;
    private LinearLayout lineGroupLayout;
    private String selectedSubwayId = "";
    private ImageView btnRealTimeRefresh;
    private TextView prevStation;
    private TextView stopStation;
    private TextView nextStation;
    private TextView upLineArvl1Msg;
    private TextView upLineArvl2Msg;
    private TextView downLineArvl1Msg;
    private TextView downLineArvl2Msg;
    private TextView upLineArvl1MsgHeading;
    private TextView upLineArvl2MsgHeading;
    private TextView downLineArvl1MsgHeading;
    private TextView downLineArvl2MsgHeading;
    private RadioGroup intervalRadioGroup;
    private RadioButton intervalWeekday;
    private RadioButton intervalsat;
    private RadioButton intervalHoli;
    private LinearLayout upLineLast;
    private LinearLayout downLineFirst;
    private LinearLayout upLineFirst;
    private LinearLayout downLineLast;
    private String tagName = "";
    private String statnFnm;
    private String statnTnm;
    private HashMap<String, StationInfo> stationInfoHashMap = new HashMap();
    private String rowNum;
    private String trainLineNm;
    private String barvlDt;
    private String bstatnNm;
    private String arvlMsg2;
    private ArrayList<RealtimeStationArrivalInfo> realtimeStationArrivalInfoArrayList = new ArrayList();
    private String subwayId;
    private String subwayNm;
    private String lastcarDiv;
    private String updnLine;
    private String expressyn;
    private String subwayename;
    private String weekendTranHour;
    private String saturdayTranHour;
    private String holidayTranHour;
    private ArrayList<FirstLastTimeInfo> firstLastTimeInfoArrayList = new ArrayList();
    private ArrayList<String> lineArrayList = new ArrayList();
    private HashSet<String> lineHashSet = new HashSet();
    private CustomProgressDialog customProgressDialog;

    //ExitInfo
    private TextView exitinfo;

    public TrafficSubwayInfo() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(1);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.setContentView(R.layout.traffic_subway_info);
        this.heightPx = (int) TypedValue.applyDimension(1, 27.0F, this.getResources().getDisplayMetrics());
        if(this.getIntent() != null && this.getIntent().getStringExtra("OpenAPIKey") != null) {
            this.openAPIKey = this.getIntent().getStringExtra("OpenAPIKey");
        }

        if(this.getIntent() != null && this.getIntent().getStringExtra("SubwayLocationAPIKey") != null) {
            this.subwayLocationAPIKey = this.getIntent().getStringExtra("SubwayLocationAPIKey");
        }

        if(this.getIntent() != null && this.getIntent().getStringExtra("StationNM") != null) {
            this.stationNM = this.getIntent().getStringExtra("StationNM");
        }

        this.initView();
        this.showCustomProgressDialog();
        (new TrafficSubwayInfo.ProcessNetworkSubwayStationInfoThread()).execute(new String[]{this.stationNM});
        (new TrafficSubwayInfo.ProcessNetworkSubwayExitInfoThread()).execute(new String[]{this.stationNM});
    }

    private void showCustomProgressDialog() {
        if(this.customProgressDialog == null) {
            this.customProgressDialog = new CustomProgressDialog(this);
            this.customProgressDialog.setCancelable(false);
        }

        if(this.customProgressDialog != null) {
            this.customProgressDialog.show();
        }

    }

    private void cancelCustomProgressDialog() {
        if(this.customProgressDialog != null && this.customProgressDialog.isShowing()) {
            this.customProgressDialog.cancel();
        }

    }

    private void initView() {
        this.btnBackSubway = (ImageView)this.findViewById(R.id.btn_back_subway);
        this.btnBackSubway.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TrafficSubwayInfo.this.finish();
            }
        });
        this.stationNm = (TextView)this.findViewById(R.id.station_nm);
        this.stationNm.setText(this.stationNM);
        this.lineGroupLayout = (LinearLayout)this.findViewById(R.id.line_group_layout);
        this.btnRealTimeRefresh = (ImageView)this.findViewById(R.id.btn_real_time_refresh);
        this.btnRealTimeRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TrafficSubwayInfo.this.realtimeStationArrivalInfoArrayList.clear();
                TrafficSubwayInfo.this.showCustomProgressDialog();
                (TrafficSubwayInfo.this.new ProcessNetworkSubwayRealtimeStationArrivalInfoThread()).execute(new String[]{TrafficSubwayInfo.this.stationNM});
                (TrafficSubwayInfo.this.new ProcessNetworkSubwayExitInfoThread()).execute(new String[]{TrafficSubwayInfo.this.stationNM});
            }
        });
        this.prevStation = (TextView)this.findViewById(R.id.prev_station);
        this.stopStation = (TextView)this.findViewById(R.id.stop_station_nm);
        this.nextStation = (TextView)this.findViewById(R.id.next_station);
        this.stopStation.setText(this.stationNM);
        this.upLineArvl1Msg = (TextView)this.findViewById(R.id.up_line_arvl_1_msg);
        this.upLineArvl2Msg = (TextView)this.findViewById(R.id.up_line_arvl_2_msg);
        this.downLineArvl1Msg = (TextView)this.findViewById(R.id.down_line_arvl_1_msg);
        this.downLineArvl2Msg = (TextView)this.findViewById(R.id.down_line_arvl_2_msg);
        this.upLineArvl1MsgHeading = (TextView)this.findViewById(R.id.up_line_arvl_1_msg_heading);
        this.upLineArvl2MsgHeading = (TextView)this.findViewById(R.id.up_line_arvl_2_msg_heading);
        this.downLineArvl1MsgHeading = (TextView)this.findViewById(R.id.down_line_arvl_1_msg_heading);
        this.downLineArvl2MsgHeading = (TextView)this.findViewById(R.id.down_line_arvl_2_msg_heading);
        this.intervalRadioGroup = (RadioGroup)this.findViewById(R.id.interval_radio_group);
        this.intervalWeekday = (RadioButton)this.findViewById(R.id.interval_weekday);
        this.intervalsat = (RadioButton)this.findViewById(R.id.interval_sat);
        this.intervalHoli = (RadioButton)this.findViewById(R.id.interval_holi);
        this.intervalRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if(id == R.id.interval_weekday) {
                    TrafficSubwayInfo.this.setWeekData();
                } else if(id == R.id.interval_sat) {
                    TrafficSubwayInfo.this.setSatData();
                } else if(id == R.id.interval_holi) {
                    TrafficSubwayInfo.this.setHoliData();
                }

            }
        });
        this.upLineFirst = (LinearLayout)this.findViewById(R.id.up_line_first);
        this.upLineLast = (LinearLayout)this.findViewById(R.id.up_line_last);
        this.downLineFirst = (LinearLayout)this.findViewById(R.id.down_line_first);
        this.downLineLast = (LinearLayout)this.findViewById(R.id.down_line_last);

        this.exitinfo = (TextView)this.findViewById(R.id.exitInfo);
    }

    private void setLineIcon() {
        if(this.lineGroupLayout.getChildCount() == 0) {
            LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService("layout_inflater");
            int drawableId = 0;
            if(this.lineArrayList.size() > 0) {
                Collections.sort(this.lineArrayList, new Comparator() {
                    @Override
                    public int compare(Object o, Object t1) {
                        return 0;
                    }

                    public int compare(String s, String t1) {
                        return s.compareToIgnoreCase(t1);
                    }
                });
            }

            for(int i = 0; i < this.lineArrayList.size(); ++i) {
                LinearLayout lineIconLayout = (LinearLayout)layoutInflater.inflate(R.layout.traffic_subway_info_type_b_line_icon, (ViewGroup)null);
                switch(Integer.parseInt((String)this.lineArrayList.get(i))) {
                    case 1001:
                        drawableId = R.drawable.selector_trafficsub_radio_1;
                        break;
                    case 1002:
                        drawableId = R.drawable.selector_trafficsub_radio_2;
                        break;
                    case 1003:
                        drawableId = R.drawable.selector_trafficsub_radio_3;
                        break;
                    case 1004:
                        drawableId = R.drawable.selector_trafficsub_radio_4;
                        break;
                    case 1005:
                        drawableId = R.drawable.selector_trafficsub_radio_5;
                        break;
                    case 1006:
                        drawableId = R.drawable.selector_trafficsub_radio_6;
                        break;
                    case 1007:
                        drawableId = R.drawable.selector_trafficsub_radio_7;
                        break;
                    case 1008:
                        drawableId = R.drawable.selector_trafficsub_radio_8;
                        break;
                    case 1009:
                        drawableId = R.drawable.selector_trafficsub_radio_9;
                        break;
                    case 1063:
                        drawableId = R.drawable.selector_trafficsub_radio_kyeong;
                        break;
                    case 1065:
                        drawableId = R.drawable.selector_trafficsub_radio_konghang;
                        break;
                    case 1067:
                        drawableId = R.drawable.selector_trafficsub_radio_kyeongchun;
                        break;
                    case 1069:
                        drawableId = R.drawable.selector_trafficsub_radio_incheon;
                        break;
                    case 1071:
                        drawableId = R.drawable.selector_trafficsub_radio_sooin;
                        break;
                    case 1075:
                        drawableId = R.drawable.selector_trafficsub_radio_bundang;
                        break;
                    case 1077:
                        drawableId = R.drawable.selector_trafficsub_radio_sinbundang;
                }

                lineIconLayout.setId(Integer.parseInt((String)this.lineArrayList.get(i)));
                if(Build.VERSION.SDK_INT >= 23) {
                    ((ImageView)lineIconLayout.findViewById(R.id.line_icon)).setBackground(this.getResources().getDrawable(drawableId, (Resources.Theme)null));
                } else if(Build.VERSION.SDK_INT >= 16) {
                    ((ImageView)lineIconLayout.findViewById(R.id.line_icon)).setBackground(this.getResources().getDrawable(drawableId));
                } else {
                    ((ImageView)lineIconLayout.findViewById(R.id.line_icon)).setBackgroundResource(drawableId);
                }

                lineIconLayout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if(TrafficSubwayInfo.this.lineGroupLayout.getChildCount() > 0) {
                            for(int i = 0; i < TrafficSubwayInfo.this.lineGroupLayout.getChildCount(); ++i) {
                                TrafficSubwayInfo.this.lineGroupLayout.getChildAt(i).setSelected(false);
                                TrafficSubwayInfo.this.lineGroupLayout.getChildAt(0).findViewById(R.id.line_icon).setSelected(false);
                            }

                            view.setSelected(true);
                            view.findViewById(R.id.line_icon).setSelected(true);
                            TrafficSubwayInfo.this.selectedSubwayId = String.valueOf(view.getId());
                            if(TrafficSubwayInfo.this.intervalWeekday.isChecked()) {
                                TrafficSubwayInfo.this.setWeekData();
                            } else {
                                TrafficSubwayInfo.this.intervalWeekday.setChecked(true);
                            }

                            TrafficSubwayInfo.this.setRealtimeArrival();

                        }

                    }
                });
                this.lineGroupLayout.addView(lineIconLayout);
            }

            if(this.lineGroupLayout.getChildCount() > 0) {
                this.lineGroupLayout.getChildAt(0).setSelected(true);
                this.lineGroupLayout.getChildAt(0).findViewById(R.id.line_icon).setSelected(true);
                this.selectedSubwayId = String.valueOf(this.lineGroupLayout.getChildAt(0).getId());
                if(this.intervalWeekday.isChecked()) {
                    this.setWeekData();
                } else {
                    this.intervalWeekday.setChecked(true);
                }

                this.setRealtimeArrival();
            }
        } else {
            this.setRealtimeArrival();
        }

    }

    private void setRealtimeArrival() {
        if(this.stationInfoHashMap.size() > 0) {
            this.prevStation.setText(((StationInfo)this.stationInfoHashMap.get(this.selectedSubwayId)).getStatnFnm());
            this.nextStation.setText(((StationInfo)this.stationInfoHashMap.get(this.selectedSubwayId)).getStatnTnm());
            ((TextView)this.findViewById(R.id.up_line_heading)).setText(((StationInfo)this.stationInfoHashMap.get(this.selectedSubwayId)).getStatnFnm() + " 방향");
            ((TextView)this.findViewById(R.id.down_line_heading)).setText(((StationInfo)this.stationInfoHashMap.get(this.selectedSubwayId)).getStatnTnm() + " 방향");
            ((TextView)this.findViewById(R.id.up_line_heading_1)).setText(((StationInfo)this.stationInfoHashMap.get(this.selectedSubwayId)).getStatnFnm() + " 방향");
            ((TextView)this.findViewById(R.id.down_line_heading_1)).setText(((StationInfo)this.stationInfoHashMap.get(this.selectedSubwayId)).getStatnTnm() + " 방향");
        }

        this.upLineArvl1MsgHeading.setText("도착정보가 없습니다.");
        this.upLineArvl1Msg.setText("-");
        this.upLineArvl2MsgHeading.setText("도착정보가 없습니다.");
        this.upLineArvl2Msg.setText("-");
        this.downLineArvl1MsgHeading.setText("도착정보가 없습니다.");
        this.downLineArvl1Msg.setText("-");
        this.downLineArvl2MsgHeading.setText("도착정보가 없습니다.");
        this.downLineArvl2Msg.setText("-");
        int upLineNo = 0;
        int downLineNo = 0;
        if(this.realtimeStationArrivalInfoArrayList.size() > 0) {
            Collections.sort(this.realtimeStationArrivalInfoArrayList, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    return 0;
                }

                public int compare(RealtimeStationArrivalInfo s, RealtimeStationArrivalInfo t1) {
                    return Integer.parseInt(s.getBarvlDt()) < Integer.parseInt(t1.getBarvlDt())?-1:(Integer.parseInt(s.getBarvlDt()) > Integer.parseInt(t1.getBarvlDt())?1:0);
                }
            });
        }

        for(int i = 0; i < this.realtimeStationArrivalInfoArrayList.size(); ++i) {
            if(((RealtimeStationArrivalInfo)this.realtimeStationArrivalInfoArrayList.get(i)).getSubwayId().equals(this.selectedSubwayId)) {
                String arrivalMsg;
                if(!((RealtimeStationArrivalInfo)this.realtimeStationArrivalInfoArrayList.get(i)).getUpdnLine().equals("상행") && !((RealtimeStationArrivalInfo)this.realtimeStationArrivalInfoArrayList.get(i)).getUpdnLine().equals("외선")) {
                    if(downLineNo < 2) {
                        arrivalMsg = ((RealtimeStationArrivalInfo)this.realtimeStationArrivalInfoArrayList.get(i)).getArvlMsg2().replaceAll("\\[", "").replaceAll("\\]", "");
                        arrivalMsg = arrivalMsg.split("\\(")[0];
                        if(!arrivalMsg.contains("도착") && !arrivalMsg.contains("출발") && !arrivalMsg.contains("진입")) {
                            arrivalMsg = arrivalMsg.concat("도착");
                        }

                        if(downLineNo == 0) {
                            this.downLineArvl1MsgHeading.setText(((RealtimeStationArrivalInfo)this.realtimeStationArrivalInfoArrayList.get(i)).getBstatnNm() + "행");
                            this.downLineArvl1Msg.setText(arrivalMsg);
                            ++downLineNo;
                        } else {
                            this.downLineArvl2MsgHeading.setText(((RealtimeStationArrivalInfo)this.realtimeStationArrivalInfoArrayList.get(i)).getBstatnNm() + "행");
                            this.downLineArvl2Msg.setText(arrivalMsg);
                            ++downLineNo;
                        }
                    }
                } else if(upLineNo < 2) {
                    arrivalMsg = ((RealtimeStationArrivalInfo)this.realtimeStationArrivalInfoArrayList.get(i)).getArvlMsg2().replaceAll("\\[", "").replaceAll("\\]", "");
                    arrivalMsg = arrivalMsg.split("\\(")[0];
                    if(!arrivalMsg.contains("도착") && !arrivalMsg.contains("출발") && !arrivalMsg.contains("진입")) {
                        arrivalMsg = arrivalMsg.concat("도착");
                    }

                    if(upLineNo == 0) {
                        this.upLineArvl1MsgHeading.setText(((RealtimeStationArrivalInfo)this.realtimeStationArrivalInfoArrayList.get(i)).getBstatnNm() + "행");
                        this.upLineArvl1Msg.setText(arrivalMsg);
                        ++upLineNo;
                    } else {
                        this.upLineArvl2MsgHeading.setText(((RealtimeStationArrivalInfo)this.realtimeStationArrivalInfoArrayList.get(i)).getBstatnNm() + "행");
                        this.upLineArvl2Msg.setText(arrivalMsg);
                        ++upLineNo;
                    }
                }
            }
        }

    }

    private void setWeekData() {
        this.upLineFirst.removeAllViews();
        this.upLineLast.removeAllViews();
        this.downLineFirst.removeAllViews();
        this.downLineLast.removeAllViews();
        if(this.firstLastTimeInfoArrayList.size() > 0) {
            Collections.sort(this.firstLastTimeInfoArrayList, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    return 0;
                }

                public int compare(FirstLastTimeInfo s, FirstLastTimeInfo t1) {
                    return s.getWeekendTranHour().compareToIgnoreCase(t1.getWeekendTranHour());
                }
            });
        }

        for(int i = 0; i < this.firstLastTimeInfoArrayList.size(); ++i) {
            if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayId().equals(this.selectedSubwayId) && !((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getWeekendTranHour().equals("-")) {
                TextView time;
                if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getUpdnLine().equals("1")) {
                    time = new TextView(this);
                    time.setHeight(this.heightPx);
                    time.setGravity(17);
                    time.setTextSize(2, 12.0F);
                    time.setTextColor(Color.parseColor("#000000"));
                    if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getLastcarDiv().equals("1")) {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getWeekendTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.upLineFirst.addView(time);
                    } else {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getWeekendTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.upLineLast.addView(time);
                    }
                } else {
                    time = new TextView(this);
                    time.setHeight(this.heightPx);
                    time.setGravity(17);
                    time.setTextSize(2, 12.0F);
                    time.setTextColor(Color.parseColor("#000000"));
                    if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getLastcarDiv().equals("1")) {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getWeekendTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.downLineFirst.addView(time);
                    } else {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getWeekendTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.downLineLast.addView(time);
                    }
                }
            }
        }

        FontUtils.getInstance(this).setGlobalFont(this.upLineFirst);
        FontUtils.getInstance(this).setGlobalFont(this.upLineLast);
        FontUtils.getInstance(this).setGlobalFont(this.downLineFirst);
        FontUtils.getInstance(this).setGlobalFont(this.downLineLast);
    }

    private void setSatData() {
        this.upLineFirst.removeAllViews();
        this.upLineLast.removeAllViews();
        this.downLineFirst.removeAllViews();
        this.downLineLast.removeAllViews();
        if(this.firstLastTimeInfoArrayList.size() > 0) {
            Collections.sort(this.firstLastTimeInfoArrayList, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    return 0;
                }

                public int compare(FirstLastTimeInfo s, FirstLastTimeInfo t1) {
                    return s.getSaturdayTranHour().compareToIgnoreCase(t1.getSaturdayTranHour());
                }
            });
        }

        for(int i = 0; i < this.firstLastTimeInfoArrayList.size(); ++i) {
            if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayId().equals(this.selectedSubwayId) && !((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSaturdayTranHour().equals("-")) {
                TextView time;
                if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getUpdnLine().equals("1")) {
                    time = new TextView(this);
                    time.setHeight(this.heightPx);
                    time.setGravity(17);
                    time.setTextSize(2, 12.0F);
                    time.setTextColor(Color.parseColor("#000000"));
                    if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getLastcarDiv().equals("1")) {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSaturdayTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.upLineFirst.addView(time);
                    } else {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSaturdayTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.upLineLast.addView(time);
                    }
                } else {
                    time = new TextView(this);
                    time.setHeight(this.heightPx);
                    time.setGravity(17);
                    time.setTextSize(2, 12.0F);
                    time.setTextColor(Color.parseColor("#000000"));
                    if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getLastcarDiv().equals("1")) {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSaturdayTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.downLineFirst.addView(time);
                    } else {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSaturdayTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.downLineLast.addView(time);
                    }
                }
            }
        }

        FontUtils.getInstance(this).setGlobalFont(this.upLineFirst);
        FontUtils.getInstance(this).setGlobalFont(this.upLineLast);
        FontUtils.getInstance(this).setGlobalFont(this.downLineFirst);
        FontUtils.getInstance(this).setGlobalFont(this.downLineLast);
    }

    private void setHoliData() {
        this.upLineFirst.removeAllViews();
        this.upLineLast.removeAllViews();
        this.downLineFirst.removeAllViews();
        this.downLineLast.removeAllViews();
        if(this.firstLastTimeInfoArrayList.size() > 0) {
            Collections.sort(this.firstLastTimeInfoArrayList, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    return 0;
                }

                public int compare(FirstLastTimeInfo s, FirstLastTimeInfo t1) {
                    return s.getHolidayTranHour().compareToIgnoreCase(t1.getHolidayTranHour());
                }
            });
        }

        for(int i = 0; i < this.firstLastTimeInfoArrayList.size(); ++i) {
            if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayId().equals(this.selectedSubwayId) && !((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getHolidayTranHour().equals("-")) {
                TextView time;
                if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getUpdnLine().equals("1")) {
                    time = new TextView(this);
                    time.setHeight(this.heightPx);
                    time.setGravity(17);
                    time.setTextSize(2, 12.0F);
                    time.setTextColor(Color.parseColor("#000000"));
                    if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getLastcarDiv().equals("1")) {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getHolidayTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.upLineFirst.addView(time);
                    } else {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getHolidayTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.upLineLast.addView(time);
                    }
                } else {
                    time = new TextView(this);
                    time.setHeight(this.heightPx);
                    time.setGravity(17);
                    time.setTextSize(2, 12.0F);
                    time.setTextColor(Color.parseColor("#000000"));
                    if(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getLastcarDiv().equals("1")) {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getHolidayTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.downLineFirst.addView(time);
                    } else {
                        time.setText(((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getHolidayTranHour() + " " + ((FirstLastTimeInfo)this.firstLastTimeInfoArrayList.get(i)).getSubwayename());
                        this.downLineLast.addView(time);
                    }
                }
            }
        }

        FontUtils.getInstance(this).setGlobalFont(this.upLineFirst);
        FontUtils.getInstance(this).setGlobalFont(this.upLineLast);
        FontUtils.getInstance(this).setGlobalFont(this.downLineFirst);
        FontUtils.getInstance(this).setGlobalFont(this.downLineLast);
    }

    //exitinfo
    class ProcessNetworkSubwayExitInfoThread extends AsyncTask<String, Void, String> {

        URL apiURL = null;

        String subwayId = "";
        String ectrcNo ="";
        String cfrBuild="";


        public ProcessNetworkSubwayExitInfoThread() {
        }

        protected String doInBackground(String... strings) {
            this.executeClient(strings);
            return "";
        }

        protected void onPostExecute(String result) {
            exitinfo.setText(cfrBuild);
        }

        void executeClient(String[] strings) {

            InputStream in = null;
            XmlPullParserFactory factory;
            XmlPullParser xpp;

            try
            {
                apiURL = new URL("http://swopenapi.seoul.go.kr/api/subway/" + TrafficSubwayInfo.this.openAPIKey + "/xml/gateInfo/0/20/"+strings[0]);
                in = apiURL.openStream();
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(in, "UTF-8");
                int eventType1 = xpp.getEventType();
                String tagName = "";
                String updnLine = "";

                for (boolean isItemTag = false; eventType1 != 1; eventType1 = xpp.next()) {
                    if (eventType1 == 2) {
                        tagName = xpp.getName();
                        if (tagName.equals("row")) {
                            isItemTag = true;
                        }
                    } else if (eventType1 == 4) {
                        if (isItemTag && !tagName.equals("") && !xpp.getText().equals("")) {
                            if (tagName.equals("subwayId")) {
                                subwayId = xpp.getText();
                            } else if (tagName.equals("subwayNm")) {
                                cfrBuild += xpp.getText()+") : ";
                            } else if (tagName.equals("ectrcNo")) {
                                cfrBuild += "\n"+xpp.getText()+"번 출구(";
                            } else if(tagName.equals("cfrBuild")) {
                                cfrBuild += xpp.getText() + "\n";
                            } else if(tagName.equals("updnLine")) {
                                updnLine = xpp.getText();
                            }
                        }
                    } else if (eventType1 == 3) {
                        tagName = xpp.getName();
                        if (tagName.equals("row")) {
                            isItemTag = false;
                            if (subwayId.equals("1065")) {
                                if (updnLine.equals("0")) {
                                    updnLine = "1";
                                } else {
                                    updnLine = "0";
                                }
                            }
                        } else {
                            tagName = "";
                        }
                    }
                }
            } catch (MalformedURLException var22) {
                var22.printStackTrace();
            } catch (IOException var23) {
                var23.printStackTrace();
            } catch (XmlPullParserException var24) {
                var24.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException var21) {
                        var21.printStackTrace();
                    }
                }

            }

        }
    }

    public class ProcessNetworkSubwayStationInfoThread extends AsyncTask<String, Void, String> {
        public ProcessNetworkSubwayStationInfoThread() {
        }

        protected String doInBackground(String... strings) {
            this.executeClient(strings);
            return "";
        }

        protected void onPostExecute(String result) {
            (TrafficSubwayInfo.this.new ProcessNetworkSubwayFirstLastTimeInfoThread()).execute(new String[]{TrafficSubwayInfo.this.stationNM});
        }

        public void executeClient(String[] strings) {
            URL apiURL = null;
            InputStream in = null;
            XmlPullParserFactory factory = null;
            XmlPullParser xpp = null;
            boolean eventType = true;

            try {
                String e = strings[0];
                if(e.equals("서울역")) {
                    e = "서울";
                }

                apiURL = new URL("http://swopenapi.seoul.go.kr/api/subway/" + TrafficSubwayInfo.this.openAPIKey + "/xml/stationInfo/1/999/" + URLEncoder.encode(e));
                in = apiURL.openStream();
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(in, "UTF-8");
                int eventType1 = xpp.getEventType();

                for(boolean isItemTag = false; eventType1 != 1; eventType1 = xpp.next()) {
                    if(eventType1 == 2) {
                        TrafficSubwayInfo.this.tagName = xpp.getName();
                        if(TrafficSubwayInfo.this.tagName.equals("row")) {
                            isItemTag = true;
                        }
                    } else if(eventType1 == 4) {
                        if(isItemTag && !TrafficSubwayInfo.this.tagName.equals("") && !xpp.getText().equals("")) {
                            if(TrafficSubwayInfo.this.tagName.equals("subwayId")) {
                                TrafficSubwayInfo.this.subwayId = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("statnFnm")) {
                                TrafficSubwayInfo.this.statnFnm = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("statnTnm")) {
                                TrafficSubwayInfo.this.statnTnm = xpp.getText();
                            }
                        }
                    } else if(eventType1 == 3) {
                        TrafficSubwayInfo.this.tagName = xpp.getName();
                        if(!TrafficSubwayInfo.this.tagName.equals("row")) {
                            TrafficSubwayInfo.this.tagName = "";
                        } else {
                            isItemTag = false;
                            StationInfo info;
                            if(!TrafficSubwayInfo.this.subwayId.equals("1002") && !TrafficSubwayInfo.this.subwayId.equals("1065")) {
                                info = new StationInfo(TrafficSubwayInfo.this.subwayId, TrafficSubwayInfo.this.statnFnm, TrafficSubwayInfo.this.statnTnm);
                            } else {
                                info = new StationInfo(TrafficSubwayInfo.this.subwayId, TrafficSubwayInfo.this.statnTnm, TrafficSubwayInfo.this.statnFnm);
                            }

                            TrafficSubwayInfo.this.stationInfoHashMap.put(info.getSubwayId(), info);
                            TrafficSubwayInfo.this.lineArrayList.add(TrafficSubwayInfo.this.subwayId);
                            info = null;
                        }
                    }
                }
            } catch (MalformedURLException var22) {
                var22.printStackTrace();
            } catch (IOException var23) {
                var23.printStackTrace();
            } catch (XmlPullParserException var24) {
                var24.printStackTrace();
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException var21) {
                        var21.printStackTrace();
                    }
                }

            }

        }
    }

    public class ProcessNetworkSubwayFirstLastTimeInfoThread extends AsyncTask<String, Void, String> {
        public ProcessNetworkSubwayFirstLastTimeInfoThread() {
        }

        protected String doInBackground(String... strings) {
            this.executeClient(strings);
            return "";
        }

        protected void onPostExecute(String result) {
            (TrafficSubwayInfo.this.new ProcessNetworkSubwayRealtimeStationArrivalInfoThread()).execute(new String[]{TrafficSubwayInfo.this.stationNM});
        }

        public void executeClient(String[] strings) {
            URL apiURL = null;
            InputStream in = null;
            XmlPullParserFactory factory = null;
            XmlPullParser xpp = null;
            boolean eventType = true;

            try {
                String e = strings[0];
                if(e.equals("서울역")) {
                    e = "서울";
                }

                apiURL = new URL("http://swopenapi.seoul.go.kr/api/subway/" + TrafficSubwayInfo.this.openAPIKey + "/xml/firstLastTimetable/1/999/" + URLEncoder.encode(e));
                in = apiURL.openStream();
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(in, "UTF-8");
                int eventType1 = xpp.getEventType();

                for(boolean isItemTag = false; eventType1 != 1; eventType1 = xpp.next()) {
                    if(eventType1 == 2) {
                        TrafficSubwayInfo.this.tagName = xpp.getName();
                        if(TrafficSubwayInfo.this.tagName.equals("row")) {
                            isItemTag = true;
                        }
                    } else if(eventType1 == 4) {
                        if(isItemTag && !TrafficSubwayInfo.this.tagName.equals("") && !xpp.getText().equals("")) {
                            if(TrafficSubwayInfo.this.tagName.equals("subwayId")) {
                                TrafficSubwayInfo.this.subwayId = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("subwayNm")) {
                                TrafficSubwayInfo.this.subwayNm = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("lastcarDiv")) {
                                TrafficSubwayInfo.this.lastcarDiv = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("updnLine")) {
                                TrafficSubwayInfo.this.updnLine = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("expressyn")) {
                                TrafficSubwayInfo.this.expressyn = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("subwayename")) {
                                TrafficSubwayInfo.this.subwayename = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("weekendTranHour")) {
                                TrafficSubwayInfo.this.weekendTranHour = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("saturdayTranHour")) {
                                TrafficSubwayInfo.this.saturdayTranHour = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("holidayTranHour")) {
                                TrafficSubwayInfo.this.holidayTranHour = xpp.getText();
                            }
                        }
                    } else if(eventType1 == 3) {
                        TrafficSubwayInfo.this.tagName = xpp.getName();
                        if(TrafficSubwayInfo.this.tagName.equals("row")) {
                            isItemTag = false;
                            if(TrafficSubwayInfo.this.subwayId.equals("1065")) {
                                if(TrafficSubwayInfo.this.updnLine.equals("0")) {
                                    TrafficSubwayInfo.this.updnLine = "1";
                                } else {
                                    TrafficSubwayInfo.this.updnLine = "0";
                                }
                            }

                            FirstLastTimeInfo info = new FirstLastTimeInfo(TrafficSubwayInfo.this.subwayId, TrafficSubwayInfo.this.subwayNm, TrafficSubwayInfo.this.lastcarDiv, TrafficSubwayInfo.this.updnLine, TrafficSubwayInfo.this.expressyn, TrafficSubwayInfo.this.subwayename, TrafficSubwayInfo.this.weekendTranHour, TrafficSubwayInfo.this.saturdayTranHour, TrafficSubwayInfo.this.holidayTranHour);
                            TrafficSubwayInfo.this.firstLastTimeInfoArrayList.add(info);
                            TrafficSubwayInfo.this.lineArrayList.add(TrafficSubwayInfo.this.subwayId);
                            info = null;
                        } else {
                            TrafficSubwayInfo.this.tagName = "";
                        }
                    }
                }
            } catch (MalformedURLException var22) {
                var22.printStackTrace();
            } catch (IOException var23) {
                var23.printStackTrace();
            } catch (XmlPullParserException var24) {
                var24.printStackTrace();
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException var21) {
                        var21.printStackTrace();
                    }
                }

            }

        }
    }

    public class ProcessNetworkSubwayRealtimeStationArrivalInfoThread extends AsyncTask<String, Void, String> {
        public ProcessNetworkSubwayRealtimeStationArrivalInfoThread() {
        }

        protected String doInBackground(String... strings) {
            this.executeClient(strings);
            return "";
        }

        protected void onPostExecute(String result) {
            TrafficSubwayInfo.this.lineHashSet.addAll(TrafficSubwayInfo.this.lineArrayList);
            TrafficSubwayInfo.this.lineArrayList.clear();
            TrafficSubwayInfo.this.lineArrayList.addAll(TrafficSubwayInfo.this.lineHashSet);
            TrafficSubwayInfo.this.setLineIcon();
            TrafficSubwayInfo.this.cancelCustomProgressDialog();
        }

        public void executeClient(String[] strings) {
            URL apiURL = null;
            InputStream in = null;
            XmlPullParserFactory factory = null;
            XmlPullParser xpp = null;
            boolean eventType = true;

            try {
                String e = strings[0];
                if(e.equals("서울역")) {
                    e = "서울";
                }

                if(e.equals("신촌(경의중앙선)")) {
                    e = "신촌(경의.중앙선)";
                }

                if(e.equals("천호")) {
                    e = "천호(풍납토성)";
                }

                if(e.equals("굽은다리")) {
                    e = "굽은다리(강동구민회관앞)";
                }

                if(e.equals("몽촌토성")) {
                    e = "몽촌토성(평화의문)";
                }

                if(TrafficSubwayInfo.this.subwayLocationAPIKey != null && !TrafficSubwayInfo.this.subwayLocationAPIKey.equals("")) {
                    apiURL = new URL("http://swopenapi.seoul.go.kr/api/subway/" + TrafficSubwayInfo.this.subwayLocationAPIKey + "/xml/realtimeStationArrival/1/999/" + URLEncoder.encode(e));
                } else {
                    apiURL = new URL("http://swopenapi.seoul.go.kr/api/subway/" + TrafficSubwayInfo.this.openAPIKey + "/xml/realtimeStationArrival/1/999/" + URLEncoder.encode(e));
                }

                in = apiURL.openStream();
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(in, "UTF-8");
                int eventType1 = xpp.getEventType();

                for(boolean isItemTag = false; eventType1 != 1; eventType1 = xpp.next()) {
                    if(eventType1 == 2) {
                        TrafficSubwayInfo.this.tagName = xpp.getName();
                        if(TrafficSubwayInfo.this.tagName.equals("row")) {
                            isItemTag = true;
                        }
                    } else if(eventType1 == 4) {
                        if(isItemTag && !TrafficSubwayInfo.this.tagName.equals("") && !xpp.getText().equals("")) {
                            if(TrafficSubwayInfo.this.tagName.equals("rowNum")) {
                                TrafficSubwayInfo.this.rowNum = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("subwayId")) {
                                TrafficSubwayInfo.this.subwayId = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("updnLine")) {
                                TrafficSubwayInfo.this.updnLine = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("trainLineNm")) {
                                TrafficSubwayInfo.this.trainLineNm = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("barvlDt")) {
                                TrafficSubwayInfo.this.barvlDt = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("bstatnNm")) {
                                TrafficSubwayInfo.this.bstatnNm = xpp.getText();
                            } else if(TrafficSubwayInfo.this.tagName.equals("arvlMsg2")) {
                                TrafficSubwayInfo.this.arvlMsg2 = xpp.getText();
                            }
                        }
                    } else if(eventType1 == 3) {
                        TrafficSubwayInfo.this.tagName = xpp.getName();
                        if(TrafficSubwayInfo.this.tagName.equals("row")) {
                            isItemTag = false;
                            RealtimeStationArrivalInfo info = new RealtimeStationArrivalInfo(TrafficSubwayInfo.this.rowNum, TrafficSubwayInfo.this.subwayId, TrafficSubwayInfo.this.updnLine, TrafficSubwayInfo.this.trainLineNm, TrafficSubwayInfo.this.barvlDt, TrafficSubwayInfo.this.bstatnNm, TrafficSubwayInfo.this.arvlMsg2);
                            TrafficSubwayInfo.this.realtimeStationArrivalInfoArrayList.add(info);
                            if(TrafficSubwayInfo.this.lineArrayList.size() == 0) {
                                TrafficSubwayInfo.this.lineArrayList.add(TrafficSubwayInfo.this.subwayId);
                            }

                            info = null;
                        } else {
                            TrafficSubwayInfo.this.tagName = "";
                        }
                    }
                }
            } catch (MalformedURLException var22) {
                var22.printStackTrace();
            } catch (IOException var23) {
                var23.printStackTrace();
            } catch (XmlPullParserException var24) {
                var24.printStackTrace();
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException var21) {
                        var21.printStackTrace();
                    }
                }

            }

        }
    }
}

