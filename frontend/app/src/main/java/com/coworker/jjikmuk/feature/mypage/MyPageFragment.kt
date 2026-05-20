package com.coworker.jjikmuk.feature.mypage

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.feature.mypage.family.FamilyAllergyFragment
import com.coworker.jjikmuk.feature.navigation.BottomNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private val viewModel: MyPageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindProfile(view)
        bindMyAllergies(view)
        bindFamilyShare(view)
        BottomNavController.bind(view, parentFragmentManager, requireContext())
    }

    private fun bindProfile(view: View) {
        view.findViewById<ImageButton>(R.id.btnMyBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        view.findViewById<ImageView>(R.id.ivMyProfileImage)
            .setImageResource(R.drawable.ic_launcher_foreground)
        view.findViewById<TextView>(R.id.tvMyName).text = viewModel.myProfile.name
    }

    private fun bindMyAllergies(view: View) {
        val tagContainer = view.findViewById<LinearLayout>(R.id.layoutMyAllergyTags)
        tagContainer.removeAllViews()
        viewModel.myProfile.allergies.forEach { allergy ->
            tagContainer.addView(createAllergyTag(allergy))
        }
    }

    private fun bindFamilyShare(view: View) {
        val selectedCount = viewModel.familyProfiles.size - 1
        view.findViewById<TextView>(R.id.tvFamilyShareCount).text = "나 외 ${selectedCount}명 참여 중"
        view.findViewById<TextView>(R.id.btnFamilyAllergySetting).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, FamilyAllergyFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun createAllergyTag(allergy: String): TextView {
        return TextView(requireContext()).apply {
            text = allergy
            textSize = 15f
            setTextColor(Color.RED)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tag_allergy)
            setPadding(dp(14), dp(10), dp(14), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = dp(10)
            }
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
