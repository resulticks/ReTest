package io.mob.resu.reandroidsdk;


import android.content.Context;

interface IResponseListener {

    void onSuccess(String response, int flag, String campaignBlastId);

    void onFailure(Throwable throwable, int flag);

    void showDialog(String response, int flag);

    void showErrorDialog(String errorResponse, int flag);

    void showInternalServerErrorDialog(String errorResponse, int flag);

    void logOut(int flag);

    Context getContext();

}
