package com.softmobile.bingo.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    TextView tvNowRange;

    String strRangeMin = null;
    String strRangeMax = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNowRange = (TextView) findViewById(R.id.tvNowRange);

        onCreateDialog();

    }


    public void onCreateDialog() {
        final AlertDialog.Builder adbBuilder = new AlertDialog.Builder(MainActivity.this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View viewDialog = inflater.inflate(R.layout.dialog_range, null);

        adbBuilder.setView(viewDialog); //設置view來源為R.layout.dialog_range
        adbBuilder.setCancelable(false);
        adbBuilder.setPositiveButton(R.string.enter, null);

        final AlertDialog alert = adbBuilder.create(); //取代原本的adbBuilder
        alert.show();
        Button btnDialogEnter = alert.getButton(AlertDialog.BUTTON_POSITIVE);  //取得alert的button
        btnDialogEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取得alertdialog的兩個edittext
                EditText etRangeMin = (EditText) viewDialog.findViewById(R.id.etRangeMin);
                EditText etRangeMax = (EditText) viewDialog.findViewById(R.id.etRangeMax);

                //如果皆有輸入值存入string變數 , 否則toast
                if (!"".equals(etRangeMin.getText().toString()) && !"".equals(etRangeMax.getText().toString())) {

                    //判斷最大值最小值 , 由小到大
                    if (Integer.parseInt(etRangeMax.getText().toString()) > Integer.parseInt(etRangeMin.getText().toString())) {
                        strRangeMin = etRangeMin.getText().toString();
                        strRangeMax = etRangeMax.getText().toString();
                    } else {
                        strRangeMin = etRangeMax.getText().toString();
                        strRangeMax = etRangeMin.getText().toString();
                    }

                    //將範圍顯示至tvNoewRange , 並關閉alert
                    tvNowRange.setText(strRangeMin + " ~ " + strRangeMax);
                    alert.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, R.string.dialogToast_Null, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
