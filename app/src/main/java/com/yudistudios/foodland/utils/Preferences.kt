package com.yudistudios.foodland.utils

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val ENCRYPT_IV = stringPreferencesKey("iv")
val CARD_DETAIL = stringPreferencesKey("card_detail")

suspend fun saveIV(context: Context, array: ByteArray) {
    val saveThis: String = Base64.encodeToString(array, Base64.NO_WRAP)

    context.dataStore.edit { settings ->
        settings[ENCRYPT_IV] = saveThis
    }
}

suspend fun saveCard(context: Context, array: ByteArray) {
    val saveThis: String = Base64.encodeToString(array, Base64.NO_WRAP)

    context.dataStore.edit { settings ->
        settings[CARD_DETAIL] = saveThis
    }
}

suspend fun getIV(context: Context): ByteArray {

    val settings = context.dataStore.data.first()
    val string = settings[ENCRYPT_IV]

    return Base64.decode(string, Base64.NO_WRAP)
}

suspend fun getCardEncrypted(context: Context): ByteArray {

    val settings = context.dataStore.data.first()
    val string = settings[CARD_DETAIL]

    return Base64.decode(string, Base64.NO_WRAP)
}

suspend fun clearPreferences(context: Context) {
    context.dataStore.edit {
        it.clear()
    }
}