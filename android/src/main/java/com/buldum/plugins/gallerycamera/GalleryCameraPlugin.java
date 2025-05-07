package com.buldum.plugins.gallerycamera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "GalleryCamera")
public class GalleryCameraPlugin extends Plugin {

    private static final int REQUEST_CAPTURE_PHOTO = 1001;
    private static final int REQUEST_CAPTURE_VIDEO = 1002;
    private static final int PERMISSION_REQUEST_CODE = 2001;

    @PluginMethod
    public void captureMedia(PluginCall call) {
        String type = call.getString("type", "photo");

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            call.reject("Camera permission not granted");
            return;
        }

        Intent intent;
        if ("video".equals(type)) {
            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
            saveCall(call);
            startActivityForResult(call, intent, REQUEST_CAPTURE_VIDEO);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            saveCall(call);
            startActivityForResult(call, intent, REQUEST_CAPTURE_PHOTO);
        }
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        PluginCall savedCall = getSavedCall();

        if (savedCall == null) return;

        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                JSObject result = new JSObject();
                result.put("path", uri.toString());
                result.put("name", uri.getLastPathSegment());
                result.put("type", getContext().getContentResolver().getType(uri));
                savedCall.resolve(result);
            } else {
                savedCall.reject("No media returned");
            }
        } else {
            savedCall.reject("Capture cancelled");
        }
    }
}