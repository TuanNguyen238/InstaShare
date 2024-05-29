package com.example.instashare.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.instashare.R;
import com.google.firebase.Timestamp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class InstaShareUtils {
    public static String createId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }
    public static String timestampToString(Timestamp timestamp){
        Date date = timestamp.toDate();
        Date now = Timestamp.now().toDate();
        if(date.getDate() == now.getDate()){
            return new SimpleDateFormat("HH:mm").format(date);
        }
        return new SimpleDateFormat("d MMM yyyy HH:mm").format(date);
    }
    public static Uri createUri(Context context) {
        // Lấy ảnh từ thư mục drawable
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.account);
        // Tạo một tệp tin tạm thời để lưu ảnh
        FileOutputStream fos = null;
        File file = null;
        try {
            file = File.createTempFile("image", "jpg");
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            Log.d("TAG", e.toString());
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("TAG", e.toString());
                }
            }
        }
        return Uri.fromFile(file);
    }

    public static String getDistanceTime(String time) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);

        long yearsDifference = ChronoUnit.YEARS.between(dateTime.toLocalDate(), currentTime.toLocalDate());
        if(yearsDifference == 0) {
            long monthsDifference = ChronoUnit.MONTHS.between(dateTime.toLocalDate(), currentTime.toLocalDate());
            if(monthsDifference  == 0) {
                long daysDifference = ChronoUnit.DAYS.between(dateTime.toLocalDate(), currentTime.toLocalDate());
                if(daysDifference == 0)
                {
                    long hoursDifference = ChronoUnit.HOURS.between(dateTime, currentTime);
                    if(hoursDifference == 0) {
                        long minutesDifference = ChronoUnit.MINUTES.between(dateTime, currentTime);
                        if(minutesDifference == 0) {
                            long secondsDifference = ChronoUnit.SECONDS.between(dateTime, currentTime);
                            if(secondsDifference < 5)
                                return "Bây giờ";
                            else return String.valueOf(secondsDifference) + " giây trước";
                        }
                        else
                            return String.valueOf(minutesDifference) + "phút trước";
                    }
                    else
                        return String.valueOf(hoursDifference) + "giờ trước";
                }
                else
                    return String.valueOf(daysDifference) + " ngày trước";
            }
            else
                return String.valueOf(monthsDifference) + " tháng trước";
        }
        else
            return String.valueOf(yearsDifference) + " năm trước";
    }

}
