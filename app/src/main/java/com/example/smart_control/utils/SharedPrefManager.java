package com.example.smart_control.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    public static final String SP_LOGIN_APP = "spLoginApp";

    public static final String SP_ID = "spID";
    public static final String SP_NIK = "spNIK";
    public static final String SP_NAMA = "spNama";
    public static final String SP_EMAIL = "spEmail";
    public static final String SP_MOBILE = "spMobile";
    public static final String SP_ADDRESS = "spAddress";
    public static final String SP_LOGIN_WEB = "spLoginWeb";

    public static final String SP_AVATAR = "spAvatar";

    public static final String SP_FCM = "spFcmKey";

    public static final String NAME_ARTIKEL = "spArtikel";

    public static final String SP_APP_TOKEN = "spAppToken";
    public static final String SP_PRODESA_TOKEN = "spProdesaToken";
    public static final String SP_DESA_CODE = "spDesaCode";

    public static final String SP_SUDAH_LOGIN = "spSudahLogin";

    public static final String SP_ROLE = "spRole";
    public static final String SP_CUR_USER_ID = "spCurrentUserId";
    public static final String SP_HAS_MASYARAKAT = "spHasMasyarakat";

    public static final String SP_ID_LETER = "spIdLeter";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(SP_LOGIN_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSpId() {
        return sp.getString(SP_ID, "");
    }

    public String getSpNik() {
        return sp.getString(SP_NIK, "");
    }

    public String getSpNama() {
        return sp.getString(SP_NAMA, "");
    }

    public String getSpEmail() {
        return sp.getString(SP_EMAIL, "");
    }

    public String getSpMobile() {
        return sp.getString(SP_MOBILE, "");
    }

    public String getSpAddress() {
        return sp.getString(SP_ADDRESS, "");
    }

    public String getSpAvatar() {
        return sp.getString(SP_AVATAR, "");
    }

    public String getSpFcm() {
        return sp.getString(SP_FCM, "");
    }

    public String getSpLoginWeb() {
        return sp.getString(SP_LOGIN_WEB, "");
    }

    public String getNameArtikel() {
        return sp.getString(NAME_ARTIKEL, "");
    }

    public String getSpAppToken() {
        return sp.getString(SP_APP_TOKEN, "");
    }

    public String getSpProdesaToken() {
        return sp.getString(SP_PRODESA_TOKEN, "");
    }

    public String getSpDesaCode() {
        return sp.getString(SP_DESA_CODE, "");
    }

    public Boolean getSPSudahLogin(){
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }

//    public String getSpRole(){
//        return sp.getString(SP_ROLE, Role.ROLE_MASYARAKAT);
//    }
//
//    public void setCurrentUserId(Integer id) {
//        saveSPInt(SP_CUR_USER_ID, id);
//    }
//
//    public boolean isKepalaDesa() {
//        return getSpRole().equals(Role.ROLE_KEPALA_DESA);
//    }
//
//    public boolean isMasyarakat() {
//        return getSpRole().equals(Role.ROLE_MASYARAKAT);
//    }

    public boolean hasMasyarakat() {
        return sp.getBoolean(SP_HAS_MASYARAKAT, false);
    }

    public String getSpIdLeter() {
        return sp.getString(SP_ID_LETER, "");
    }

    public void setSpNama(String SpNama) {
        if (!SpNama.equals(""))
            saveSPString(SharedPrefManager.SP_NAMA, SpNama);
    }
}
