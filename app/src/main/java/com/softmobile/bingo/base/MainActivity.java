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
import android.view.ViewTreeObserver;
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
    String strWinLine  = null;

    int iWidth = 3;
    int iLine = 0;
    int iTxtViewWidth = 0;

    boolean bIsPlay = false;
    boolean bIsLine[] = {false, false, false,
            false, false, false,
            false, false, false,};

    TextView tvNowRange;
    TextView tvNumArray[] = new TextView[iWidth * iWidth];
    TextView tvLine;
    TextView tvAimsLine;

    ImageView ivRangeEdit;
    ImageView ivRandom;
    ImageView ivMode;
    ImageView ivCanvas;

    EditText etDialogMin;
    EditText etDialogMax;
    EditText etDialogWinLine;
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
                    bIsLine[i] = !bIsLine[i];
                    censorLine(i);
                } else {
                    createEditNumDialog(tvNumArray[i], strRangeMin, strRangeMax);
                }
            }
        }
    }

    private void initView() {
        tvNowRange  = (TextView) findViewById(R.id.tvNowRange);
        tvLine      = (TextView) findViewById(R.id.tvLine);
        tvAimsLine  = (TextView) findViewById(R.id.tvAimsLine);
        ivRandom    = (ImageView) findViewById(R.id.ivRandom);
        ivRangeEdit = (ImageView) findViewById(R.id.ivRangeEdit);
        ivMode      = (ImageView) findViewById(R.id.ivMode);
        ivCanvas    = (ImageView) findViewById(R.id.ivCanves);

        ivRandom.setOnClickListener(this);
        ivRangeEdit.setOnClickListener(this);
        ivMode.setOnClickListener(this);

        ivMode.setEnabled(false);
        ivMode.setImageResource(R.drawable.playoff);

        String strTxtID = null;
        int iRegID = -1;

        //註冊9個TextView物件
        for (int i = 0; i < tvNumArray.length; i++) {
            strTxtID = "tvNum" + i;
            iRegID = getResources().getIdentifier(strTxtID, "id", "com.softmobile.bingo.base");
            tvNumArray[i] = (TextView) findViewById(iRegID);
            tvNumArray[i].setOnClickListener(this);
           // tvNumArray[i].getLayoutParams().height = tvNumArray[i].getMeasuredWidth();
        }

        //在onCreate下取得物件長寬之方法
        ViewTreeObserver vtoView = tvNumArray[0].getViewTreeObserver();
        vtoView.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tvNumArray[0].getViewTreeObserver().removeGlobalOnLayoutListener(this);
                for(int i = 0; i < tvNumArray.length; i++){
                    tvNumArray[i].setHeight(tvNumArray[0].getWidth());
                }
            }
        });

        //取得螢幕長寬
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        pPaint = new Paint(); //新增畫筆

        pPaint.setStrokeWidth(15);//筆寬
        pPaint.setColor(Color.parseColor("#FFFA281C"));//筆色

        bmBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888); //設置點陣圖的寬高,bitmap為透明
        cCanvas = new Canvas(bmBitmap);
        cCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//設置為透明，畫布也是透明
    }

    private void createRangeDialog() {
        AlertDialog.Builder adDialogOld = new AlertDialog.Builder(MainActivity.this);
        final View viewDialog = View.inflate(MainActivity.this, R.layout.dialog_range, null);

        adDialogOld.setView(viewDialog); //設置view來源為R.layout.dialog_range
        adDialogOld.setCancelable(false);
        adDialogOld.setPositiveButton(R.string.enter, null);

        //取得dialog_range layout的兩個EditText
        etDialogMin     = (EditText) viewDialog.findViewById(R.id.etRangeMin);
        etDialogMax     = (EditText) viewDialog.findViewById(R.id.etRangeMax);
        etDialogWinLine = (EditText) viewDialog.findViewById(R.id.etWinLine);

        //如果最小值變數有值時，將最大及最小置入dialog_range layout的EditText
        if (!"".equals(strRangeMin)) {
            etDialogMin.setText(strRangeMin);
            etDialogMax.setText(strRangeMax);
            etDialogWinLine.setText(strWinLine);
        }

        final AlertDialog adDialogNew = adDialogOld.create(); //取代原本的adDialogOld
        //adDialogNew彈出時觸發事件
        adDialogNew.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //顯示輸入鍵盤
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etDialogMin, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        adDialogNew.show();

        Button btnDialogEnter = adDialogNew.getButton(AlertDialog.BUTTON_POSITIVE);  //取得Dialog的button
        //按鈕Click事件
        btnDialogEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果皆有輸入值存入string變數 , 否則toast
                if (!"".equals(etDialogMin.getText().toString()) &&
                        !"".equals(etDialogMax.getText().toString()) &&
                        !"".equals(etDialogWinLine.getText().toString())) {

                    //判斷最大值最小值 , 將較小值存入Min , 較大值存入Max
                    if (Integer.parseInt(etDialogMax.getText().toString()) >
                            Integer.parseInt(etDialogMin.getText().toString())) {

                        strRangeMin = etDialogMin.getText().toString();
                        strRangeMax = etDialogMax.getText().toString();
                    } else {
                        strRangeMin = etDialogMax.getText().toString();
                        strRangeMax = etDialogMin.getText().toString();
                    }

                    //範圍數是否超過9
                    if (Integer.parseInt(strRangeMax) - Integer.parseInt(strRangeMin) >= tvNumArray.length - 1) {

                        //獲勝條件是否在1~8
                        if (Integer.parseInt(etDialogWinLine.getText().toString()) > 0 &&
                                Integer.parseInt(etDialogWinLine.getText().toString()) <= 8) {

                            strWinLine = etDialogWinLine.getText().toString();
                            //將範圍顯示至tvNoewRange , 並關閉dialog
                            tvNowRange.setText(strRangeMin + " ~ " + strRangeMax);
                            tvAimsLine.setText(strWinLine);
                            adDialogNew.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.dialogToast_WinLine, Toast.LENGTH_SHORT).show();
                        }

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
        AlertDialog.Builder adDialogOld = new AlertDialog.Builder(MainActivity.this);
        final View viewDialog = View.inflate(MainActivity.this, R.layout.dialog_editnum, null);

        adDialogOld.setView(viewDialog); //設置view來源為R.layout.dialog_range
        adDialogOld.setCancelable(false);
        adDialogOld.setPositiveButton(R.string.enter, null);

        //取得dialog_editnum layout的EditText , 並給目前TextView的值
        etEtDialogNum = (EditText) viewDialog.findViewById(R.id.etNum);
        etEtDialogNum.setText(tvDialogNum.getText().toString());

        final AlertDialog alert = adDialogOld.create(); //取代原本的adDialogOld

        //adDialogNew彈出時觸發事件
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //顯示輸入鍵盤
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etEtDialogNum, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        alert.show();

        Button btnDialogEnter = alert.getButton(AlertDialog.BUTTON_POSITIVE);  //取得Dialog的button
        //按鈕Click事件
        btnDialogEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判斷是否有輸入
                if (!"".equals(etEtDialogNum.getText().toString())) {
                    int iEtDialogMin = Integer.parseInt(strEtDialogMin); //字串Min變數轉成數字
                    int iEtDialogMax = Integer.parseInt(strEtDialogMax); //字串Max變數轉成數字
                    int iEtDialogNum = Integer.parseInt(etEtDialogNum.getText().toString()); //Dialog中的EditText存入int變數
                    //如果輸入值在範圍內
                    if (iEtDialogNum >= iEtDialogMin && iEtDialogNum <= iEtDialogMax) {
                        boolean bComp = false;
                        //與每個TextView Num做判斷
                        for (int i = 0; i < tvNumArray.length; i++) {
                            //非TextView陣列中的數字 或 為傳入的數字
                            if (!Integer.toString(iEtDialogNum).equals(tvNumArray[i].getText().toString()) ||
                                tvNumArray[i].getText().toString().equals(tvDialogNum.getText().toString())) {
                                bComp = false;
                            } else {
                                bComp = true;
                                break; // 其中一迴圈不符合就跳出
                            }
                        }

                        //如果比較後皆為false
                        if (bComp == false) {
                            tvDialogNum.setText(etEtDialogNum.getText().toString());
                            alert.dismiss();
                            censorAllTvEdit(); //判斷play是否可按
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

    private void createWinDialog() {
        AlertDialog.Builder adDialogOld = new AlertDialog.Builder(MainActivity.this);
        final View viewDialog = View.inflate(MainActivity.this, R.layout.dialog_win, null);

        adDialogOld.setView(viewDialog); //設置view來源為R.layout.dialog_range
        adDialogOld.setCancelable(false);
        adDialogOld.setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeMode(bIsPlay);
            }
        });
        adDialogOld.create().show();
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

    private void changeMode(boolean bEditMode) {
        int iModeColor = -1;
        int iModeImage = -1;
        int iModeRangeImage = -1;
        int iModeRandomImage = -1;
        //判斷是否編輯中
        if (bEditMode == true) {
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
            tvNumArray[i].setBackgroundResource(iModeColor); //將所有TextView變色
            bIsLine[i] = false; //所有是否點擊變數回預設值
        }

        iLine = 0; // 連線數回到 0
        tvLine.setText("0");
        ivRangeEdit.setEnabled(bEditMode);  //編輯按鈕是否啟用
        ivRangeEdit.setImageResource(iModeRangeImage); //編輯按鈕圖片
        ivRandom.setEnabled(bEditMode); //隨機按鈕是否啟用
        ivRandom.setImageResource(iModeRandomImage); //隨機按鈕圖片
        ivMode.setImageResource(iModeImage); //模式按鈕圖片
        bIsPlay = !bEditMode; //模式轉變後儲存

        //清除畫板
        pPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        cCanvas.drawPaint(pPaint);
        pPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    private void censorAllTvEdit() {
        int iTvTxt;
        int iMin = Integer.parseInt(strRangeMin);
        int iMax = Integer.parseInt(strRangeMax);

        //判斷每個TextView值是否都在範圍內 , 全部皆是啟用Play按鈕
        for (int i = 0; i < tvNumArray.length; i++) {
            iTvTxt = Integer.parseInt(tvNumArray[i].getText().toString());
            if ((iTvTxt >= iMin && iTvTxt <= iMax) == false) {
                break;
            }
            //如果 i 有執行到最後一次
            if (i == tvNumArray.length - 1) {
                ivMode.setEnabled(true);
                ivMode.setImageResource(R.drawable.play);
            }
        }
    }

    private void censorLine(int iView) {
        iLine = 0;
        int iPoint;
        int iLineArray[] = new int[iWidth];


        //清除畫板
        pPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        cCanvas.drawPaint(pPaint);
        pPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        //判斷目前點擊狀態給予顏色
        if(bIsLine[iView] == true){
            tvNumArray[iView].setBackgroundResource(R.color.clickTextView);
        }else{
            tvNumArray[iView].setBackgroundResource(R.color.onPlay);
        }

        //橫向連線數
        for (int i = 0; i < iWidth * iWidth; i += iWidth) {
            iPoint = 0;
            for (int j = i; j < i + iWidth; j++) {
                if (bIsLine[j] == false) {
                    continue;
                }
                iLineArray[iPoint] = j; //將點擊到的TextView存入陣列
                iPoint++; //點擊數+1
            }
            //如果點擊數=3
            if (iWidth == iPoint) {
                drawLine(iLineArray); //畫線
                iLine++; //連線數+1
            }
        }
        //直向連線數
        for (int i = 0; i < iWidth; i++) {
            iPoint = 0;
            for (int j = i; j < iWidth * iWidth; j += iWidth) {
                if (bIsLine[j] == false) {
                    continue;
                }
                iLineArray[iPoint] = j; //將點擊到的TextView存入陣列
                iPoint++; //點擊數+1
            }
            //如果點擊數=3
            if (iWidth == iPoint) {
                drawLine(iLineArray); //畫線
                iLine++; //連線數+1
            }
        }
        //左上至右下
        iPoint = 0;
        for (int i = 0; i < iWidth * iWidth; i += (iWidth + 1)) {
            if (bIsLine[i] == false) {
                continue;
            }
            iLineArray[iPoint] = i; //將點擊到的TextView存入陣列
            iPoint++; //點擊數+1
            //如果點擊數=3
            if (iWidth == iPoint) {
                drawLine(iLineArray); //畫線
                iLine++; //連線數+1
            }
        }
        //右上至左下
        iPoint = 0;
        for (int i = iWidth - 1; i <= (iWidth * iWidth) - iWidth; i += (iWidth - 1)) {
            if (bIsLine[i] == false) {
                continue;
            }
            iLineArray[iPoint] = i; //將點擊到的TextView存入陣列
            iPoint++; //點擊數+1
            //如果點擊數=3
            if (iWidth == iPoint) {
                drawLine(iLineArray); //畫線
                iLine++; //連線數+1
            }
        }

        tvLine.setText(Integer.toString(iLine));

        if(iLine >= Integer.parseInt(strWinLine)){
            createWinDialog();
        }
    }

    private void drawLine(int iDwLine[]) {
        int[] iStart = new int[2];
        int[] iEnd = new int[2];
        //取得起始和終止物件的座標
        tvNumArray[iDwLine[0]].getLocationOnScreen(iStart);
        tvNumArray[iDwLine[2]].getLocationOnScreen(iEnd);
        //計算座標
        int iStrartX = iStart[0] + (tvNumArray[iDwLine[0]].getWidth() / 2);
        int iStrartY = iStart[1] + (tvNumArray[iDwLine[0]].getHeight() / 2);
        int iEndX = iEnd[0] + (tvNumArray[iDwLine[2]].getWidth() / 2);
        int iEndY = iEnd[1] + (tvNumArray[iDwLine[2]].getHeight() / 2);
        Log.d("xx",Integer.toString(tvNumArray[iDwLine[0]].getWidth()));
        //畫線
        cCanvas.drawLine(iStrartX, iStrartY, iEndX, iEndY, pPaint);
        ivCanvas.setImageBitmap(bmBitmap);
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
