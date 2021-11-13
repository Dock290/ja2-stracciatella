package io.github.ja2stracciatella

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import io.github.ja2stracciatella.ui.main.SectionsPagerAdapter
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File
import java.io.IOException

class LauncherActivity : AppCompatActivity() {
    private val activityLogTag = "LauncherActivity"
    private val requestPermissionsCode = 1000
    private val jsonFormat = Json {
        prettyPrint = true
    }

    private val ja2JsonFilename = "ja2.json"
    private val gameDirKey = "game_dir"
    private val modsKey = "mods"
    private val resKey = "res"
    private val brightnessKey = "brightness"
    private val resversionKey = "resversion"
    private val fullscreenKey = "fullscreen"
    private val scalingKey = "scaling"
    private val debugKey = "debug"
    private val nosoundKey = "nosound"

    private lateinit var exceptionAlertDialog: AlertDialog

    override fun onStart() {
        super.onStart()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exception Alert")
        builder.setNeutralButton("OK") { dialog, _ -> dialog.dismiss() }
        exceptionAlertDialog = builder.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadJA2Json()

        setContentView(R.layout.activity_launcher)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener {
            startGame()
        }
    }

    override fun onResume() {
        super.onResume()

        val exception = NativeExceptionContainer.getException()
        Log.i(activityLogTag, "Resuming LauncherActivity, previous exception: $exception")
        if (exception != null) {
            exceptionAlertDialog.setMessage("An exception has occurred when running the game:\n$exception")
            exceptionAlertDialog.show()
            NativeExceptionContainer.resetException()
        }
    }

    private fun getPermissionsIfNecessaryForAction(action: () -> Unit) {
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val hasAllPermissions = permissions.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (hasAllPermissions) {
            action()
        } else {
            requestPermissions(permissions, requestPermissionsCode)
        }
    }

