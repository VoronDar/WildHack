package com.astery.thisapp.ui.fragments.login

import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import com.astery.thisapp.R
import com.astery.thisapp.databinding.FragmentLoginBinding
import com.astery.thisapp.states.*
import com.astery.thisapp.ui.activity.MainActivity
import com.astery.thisapp.ui.fragments.TFragment
import com.astery.thisapp.ui.fragments.main.MainFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : TFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loginState.observe(viewLifecycleOwner) {
            if (it is JSuccess) {
                (activity as MainActivity).move(MainFragment())
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    object BindingAdapters {

        @BindingAdapter("app:enabled")
        @JvmStatic
        fun setEnabled(view: View?, state: JobState) {
            val enabled = state !is JRunning
            view?.isEnabled = enabled

            if (view is MaterialButton) {
                (view).setBackgroundColor(
                    if (enabled) ContextCompat.getColor(view.context, R.color.button)
                    else ContextCompat.getColor(view.context, R.color.disabled_button)
                )
                (view).setTextColor(
                    if (enabled) ContextCompat.getColor(view.context, R.color.white)
                    else ContextCompat.getColor(view.context, R.color.disabled_button_text)
                )
            }
        }

        @BindingAdapter("android:text")
        @JvmStatic
        fun setErrorText(view: TextView?, state: JobState) {
            if (state is JFailure) view?.setText(state.reason.stringId())
            else if (state is JError) view?.setText(state.error.stringId())
        }


        @BindingAdapter("app:usernameIncorrect")
        @JvmStatic
        fun setUsernameIncorrect(view: TextInputEditText?, state: JobState) {
            setTextIncorrectColor(
                view,
                (state is JFailure && (state.reason == LoginViewModel.InvalidLoginInput.Username ||
                        state.reason == LoginViewModel.InvalidLoginInput.Both))
            )
        }

        @BindingAdapter("app:passwordIncorrect")
        @JvmStatic
        fun setPasswordIncorrect(view: TextInputEditText?, state: JobState) {
            setTextIncorrectColor(
                view,
                ((state is JFailure && (state.reason == LoginViewModel.InvalidLoginInput.Password ||
                        state.reason == LoginViewModel.InvalidLoginInput.Both)))
            )
        }

        private fun setTextIncorrectColor(view: TextInputEditText?, error: Boolean) {
            Timber.d("set textIncorrect color ${view?.id}, $error")
            view?.setTextColor(
                if (error) ContextCompat.getColor(view.context, R.color.error_text) else
                    ContextCompat.getColor(view.context, R.color.text)
            )
        }


        /** show "invalid login" error card */
        @BindingAdapter("app:showError")
        @JvmStatic
        fun setShowError(view: View?, state: JobState) {
            if (state is JFailure || state is JError) {
                val auth = view?.findViewById<View>(R.id.auth_card)!!
                val error = view.findViewById<View>(R.id.error_card)!!

                if (!error.isVisible) animateTranslation(error, auth)
                else animateBlink(error)
            }
        }


        private fun animateBlink(error: View) {
            val alphaAnimator = ValueAnimator.ofFloat(1f, 0.5f)
            alphaAnimator.addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                error.alpha = value
            }
            alphaAnimator.repeatMode = REVERSE
            alphaAnimator.repeatCount = 1
            alphaAnimator.interpolator = AccelerateDecelerateInterpolator()
            alphaAnimator.duration = 150
            alphaAnimator.start()
        }

        private fun animateTranslation(error: View, auth: View) {
            error.isVisible = true
            val start = -error.height.toFloat() - 12
            error.translationY = start


            val transitionAnimator = ValueAnimator.ofFloat(0f, (error.height / 2).toFloat())
            transitionAnimator.addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                auth.translationY = -value
                error.translationY = value + start
            }
            transitionAnimator.duration = 300
            transitionAnimator.interpolator = DecelerateInterpolator()
            transitionAnimator.start()
        }
    }
}