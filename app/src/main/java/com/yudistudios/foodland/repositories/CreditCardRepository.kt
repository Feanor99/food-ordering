package com.yudistudios.foodland.repositories

import android.content.Context
import com.yudistudios.foodland.models.CreditCard
import com.yudistudios.foodland.security.SecurityUtils
import com.yudistudios.foodland.utils.getCardEncrypted
import com.yudistudios.foodland.utils.getIV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class CreditCardRepository @Inject constructor() {

    suspend fun getDefaultCreditCard(context: Context): Flow<CreditCard?> {
        return flow {
            try {
                val securityUtils = SecurityUtils()
                val iv = getIV(context)
                val card = getCardEncrypted(context)
                val stringCreditCard = securityUtils.decrypt(card, iv)
                val fields = stringCreditCard?.split(",")
                fields?.let {
                    val creditCard = CreditCard(
                        bank = it[0],
                        cardNo = it[1],
                        holderName = it[2],
                        expireMonth = it[3],
                        expireYear = it[4],
                        CVV = it[5]
                    )
                    emit(creditCard)
                }
            } catch (e: Exception) {
                Timber.e("$e")
            }

        }
    }

}