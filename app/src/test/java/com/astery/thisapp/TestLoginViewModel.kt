package com.astery.thisapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.astery.thisapp.repository.Repository
import com.astery.thisapp.ui.fragments.login.JobState
import com.astery.thisapp.ui.fragments.login.LoginViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TestLoginViewModel {
    private lateinit var repository: Repository
    private lateinit var viewModel: LoginViewModel


    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)


    @Before
    fun createViewModel() {
        this.repository = mockk()
        this.viewModel = LoginViewModel()
        this.viewModel.repository = repository
    }

    @Before
    fun mockRepository() {
        // return failure if at least one value is failure
        coEvery {
            repository.auth(
                TestHelper.failureLogin(),
                TestHelper.successPassword()
            )
        } returns JobState.Failure

        coEvery {
            repository.auth(
                TestHelper.failureLogin(),
                TestHelper.failurePassword()
            )
        } returns JobState.Failure

        coEvery {
            repository.auth(
                TestHelper.successLogin(),
                TestHelper.failurePassword()
            )
        } returns JobState.Failure

        // return success if each value is correct
        coEvery {
            repository.auth(
                TestHelper.successLogin(),
                TestHelper.successPassword()
            )
        } returns JobState.Success

    }

    @Test
    fun checkInitialStateIsIdle() {
        assertEquals(JobState.Idle, viewModel.loginState.value)
    }


    // TODO(it doesn't work, but I don't know what to do)
    //@Test
    fun checkIsItRunningStateAfterAuth() = runBlocking {
        viewModel.auth()
        viewModel.loginState.observeForever {
            when (it){
                JobState.Idle -> {}
                JobState.Running -> cancel()
                else -> assertEquals(JobState.Running, viewModel.loginState.value)
            }
        }
    }

    @Test
    fun checkIsItMistakeStateWithAllInvalidData() = runBlocking {
        viewModel.login.value = TestHelper.invalidLogin()
        viewModel.password.value = TestHelper.invalidPassword()
        viewModel.auth()
        assertEquals(JobState.Mistake, viewModel.loginState.value)
    }

    @Test
    fun checkIsItMistakeStateWithInvalidLogin() = runBlocking {
        viewModel.login.value = TestHelper.invalidLogin()
        viewModel.password.value = TestHelper.successPassword()
        viewModel.auth()
        assertEquals(JobState.Mistake, viewModel.loginState.value)
    }

    @Test
    fun checkIsItMistakeStateWithAllInvalidPassword() = runBlocking {
        viewModel.login.value = TestHelper.successLogin()
        viewModel.password.value = TestHelper.invalidPassword()
        viewModel.auth()
        assertEquals(JobState.Mistake, viewModel.loginState.value)
    }

    @Test
    fun checkIsItSuccessWithCorrectData() = runBlocking {
        viewModel.login.value = TestHelper.successLogin()
        viewModel.password.value = TestHelper.successPassword()
        viewModel.auth()
        assertEquals(JobState.Success, viewModel.loginState.value)
    }


    @Test
    fun checkIsItFailureWithFailurePassword() = runBlocking {
        viewModel.login.value = TestHelper.successLogin()
        viewModel.password.value = TestHelper.failurePassword()
        viewModel.auth()
        assertEquals(JobState.Failure, viewModel.loginState.value)
    }

    @Test
    fun checkIsItFailureWithFailureLogin() = runBlocking {
        viewModel.login.value = TestHelper.failureLogin()
        viewModel.password.value = TestHelper.successPassword()
        viewModel.auth()
        assertEquals(JobState.Failure, viewModel.loginState.value)
    }

    @Test
    fun checkIsItFailureWithFailureLoginAndPassword() = runBlocking {
        viewModel.login.value = TestHelper.failureLogin()
        viewModel.password.value = TestHelper.failurePassword()
        viewModel.auth()
        assertEquals(JobState.Failure, viewModel.loginState.value)
    }


    object TestHelper {
        fun invalidLogin() = ""
        fun invalidPassword() = ""
        fun successPassword() = "success"
        fun failurePassword() = "failure"
        fun successLogin() = "success"
        fun failureLogin() = "failure"
    }
}

