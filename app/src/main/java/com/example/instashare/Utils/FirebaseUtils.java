package com.example.instashare.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.instashare.Model.Comment;
import com.example.instashare.Model.Chatroom;
import com.example.instashare.Model.Message;
import com.example.instashare.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class FirebaseUtils {
    static  FirebaseUtils instance;
    protected FirebaseUtils(){}
    public static FirebaseUtils Instance(){
        if(instance == null){
            instance = new FirebaseUtils();
        }
        return instance;
    }

    private void deleteMessage(String idchatroom){
        FirebaseUtils.Instance().getChats(idchatroom)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FirebaseUtils.Instance().getChats(idchatroom).document(document.getId()).delete();
                            }
                        }
                    }
                });
    }
    public void deleteImage(String uid, String nameImage){
        FirebaseStorage.getInstance().getReference().child("Images/"+nameImage).delete();
        FirebaseDatabase.getInstance().getReference("Caption").child(nameImage).removeValue();
        FirebaseUtils.Instance().getIcon(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String uri = document.get("uri").toString();
                                if(uri.equals(uri.toString())){

                                    FirebaseUtils.Instance().getIcon(uid).document(document.getId()).delete();
                                }
                            }
                        }
                    }
                });

    }
    public void unFriend(String idrequest, String uid1, String uid2){
        setStateRequest(idrequest, "0");
        deleteMessage(idrequest);// delete chatroom message
        getAllChatroom().child(idrequest).removeValue(); // delete last message
        //deleteIcon(uid1, uid2); // delete icon1
        deleteIcon(uid2, uid1); // delete icon2
    }
    /*private void deleteIcon(String uid1, String uid2){
        FirebaseFirestore.getInstance().collection("comments")
                .document(uid1).collection("icons").document(uid2).delete();

    }*/
    public void setStateRequest(String idrequest, String state){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Requests");
        reference.child(idrequest).child("state").setValue(state);
    }

    public DatabaseReference getAllUser(){
        return FirebaseDatabase.getInstance().getReference().child("Users");
    }
    public DatabaseReference getAllRequest(){
        return FirebaseDatabase.getInstance().getReference("Requests");
    }
    public DatabaseReference getAllChatroom(){
        return FirebaseDatabase.getInstance().getReference("Chatrooms");
    }
    public DatabaseReference getAllCaption(){
        return FirebaseDatabase.getInstance().getReference("Caption");
    }
    public Query getRequestById(String idRequest){
        return getAllRequest().orderByChild("idrequest").equalTo(idRequest);
    }
    public DocumentReference getChatRoom(String idchatroom){
        return FirebaseFirestore.getInstance().collection("chatrooms")
                .document(idchatroom);
    }
    public CollectionReference getIcon(String reveiceId){
        return FirebaseFirestore.getInstance().collection("comments")
                .document(reveiceId).collection("icons");
    }
    public void sendIcon(String reveiceId, Comment comment){
        getIcon(reveiceId).document(comment.getSendId()).set(comment);
    }

    public CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatRoom(chatroomId).collection("chats");
    }

    private void setLastMessage(String idchatroom, Message message){
        DatabaseReference reference = getAllChatroom();
        reference.child(idchatroom).child("lastMessage").setValue(message.getMessage());
        reference.child(idchatroom).child("lastMessageTimestamp").setValue(message.getTimestamp());
        reference.child(idchatroom).child("lastMessageSenderId").setValue(message.getSenderId());
    }
    public void setReadMessage(String idchatroom, String uid, String  state){
        DatabaseReference reference = getAllChatroom();
        reference.child(idchatroom).child(uid).setValue(state);
    }
    public void sendMessage(String idchatroom, Message message){
        getChatroomMessageReference(idchatroom).add(message)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            setLastMessage(idchatroom, message);
                        }
                    }
                });
    }
    public static void uploadProfileImage(Uri fileUri) {
        // Tải ảnh từ tệp tin tạm thời lên Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("Images/Profile/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + "Profile_Image");

        UploadTask uploadTask = imageRef.putFile(fileUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Xử lý khi tải ảnh thành công
                // Lấy đường dẫn của ảnh từ taskSnapshot.getDownloadUrl()
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý khi có lỗi xảy ra trong quá trình tải ảnh
            }
        });
    }

    private void deleteIcon(String uid1, String uid2){
        FirebaseFirestore.getInstance().collection("comments")
                .document(uid1).collection("icons").document(uid2).delete();

    }
    private void deleteIconByUid(String uid){
        FirebaseUtils.Instance().getIcon(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String uri = document.get("uri").toString();
                                if(uri.equals(uri.toString())){
                                    FirebaseUtils.Instance().getIcon(uid).document(document.getId()).delete();
                                }
                            }
                        }
                    }
                });
    }

    public void deleteImage(String uid, String nameImage, Uri uri){
//        deleteMessageByUri(uri);
        FirebaseStorage.getInstance().getReference().child("Images/"+nameImage).delete();
        FirebaseDatabase.getInstance().getReference("Caption").child(nameImage).removeValue();
        deleteIconByUid(uid);
    }
    public static void getName(User user, Context context, UserNameCallback callback) {
        // Tham chiếu đến Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users").child(user.getUid());

        // Đọc dữ liệu từ nút "Users" trong Realtime Database
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra xem có dữ liệu tồn tại hay không
                if (dataSnapshot.exists()) {
                    user.setFirstName(dataSnapshot.child("firstName").getValue(String.class));
                    user.setLastName(dataSnapshot.child("lastName").getValue(String.class));
                } else {
                    Toast.makeText(context, "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                }
                // Gọi callback khi dữ liệu tên người dùng đã được cập nhật
                callback.onUserNameLoaded();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                // Gọi callback nếu xảy ra lỗi
                callback.onUserNameLoaded();
            }
        });
    }

    public static void getProfileImage(User user, Context context, ProfileImageCallback callback) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("Images").child("Profile")
                .child(user.getUid()).child("Profile_Image");
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Tạo đối tượng Uri từ URL
                // Gọi callback khi việc lấy URL hoàn tất
                callback.onProfileImageLoaded(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý khi có lỗi xảy ra trong quá trình lấy URL hình ảnh
                // Gọi callback nếu việc lấy URL thất bại
                callback.onProfileImageLoaded(null);
            }
        });
    }
    public static interface ProfileImageCallback {
        void onProfileImageLoaded(Uri uri);
    }

    public static interface UserNameCallback {
        void onUserNameLoaded();
    }

    public static void checkState(String uid, StateCallback callback) {
        FirebaseStorage.getInstance().getReference().child("Images").listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        boolean flag = false;
                        for (StorageReference item : listResult.getItems()) {
                            // Lấy đường dẫn URL của ảnh
                            String[] parts = item.getName().split("_");
                            if(parts[1].equals(uid)) {
                                flag = true;
                                break;
                            }
                        }
                        // Gọi callback với kết quả trạng thái
                        callback.onStateChanged(flag);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xảy ra lỗi khi lấy danh sách ảnh
                        Log.d("TAG_IMAGE", e.getMessage());
                        // Gọi callback với giá trị false khi có lỗi xảy ra
                        callback.onStateChanged(false);
                    }
                });
    }


    public interface StateCallback {
        void onStateChanged(boolean flag);
    }
    public void sendRequest(String idRequest, String sendId, String idreceive){
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("idrequest",idRequest);
        hashMap.put("idsend", sendId);
        hashMap.put("idreceive", idreceive);
        hashMap.put("state", "1");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Requests");
        reference.child(idRequest).setValue(hashMap);
    }
    public void acceptRequest(String idrequest){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Requests");
        reference.child(idrequest).child("state").setValue("2");
    }
    public void createChatRoom(String idchatroom, String uid1, String uid2){
        Chatroom nchatroom = new Chatroom(
                idchatroom,
                (Timestamp) Timestamp.now(),
                "",
                "Chưa có câu trả lời nào!",
                uid1,
                uid2,
                "0",
                "0"
        );
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Chatrooms");
        reference.child(idchatroom).setValue(nchatroom);
    }
    public CollectionReference getChats(String idchatroom){
        return getChatRoom(idchatroom).collection("chats");
    }
    public Query searchUser(String searchText){
        DatabaseReference query = FirebaseUtils.Instance().getAllUser();
        Query query1 = query
                .orderByChild("firstName")
                .startAt(searchText)
                .endAt(searchText+"\uf8ff");
        return query1;
    }
}
