package com.winlite10

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.util.Locale

/**
 * MacLite: a native Android desktop-inspired shell.
 * This is not Apple's macOS and does not currently run Mac or Windows desktop apps.
 */
class MainActivity : Activity() {
    private val navy = Color.rgb(8, 18, 36)
    private val panel = Color.rgb(19, 31, 53)
    private val panelLight = Color.rgb(31, 48, 75)
    private val blue = Color.rgb(65, 158, 255)
    private val white = Color.rgb(244, 247, 255)
    private val muted = Color.rgb(170, 188, 215)
    private lateinit var workspace: File
    private var darkMode = true
    private var menuOpen = false
    private val importFileRequest = 1001
    private val openFileRequest = 1002
    private val prefs by lazy { getSharedPreferences("maclite_settings", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        darkMode = prefs.getBoolean("dark_mode", true)
        workspace = File(filesDir, "workspace").apply { mkdirs() }
        window.statusBarColor = Color.rgb(13, 32, 65)
        window.navigationBarColor = Color.BLACK
        try { showDesktop() } catch (e: Throwable) {
            android.util.Log.e("MacLite", "Startup failed", e)
            val page = page("MacLite couldn't start")
            page.addView(label(e.javaClass.simpleName + ": " + (e.message ?: "Unknown error")), matchWrap())
            page.addView(actionButton("Try again") { showDesktop() }, matchWrap())
            setContentView(page)
        }
    }

    private fun showDesktop() {
        menuOpen = false
        window.statusBarColor = Color.rgb(17, 46, 93)
        window.navigationBarColor = Color.BLACK
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(7), dp(12), dp(8))
            background = GradientDrawable(GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.rgb(100, 161, 226), Color.rgb(28, 72, 132), Color.rgb(8, 19, 43)))
        }

        val menuBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(10), dp(8), dp(10), dp(8))
            background = rounded(0xD9162C50.toInt(), 18, 0x33FFFFFF)
        }
        menuBar.addView(text("✦", 21f, white, true), wrapWrap())
        menuBar.addView(text("  MacLite", 15f, white, true), LinearLayout.LayoutParams(0, -2, 1f))
        menuBar.addView(actionButton("Finder") { showExplorer() }, wrapWrap())
        menuBar.addView(actionButton("⋯") { showQuickMenu() }, wrapWrap())
        root.addView(menuBar, matchWrap())

        val welcome = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(24), dp(12), dp(20))
        }
        welcome.addView(text("Good evening", 13f, 0xFFD9E9FF.toInt(), false), matchWrap())
        welcome.addView(text("Welcome to MacLite", 27f, white, true), matchWrap())
        welcome.addView(text("A desktop-inspired experience for your Android phone", 12f, 0xFFD4E3F8.toInt(), false), matchWrap())
        root.addView(welcome, matchWrap())

        val cards = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        cards.addView(launchCard("▧", "Finder", "Browse files and folders", blue) { showExplorer() }, matchWrap())
        cards.addView(launchCard("⚙", "System Settings", "Appearance and workspace controls", Color.rgb(139, 126, 255)) { showSettings() }, matchWrap())
        cards.addView(launchCard("◈", "About This MacLite", "Device details and project status", Color.rgb(45, 204, 153)) { showAbout() }, matchWrap())
        cards.addView(launchCard("⌘", "Compatibility Center", "Check what this build can do", Color.rgb(255, 174, 75)) { showCompatibility() }, matchWrap())
        root.addView(cards, LinearLayout.LayoutParams(-1, 0, 1f))

        val dock = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(6), dp(7), dp(6), dp(7))
            background = rounded(0xE61A2A43.toInt(), 22, 0x66FFFFFF)
        }
        dock.addView(dockItem("▧", "Finder") { showExplorer() }, LinearLayout.LayoutParams(0, dp(54), 1f))
        dock.addView(dockItem("⚙", "Settings") { showSettings() }, LinearLayout.LayoutParams(0, dp(54), 1f))
        dock.addView(dockItem("◉", "About") { showAbout() }, LinearLayout.LayoutParams(0, dp(54), 1f))
        dock.addView(dockItem("⌘", "Tools") { showQuickMenu() }, LinearLayout.LayoutParams(0, dp(54), 1f))
        root.addView(dock, matchWrap())
        root.addView(text("MacLite • Android " + android.os.Build.VERSION.RELEASE + " • Preview", 10f, 0xFFD2E3FA.toInt(), false).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(7), 0, 0)
        }, matchWrap())
        setContentView(root)
    }

    private fun launchCard(symbol: String, title: String, subtitle: String, tint: Int, action: () -> Unit): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(13), dp(10), dp(10), dp(10))
            background = rounded(if (darkMode) 0xD91A2B46.toInt() else 0xEAF6FAFF.toInt(), 19, 0x44FFFFFF)
            isClickable = true
            isFocusable = true
            setOnClickListener { action() }
        }
        val icon = TextView(this).apply {
            text = symbol
            textSize = 23f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = rounded(tint, 14, 0x44FFFFFF)
        }
        card.addView(icon, LinearLayout.LayoutParams(dp(48), dp(48)))
        val words = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, dp(5), 0)
            addView(text(title, 15f, if (darkMode) white else Color.rgb(25, 38, 58), true), matchWrap())
            addView(text(subtitle, 11f, if (darkMode) muted else Color.DKGRAY, false), matchWrap())
        }
        card.addView(words, LinearLayout.LayoutParams(0, -2, 1f))
        card.addView(text("›", 26f, white, false), wrapWrap())
        card.setMarginsCompat(dp(0), dp(4), dp(0), dp(4))
        return card
    }

    private fun LinearLayout.setMarginsCompat(l: Int, t: Int, r: Int, b: Int) {
        layoutParams = LinearLayout.LayoutParams(-1, -2).apply { setMargins(l, t, r, b) }
    }

    private fun dockItem(symbol: String, name: String, action: () -> Unit): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            isClickable = true
            setOnClickListener { action() }
            addView(text(symbol, 23f, white, true), wrapWrap())
            addView(text(name, 9f, 0xFFE0EBFA.toInt(), false), wrapWrap())
        }
    }

    private fun showQuickMenu() {
        val choices = arrayOf("Finder — file manager", "System Settings", "About MacLite", "Compatibility Center", "Diagnostics")
        AlertDialog.Builder(this).setTitle("MacLite menu").setItems(choices) { _, which ->
            when (which) {
                0 -> showExplorer()
                1 -> showSettings()
                2 -> showAbout()
                3 -> showCompatibility()
                4 -> showDiagnostics()
            }
        }.setNegativeButton("Close", null).show()
    }

    private fun page(title: String): LinearLayout {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            setBackgroundColor(if (darkMode) navy else Color.rgb(239, 244, 251))
        }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(12))
        }
        top.addView(actionButton("‹") { showDesktop() }, wrapWrap())
        top.addView(text(title, 22f, if (darkMode) white else Color.rgb(20, 33, 52), true).apply {
            setPadding(dp(10), dp(5), 0, dp(5))
        }, LinearLayout.LayoutParams(0, -2, 1f))
        root.addView(top, matchWrap())
        return root
    }

    private fun showExplorer() {
        val root = page("Finder")
        root.addView(text("ON MY DEVICE  /  MACLITE WORKSPACE", 10f, blue, true), matchWrap())
        root.addView(text("Private app storage • Your files stay in this app's workspace", 12f, muted, false), matchWrap())
        val buttons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        buttons.addView(actionButton("+ Folder") { createFolderDialog() }, LinearLayout.LayoutParams(0, -2, 1f))
        buttons.addView(actionButton("Import") { pickAnyFile() }, LinearLayout.LayoutParams(0, -2, 1f))
        buttons.addView(actionButton("Refresh") { showExplorer() }, LinearLayout.LayoutParams(0, -2, 1f))
        root.addView(buttons, matchWrap())
        val scroll = ScrollView(this)
        val listing = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(10), 0, dp(10))
        }
        val entries = workspace.listFiles()?.sortedBy { it.name.lowercase(Locale.ROOT) }.orEmpty()
        if (entries.isEmpty()) {
            val empty = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(12), dp(40), dp(12), dp(40))
                addView(text("▧", 42f, blue, true), wrapWrap())
                addView(text("This folder is empty", 17f, white, true), wrapWrap())
                addView(text("Import a file or create a folder to get started.", 12f, muted, false), wrapWrap())
            }
            listing.addView(empty, matchWrap())
        } else entries.forEach { file ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(8), dp(4), dp(4), dp(4))
                background = rounded(if (darkMode) panel else Color.WHITE, 14, 0x223A8DFF)
            }
            row.addView(text((if (file.isDirectory) "📁  " else "📄  ") + file.name, 13f,
                if (darkMode) white else Color.DKGRAY, false), LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(actionButton("Info") { showFileInfo(file) }, wrapWrap())
            row.addView(actionButton("×") { confirmDelete(file) }, wrapWrap())
            listing.addView(row, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(3), 0, dp(3)) })
        }
        scroll.addView(listing)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun showFileInfo(file: File) {
        AlertDialog.Builder(this).setTitle(file.name)
            .setMessage("Type: ${if (file.isDirectory) "Folder" else "File"}\nSize: ${if (file.isFile) file.length().toString() + " bytes" else "—"}\nPath: ${file.absolutePath}")
            .setPositiveButton("OK", null).show()
    }

    private fun confirmDelete(file: File) {
        AlertDialog.Builder(this).setTitle("Delete ${file.name}?")
            .setMessage("This removes the item from MacLite's private workspace.")
            .setNegativeButton("Cancel", null).setPositiveButton("Delete") { _, _ ->
                val ok = file.deleteRecursively()
                Toast.makeText(this, if (ok) "Deleted" else "Could not delete item", Toast.LENGTH_SHORT).show()
                showExplorer()
            }.show()
    }

    private fun createFolderDialog() {
        val input = EditText(this).apply { hint = "Folder name" }
        AlertDialog.Builder(this).setTitle("New Folder").setView(input)
            .setNegativeButton("Cancel", null).setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (isSafeName(name)) {
                    val ok = File(workspace, name).mkdir()
                    Toast.makeText(this, if (ok) "Folder created" else "Could not create folder", Toast.LENGTH_SHORT).show()
                    showExplorer()
                } else Toast.makeText(this, "Enter a simple folder name", Toast.LENGTH_LONG).show()
            }.show()
    }

    private fun isSafeName(name: String) = name.isNotBlank() && name != "." && name != ".." &&
        !name.contains("/") && !name.contains("\\") && !name.contains('\u0000')

    private fun pickAnyFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { addCategory(Intent.CATEGORY_OPENABLE); type = "*/*" }
        startActivityForResult(intent, openFileRequest)
    }

    private fun pickExecutable() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { addCategory(Intent.CATEGORY_OPENABLE); type = "*/*" }
        startActivityForResult(intent, importFileRequest)
    }

    @Deprecated("Legacy result API supports older Android versions")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        if (requestCode == importFileRequest) {
            AlertDialog.Builder(this).setTitle("Windows app selected")
                .setMessage("Selected: ${uri.lastPathSegment ?: uri}\n\nMacLite currently does not include Wine or a CPU translation engine, so this file cannot be executed yet.")
                .setPositiveButton("OK", null).show()
        } else if (requestCode == openFileRequest) importIntoWorkspace(uri)
    }

    private fun importIntoWorkspace(uri: Uri) {
        val displayName = queryDisplayName(uri) ?: "imported_${System.currentTimeMillis()}"
        val safeName = displayName.replace(Regex("[^A-Za-z0-9._ -]"), "_").take(100).ifBlank { "imported_file" }
        val target = uniqueFile(File(workspace, safeName))
        try {
            contentResolver.openInputStream(uri)?.use { input -> target.outputStream().use { output -> input.copyTo(output) } }
                ?: throw IllegalStateException("No input stream")
            Toast.makeText(this, "Imported ${target.name}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            target.delete()
            Toast.makeText(this, "Import failed: ${e.message ?: "unknown error"}", Toast.LENGTH_LONG).show()
        }
        showExplorer()
    }

    private fun queryDisplayName(uri: Uri): String? = try {
        contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
            if (it.moveToFirst()) it.getString(0) else null
        }
    } catch (_: Exception) { null }

    private fun uniqueFile(original: File): File {
        if (!original.exists()) return original
        val base = original.nameWithoutExtension
        val ext = original.extension.takeIf { it.isNotEmpty() }?.let { ".$it" } ?: ""
        var i = 1
        while (true) {
            val candidate = File(original.parentFile, "$base ($i)$ext")
            if (!candidate.exists()) return candidate
            i++
        }
    }

    private fun showSettings() {
        val root = page("System Settings")
        root.addView(settingsRow("Appearance", if (darkMode) "Dark glass" else "Light", "Change appearance") {
            darkMode = !darkMode
            prefs.edit().putBoolean("dark_mode", darkMode).apply()
            showSettings()
        }, matchWrap())
        root.addView(settingsRow("Workspace", "Private app folder", "Reset stored files") {
            AlertDialog.Builder(this).setTitle("Reset workspace?")
                .setMessage("This permanently deletes files stored in MacLite's private workspace.")
                .setNegativeButton("Cancel", null).setPositiveButton("Reset") { _, _ ->
                    workspace.listFiles()?.forEach { it.deleteRecursively() }
                    Toast.makeText(this, "Workspace reset", Toast.LENGTH_SHORT).show()
                    showSettings()
                }.show()
        }, matchWrap())
        root.addView(settingsRow("About", "MacLite Preview", "View device information") { showAbout() }, matchWrap())
        root.addView(settingsRow("Compatibility", "No desktop runtime bundled", "View limitations") { showCompatibility() }, matchWrap())
        setContentView(root)
    }

    private fun settingsRow(title: String, subtitle: String, actionText: String, action: () -> Unit): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(13), dp(12), dp(13), dp(8))
            background = rounded(if (darkMode) panel else Color.WHITE, 16, 0x223A8DFF)
        }
        card.addView(text(title, 15f, if (darkMode) white else Color.DKGRAY, true), matchWrap())
        card.addView(text(subtitle, 11f, muted, false), matchWrap())
        card.addView(actionButton(actionText, action), wrapWrap())
        card.layoutParams = LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(5), 0, dp(5)) }
        return card
    }

    private fun showAbout() {
        val root = page("About This MacLite")
        root.addView(text("✦", 54f, blue, true).apply { gravity = Gravity.CENTER; setPadding(0, dp(15), 0, dp(8)) }, matchWrap())
        root.addView(text("MacLite", 27f, white, true).apply { gravity = Gravity.CENTER }, matchWrap())
        root.addView(text("Version 1.0 • Android desktop concept", 12f, muted, false).apply { gravity = Gravity.CENTER }, matchWrap())
        root.addView(label("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}\nAndroid: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})\nMinimum Android: 10 (API 29)\nWorkspace: private app storage\nDesktop app runtime: not installed"), matchWrap())
        root.addView(actionButton("System Diagnostics") { showDiagnostics() }, matchWrap())
        setContentView(root)
    }

    private fun showCompatibility() {
        val root = page("Compatibility Center")
        root.addView(label("AVAILABLE NOW\n✓ Native Android interface\n✓ Finder-style private file browser\n✓ Import and manage files\n✓ Appearance setting\n✓ Device diagnostics"), matchWrap())
        root.addView(label("NOT AVAILABLE YET\n✕ Running macOS applications\n✕ Running Windows EXE/MSI applications\n✕ Actual Apple macOS virtualization"), matchWrap())
        root.addView(label("This project is a macOS-inspired desktop shell, not the Apple operating system. Desktop apps need a real compatible runtime; changing the UI alone cannot run them."), matchWrap())
        root.addView(actionButton("Choose EXE / MSI to inspect") { pickExecutable() }, matchWrap())
        setContentView(root)
    }

    private fun showDiagnostics() {
        val root = page("System Diagnostics")
        val am = getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager
        val mi = android.app.ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val info = """
            Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
            Android: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})
            Supported CPU ABIs: ${android.os.Build.SUPPORTED_ABIS.joinToString()}
            Total RAM: ${mi.totalMem / (1024 * 1024)} MB
            Available RAM: ${mi.availMem / (1024 * 1024)} MB
            Private workspace: ${workspace.absolutePath}
            macOS runtime: not included
            Windows runtime: not included
        """.trimIndent()
        root.addView(label(info), matchWrap())
        root.addView(actionButton("Refresh") { showDiagnostics() }, matchWrap())
        setContentView(root)
    }

    private fun label(value: String) = text(value, 13f, if (darkMode) white else Color.rgb(25, 43, 60), false).apply {
        setPadding(dp(12), dp(12), dp(12), dp(12))
        background = rounded(if (darkMode) panel else Color.WHITE, 16, 0x223A8DFF)
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(3), dp(3), dp(3), dp(3))
    }

    private fun actionButton(value: String, action: () -> Unit): Button = Button(this).apply {
        text = value
        isAllCaps = false
        textSize = 12f
        minHeight = dp(40)
        setTextColor(if (darkMode) white else Color.rgb(20, 36, 55))
        background = rounded(if (darkMode) panelLight else Color.rgb(231, 240, 250), 13, 0x448FCBFF)
        stateListAnimator = null
        setOnClickListener { action() }
    }

    private fun rounded(color: Int, radiusDp: Int, stroke: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radiusDp).toFloat()
        setStroke(dp(1), stroke)
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun wrapWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    override fun onBackPressed() { showDesktop() }
}
