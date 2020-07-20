package com.base.library.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Base64
import androidx.preference.PreferenceManager
import java.io.*


object PreferenceUtils {


    fun saveParcelable(context: Context, key: String, obj: Parcelable?) {
        var personBase64: String? = null
        if (obj != null) {
            try {
                val parcel = Parcel.obtain()
                parcel.setDataPosition(0)
                obj.writeToParcel(parcel, 0)
                val bytes = parcel.marshall()
                parcel.recycle()
                personBase64 = String(Base64.encode(bytes, Base64.DEFAULT))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        setPrefString(context, key, personBase64)
    }

    fun <T: Parcelable> getParcelable(context: Context, key: String, createFromParcel:(Parcel)-> T): T? {
        val str = getPrefString(context, key, null)
        if (!TextUtils.isEmpty(str)) {
            val objByte = Base64.decode(str, Base64.DEFAULT)
            try {
                val parcel = Parcel.obtain()
                parcel.unmarshall(objByte, 0, objByte.size)
                parcel.setDataPosition(0)
                //T.CREATOR.createFromParcel(parcel)
                val bean = createFromParcel(parcel)
                parcel.recycle()
                return bean
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }


    fun saveObject(context: Context, key: String, obj: Serializable?) {
        var personBase64: String? = null
        if (obj != null) {
            try {
                val baos = ByteArrayOutputStream()
                val oos = ObjectOutputStream(baos)
                oos.writeObject(obj)
                personBase64 = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        setPrefString(context, key, personBase64)
    }

    fun <T> getObject(context: Context, key: String): T? {
        val str = getPrefString(context, key, null)
        if (!TextUtils.isEmpty(str)) {
            val base64Bytes = Base64.decode(str, Base64.DEFAULT)
            val bais = ByteArrayInputStream(base64Bytes)
            try {
                val ois = ObjectInputStream(bais)
                return ois.readObject() as T
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return null
    }


    fun getPrefString(context: Context, key: String, defaultValue: String?): String? {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        return settings.getString(key, defaultValue)
    }

    fun getPrefString(context: Context, key: String): String {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        return settings.getString(key, "")?:""
    }

    fun setPrefString(context: Context, key: String, value: String?) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        settings.edit().putString(key, value).apply()
    }

    fun getPrefBoolean(context: Context, key: String, defaultValue: Boolean): Boolean {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        return settings.getBoolean(key, defaultValue)
    }

    fun hasKey(context: Context, key: String): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key)
    }

    fun setPrefBoolean(context: Context, key: String, value: Boolean) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        settings.edit().putBoolean(key, value).apply()
    }

    fun setPrefInt(context: Context, key: String, value: Int) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        settings.edit().putInt(key, value).apply()
    }

    fun getPrefInt(context: Context, key: String, defaultValue: Int): Int {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        return settings.getInt(key, defaultValue)
    }

    fun setPrefFloat(context: Context, key: String, value: Float) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        settings.edit().putFloat(key, value).apply()
    }

    fun getPrefFloat(context: Context, key: String, defaultValue: Float): Float {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        return settings.getFloat(key, defaultValue)
    }

    fun setSettingLong(context: Context, key: String, value: Long) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        settings.edit().putLong(key, value).apply()
    }

    fun getPrefLong(context: Context, key: String, defaultValue: Long): Long {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        return settings.getLong(key, defaultValue)
    }

    fun clearPreference(context: Context, p: SharedPreferences) {
        val editor = p.edit()
        editor.clear()
        editor.apply()
    }
}
