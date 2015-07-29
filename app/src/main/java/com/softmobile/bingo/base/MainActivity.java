package com.softmobile.bingo.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
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
    ImageView ivCanvas;

    EditText etDialogMin;
    EditText etDialogMax;
    EditText etEtDialogNum;

    Bitmap bmBitmap;
    Paint pPaint;
    Canvas cCanvas;

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
        ivCanvas = (ImageView) findViewById(R.id.ivCanves);

        ivRandom.setOnClickListener(this);
        ivRangeEdit.setOnClickListener(this);
        ivMode.setOnClickListener(this);

        ivMode.setEnabled(false);
        ivMode.setImageResource(R.drawable.playoff);

        String strTxtID = null;
        int iRegID = -1;
        for (int i = 0; i < tvNumArray.length; i++) {
            //註冊9個TextView物件
            strTxtID = "tvNum" + i;
            iRegID = getResources().getIdentifier(strTxtID, "id", "com.softmobile.bingo.base");
            tvNumArray[i] = (TextView) findViewById(iRegID);
            tvNumArray[i].setOnClickListener(this);
        }

        //取得螢幕長寬
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        pPaint = new Paint(); //新增畫筆

        pPaint.setStrokeWidth(15);//筆寬
        pPaint.setColor(Color.parseColor("#FF7E74FF"));//筆色

        bmBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888); //設置點陣圖的寬高,bitmap為透明
        cCanvas = new Canvas(bmBitmap);
        cCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//設置為透明，畫布也是透明
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
                            censorAllTvEdit();
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
        int iModeRangeImage = -1;
        int iModeRandomImage = -1;
        if (bChangePlay == true) {
            iModeColor = R.color.onPause;
            iModeImage = R.drawable.play;
            iModeRangeImage = R.drawable.edit;
            iModeRandomImage = R.drawable.dice;
        } else {
            iModeColor = R.color.onPlay;
            iModeImage = R.drawable.pause;
            iModeRangeImage = R.drawable.editoff;
            iModeRandomImage = R.drawable.diceoff;
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
        ivRandom.setImageResource(iModeRandomImage);
        ivRangeEdit.setEnabled(bChangePlay);
        ivRangeEdit.setImageResource(iModeRangeImage);
        ivMode.setImageResource(iModeImage);
        bIsPlay = !bChangePlay;

        Paint p = new Paint();
        //清屏
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        cCanvas.drawPaint(p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    private void censorAllTvEdit() {
        int iTvTxt;
        int iMin = Integer.parseInt(strRangeMin);
        int iMax = Integer.parseInt(strRangeMax);
        for (int i = 0; i < tvNumArray.length; i++) {
            iTvTxt = Integer.parseInt(tvNumArray[i].getText().toString());
            if ((iTvTxt >= iMin && iTvTxt <= iMax) == false) {
                break;
            }
            if (i == tvNumArray.length - 1) {
                ivMode.setEnabled(true);
                ivMode.setImageResource(R.drawable.play);
            }
        }
    }

    private void censorLine() {
        iLine = 0;
        int iPoint;
        int iLineArray[] = new int[iWidth];
        //橫向連線數
        for (int i = 0; i < iWidth * iWidth; i += iWidth) {
            iPoint = 0;
            for (int j = i; j < i + iWidth; j++) {
                if (bIsLine[j] == false) {
                    continue;
                }
                //將連線TextView存入陣列
                iLineArray[iPoint] = j;
                iPoint++;
            }
            //如果點擊數=3
            if (iWidth == iPoint) {
                //畫線
                drawLine(iLineArray);
                iLine++;
            }
        }
        //直向連線數
        for (int i = 0; i < iWidth; i++) {
            iPoint = 0;
            for (int j = i; j < iWidth * iWidth; j += iWidth) {
                if (bIsLine[j] == false) {
                    continue;
                }
                //將連線TextView存入陣列
                iLineArray[iPoint] = j;
                iPoint++;
            }
            if (iWidth == iPoint) {
                drawLine(iLineArray);
                iLine++;
            }
        }
        //左上至右下
        iPoint = 0;
        for (int i = 0; i < iWidth * iWidth; i += (iWidth + 1)) {
            if (bIsLine[i] == false) {
                continue;
            }
            //將連線TextView存入陣列
            iLineArray[iPoint] = i;
            iPoint++;
            if (iWidth == iPoint) {
                drawLine(iLineArray);
                iLine++;
            }
        }
        //右上至左下
        iPoint = 0;
        for (int i = iWidth - 1; i <= (iWidth * iWidth) - iWidth; i += (iWidth - 1)) {
            if (bIsLine[i] == false) {
                continue;
            }
            //將連線TextView存入陣列
            iLineArray[iPoint] = i;
            iPoint++;
            if (iWidth == iPoint) {
                drawLine(iLineArray);
                iLine++;
            }
        }
        tvLine.setText(Integer.toString(iLine));
    }

    private void drawLine(int iDwLine[]) {
        int[] iStart = new int[2];
        int[] iEnd = new int[2];
        //取得起始和終止物件的座標
        tvNumArray[iDwLine[0]].getLocationOnScreen(iStart);
        tvNumArray[iDwLine[2]].getLocationOnScreen(iEnd);
        //計算座標
        int iStrartX = iStart[0] + (tvNumArray[iDwLine[0]].getMeasuredWidth() / 2);
        int iStrartY = iStart[1] + (tvNumArray[iDwLine[0]].getMeasuredHeight() / 2);
        int iEndX = iEnd[0] + (tvNumArray[iDwLine[2]].getMeasuredWidth() / 2);
        int iEndY = iEnd[1] + (tvNumArray[iDwLine[2]].getMeasuredHeight() /2);
        //畫線
        cCanvas.drawLine(iStrartX, iStrartY, iEndX, iEndY, pPaint);
        ivCanvas.setImageBitmap(bmBitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivRangeEdit:
                createRangeDialog();
                break;
            case R.id.ivRandom:
                createRandom(Integer.parseInt(strRangeMin), Integer.parseInt(strRangeMax));
                censorAllTvEdit();
                break;
            case R.id.ivMode:
                changeMode(bIsPlay);
                break;
        }

        for (int i = 0; i < tvNumArray.length; i++) {
            if (v.getId() == tvNumArray[i].getId()) {
                //判斷遊戲模式, 遊戲中為更改顏色及判斷連線, 非遊戲中為彈出輸入視窗
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
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }


}
