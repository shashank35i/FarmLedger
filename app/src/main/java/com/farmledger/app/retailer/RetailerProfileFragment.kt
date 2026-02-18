package com.farmledger.app.retailer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.farmledger.app.AppPrefs
import com.farmledger.app.LoginActivity
import com.farmledger.app.R
import com.farmledger.app.Session
import com.google.android.material.button.MaterialButton

class RetailerProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_retailerprofile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvStoreName = view.findViewById<TextView>(R.id.tvStoreName)
        val tvSubtitle = view.findViewById<TextView>(R.id.tvSubtitle)

        val name = Session.name(requireContext())?.trim().orEmpty()
        tvStoreName.text = if (name.isNotBlank()) name else "Retailer"
        tvSubtitle.text = "Verified Retailer"

        bindRow(
            view.findViewById(R.id.rowStoreProfile),
            R.drawable.ic_user_outline,
            "Store Profile"
        ) { openFirstAvailable("com.farmledger.app.edit_profile") }

        bindRow(
            view.findViewById(R.id.rowNotifications),
            R.drawable.ic_bell_outline,
            "Notifications"
        ) {
            openFirstAvailable(
                "com.farmledger.app.NotificationsActivity",
                "com.farmledger.app.ui.NotificationsActivity"
            )
        }

        bindRow(
            view.findViewById(R.id.rowPrivacy),
            R.drawable.ic_shield_outline,
            "Privacy & Security"
        ) { openFirstAvailable("com.farmledger.app.PrivacyAndSecurityActivity") }

        bindRow(
            view.findViewById(R.id.rowHelp),
            R.drawable.ic_help_outline,
            "Help & Support"
        ) { openFirstAvailable("com.farmledger.app.HelpAndSupportActivity") }

        bindRow(
            view.findViewById(R.id.rowSettings),
            R.drawable.ic_settings_outline,
            "Settings"
        ) { openFirstAvailable("com.farmledger.app.dist.DistributorSettingsActivity", "com.farmledger.app.farmer_settings") }

        view.findViewById<MaterialButton>(R.id.btnSignOut).setOnClickListener { hardLogout() }
    }

    private fun hardLogout() {
        val ctx = requireContext()
        try {
            Session.clear(ctx)
            AppPrefs.clearPendingEmailVerification(ctx)
            try { com.farmledger.app.session.SessionManager(ctx).logout(userInitiated = true) } catch (_: Throwable) {}

            val i = Intent(ctx, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            requireActivity().finish()
        } catch (_: Throwable) {
            Toast.makeText(ctx, "Logout failed. Try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindRow(row: View, iconRes: Int, title: String, onClick: () -> Unit) {
        row.findViewById<AppCompatImageView>(R.id.ivIcon).setImageResource(iconRes)
        row.findViewById<TextView>(R.id.tvTitle).text = title
        row.setOnClickListener { onClick() }
    }

    private fun openFirstAvailable(vararg classNames: String) {
        val ctx = requireContext()
        for (name in classNames) {
            try {
                val cls = Class.forName(name)
                startActivity(Intent(ctx, cls))
                return
            } catch (_: Throwable) {}
        }
        Toast.makeText(ctx, "Coming soon", Toast.LENGTH_SHORT).show()
    }
}
