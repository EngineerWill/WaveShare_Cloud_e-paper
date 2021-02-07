package com.waveshare.cloud_esp32;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.waveshare.cloud_esp32.image_processing.EPaperDisplay;
import com.waveshare.cloud_esp32.image_processing.EPaperPicture;

/**
 * <h1>Filtering activity</h1>
 * The activity offers to select one of available image filters,
 * which converts the loaded image for better pixel format
 * converting required for selected display.
 *
 * @author  Waveshare team
 * @version 1.0
 * @since   8/18/2018
 */

public class FilteringActivity extends AppCompatActivity
{
    // View
    //------------------------------------------
    private Button    button;
    private TextView  textView;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.palettes_activity);
        getSupportActionBar().setTitle(R.string.filt);

        // View
        //------------------------------------------
        textView  = findViewById(R.id.txt_indexed);
        imageView = findViewById(R.id.img_indexed);

        // Disable unavailable palettes
        //------------------------------------------
        boolean redIsEnabled = (EPaperDisplay.getDisplays()[EPaperDisplay.epdInd].index & 1) != 0;
        findViewById(R.id.btn_wbrl).setEnabled(redIsEnabled);
        findViewById(R.id.btn_wbrd).setEnabled(redIsEnabled);
    }

    // Accept the selected
    public void onOk(View view)
    {
        // If palette is not selected, then exit
        //-----------------------------------------------------
        if (button == null) return;

        // Close palette activity and return palette's name
        //-----------------------------------------------------
        Intent intent = new Intent();
        intent.putExtra("NAME", button.getText().toString());

        setResult(RESULT_OK, intent);
        finish();
    }

    public void onCancel(View view)
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onWhiteAndBlackLevelClick(View view)
    {
        // Save pushed button and run image processing
        //-----------------------------------------------------
        button = (Button)view;
        run(true, false);
    }

    public void onWhiteBlackRedLevelClick(View view)
    {
        // Save pushed button and run image processing
        //-----------------------------------------------------
        button = (Button)view;
        run(true, true);
    }

    public void onWhiteAndBlackDitheringClick(View view)
    {
        // Save pushed button and run image processing
        //-----------------------------------------------------
        button = (Button)view;
        run(false, false);
    }

    public void onWhiteBlackRedDitheringClick(View view)
    {
        // Save pushed button and run image processing
        //-----------------------------------------------------
        button = (Button)view;
        run(false, true);
    }

    public void run(boolean isLvl, boolean isRed)
    {
        textView.setText(button.getText());
    }
}
