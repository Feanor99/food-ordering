package com.yudistudios.foodland.ui.activities.main.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yudistudios.foodland.models.CreditCard
import com.yudistudios.foodland.repositories.CreditCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreditCardsViewModel @Inject constructor(
    private val creditCardRepository: CreditCardRepository
) : ViewModel() {

    val buttonNewCardIsClicked = MutableLiveData(false)

    fun buttonNewCardOnClick() {
        buttonNewCardIsClicked.value = true
    }

    suspend fun getCreditCard(context: Context): CreditCard? {
        return withContext(Dispatchers.IO) {
            creditCardRepository.getDefaultCreditCard(context).firstOrNull()
        }
    }
}