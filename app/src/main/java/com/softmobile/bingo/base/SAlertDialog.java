package com.softmobile.bingo.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SAlertDialog extends AlertDialog {

    MainActivity maActivity = null;
    AlertDialog.Builder adDialogOld;

    int m_iOldWidth;

    public SAlertDialog(Context context) {
        super(context);
        maActivity = (MainActivity) context;
        adDialogOld = new AlertDialog.Builder(context);
    }

    public void showLoadDialog(final View viewLayout){
        adDialogOld.setView(viewLayout); //設置view來源為R.layout.dialog_range
        adDialogOld.setCancelable(false);
        adDialogOld.setPositiveButton(R.string.bingoLoad_Yes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                maActivity.loadSetting();
            }
        });
        adDialogOld.setNegativeButton(R.string.bingoLoad_No, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                View viewDialog = View.inflate(maActivity, R.layout.dialog_range, null);
                showSettingDialog(viewDialog);
            }
        });
        adDialogOld.create().show();
    }

    public void showSettingDialog(View viewLayout){
        adDialogOld.setView(viewLayout);
        adDialogOld.setCancelable(false);
        adDialogOld.setNegativeButton(null, null);
        adDialogOld.setPositiveButton(R.string.enter, null);

        //取得dialog_range layout的兩個EditText
        maActivity.etDialogMin     = (EditText) viewLayout.findViewById(R.id.etRangeMin);
        maActivity.etDialogMax     = (EditText) viewLayout.findViewById(R.id.etRangeMax);
        maActivity.etDialogWinLine = (EditText) viewLayout.findViewById(R.id.etWinLine);
        maActivity.etDialogWidth   = (EditText) viewLayout.findViewById(R.id.etWidth);

        //如果最小值變數有值時，將最大及最小置入dialog_range layout的EditText
        if (0 != maActivity.m_iWidth) {
            maActivity.etDialogMin.setText(maActivity.m_strRangeMin);
            maActivity.etDialogMax.setText(maActivity.m_strRangeMax);
            maActivity.etDialogWinLine.setText(maActivity.m_strWinLine);
            maActivity.etDialogWidth.setText(Integer.toString(maActivity.m_iWidth));

            m_iOldWidth = maActivity.m_iWidth;
        }

        final AlertDialog adDialogNew = adDialogOld.create(); //取代原本的adDialogOld
        //adDialogNew彈出時觸發事件
        adDialogNew.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //顯示輸入鍵盤
                InputMethodManager imm = (InputMethodManager) maActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(maActivity.etDialogWidth, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        adDialogNew.show();

        Button btnDialogEnter = adDialogNew.getButton(AlertDialog.BUTTON_POSITIVE);  //取得Dialog的button
        //按鈕Click事件
        btnDialogEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果皆有輸入值存入string變數 , 否則toast
                if (false == "".equals(maActivity.etDialogMin.getText().toString())&&
                        false == "".equals(maActivity.etDialogMax.getText().toString()) &&
                        false == "".equals(maActivity.etDialogWidth.getText().toString()) &&
                        false == "".equals(maActivity.etDialogWinLine.getText().toString())) {

                    //判斷最大值最小值 , 將較小值存入Min , 較大值存入Max
                    if (Integer.parseInt(maActivity.etDialogMax.getText().toString()) >
                            Integer.parseInt(maActivity.etDialogMin.getText().toString())) {

                        maActivity.m_strRangeMin = maActivity.etDialogMin.getText().toString();
                        maActivity.m_strRangeMax = maActivity.etDialogMax.getText().toString();
                    } else {
                        maActivity.m_strRangeMin = maActivity.etDialogMax.getText().toString();
                        maActivity.m_strRangeMax = maActivity.etDialogMin.getText().toString();
                    }

                    maActivity.m_iRangeMin = Integer.parseInt(maActivity.m_strRangeMin);
                    maActivity.m_iRangeMax = Integer.parseInt(maActivity.m_strRangeMax);
                    maActivity.m_iWidth = Integer.parseInt(maActivity.etDialogWidth.getText().toString());

                    //寬度是否大於等於2
                    if(Integer.parseInt(maActivity.etDialogWidth.getText().toString()) >= 2){

                        //範圍數是否超過總格數 , 或大於 0
                        if (maActivity.m_iRangeMax - maActivity.m_iRangeMin >= maActivity.m_iWidth * maActivity.m_iWidth - 1 &&
                            maActivity.m_iRangeMin > 0) {

                            int iSetWinLine = maActivity.MAX_LINE + ((maActivity.m_iWidth - 3) * 2);
                            //獲勝條件是否在範圍內
                            if (Integer.parseInt(maActivity.etDialogWinLine.getText().toString()) > 0 &&
                                    Integer.parseInt(maActivity.etDialogWinLine.getText().toString()) <= iSetWinLine) {

                                maActivity.m_strWinLine = maActivity.etDialogWinLine.getText().toString();
                                maActivity.m_iWinLine = Integer.parseInt(maActivity.m_strWinLine);
                                //將範圍顯示至tvNoewRange , 並關閉dialog
                                maActivity.tvNowRange.setText(maActivity.m_strRangeMin + " ~ " + maActivity.m_strRangeMax);
                                maActivity.tvAimsLine.setText(maActivity.m_strWinLine);
                                //如寬度為目前寬度就不再產生格子
                                if(false == (m_iOldWidth == maActivity.m_iWidth) || m_iOldWidth == 0){
                                    maActivity.initGirdView();
                                }

                                maActivity.censorAllTvEdit();
                                adDialogNew.dismiss();
                            } else {
                                String strToast = Integer.toString(maActivity.m_iWidth) + "x" + Integer.toString(maActivity.m_iWidth);
                                strToast += maActivity.getString(R.string.dialogToast_WinLine);
                                strToast += Integer.toString(iSetWinLine);
                                Toast.makeText(maActivity, strToast, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(maActivity, maActivity.getString(R.string.dialogToast_lengthError) + Integer.toString(maActivity.m_iWidth * maActivity.m_iWidth), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(maActivity, maActivity.getString(R.string.dialogToast_Width),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(maActivity, R.string.dialogToast_Null, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showEditGridDialog(View viewLayout,final TextView tvDialogNum, final String strEtDialogMin, final String strEtDialogMax){
        adDialogOld.setView(viewLayout);
        adDialogOld.setCancelable(true);
        adDialogOld.setNegativeButton(null,null);
        adDialogOld.setPositiveButton(R.string.enter, null);

        //取得dialog_editnum layout的EditText , 並給目前TextView的值
        maActivity.etEtDialogNum = (EditText) viewLayout.findViewById(R.id.etNum);
        maActivity.etEtDialogNum.setText(tvDialogNum.getText().toString());

        final AlertDialog alert = adDialogOld.create(); //取代原本的adDialogOld

        //adDialogNew彈出時觸發事件
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //顯示輸入鍵盤
                InputMethodManager imm = (InputMethodManager) maActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(maActivity.etEtDialogNum, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        alert.show();

        Button btnDialogEnter = alert.getButton(AlertDialog.BUTTON_POSITIVE);  //取得Dialog的button
        //按鈕Click事件
        btnDialogEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判斷是否有輸入
                if (false == "".equals(maActivity.etEtDialogNum.getText().toString())) {
                    int iEtDialogMin = Integer.parseInt(strEtDialogMin); //字串Min變數轉成數字
                    int iEtDialogMax = Integer.parseInt(strEtDialogMax); //字串Max變數轉成數字
                    int iEtDialogNum = Integer.parseInt(maActivity.etEtDialogNum.getText().toString()); //Dialog中的EditText存入int變數
                    //如果輸入值在範圍內
                    if (iEtDialogNum >= iEtDialogMin && iEtDialogNum <= iEtDialogMax) {
                        boolean bComp = false;
                        //與每個TextView Num做判斷
                        for (int i = 0; i < maActivity.tvNumArray.length; i++) {
                            //非TextView陣列中的數字 或 為傳入的數字
                            if (false == Integer.toString(iEtDialogNum).equals(maActivity.tvNumArray[i].getText().toString()) ||
                                    maActivity.tvNumArray[i].getText().toString().equals(tvDialogNum.getText().toString())) {
                                bComp = false;
                            } else {
                                bComp = true;
                                break; // 其中一迴圈不符合就跳出
                            }
                        }

                        //如果比較後皆為false
                        if (false == bComp) {
                            tvDialogNum.setText(maActivity.etEtDialogNum.getText().toString());
                            alert.dismiss();
                            maActivity.censorAllTvEdit(); //判斷play是否可按
                        } else {
                            Toast.makeText(maActivity, R.string.dialogToast_EditNumComp, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(maActivity, maActivity.getString(R.string.dialogToast_EditNumError) + maActivity.tvNowRange.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(maActivity, R.string.dialogToast_NumNull, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void showWinDialog(View viewLayout){
        adDialogOld.setView(viewLayout); //設置view來源為R.layout.dialog_range
        adDialogOld.setCancelable(false);
        adDialogOld.setNegativeButton(null,null);
        adDialogOld.setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                maActivity.changeMode(maActivity.m_bIsPlay);
            }
        });
        adDialogOld.create().show();
    }

}
