package com.cartrack.challenge.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.cartrack.challenge.RxImmediateSchedulerRule
import com.cartrack.challenge.TestSchedulerProvider
import com.cartrack.challenge.api.common.ApiResponse
import com.cartrack.challenge.api.common.Status
import com.cartrack.challenge.api.crimes.service.CrimeDataService
import io.reactivex.Observable
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response


@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Mock
    lateinit var userService: CrimeDataService

    @Mock
    lateinit var scheduler: TestSchedulerProvider

    @Mock
    lateinit var observerObject: Observer<ApiResponse<ArrayList<User>>>

    lateinit var crimesViewModel: CrimesViewModel

    private fun getErrorResponse() : String {
        return "fake-error-message-here"
    }

    @Before
    fun setup() {
        initMocks(this)
    }

    @Test
    fun shouldShowSendOtpSuccess() {
        val userResponse = ArrayList<User>()

        Mockito.`when`(userService.getCrimesByCustomArea())
                .thenReturn(Observable.just(Response.success(userResponse)))
        crimesViewModel = CrimesViewModel(userService, scheduler)
        crimesViewModel.crimesByAreaResponseLiveData.observeForever(observerObject)
        crimesViewModel.getUsers()

        assert(crimesViewModel.crimesByAreaResponseLiveData.value?.mStatus?.name == Status.SUCCESS.name)
        assert(crimesViewModel.crimesByAreaResponseLiveData.value?.mResponse == userResponse)
    }

    /*@Test
    fun shouldShowSendOtpError() {
        val otpBody = getOtpBody()
        val errorResponse = getErrorResponse()

        Mockito.`when`(userService.postVerify("fake-token", otpBody))
                .thenReturn(Observable.just(Response.error(422, ResponseBody.create(MediaType.parse("application/json"),
                        errorResponse.toString()))))

        usersViewModel.verifyResponseLiveData.observeForever(observerObject)
        usersViewModel.sendRequest("fake-token", otpBody)

        assert(usersViewModel.verifyResponseLiveData.value?.mStatus?.name == Status.ERROR.name)
        assert(usersViewModel.verifyResponseLiveData.value?.mError?.error?.message == errorResponse.error?.message)
    }*/


}

