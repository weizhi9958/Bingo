package com.softmobile.bingo.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG_BINGO = "bingo";
    private static final String TAG_MIN = "min";
    private static final String TAG_MAX = "max";
    private static final String TAG_WIDTH = "width";
    private static final String TAG_NOWLINE = "nowline";
    private static final String TAG_WINLINE = "winline";
    private static final String TAG_GRID = "grid_";
    private static final String TAG_GAMEMODE = "gamemode";
    private static final String TAG_GRIDLINE = "gridline_";

    public String m_strRangeMin = null;
    public String m_strRangeMax = null;
    public String m_strWinLine  = null;

    final int MAX_LINE = 8;
    int m_iRangeMin = 0;
    int m_iRangeMax = 0;
    int m_iWidth = 0;
    int m_iLine = 0;
    int m_iWinLine = 0;

    boolean m_bIsPlay = false;
    boolean m_bIsLine[];

    SAlertDialog saDialog;
    View viewDialog;

    TableLayout tbLayout;
    GridView gvBingoMain;

    TextView tvNowRange;
    TextView tvNumArray[];
    TextView tvLine;
    TextView tvAimsLine;

    ImageView ivRangeEdit;
    ImageView ivRandom;
    ImageView ivMode;
    ImageView ivCanvas;

    EditText etDialogMin;
    EditText etDialogMax;
    EditText etDialogWinLine;
    EditText etDialogWidth;
    EditText etEtDialogNum;

    Bitmap bmBitmap;
    Paint pPaint;
    Canvas cCanvas;

    SharedPreferences settings;
    private Handler hdHandler;
    private Runnable raRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        settings = getSharedPreferences(TAG_BINGO, 0);
        if(0 == settings.getInt(TAG_MIN, 0)){
            viewDialog = View.inflate(MainActivity.this, R.layout.dialog_range, null);
            saDialog.showSettingDialog(viewDialog);
        } else {
            viewDialog = View.inflate(MainActivity.this, R.layout.dialog_load, null);
            saDialog.showLoadDialog(viewDialog);
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        if(0 != m_iWidth) {
            settings = getSharedPreferences(TAG_BINGO, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.putInt(TAG_MIN, m_iRangeMin)
                    .putInt(TAG_MAX, m_iRangeMax)
                    .putInt(TAG_WIDTH, m_iWidth)
                    .putInt(TAG_NOWLINE, m_iLine)
                    .putInt(TAG_WINLINE, m_iWinLine)
                    .putBoolean(TAG_GAMEMODE, m_bIsPlay);

            for (int i = 0; i < tvNumArray.length; i++) {
                editor.putString(TAG_GRID + i, tvNumArray[i].getText().toString());
                editor.putBoolean(TAG_GRIDLINE + i, m_bIsLine[i]);
            }
            editor.commit();
        }

        if(null != hdHandler){
            hdHandler.removeCallbacks(raRun);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivRangeEdit:
                viewDialog = View.inflate(MainActivity.this, R.layout.dialog_range, null);
                saDialog.showSettingDialog(viewDialog);
                break;
            case R.id.ivRandom:
                SRandom rnd = new SRandom(m_iRangeMin, m_iRangeMax); //new個SRandom物件並帶入最大及最小值
                int[] iArray = rnd.getRandom(m_iWidth * m_iWidth); //呼叫getRandom方法帶入最大長度取得回傳陣列
                for(int i = 0; i < iArray.length; i++){
                    tvNumArray[i].setText(Integer.toString(iArray[i]));
                }
                censorAllTvEdit();
                break;
            case R.id.ivMode:
                changeMode(m_bIsPlay);
                break;
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
        tbLayout    = (TableLayout) findViewById(R.id.tbLayout);
        gvBingoMain = (GridView) findViewById(R.id.gvBingoMain);

        ivRandom.setOnClickListener(this);
        ivRangeEdit.setOnClickListener(this);
        ivMode.setOnClickListener(this);

        ivMode.setEnabled(false);
        ivMode.setImageResource(R.drawable.playoff);

        saDialog = new SAlertDialog(this);


        //取得螢幕長寬
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        pPaint = new Paint(); //新增畫筆

        pPaint.setStrokeWidth(15);//筆寬
        pPaint.setColor(getResources().getColor(R.color.paint));//筆色

        bmBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888); //設置點陣圖的寬高,bitmap為透明
        cCanvas = new Canvas(bmBitmap);
        cCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//設置為透明，畫布也是透明
    }

    public void initGirdView(){
        m_bIsLine = new boolean[m_iWidth * m_iWidth];
        tvNumArray = null;
        tvNumArray = new TextView[m_iWidth * m_iWidth];
        int iCount = 0;

        tbLayout.removeAllViews();

        TableRow.LayoutParams view_layout = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view_layout.setMargins(10, 10, 10, 10);

        for(int i = 0; i < m_iWidth; i++){
            TableRow tbRow = new TableRow(MainActivity.this);
            for(int j = 0; j < m_iWidth; j++){
                tvNumArray[iCount] = null;
                tvNumArray[iCount] = new TextView(MainActivity.this);
                tvNumArray[iCount].setId(iCount);
                tvNumArray[iCount].setTag(iCount);
                tvNumArray[iCount].setBackgroundResource(R.color.onPause);
                tvNumArray[iCount].setGravity(Gravity.CENTER);
                tvNumArray[iCount].setTextColor(Color.WHITE);
                tvNumArray[iCount].setText("0");
                tvNumArray[iCount].setOnClickListener(new SBingoTvListener());
                tvNumArray[iCount].setLayoutParams(view_layout);
                tbRow.addView(tvNumArray[iCount]);
                m_bIsLine[iCount] = false;
                iCount++;
            }
            tbLayout.addView(tbRow);
        }

        //設定每個TextView的高度等於寬度
        //onCreate執行結束後再設定高度
        ViewTreeObserver vtoView = tvNumArray[0].getViewTreeObserver();
        vtoView.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //解決removeGlobalOnLayoutListener再SDK版本16以上被棄用問題
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvNumArray[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                else {
                    tvNumArray[0].getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                for (int i = 0; i < tvNumArray.length; i++) {
                    tvNumArray[i].setHeight(tvNumArray[0].getWidth());
                    tvNumArray[i].setTextSize((tvNumArray[0].getWidth()) / 9);
                }
            }
        });

    }

    public void loadSetting(){
        m_iRangeMin   = settings.getInt(TAG_MIN, 0);
        m_iRangeMax   = settings.getInt(TAG_MAX, 0);
        m_iWidth      = settings.getInt(TAG_WIDTH, 0);
        m_iLine       = settings.getInt(TAG_NOWLINE, 0);
        m_iWinLine    = settings.getInt(TAG_WINLINE, 0);
        m_strRangeMin = Integer.toString(m_iRangeMin);
        m_strRangeMax = Integer.toString(m_iRangeMax);
        m_strWinLine  = Integer.toString(m_iWinLine);
        m_bIsPlay     = settings.getBoolean(TAG_GAMEMODE,false);

        tvNowRange.setText(m_strRangeMin + " ~ " + m_strRangeMax);
        tvLine.setText(Integer.toString(m_iLine));
        tvAimsLine.setText(m_strWinLine);
        initGirdView();

        for(int i = 0; i < tvNumArray.length; i++){
            tvNumArray[i].setText(settings.getString(TAG_GRID + i, null));
            m_bIsLine[i] = settings.getBoolean(TAG_GRIDLINE + i, false);

            Log.d(Integer.toString(i),String.valueOf(m_bIsLine[i]));
        }

        censorAllTvEdit();
        if(true == m_bIsPlay) {
            changeMode(!m_bIsPlay);

            hdHandler=new Handler();
            raRun=new Runnable() {
                @Override
                public void run() {
                    //遞迴檢查元件的寬高是否一致
                    if (tvNumArray[0].getWidth() == tvNumArray[0].getHeight()) {

                        for (int i = 0; i < tvNumArray.length; i++) {
                            censorLine(i);
                        }
                    }
                    else{
                        hdHandler.postDelayed(raRun, 100);
                    }
                }
            };
            hdHandler.postDelayed(raRun, 100);


        }
    }

    class SBingoTvListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int iVTag = (Integer)v.getTag();

            if(true == m_bIsPlay){
                m_bIsLine[iVTag] = !m_bIsLine[iVTag];
                censorLine(iVTag);
            } else {
                viewDialog = View.inflate(MainActivity.this, R.layout.dialog_editnum, null);
                saDialog.showEditGridDialog(viewDialog, tvNumArray[iVTag], m_strRangeMin, m_strRangeMax);
            }
        }
    }

    public void changeMode(boolean bPlayMode) {
        int iModeColor = -1;
        int iModeImage = -1;
        int iModeRangeImage = -1;
        int iModeRandomImage = -1;
        //判斷是否編輯中
        if (true == bPlayMode) {
            iModeColor = R.color.onPause;
            iModeImage = R.drawable.play;
            iModeRangeImage = R.drawable.edit;
            iModeRandomImage = R.drawable.dice;
            for (int i = 0; i < tvNumArray.length; i++) {
                tvNumArray[i].setBackgroundResource(iModeColor); //將所有TextView變色
                m_bIsLine[i] = false; //所有是否點擊變數回預設值
            }
        } else {
            iModeColor = R.color.onPlay;
            iModeImage = R.drawable.pause;
            iModeRangeImage = R.drawable.editoff;
            iModeRandomImage = R.drawable.diceoff;
            for (int i = 0; i < tvNumArray.length; i++) {
                tvNumArray[i].setBackgroundResource(iModeColor); //將所有TextView變色
            }
        }


        m_iLine = 0; // 連線數回到 0
        tvLine.setText("0");
        ivRangeEdit.setEnabled(bPlayMode);  //編輯按鈕是否啟用
        ivRangeEdit.setImageResource(iModeRangeImage); //編輯按鈕圖片
        ivRandom.setEnabled(bPlayMode); //隨機按鈕是否啟用
        ivRandom.setImageResource(iModeRandomImage); //隨機按鈕圖片
        ivMode.setImageResource(iModeImage); //模式按鈕圖片
        m_bIsPlay = !bPlayMode; //模式轉變後儲存

        //清除畫板
        pPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        cCanvas.drawPaint(pPaint);
        pPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    public void censorAllTvEdit() {
        int iTvTxt;
        int iMin = Integer.parseInt(m_strRangeMin);
        int iMax = Integer.parseInt(m_strRangeMax);

        //判斷每個TextView值是否都在範圍內 , 全部皆是啟用Play按鈕
        for (int i = 0; i < tvNumArray.length; i++) {
            iTvTxt = Integer.parseInt(tvNumArray[i].getText().toString());
            if (false == (iTvTxt >= iMin && iTvTxt <= iMax)) {
                ivMode.setEnabled(false);
                ivMode.setImageResource(R.drawable.playoff);
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
        m_iLine = 0;
        int iPoint;
        int iLineArray[] = new int[m_iWidth];


        //清除畫板
        pPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        cCanvas.drawPaint(pPaint);
        pPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        //判斷目前點擊狀態給予顏色
        if(true == m_bIsLine[iView]){
            tvNumArray[iView].setBackgroundResource(R.color.clickTextView);
        }else{
            tvNumArray[iView].setBackgroundResource(R.color.onPlay);
        }

        //橫向連線數
        for (int i = 0; i < m_iWidth * m_iWidth; i += m_iWidth) {
            iPoint = 0;
            for (int j = i; j < i + m_iWidth; j++) {
                if (false == m_bIsLine[j]) {
                    continue;
                }
                iLineArray[iPoint] = j; //將點擊到的TextView存入陣列
                iPoint++; //點擊數+1
            }
            //如果點擊數=3
            if (m_iWidth == iPoint) {
                drawLine(iLineArray); //畫線
                m_iLine++; //連線數+1
            }
        }
        //直向連線數
        for (int i = 0; i < m_iWidth; i++) {
            iPoint = 0;
            for (int j = i; j < m_iWidth * m_iWidth; j += m_iWidth) {
                if (false == m_bIsLine[j]) {
                    continue;
                }
                iLineArray[iPoint] = j; //將點擊到的TextView存入陣列
                iPoint++; //點擊數+1
            }
            //如果點擊數=3
            if (m_iWidth == iPoint) {
                drawLine(iLineArray); //畫線
                m_iLine++; //連線數+1
            }
        }
        //左上至右下
        iPoint = 0;
        for (int i = 0; i < m_iWidth * m_iWidth; i += (m_iWidth + 1)) {
            if (false == m_bIsLine[i]) {
                continue;
            }
            iLineArray[iPoint] = i; //將點擊到的TextView存入陣列
            iPoint++; //點擊數+1
            //如果點擊數=3
            if (m_iWidth == iPoint) {
                drawLine(iLineArray); //畫線
                m_iLine++; //連線數+1
            }
        }
        //右上至左下
        iPoint = 0;
        for (int i = m_iWidth - 1; i <= (m_iWidth * m_iWidth) - m_iWidth; i += (m_iWidth - 1)) {
            if (false == m_bIsLine[i]) {
                continue;
            }
            iLineArray[iPoint] = i; //將點擊到的TextView存入陣列
            iPoint++; //點擊數+1
            //如果點擊數=3
            if (m_iWidth == iPoint) {
                drawLine(iLineArray); //畫線
                m_iLine++; //連線數+1
            }
        }

        tvLine.setText(Integer.toString(m_iLine));

        if(m_iLine >= Integer.parseInt(m_strWinLine)){
            viewDialog = View.inflate(MainActivity.this, R.layout.dialog_win, null);
            saDialog.showWinDialog(viewDialog);
        }
    }

    private void drawLine(int iDwLine[]) {
        int[] iStart = new int[2];
        int[] iEnd = new int[2];
        //取得起始和終止物件的座標
        tvNumArray[iDwLine[0]].getLocationOnScreen(iStart);
        tvNumArray[iDwLine[m_iWidth-1]].getLocationOnScreen(iEnd);
        //計算座標
        int iStartX = iStart[0] + (tvNumArray[0].getWidth() / 2);
        int iStartY = iStart[1] + (tvNumArray[0].getHeight() / 2);
        int iEndX = iEnd[0] + (tvNumArray[0].getWidth() / 2);
        int iEndY = iEnd[1] + (tvNumArray[0].getHeight() / 2);


        //畫線
        cCanvas.drawLine(iStartX, iStartY, iEndX, iEndY, pPaint);
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
