package com.learning.firhan.aquacare.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.learning.firhan.aquacare.Constants.ActivityResultsCode;
import com.learning.firhan.aquacare.R;

public class CameraOrGalleryDialogFragment extends DialogFragment {

    LinearLayout optionCameraContainer, optionGalleryContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_camera_gallery, container, false);

        optionCameraContainer = (LinearLayout)view.findViewById(R.id.optionCameraContainer);
        optionGalleryContainer = (LinearLayout)view.findViewById(R.id.optionGalleryContainer);

        optionCameraContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open camera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getActivity().startActivityForResult(takePictureIntent, ActivityResultsCode.CAMERA_REQUEST);
                }
                getDialog().dismiss();
            }
        });

        optionGalleryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //choose from gallery
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                getActivity().startActivityForResult(chooserIntent, ActivityResultsCode.GALLERY_REQUEST);

                //dismiss dialog
                getDialog().dismiss();
            }
        });

        return view;
    }
}
