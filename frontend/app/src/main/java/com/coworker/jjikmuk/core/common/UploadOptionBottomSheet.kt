package com.coworker.jjikmuk.core.common

import android.view.View
import androidx.fragment.app.Fragment
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.domain.model.UploadOption
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.showUploadOptionBottomSheet(
    onOptionSelected: (UploadOption) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext())
    val bottomSheetView = layoutInflater.inflate(
        R.layout.bottom_sheet_home_upload_options,
        null
    )

    bottomSheetView.findViewById<View>(R.id.layoutTakePhoto).setOnClickListener {
        dialog.dismiss()
        onOptionSelected(UploadOption.TAKE_PHOTO)
    }

    bottomSheetView.findViewById<View>(R.id.layoutUploadImage).setOnClickListener {
        dialog.dismiss()
        onOptionSelected(UploadOption.UPLOAD_IMAGE)
    }

    bottomSheetView.findViewById<View>(R.id.layoutUploadFile).setOnClickListener {
        dialog.dismiss()
        onOptionSelected(UploadOption.UPLOAD_FILE)
    }

    dialog.setContentView(bottomSheetView)
    dialog.show()
}
