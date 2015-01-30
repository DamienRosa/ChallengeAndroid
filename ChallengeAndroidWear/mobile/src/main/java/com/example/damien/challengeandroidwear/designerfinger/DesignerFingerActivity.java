package com.example.damien.challengeandroidwear.designerfinger;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.damien.challengeandroidwear.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class DesignerFingerActivity extends Fragment {

    private static final String TAG = DesignerFingerActivity.class.getSimpleName();
    private static String color = "";
    private static String sizeBrush = "";
    private DrawerView mDrawerView;
    private Button mClearButton;
    private Button mSaveButton;
    private EditText mColorEText;
    private EditText mSizeBrushEText;
    private Button mSetButton;

    public DesignerFingerActivity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_designer_finger, container, false);
        String text = String.format("Designer Finger");
        getActivity().setTitle(text);

        mDrawerView = (DrawerView) rootView.findViewById(R.id.drawer_view);

        mClearButton = (Button) rootView.findViewById(R.id.clear_button);
        mClearButton.setOnClickListener(new OnClickClearButton());

        mSaveButton = (Button) rootView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new OnClickSaveButton());

        mColorEText = (EditText) rootView.findViewById(R.id.color_edit_text);


        mSizeBrushEText = (EditText) rootView.findViewById(R.id.size_brush_edit_text);

        mSetButton = (Button) rootView.findViewById(R.id.set_configs_button);
        mSetButton.setOnClickListener(new OnClickSetConfigs());
        return rootView;
    }

    //clear button
    private class OnClickClearButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mDrawerView.clearDraw();
            Toast.makeText(getActivity(), "View cleared", Toast.LENGTH_LONG).show();
        }
    }

    //save button
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
                MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), newFile.getAbsolutePath(), newFile.getName(), newFile.getName());
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Error: Image couldn't be saved. :S", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            Toast.makeText(getActivity(), "Image saved. Please, check it out!", Toast.LENGTH_LONG).show();

            mDrawerView.destroyDrawingCache();
        }
    }

    //reconfig color and sizebrush
    private class OnClickSetConfigs implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            color = mColorEText.getText().toString();
            sizeBrush = mSizeBrushEText.getText().toString();
            mDrawerView.setConfigs(color, sizeBrush);
        }
    }
}
