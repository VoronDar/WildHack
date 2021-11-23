package com.astery.thisapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.astery.thisapp.repository.Repository
import com.astery.thisapp.states.JFailure
import com.astery.thisapp.states.JIdle
import com.astery.thisapp.states.JRunning
import com.astery.thisapp.states.JSuccess
import com.astery.thisapp.ui.fragments.login.LoginViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertTrue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TestUsernameViewModel {
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
            repository.login(
                TestHelper.failureLogin(),
                TestHelper.successPassword()
            )
        } returns JFailure(LoginViewModel.InvalidLoginInput.Username)

        coEvery {
            repository.login(
                TestHelper.failureLogin(),
                TestHelper.failurePassword()
            )
        } returns JFailure(LoginViewModel.InvalidLoginInput.Both)

        coEvery {
            repository.login(
                TestHelper.successLogin(),
                TestHelper.failurePassword()
            )
        } returns JFailure(LoginViewModel.InvalidLoginInput.Password)

        // return success if each value is correct
        coEvery {
            repository.login(
                TestHelper.successLogin(),
                TestHelper.successPassword()
            )
        } returns JSuccess()

    }

    @Test
    fun checkInitialStateIsIdle() {
        assertLoginStateIs<JIdle>()
    }

    @Test
    fun checkIsItRunningStateAfterAuth() = runBlocking {
        viewModel.auth()
        viewModel.loginState.observeForever {
            when (it) {
                is JIdle -> {
                }
                is JRunning -> cancel()
                else -> assertLoginStateIs<JRunning>()
            }
        }
    }

    @Test
    fun checkIsItInvalidInputStateWithAllInvalidData() = runBlocking {
        viewModel.username.value = TestHelper.invalidLogin()
        viewModel.password.value = TestHelper.invalidPassword()
        viewModel.auth()
        assertEquals(JFailure(LoginViewModel.InvalidLoginInput.Both), viewModel.loginState.value)
    }

    @Test
    fun checkIsItMistakeStateWithInvalidUsername() = runBlocking {
        viewModel.username.value = TestHelper.invalidLogin()
        viewModel.password.value = TestHelper.successPassword()
        viewModel.auth()
        assertEquals(
            JFailure(LoginViewModel.InvalidLoginInput.Username),
            viewModel.loginState.value
        )
    }

    @Test
    fun checkIsItMistakeStateWithAllInvalidPassword() = runBlocking {
        viewModel.username.value = TestHelper.successLogin()
        viewModel.password.value = TestHelper.invalidPassword()
        viewModel.auth()
        assertEquals(
            JFailure(LoginViewModel.InvalidLoginInput.Password),
            viewModel.loginState.value
        )
    }

    @Test
    fun checkIsItSuccessWithCorrectData() = runBlocking {
        viewModel.username.value = TestHelper.successLogin()
        viewModel.password.value = TestHelper.successPassword()
        viewModel.auth()
        assertLoginStateIs<JSuccess>()
    }


    @Test
    fun checkIsItFailureWithFailurePassword() = runBlocking {
        viewModel.username.value = TestHelper.successLogin()
        viewModel.password.value = TestHelper.failurePassword()
        viewModel.auth()
        assertEquals(
            JFailure(LoginViewModel.InvalidLoginInput.Password),
            viewModel.loginState.value
        )
    }

    @Test
    fun checkIsItFailureWithFailureLogin() = runBlocking {
        viewModel.username.value = TestHelper.failureLogin()
        viewModel.password.value = TestHelper.successPassword()
        viewModel.auth()
        assertEquals(
            JFailure(LoginViewModel.InvalidLoginInput.Username),
            viewModel.loginState.value
        )
    }

    @Test
    fun checkIsItFailureWithFailureLoginAndPassword() = runBlocking {
        viewModel.username.value = TestHelper.failureLogin()
        viewModel.password.value = TestHelper.failurePassword()
        viewModel.auth()
        assertEquals(JFailure(LoginViewModel.InvalidLoginInput.Both), viewModel.loginState.value)
    }

    private inline fun <reified T> assertLoginStateIs() {
        assertTrue(
            "login state is invalid ,required ${T::class.simpleName}, actual - " +
                    "${viewModel.loginState.value!!::class.simpleName}",
            viewModel.loginState.value is T
        )
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

