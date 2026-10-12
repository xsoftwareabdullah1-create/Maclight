package com.winlite10

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
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
 * WinLite 10 prototype shell.
 *
 * This first milestone is a native desktop-like Android shell. It does NOT yet
 * bundle Wine, a Linux userspace, or x86/x64 translation, so it cannot execute EXE files.
 */
class MainActivity : Activity() {
    private val bg = Color.rgb(7, 20, 38)
    private val panel = Color.rgb(13, 34, 59)
    private val panel2 = Color.rgb(20, 48, 78)
    private val accent = Color.rgb(35, 145, 240)
    private val textColor = Color.rgb(235, 243, 252)
    private val muted = Color.rgb(165, 190, 215)

    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var status: TextView
    private var startMenuVisible = false
    private var darkMode = true

    private val prefs by lazy { getSharedPreferences("winlite_settings", MODE_PRIVATE) }
    private val workspace by lazy { File(filesDir, "workspace").apply { mkdirs() } }

    private val importFileRequest = 1001
    private val openFileRequest = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        darkMode = prefs.getBoolean("dark_mode", true)
        showDesktop()
    }

    private fun baseLayout(title: String): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(if (darkMode) bg else Color.rgb(225, 236, 247))
            setPadding(dp(12), dp(12), dp(12), dp(8))
        }
        val header = TextView(this).apply {
            text = title
            textSize = 21f
            setTextColor(if (darkMode) textColor else Color.rgb(15, 35, 55))
            setPadding(dp(4), dp(8), dp(4), dp(12))
        }
        layout.addView(header, matchWrap())
        return layout
    }

    private fun showDesktop() {
        root = baseLayout("WinLite 10")
        val subtitle = label("A lightweight Windows-style workspace")
        root.addView(subtitle, matchWrap())

        val info = label("Prototype milestone 1  •  Android ${android.os.Build.VERSION.RELEASE}")
        info.setPadding(dp(4), dp(3), dp(4), dp(12))
        root.addView(info, matchWrap())

        val desktop = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(if (darkMode) panel else Color.WHITE)
            setPadding(dp(10), dp(10), dp(10), dp(10))
        }
        desktop.addView(label("DESKTOP"), matchWrap())
        val shortcuts = listOf(
            "📁  File Explorer" to ::showExplorer,
            "🧰  App compatibility" to ::showCompatibility,
            "⚙️  Settings" to ::showSettings,
            "🖥️  System diagnostics" to ::showDiagnostics
        )
        shortcuts.forEach { (name, action) ->
            desktop.addView(actionButton(name, action), matchWrap())
        }
        root.addView(desktop, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        val taskbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), 0, 0)
        }
        taskbar.addView(actionButton("⊞ Start") { toggleStartMenu() },
            LinearLayout.LayoutParams(0, dp(48), 1f))
        taskbar.addView(actionButton("Explorer") { showExplorer() },
            LinearLayout.LayoutParams(0, dp(48), 1f))
        taskbar.addView(actionButton("Settings") { showSettings() },
            LinearLayout.LayoutParams(0, dp(48), 1f))
        root.addView(taskbar, matchWrap())

        status = label("Ready  •  Workspace stored inside app")
        status.textSize = 11f
        status.setPadding(dp(4), dp(8), dp(4), 0)
        root.addView(status, matchWrap())
        setContentView(root)
    }

    private fun toggleStartMenu() {
        if (startMenuVisible) {
            showDesktop()
            return
        }
        startMenuVisible = true
        val menu = baseLayout("Start")
        menu.addView(label("Pinned tools"), matchWrap())
        menu.addView(actionButton("File Explorer") { startMenuVisible = false; showExplorer() }, matchWrap())
        menu.addView(actionButton("Import Windows EXE / MSI") { startMenuVisible = false; pickExecutable() }, matchWrap())
        menu.addView(actionButton("Compatibility status") { startMenuVisible = false; showCompatibility() }, matchWrap())
        menu.addView(actionButton("Settings") { startMenuVisible = false; showSettings() }, matchWrap())
        menu.addView(actionButton("Back to desktop") { startMenuVisible = false; showDesktop() }, matchWrap())
        setContentView(menu)
    }

    private fun showExplorer() {
        val page = baseLayout("File Explorer")
        page.addView(label("App workspace: /workspace"), matchWrap())
        page.addView(actionButton("Create folder") { createFolderDialog() }, matchWrap())
        page.addView(actionButton("Import file from device") { pickAnyFile() }, matchWrap())
        page.addView(actionButton("Refresh") { showExplorer() }, matchWrap())

        val scroll = ScrollView(this)
        val listing = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, 0)
        }
        val entries = workspace.listFiles()?.sortedBy { it.name.lowercase(Locale.ROOT) }.orEmpty()
        if (entries.isEmpty()) {
            listing.addView(label("This folder is empty. Import a file or create a folder."), matchWrap())
        } else {
            entries.forEach { file ->
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(6), dp(3), dp(2), dp(3))
                }
                val name = TextView(this).apply {
                    text = (if (file.isDirectory) "📁  " else "📄  ") + file.name
                    setTextColor(if (darkMode) textColor else Color.DKGRAY)
                    textSize = 14f
                    setPadding(dp(5), dp(10), dp(5), dp(10))
                }
                row.addView(name, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                row.addView(actionButton("Info") { showFileInfo(file) }, wrapWrap())
                row.addView(actionButton("Delete") { confirmDelete(file) }, wrapWrap())
                listing.addView(row, matchWrap())
            }
        }
        scroll.addView(listing)
        page.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))
        page.addView(actionButton("← Desktop") { showDesktop() }, matchWrap())
        setContentView(page)
    }

    private fun showFileInfo(file: File) {
        AlertDialog.Builder(this)
            .setTitle(file.name)
            .setMessage("Type: ${if (file.isDirectory) "Folder" else "File"}\nSize: ${if (file.isFile) file.length() else "—"} bytes\nPath: ${file.absolutePath}")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun confirmDelete(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Delete ${file.name}?")
            .setMessage("This permanently removes the item from WinLite's private workspace.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                val ok = file.deleteRecursively()
                Toast.makeText(this, if (ok) "Deleted" else "Could not delete item", Toast.LENGTH_SHORT).show()
                showExplorer()
            }.show()
    }

    private fun createFolderDialog() {
        val input = EditText(this).apply { hint = "Folder name" }
        AlertDialog.Builder(this)
            .setTitle("Create folder")
            .setView(input)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (isSafeName(name)) {
                    val created = File(workspace, name).mkdir()
                    Toast.makeText(this, if (created) "Folder created" else "Folder could not be created", Toast.LENGTH_SHORT).show()
                    showExplorer()
                } else {
                    Toast.makeText(this, "Use a simple folder name without path separators", Toast.LENGTH_LONG).show()
                }
            }.show()
    }

    private fun isSafeName(name: String): Boolean =
        name.isNotBlank() && name != "." && name != ".." &&
            !name.contains("/") && !name.contains("\\") && !name.contains('\u0000')

    private fun pickAnyFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, openFileRequest)
    }

    private fun pickExecutable() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, importFileRequest)
    }

    @Deprecated("Uses activity result API compatible with older Android releases")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        if (requestCode == importFileRequest) {
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {
                // Some document providers do not grant persistable permissions.
            }
            AlertDialog.Builder(this)
                .setTitle("File selected")
                .setMessage("Selected: ${uri.lastPathSegment ?: uri}\n\nThis prototype can select an EXE/MSI, but it cannot run Windows programs yet because the Wine engine is not integrated.")
                .setPositiveButton("OK", null)
                .show()
        } else if (requestCode == openFileRequest) {
            importIntoWorkspace(uri)
        }
    }

    private fun importIntoWorkspace(uri: Uri) {
        val displayName = queryDisplayName(uri) ?: "imported_${System.currentTimeMillis()}"
        val safeName = displayName.replace(Regex("[^A-Za-z0-9._ -]"), "_").take(100).ifBlank { "imported_file" }
        val target = uniqueFile(File(workspace, safeName))
        try {
            contentResolver.openInputStream(uri)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            } ?: throw IllegalStateException("Provider returned no input stream")
            Toast.makeText(this, "Imported ${target.name}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            target.delete()
            Toast.makeText(this, "Import failed: ${e.message ?: "unknown error"}", Toast.LENGTH_LONG).show()
        }
        showExplorer()
    }

    private fun queryDisplayName(uri: Uri): String? {
        return try {
            contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
        } catch (_: Exception) { null }
    }

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

    private fun showCompatibility() {
        val page = baseLayout("Compatibility")
        page.addView(label("Current status: desktop shell only"), matchWrap())
        page.addView(label("✓ Native Android interface\n✓ Private persistent workspace\n✓ Import files from Android storage\n✓ Select EXE/MSI files for inspection\n✗ Wine runtime not yet bundled\n✗ x86/x64 CPU translation not yet integrated\n✗ EXE/MSI execution is not available in this milestone"), matchWrap())
        page.addView(label("An EXE is not an Android app. Running it requires a compatible Wine userspace and, for x86/x64 binaries on ARM, a suitable translation layer."), matchWrap())
        page.addView(actionButton("Import EXE / MSI") { pickExecutable() }, matchWrap())
        page.addView(actionButton("← Desktop") { showDesktop() }, matchWrap())
        setContentView(page)
    }

    private fun showSettings() {
        val page = baseLayout("Settings")
        page.addView(label("Appearance"), matchWrap())
        page.addView(actionButton(if (darkMode) "Switch to light theme" else "Switch to dark theme") {
            darkMode = !darkMode
            prefs.edit().putBoolean("dark_mode", darkMode).apply()
            showSettings()
        }, matchWrap())
        page.addView(label("Target device guidance"), matchWrap())
        page.addView(label("This app targets Android API 27 (Android 8.1) and newer. Real Wine support depends on CPU architecture, native libraries, graphics/input integration, and available RAM."), matchWrap())
        page.addView(actionButton("Reset workspace…") {
            AlertDialog.Builder(this)
                .setTitle("Reset workspace?")
                .setMessage("This deletes files stored inside WinLite's private workspace. Export anything important first.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Reset") { _, _ ->
                    workspace.listFiles()?.forEach { it.deleteRecursively() }
                    Toast.makeText(this, "Workspace reset", Toast.LENGTH_SHORT).show()
                    showSettings()
                }.show()
        }, matchWrap())
        page.addView(actionButton("← Desktop") { showDesktop() }, matchWrap())
        setContentView(page)
    }

    private fun showDiagnostics() {
        val page = baseLayout("System diagnostics")
        val abi = android.os.Build.SUPPORTED_ABIS.joinToString()
        val mem = getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager
        val mi = android.app.ActivityManager.MemoryInfo()
        mem.getMemoryInfo(mi)
        val info = """
            Android: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})
            Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
            Supported ABIs: $abi
            Total RAM reported by Android: ${mi.totalMem / (1024 * 1024)} MB
            Available RAM: ${mi.availMem / (1024 * 1024)} MB
            App workspace: ${workspace.absolutePath}
            Wine engine: not integrated
        """.trimIndent()
        page.addView(label(info), matchWrap())
        page.addView(actionButton("Refresh diagnostics") { showDiagnostics() }, matchWrap())
        page.addView(actionButton("← Desktop") { showDesktop() }, matchWrap())
        setContentView(page)
    }

    override fun onBackPressed() {
        startMenuVisible = false
        showDesktop()
    }

    private fun label(value: String): TextView = TextView(this).apply {
        text = value
        textSize = 14f
        setTextColor(if (darkMode) textColor else Color.rgb(25, 43, 60))
        setPadding(dp(5), dp(7), dp(5), dp(7))
    }

    private fun actionButton(value: String, action: () -> Unit): Button = Button(this).apply {
        text = value
        isAllCaps = false
        textSize = 13f
        setTextColor(if (darkMode) textColor else Color.rgb(15, 35, 55))
        background = android.graphics.drawable.GradientDrawable().apply {
            setColor(if (darkMode) panel2 else Color.rgb(236, 244, 251))
            setStroke(dp(1), accent)
            cornerRadius = dp(9).toFloat()
        }
        setPadding(dp(8), dp(5), dp(8), dp(5))
        stateListAnimator = null
        setOnClickListener {
            alpha = 0.72f
            postDelayed({ alpha = 1f }, 100)
            action()
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun wrapWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
}
