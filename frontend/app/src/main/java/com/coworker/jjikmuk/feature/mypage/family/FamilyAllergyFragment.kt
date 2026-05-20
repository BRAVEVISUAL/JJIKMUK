package com.coworker.jjikmuk.feature.mypage.family

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
import com.coworker.jjikmuk.domain.model.ProfileRelation
import com.coworker.jjikmuk.domain.model.UserProfile
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FamilyAllergyFragment : Fragment(R.layout.fragment_family_allergy) {

    private val viewModel: FamilyAllergyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnFamilyAllergyBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<TextView>(R.id.tvFamilyMemberCount).text =
            "가족 구성원 (${viewModel.profiles.size}명)"
        bindFamilyMembers(view)
    }

    private fun bindFamilyMembers(view: View) {
        val listContainer = view.findViewById<LinearLayout>(R.id.layoutFamilyMemberList)
        listContainer.removeAllViews()

        viewModel.profiles.forEach { profile ->
            val itemView = layoutInflater.inflate(
                R.layout.item_family_allergy_member,
                listContainer,
                false
            )

            itemView.findViewById<ImageView>(R.id.ivFamilyMemberImage)
                .setImageResource(R.drawable.ic_launcher_foreground)
            itemView.findViewById<TextView>(R.id.tvFamilyMemberName).text = profile.name
            itemView.findViewById<TextView>(R.id.tvFamilyMemberRelation).text = profile.relation.toLabel()
            itemView.findViewById<TextView>(R.id.tvFamilyMemberAllergyCount).text =
                "${profile.allergies.size}개의 알레르기 성분"

            val tagContainer = itemView.findViewById<LinearLayout>(R.id.layoutFamilyMemberAllergyTags)
            profile.allergies.forEach { allergy ->
                tagContainer.addView(createAllergyTag(allergy))
            }

            listContainer.addView(itemView)
        }
    }

    private fun createAllergyTag(allergy: String): TextView {
        return TextView(requireContext()).apply {
            text = allergy
            textSize = 13f
            setTextColor(Color.RED)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tag_allergy)
            setPadding(dp(10), dp(6), dp(10), dp(6))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = dp(8)
            }
        }
    }

    private fun ProfileRelation.toLabel(): String {
        return when (this) {
            ProfileRelation.ME -> "나 (관리자)"
            ProfileRelation.COWORKER -> "배우자"
            ProfileRelation.FAMILY -> "자녀"
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
