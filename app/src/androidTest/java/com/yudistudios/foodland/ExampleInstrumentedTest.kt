package com.yudistudios.foodland

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yudistudios.foodland.repositories.FoodRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ExampleInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var foodRepository: FoodRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun getAllFoods() {
//        foodRepository.getAllFoods()
//
//        runBlocking {
//            delay(3000)
//        }
//
//        assertNotEquals(-1, foodRepository.getAllFoodsResponseCode)
//        assertEquals(200, foodRepository.getAllFoodsResponseCode)
//        assertNotEquals(null, foodRepository.foods.value)
    }

}