    override fun onPause() {
        super.onPause()
        saveJA2Json()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == requestPermissionsCode) {
            if (grantResults.all { r -> r == PackageManager.PERMISSION_GRANTED }) {
                startGame()
            } else {
                Toast.makeText(
                    this,
                    "Cannot start the game without proper permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startGame() {
        try {
            getPermissionsIfNecessaryForAction {
                saveJA2Json()
                val intent = Intent(this@LauncherActivity, StracciatellaActivity::class.java)
                startActivity(intent)
            }
        } catch (e: IOException) {
            val message = "Could not write ${ja2JsonPath}: ${e.message}"
            Log.e(activityLogTag, message)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(activityLogTag, e.stackTraceToString())
            exceptionAlertDialog.setMessage("An exception has occurred when starting the game:\n$e.message")
            exceptionAlertDialog.show()
        }
    }

    private val ja2JsonPath: String
        get() {
            return "${applicationContext.getExternalFilesDir("")?.absolutePath}/$ja2JsonFilename"
        }

    private fun loadJA2Json() {
        val configurationModel = ViewModelProvider(this).get(ConfigurationModel::class.java)
        try {
            val json = File(ja2JsonPath).readText()
            // For some reason it is not possible to decode to Any, so we decode to JsonElement instead
            val jsonMap: Map<String, JsonElement> = jsonFormat.decodeFromString(json)

            Log.i(activityLogTag, "Loaded ja2.json: $jsonMap")

            val vanillaGameDir = jsonMap[gameDirKey]
            if (vanillaGameDir is JsonPrimitive && vanillaGameDir.isString) {
                configurationModel.setVanillaGameDir(vanillaGameDir.content)
            } else {
                Log.w(activityLogTag, "$gameDirKey is not a string")
            }

            val mods = jsonMap[modsKey]
            if (mods is JsonArray) {
                var areAllModsValid = true
                val modList = ArrayList<String>()
                for (mod in mods) {
                    if (mod is JsonPrimitive && mod.isString) {
                        modList.add(mod.content)
                    } else {
                        areAllModsValid = false
                    }
                }
                if (!areAllModsValid) {
                    Log.w(activityLogTag, "Some mods have wrong type")
                }
                configurationModel.setMods(modList)
            } else {
                Log.w(activityLogTag, "$modsKey is not an array")
            }

            val res = jsonMap[resKey]
            if (res is JsonPrimitive && res.isString) {
                configurationModel.setRes(res.content)
            } else {
                Log.w(activityLogTag, "$resKey is not a string")
            }

            val brightness = jsonMap[brightnessKey]
            if (brightness is JsonPrimitive && brightness.floatOrNull is Float) {
                configurationModel.setBrightness(brightness.float)
            } else {
                Log.w(activityLogTag, "$brightnessKey is not a float")
            }

            val resversion = jsonMap[resversionKey]
            if (resversion is JsonPrimitive && resversion.isString) {
                configurationModel.setResversion(resversion.content)
            } else {
                Log.w(activityLogTag, "$resversionKey is not a string")
            }

            val fullscreen = jsonMap[fullscreenKey]
            if (fullscreen is JsonPrimitive && fullscreen.booleanOrNull is Boolean) {
                configurationModel.setFullscreen(fullscreen.boolean)
            } else {
                Log.w(activityLogTag, "$fullscreenKey is not a bool")
            }

            val scaling = jsonMap[scalingKey]
            if (scaling is JsonPrimitive && scaling.isString) {
                configurationModel.setScaling(scaling.content)
            } else {
                Log.w(activityLogTag, "$scalingKey is not a string")
            }

            val debug = jsonMap[debugKey]
            if (debug is JsonPrimitive && debug.booleanOrNull is Boolean) {
                configurationModel.setDebug(debug.boolean)
            } else {
                Log.w(activityLogTag, "$$debugKey is not a string")
            }

            val nosound = jsonMap[nosoundKey]
            if (nosound is JsonPrimitive && nosound.booleanOrNull is Boolean) {
                configurationModel.setNosound(nosound.boolean)
            } else {
                Log.w(activityLogTag, "$$nosoundKey is not a string")
            }
        } catch (e: SerializationException) {
            Log.w(activityLogTag, "Could not decode ja2.json: ${e.message}")
        } catch (e: IOException) {
            Log.w(activityLogTag, "Could not read $ja2JsonPath: ${e.message}")
        }
    }

    private fun saveJA2Json() {
        val configurationModel = ViewModelProvider(this).get(ConfigurationModel::class.java)
        var jsonMap: MutableMap<String, JsonElement> = mutableMapOf()
        try {
            val json = File(ja2JsonPath).readText()
            // For some reason it is not possible to decode to Any, so we decode to JsonElement instead
            jsonMap = jsonFormat.decodeFromString(json)
        } catch (e: SerializationException) {
            Log.w(activityLogTag, "Could not decode ja2.json: ${e.message}")
        } catch (e: IOException) {
            Log.w(activityLogTag, "Could not read ${ja2JsonPath}: ${e.message}")
        }

        jsonMap[gameDirKey] = JsonPrimitive(configurationModel.vanillaGameDir.value)

        val mods = ArrayList<JsonElement>()
        configurationModel.mods.value?.forEach { mods.add(JsonPrimitive(it)) }
        jsonMap[modsKey] = JsonArray(mods)

        var res = configurationModel.res.value
        if (res == null || res.isBlank()) {
            res = "640x480"
            configurationModel.setRes(res)
        }
        jsonMap[resKey] = JsonPrimitive(res)

        jsonMap[brightnessKey] = JsonPrimitive(configurationModel.brightness.value)
        jsonMap[resversionKey] = JsonPrimitive(configurationModel.resversion.value)
        jsonMap[fullscreenKey] = JsonPrimitive(configurationModel.fullscreen.value)
        jsonMap[scalingKey] = JsonPrimitive(configurationModel.scaling.value)
        jsonMap[debugKey] = JsonPrimitive(configurationModel.debug.value)
        jsonMap[nosoundKey] = JsonPrimitive(configurationModel.nosound.value)

        Log.i(activityLogTag, "Saving ja2.json: $jsonMap")
        val parentDir = File(ja2JsonPath).parentFile
        if (parentDir?.exists() != true) {
            parentDir?.mkdirs()
        }
        File(ja2JsonPath).writeText(jsonFormat.encodeToString(jsonMap))
    }
}