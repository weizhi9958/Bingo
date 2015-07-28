package com.softmobile.bingo.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener {

    String strRangeMin = null;
    String strRangeMax = null;

    int iWidth = 3;
    int iLine = 0;

    boolean bIsPlay = false;
    boolean bIsLine[] = {false, false, false,
            false, false, false,
            false, false, false,};

    TextView tvNowRange;
    TextView tvNumArray[] = new TextView[iWidth * iWidth];
    TextView tvLine;
    ImageView ivRangeEdit;
    ImageView ivRandom;
    ImageView ivMode;
    EditText etDialogMin;
    EditText etDialogMax;
    EditText etEtDialogNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        createRangeDialog();

    }

    private void initView() {
        tvNowRange = (TextView) findViewById(R.id.tvNowRange);
        tvLine = (TextView) findViewById(R.id.tvLine);
        ivRandom = (ImageView) findViewById(R.id.ivRandom);
        ivRangeEdit = (ImageView) findViewById(R.id.ivRangeEdit);
        ivMode = (ImageView) findViewById(R.id.ivMode);

        ivRandom.setOnClickListener(this);
        ivRangeEdit.setOnClickListener(this);
        ivMode.setOnClickListener(this);

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

    private void createEditNumDialog(final TextView tvDialogNum, final String strEtDialogMin, final String strEtDialogMax) {
        AlertDialog.Builder adbBuilder = new AlertDialog.Builder(MainActivity.this);
        final View viewDialog = View.inflate(MainActivity.this, R.layout.dialog_editnum, null);

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
                imm.showSoftInput(etEtDialogNum, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        alert.show();

        Button btnDialogEnter = alert.getButton(AlertDialog.BUTTON_POSITIVE);  //取得alert的button
        btnDialogEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(etEtDialogNum.getText().toString())) {
                    int iEtDialogMin = Integer.parseInt(strEtDialogMin);
                    int iEtDialogMax = Integer.parseInt(strEtDialogMax);
                    int iEtDialogNum = Integer.parseInt(etEtDialogNum.getText().toString());
                    //如果輸入值在範圍內
                    if (iEtDialogNum >= iEtDialogMin && iEtDialogNum <= iEtDialogMax) {
                        boolean bComp = false;
                        for (int i = 0; i < tvNumArray.length; i++) {
                            //非TextView陣列中的字
                            if (!Integer.toString(iEtDialogNum).equals(tvNumArray[i].getText().toString())) {
                                bComp = false;
                                //是否為傳入的數字
                            } else if (tvNumArray[i].getText().toString().equals(tvDialogNum.getText().toString())) {
                                bComp = false;
                            } else {
                                bComp = true;
                                break;
                            }
                        }

                        if (bComp != true) {
                            tvDialogNum.setText(etEtDialogNum.getText().toString());
                            alert.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.dialogToast_EditNumComp, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.dialogToast_EditNumError) + tvNowRange.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.dialogToast_NumNull, Toast.LENGTH_SHORT).show();
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

    private void changeMode(boolean bChangePlay) {
        int iModeColor = -1;
        int iModeImage = -1;
        if (bChangePlay == true) {
            iModeColor = R.color.onPause;
            iModeImage = R.drawable.play;
        } else {
            iModeColor = R.color.onPlay;
            iModeImage = R.drawable.pause;
        }
        for (int i = 0; i < tvNumArray.length; i++) {
            tvNumArray[i].setBackgroundResource(iModeColor);
        }
        for (int i = 0; i < bIsLine.length; i++) {
            bIsLine[i] = false;
        }
        iLine = 0;
        tvLine.setText("0");
        ivRandom.setEnabled(bChangePlay);
        ivRangeEdit.setEnabled(bChangePlay);
        ivMode.setImageResource(iModeImage);
        bIsPlay = !bChangePlay;
    }

    private void censorLine() {
        iLine = 0;
        int iStraight;
        for (int i = 0; i < iWidth * iWidth; i += iWidth) {
            iStraight = 0;
            for (int j = i; j < i + iWidth; j++) {
                if (bIsLine[j] == false) {
                    iStraight--;
                    continue;
                }
                iStraight++;
            }
            if (iWidth == iStraight) {
                iLine++;
            }
        }
        tvLine.setText(Integer.toString(iLine));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivRangeEdit:
                createRangeDialog();
                break;
            case R.id.ivRandom:
                createRandom(Integer.parseInt(strRangeMin), Integer.parseInt(strRangeMax));
                break;
            case R.id.ivMode:
                changeMode(bIsPlay);
                break;
        }

        for (int i = 0; i < tvNumArray.length; i++) {
            if (v.getId() == tvNumArray[i].getId()) {
                //判斷遊戲模式, 遊戲中為更改顏色, 非遊戲中為彈出輸入視窗
                if (bIsPlay == true) {
                    bIsLine[i] = true;
                    censorLine();
                    tvNumArray[i].setBackgroundResource(R.color.clickTextView);
                } else {
                    createEditNumDialog(tvNumArray[i], strRangeMin, strRangeMax);
                }
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
