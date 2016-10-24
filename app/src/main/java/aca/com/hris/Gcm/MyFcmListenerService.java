/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aca.com.hris.Gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import aca.com.hris.AbsensiApprovalActivity;
import aca.com.hris.AbsensiCancellationApprovalActivity;
import aca.com.hris.AbsensiCancellationListActivity;
import aca.com.hris.AbsensiListActivity;
import aca.com.hris.CutiApprovalActivity;
import aca.com.hris.CutiCancellationApprovalActivity;
import aca.com.hris.CutiCancellationListActivity;
import aca.com.hris.CutiListActivity;
import aca.com.hris.R;
import aca.com.hris.Util.Var;

public class MyFcmListenerService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage message) {
        String tipeForm = null;
        String isApproval = null;
        String title;
        String body;
        String clickAction;

        if (message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body = message.getNotification().getBody();
            clickAction = message.getNotification().getClickAction();

            if (message.getData() != null) {
                tipeForm = message.getData().get("form").toString();
                isApproval = message.getData().get("isApproval").toString();
            }
            sendNotification(title, body, tipeForm, isApproval, clickAction);
        }
//        Intent intent = new Intent(getApplicationContext(), AbsensiApprovalActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

    private void sendNotification(String titleMessage, String bodyMessage, String tipeForm, String isApproval, String clickAction) {
        try {
            Class mClass;
            mClass = getTargetClass(tipeForm, isApproval);

            Intent intent;
//            intent = new Intent(this, mClass);
            intent = new Intent(clickAction);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(mClass);
            stackBuilder.addNextIntent(intent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                    (int)System.currentTimeMillis(),
                    PendingIntent.FLAG_UPDATE_CURRENT);

//            PendingIntent pendingIntent = PendingIntent.getActivity(
//                    this,
//                    (int) System.currentTimeMillis()/* Request code */,
//                    intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Notification notification = new Notification();
            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(bodyMessage)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{1000, 1000, 1000})
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(getString(R.string.app_name))
                            .bigText(bodyMessage)

                    ).build();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class getTargetClass(String tipeForm, String isApproval) {
        Class mClass = null;

        if (isApproval.equalsIgnoreCase("1")) {
            if (tipeForm.equalsIgnoreCase(Var.absensi))
                mClass = AbsensiApprovalActivity.class;
            else if (tipeForm.equalsIgnoreCase(Var.cuti))
                mClass = CutiApprovalActivity.class;
            else if (tipeForm.equalsIgnoreCase(Var.absensiCancel))
                mClass = AbsensiCancellationApprovalActivity.class;
            else if (tipeForm.equalsIgnoreCase(Var.cutiCancel))
                mClass = CutiCancellationApprovalActivity.class;
        } else {
            if (tipeForm.equalsIgnoreCase(Var.absensi))
                mClass = AbsensiListActivity.class;
            else if (tipeForm.equalsIgnoreCase(Var.cuti))
                mClass = CutiListActivity.class;
            else if (tipeForm.equalsIgnoreCase(Var.absensiCancel))
                mClass = AbsensiCancellationListActivity.class;
            else if (tipeForm.equalsIgnoreCase(Var.cutiCancel))
                mClass = CutiCancellationListActivity.class;
        }

        return mClass;
    }
}
