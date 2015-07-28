package com.softmobile.bingo.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Target;


public class MainActivity extends Activity implements View.OnClickListener {

    TextView tvNowRange;
    TextView tvNumArray[] = new TextView[9];
    Button btnRandom;
    ImageView ivRangeEdit;
    EditText etDialogMin;
    EditText etDialogMax;
    EditText etEtDialogNum;

    String strRangeMin = null;
    String strRangeMax = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        createRangeDialog();

    }

    private void initView() {
        tvNowRange = (TextView) findViewById(R.id.tvNowRange);
        btnRandom = (Button) findViewById(R.id.btnRandom);
        ivRangeEdit = (ImageView) findViewById(R.id.ivRangeEdit);

        btnRandom.setOnClickListener(this);
        ivRangeEdit.setOnClickListener(this);

        String strTxtID = null;
        int iRegID = -1;
        for (int i = 0; i < tvNumArray.length; i++) {
            //註冊9個TextView物件
            strTxtID = "tvNum" + i;
            iRegID = getResources().getIdentifier(strTxtID, "id", "com.softmobile.bingo.base");
            tvNumArray[i] = (TextView) findViewById(iRegID);
            tvNumArray[i].setOnClickListener(this);
        }
    }

    private void createRangeDialog() {
        AlertDialog.Builder adbBuilder = new AlertDialog.Builder(MainActivity.this);
        final View viewDialog = View.inflate(MainActivity.this, R.layout.dialog_range, null);

        adbBuilder.setView(viewDialog); //設置view來源為R.layout.dialog_range
        adbBuilder.setCancelable(false);
        adbBuilder.setPositiveButton(R.string.enter, null);

        //取得alertdialog的兩個edittext
        etDialogMin = (EditText) viewDialog.findViewById(R.id.etRangeMin);
        etDialogMax = (EditText) viewDialog.findViewById(R.id.etRangeMax);

        if (!"".equals(strRangeMin)) {
            etDialogMin.setText(strRangeMin);
            etDialogMax.setText(strRangeMax);
        }

        final AlertDialog alert = adbBuilder.create(); //取代原本的adbBuilder
        //alert彈出時自動彈出鍵盤
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etDialogMin, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        alert.show();

        Button btnDialogEnter = alert.getButton(AlertDialog.BUTTON_POSITIVE);  //取得alert的button
        btnDialogEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果皆有輸入值存入string變數 , 否則toast
                if (!"".equals(etDialogMin.getText().toString()) && !"".equals(etDialogMax.getText().toString())) {

                    //判斷最大值最小值 , 由小到大
                    if (Integer.parseInt(etDialogMax.getText().toString()) > Integer.parseInt(etDialogMin.getText().toString())) {
                        strRangeMin = etDialogMin.getText().toString();
                        strRangeMax = etDialogMax.getText().toString();
                    } else {
                        strRangeMin = etDialogMax.getText().toString();
                        strRangeMax = etDialogMin.getText().toString();
                    }

                    //判斷範圍數是否超過9
                    if (Integer.parseInt(strRangeMax) - Integer.parseInt(strRangeMin) >= tvNumArray.length - 1) {
                        //將範圍顯示至tvNoewRange , 並關閉alert
                        tvNowRange.setText(strRangeMin + " ~ " + strRangeMax);
                        alert.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.dialogToast_lengthError, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.dialogToast_Null, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createEditNumDialog(TextView tvDialogNum, final String strEtDialogMin, final String strEtDialogMax) {
        AlertDialog.Builder adbBuilder = new AlertDialog.Builder(MainActivity.this);
        final View viewDialog = View.inflate(MainActivity.this, R.layout.dialog_editNum, null);

        adbBuilder.setView(viewDialog); //設置view來源為R.layout.dialog_range
        adbBuilder.setCancelable(false);
        adbBuilder.setPositiveButton(R.string.enter, null);

        //取得alertdialog的兩個edittext
        etEtDialogNum = (EditText) viewDialog.findViewById(R.id.etNum);
        etEtDialogNum.setText(tvDialogNum.getText().toString());
        final AlertDialog alert = adbBuilder.create(); //取代原本的adbBuilder
        //alert彈出時自動彈出鍵盤
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etDialogMin, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        alert.show();

        Button btnDialogEnter = alert.getButton(AlertDialog.BUTTON_POSITIVE);  //取得alert的button
        btnDialogEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int iEtDialogMin = Integer.parseInt(strEtDialogMin);
                int iEtDialogMax = Integer.parseInt(strEtDialogMax);
                int iEtDialogNum = Integer.parseInt(etEtDialogNum.getText().toString());
                //如果皆有輸入值存入string變數 , 否則toast
                if (!"".equals(etDialogMin.getText().toString()) && !"".equals(etDialogMax.getText().toString())) {


                } else {
                    Toast.makeText(MainActivity.this, R.string.dialogToast_Null, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createRandom(int iRMin, int iRMax) {
        //產生範圍陣列
        int iRangeArray[] = new int[(iRMax - iRMin) + 1];
        int iCount = 0;

        //將範圍內數字依序存入陣列
        for (int i = iRMin; i <= iRMax; i++) {
            iRangeArray[iCount] = i;
            iCount++;
        }

        //洗牌法打亂陣列
        for (int i = 0; i < iRangeArray.length; i++) {
            int iN1 = (int) (Math.random() * iRangeArray.length);
            int iN2 = (int) (Math.random() * iRangeArray.length);

            int iTemp = iRangeArray[iN1];
            iRangeArray[iN1] = iRangeArray[iN2];
            iRangeArray[iN2] = iTemp;
        }

        //將陣列前9個存入TextView
        for (int i = 0; i < tvNumArray.length; i++) {
            tvNumArray[i].setText(Integer.toString(iRangeArray[i]));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivRangeEdit:
                createRangeDialog();
                break;
            case R.id.btnRandom:
                createRandom(Integer.parseInt(strRangeMin), Integer.parseInt(strRangeMax));
                break;
        }

        for (int i = 0; i < tvNumArray.length; i++) {

            if (v.getId() == tvNumArray[i].getId()) {
                createEditNumDialog(tvNumArray[0], strRangeMin, strRangeMax);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
