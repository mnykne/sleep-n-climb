package sleepnclimb.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import sleepnclimb.R;
import sleepnclimb.activities.WakeUpActivity;
import sleepnclimb.models.Alarm;

public class AlarmPhotoDismissFragment extends Fragment {
    static final int REQUEST_TAKE_PHOTO = 1;

    static final String CLARIFAI_API_CODE = "YOUR_API_KEY";

    Button mPhotoButton;
    TextView mPhotoCategory;
    ImageView mPhotoPlaceholder;
    Handler mFgHandler;
    Handler mBgHandler;
    HandlerThread mBgHandlerThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_photo_dismiss,
                container, false);

        mPhotoButton = view.findViewById(R.id.take_photo_btn);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(pictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(pictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        });

        mPhotoPlaceholder = view.findViewById(R.id.photo_placeholder);
        mPhotoCategory = view.findViewById(R.id.dismiss_category);

        Alarm alarm = ((WakeUpActivity) getActivity()).getAlarm();
        mPhotoCategory.setText(
                "Take a photo of '"
                + alarm.objectCategories
                + "' to dismiss the alarm");
        mPhotoPlaceholder.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBgHandlerThread = new HandlerThread("bg_handler");
        mBgHandlerThread.start();
        mBgHandler = new Handler(mBgHandlerThread.getLooper());
        mFgHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onStop() {
        mBgHandler.removeCallbacksAndMessages(null);
        mBgHandlerThread.quitSafely();
        super.onStop();
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO &&
                resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                final Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                mPhotoPlaceholder.setVisibility(View.VISIBLE);
                mPhotoPlaceholder.setImageBitmap(imageBitmap);
                Toast.makeText(
                        getContext(),
                        "Analyzing...",
                        Toast.LENGTH_SHORT
                ).show();
                new ClarifaiTask().execute(imageBitmap);
            }
        }
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }

    private class ClarifaiTask extends AsyncTask<Bitmap, Integer, Boolean> {
        protected Boolean doInBackground(Bitmap... images) {
            Alarm alarm = ((WakeUpActivity) getActivity()).getAlarm();
            ClarifaiClient client = new ClarifaiBuilder(CLARIFAI_API_CODE).buildSync();
            List<ClarifaiOutput<Concept>> predictionResults;
            for (Bitmap image : images) {
                predictionResults = client.getDefaultModels().generalModel().predict()
                        .withInputs(ClarifaiInput.forImage(convertBitmapToByteArray(image))).executeSync().get();
                for (ClarifaiOutput<Concept> result : predictionResults) {
                    for (Concept datum : result.data()) {
                        if (alarm.objectCategories.equals(datum.name())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        protected void onPostExecute(Boolean result) {
            if (result) {
                ((WakeUpActivity) getActivity()).dismissAndFinalize();
            } else {
                Toast.makeText(
                    getContext(),
                    "Object could not be detected, please take another photo.",
                    Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}