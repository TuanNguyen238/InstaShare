package com.example.instashare.Model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.example.instashare.Activity.ListChatRoomsActivity;
import com.example.instashare.Activity.MainPageActivity;
import com.example.instashare.Activity.ProfileActivity;
import com.example.instashare.Activity.UploadActivity;
import com.example.instashare.Activity.WidgetActivity;
import com.example.instashare.R;
import com.example.instashare.Utils.FirebaseUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountFragment extends Fragment {
    private ImageButton imbCapture, imbRecam, imbChat, imbWidget;
    private CircleImageView cmvProfileAcc;
    private User user;
    private ExecutorService service;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private int cameraFacing;
    private boolean permissionRequested = false;
    private List<String> listName = new ArrayList<>();
    private Context context;
    private Button btnListFriend;

    public AccountFragment(User user, Context context) {
        this.user = user;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.page_account, container, false);

        imbCapture = rootView.findViewById(R.id.imbCapture);
        imbRecam = rootView.findViewById(R.id.imbRecam);
        imbChat = rootView.findViewById(R.id.imbChat);
        imbWidget = rootView.findViewById(R.id.imbWidget);
        cmvProfileAcc = rootView.findViewById(R.id.cmvProfileAcc);

        cameraFacing = CameraSelector.LENS_FACING_BACK;
        cmvProfileAcc = rootView.findViewById(R.id.cmvProfileAcc);
        previewView = rootView.findViewById(R.id.viewFinder);
        getUri();
        imbCapture = rootView.findViewById(R.id.imbCapture);
        imbRecam = rootView.findViewById(R.id.imbRecam);
        imbChat = rootView.findViewById(R.id.imbChat);
        imbWidget = rootView.findViewById(R.id.imbWidget);
        btnListFriend = rootView.findViewById(R.id.btnListFriend);


        checkCameraPermission();
        imbCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });

        imbRecam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reCamera();
            }
        });

        cmvProfileAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        imbChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        });

        imbWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWidget();
            }
        });

        btnListFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openListFriend();
            }
        });

        setupCountFriend();

        service = Executors.newSingleThreadExecutor();

        return rootView;
    }

    private void openListFriend() {
        FriendBottomSheetDialog friendBottomSheetDialog = new FriendBottomSheetDialog(context, user);
        friendBottomSheetDialog.show();
    }

    public void openCamera() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA}, 1);
            return;
        }
        ListenableFuture<ProcessCameraProvider> processCameraProvider = ProcessCameraProvider.getInstance(context);

        processCameraProvider.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = processCameraProvider.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                cameraProvider.unbindAll();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void setupCountFriend(){
        FirebaseUtils.Instance().getAllRequest()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Request request = ds.getValue(Request.class);
                            if(request.getState().equals("2")){
                                if (request.getIdreceive().equals(user.getUid()) || request.getIdsend().equals(user.getUid())){
                                    count = count + 1;
                                }
                            }
                        }
                        String text = count + " bạn bè";
                        btnListFriend.setText(text);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.shutdown();
    }

    public void capturePhoto() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA}, 1);
            return;
        }
        File photoFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),  System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri photoUri = Uri.fromFile(photoFile);
                Intent intent = new Intent(context, UploadActivity.class);
                intent.putExtra("imageUri", photoUri.toString());
                intent.putExtra("len", cameraFacing);
                intent.putExtra("user", user);
                startActivity(intent);
                ((Activity)context).finish();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                // Error occurred while capturing photo
                exception.printStackTrace();
                Toast.makeText(context, "Lỗi ảnh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUri()
    {
        Glide.with(context).load(user.getUri()).circleCrop().into(cmvProfileAcc);
    }

    private void openWidget() {
        Intent intent = new Intent(context, WidgetActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        ((Activity)context).finish();
    }

    private void openChat() {
        Intent intent = new Intent(context, ListChatRoomsActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        ((Activity)context).finish();
    }

    private void openProfile() {
        Intent intent = new Intent(context, ProfileActivity.class);
        if(user == null)
        {
            Toast.makeText(context, "Mạng không ổn định", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("user", user);
        startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        ((Activity)context).finish();
    }

    private void reCamera()
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{android.Manifest.permission.CAMERA}, 1);
            return;
        }
        if (cameraFacing == CameraSelector.LENS_FACING_BACK)
            cameraFacing = CameraSelector.LENS_FACING_FRONT;
        else
            cameraFacing = CameraSelector.LENS_FACING_BACK;
        openCamera();
    }

    private void checkCameraPermission() {
        // Kiểm tra xem quyền truy cập camera đã được cấp chưa
        if (ContextCompat.checkSelfPermission((Activity)context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Nếu quyền chưa được cấp, yêu cầu quyền truy cập camera
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.CAMERA}, 1);
            // Đánh dấu rằng đã yêu cầu quyền
            permissionRequested = true;
        } else {
            // Nếu quyền đã được cấp, tiếp tục thực hiện các hoạt động cần thiết (ví dụ: mở camera)
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, tiếp tục thực hiện các hoạt động cần thiết (ví dụ: mở camera)
                openCamera();
            } else {
                // Nếu quyền không được cấp và chưa từng yêu cầu trước đó, tiếp tục yêu cầu quyền
                if (!permissionRequested) {
                    checkCameraPermission();
                } else {
                    // Nếu đã yêu cầu trước đó và người dùng từ chối, hiển thị thông báo và thoát ứng dụng
                    Toast.makeText(context, "Ứng dụng cần quyền truy cập camera để tiếp tục", Toast.LENGTH_SHORT).show();
                    ((Activity)context).finish();
                }
            }
        }
    }
}
