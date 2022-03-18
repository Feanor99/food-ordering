package com.yudistudios.foodland.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.yudistudios.foodland.utils.saveIV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


class SecurityUtils {

    companion object {
        private val TRANSFORMATION: String = "AES/GCM/NoPadding"
        private val ANDROID_KEY_STORE: String = "AndroidKeyStore"
        private val SAMPLE_ALIAS: String = "KEYSTORE_ALIAS"
    }

    private lateinit var keyStore: KeyStore

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun getSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            SAMPLE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    @Throws(Exception::class)
    fun encrypt(
        context: Context,
        plaintext: ByteArray
    ): ByteArray? {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        CoroutineScope(Dispatchers.IO).launch {
            saveIV(context, cipher.iv!!)
        }
        return cipher.doFinal(plaintext)
    }

    fun decrypt(cipherText: ByteArray?, encryptIV: ByteArray): String? {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore.load(null)
            val secretKeyEntry = keyStore
                .getEntry(SAMPLE_ALIAS, null) as KeyStore.SecretKeyEntry

            val secretKey = secretKeyEntry.secretKey
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, encryptIV)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedText = cipher.doFinal(cipherText)
            return String(decryptedText)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}
