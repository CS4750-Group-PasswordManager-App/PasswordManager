package com.bignerdranch.android.passwordmanager

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)

class CryptoManager {

    private val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    private fun createKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "secret",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(PADDING)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()

    }


    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }


    private fun getKey(): SecretKey {
        val existingKey =  keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    fun encrypt(data: String): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        var temp = data

        while(temp.toByteArray().size % 16 != 0) {
            temp += "\u0020"
        }

        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val ivBytes = cipher.iv

        val encryptedBytes = cipher.doFinal(temp.toByteArray(Charsets.UTF_8))

        return Pair(encryptedBytes, ivBytes)
    }

    fun decrypt(data: ByteArray, ivBytes: ByteArray): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = IvParameterSpec(ivBytes)

        cipher.init(Cipher.DECRYPT_MODE,getKey(), spec)

        return String(cipher.doFinal(data)).trim()
    }

}