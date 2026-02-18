package com.farmledger.app.dist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.farmledger.app.AppPrefs
import com.farmledger.app.LoginActivity
import com.farmledger.app.R
import com.farmledger.app.Session
import com.farmledger.app.session.DistributorSessionManager

class DistributorProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dist__profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvCompany = view.findViewById<TextView>(R.id.tvCompanyName)
        val tvSubtitle = view.findViewById<TextView>(R.id.tvSubtitle)

        tvCompany.text = Session.name(requireContext()).takeIf { !it.isNullOrBlank() } ?: "FastTrans Logistics"
        tvSubtitle.text = "Authorized Distributor"

        // Remove/hide Vehicle row from UI (ID still exists in XML)
        view.findViewById<View>(R.id.rowVehicle)?.visibility = View.GONE

        bindRow(
            view,
            rowId = R.id.rowCompany,
            icon = R.drawable.ic_user_outline,
            title = "Company Profile"
        ) { openFirstAvailable("com.farmledger.app.edit_profile") }

        bindRow(
            view,
            rowId = R.id.rowNotifications,
            icon = R.drawable.ic_bell_outline,
            title = "Notifications"
        ) {
            openFirstAvailable(
                "com.farmledger.app.NotificationsActivity",
                "com.farmledger.app.ui.NotificationsActivity"
            )
        }

        bindRow(
            view,
            rowId = R.id.rowPrivacy,
            icon = R.drawable.ic_shield_outline,
            title = "Privacy & Security"
        ) { openFirstAvailable("com.farmledger.app.PrivacyAndSecurityActivity") }

        bindRow(
            view,
            rowId = R.id.rowHelp,
            icon = R.drawable.ic_help_outline,
            title = "Help & Support"
        ) { openFirstAvailable("com.farmledger.app.HelpAndSupportActivity") }

        bindRow(
            view,
            rowId = R.id.rowSettings,
            icon = R.drawable.ic_settings_outline,
            title = "Settings"
        ) { openFirstAvailable("com.farmledger.app.dist.DistributorSettingsActivity", "com.farmledger.app.farmer_settings") }

        view.findViewById<View>(R.id.btnSignOut).setOnClickListener {
            hardLogout()
        }
    }

    private fun bindRow(
        root: View,
        rowId: Int,
        icon: Int,
        title: String,
        onClick: () -> Unit
    ) {
        val row = root.findViewById<View>(rowId)
        val iv = row.findViewById<android.widget.ImageView>(R.id.ivIcon)
        val tv = row.findViewById<TextView>(R.id.tvTitle)
        iv.setImageResource(icon)
        tv.text = title
        row.setOnClickListener { onClick() }
    }

    private fun hardLogout() {
        val ctx = requireContext()
        try {
            Session.clear(ctx)
            AppPrefs.clearPendingEmailVerification(ctx)
            try { DistributorSessionManager(ctx).clearSession() } catch (_: Throwable) {}
            try { com.farmledger.app.session.SessionManager(ctx).logout(userInitiated = true) } catch (_: Throwable) {}

            val i = Intent(ctx, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            requireActivity().finish()
        } catch (_: Throwable) {
            Toast.makeText(ctx, "Logout failed. Try again.", Toast.LENGTH_SHORT).show()
        }
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
