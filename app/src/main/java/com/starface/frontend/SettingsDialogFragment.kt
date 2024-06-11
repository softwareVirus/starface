package com.starface.frontend
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

class SettingsDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle("Settings")
            .setMessage("Settings options can be added here.")
            .setPositiveButton("OK") { _, _ -> }
            .create()
    }
}
