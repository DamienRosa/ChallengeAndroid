package com.example.damien.challengeandroidwear.designerfinger;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.damien.challengeandroidwear.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class DesignerFingerActivity extends Activity {

    private static final String TAG = DesignerFingerActivity.class.getSimpleName();

    private DrawerView mDrawerView;

    private Button mClearButton;
    private Button mSaveButton;
    private EditText mColorEText;
    private EditText mSizeBrushEText;
    private Button mSetButton;

    private String color = "";
    private String sizeBrush = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_finger);
        mDrawerView = (DrawerView) findViewById(R.id.drawer_view);

        mClearButton = (Button) findViewById(R.id.clear_button);
        mClearButton.setOnClickListener(new OnClickClearButton());

        mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new OnClickSaveButton());

        mColorEText = (EditText) findViewById(R.id.color_edit_text);


        mSizeBrushEText = (EditText) findViewById(R.id.size_brush_edit_text);

        mSetButton = (Button) findViewById(R.id.set_configs_button);
        mSetButton.setOnClickListener(new OnClickSetConfigs());

    }

    private class OnClickClearButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mDrawerView.clearDraw();
            Toast.makeText(getApplicationContext(), "View cleared", Toast.LENGTH_LONG).show();
        }
    }

    private class OnClickSaveButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mDrawerView.setDrawingCacheEnabled(true);

            String path = Environment.getExternalStorageDirectory().toString();
            File newDir = new File(path + "/challengeandroid/");
            if (!newDir.exists()) {
                newDir.mkdirs();
            }

            String imageName = "image" + UUID.randomUUID().toString() + ".png";
            File newFile = new File(newDir, imageName);

            try {
                OutputStream out = new FileOutputStream(newFile);
                Bitmap image = mDrawerView.getDrawingCache();
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                MediaStore.Images.Media.insertImage(getContentResolver(), newFile.getAbsolutePath(), newFile.getName(), newFile.getName());
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "Error: Image couldn't be saved. :S", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error: Image couldn't be saved. :S", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            Toast.makeText(getApplicationContext(), "Image saved. Please, check it out!", Toast.LENGTH_LONG).show();

            mDrawerView.destroyDrawingCache();
        }
    }

    private class OnClickSetConfigs implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            color = mColorEText.getText().toString();
            sizeBrush = mSizeBrushEText.getText().toString();

            mDrawerView.setConfigs(color, sizeBrush);
        }
    }
}
