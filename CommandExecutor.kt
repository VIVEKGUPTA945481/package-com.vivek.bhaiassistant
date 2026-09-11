package com.vivek.bhaiassistant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import java.util.Locale

class CommandExecutor(
    private val context: Context,
    private val audio: AudioManager
) {

    fun execute(command: String) {

        val cmd = command
            .lowercase(Locale("hi", "IN"))
            .trim()

        when {

            // YouTube
            cmd.contains("youtube") &&
                    (
                        cmd.contains("khol") ||
                        cmd.contains("open") ||
                        cmd.contains("chala")
                    ) -> {

                openApp(
                    "com.google.android.youtube"
                )
            }

            // Gallery
            cmd.contains("gallery") ||
                    cmd.contains("photos") ||
                    cmd.contains("photo kholo") -> {

                openGallery()
            }

            // WhatsApp
            cmd.contains("whatsapp") -> {

                openApp(
                    "com.whatsapp"
                )
            }

            // Volume UP
            cmd.contains("volume") &&
                    (
                        cmd.contains("badha") ||
                        cmd.contains("zyada") ||
                        cmd.contains("increase")
                    ) -> {

                audio.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI
                )
            }

            // Volume DOWN
            cmd.contains("volume") &&
                    (
                        cmd.contains("kam") ||
                        cmd.contains("ghata") ||
                        cmd.contains("decrease")
                    ) -> {

                audio.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_SHOW_UI
                )
            }

            // Silent
            cmd.contains("silent") ||
                    cmd.contains("mute") -> {

                audio.adjustStreamVolume(
                    AudioManager.STREAM_RING,
                    AudioManager.ADJUST_MUTE,
                    AudioManager.FLAG_SHOW_UI
                )
            }

            // Unmute
            cmd.contains("normal") ||
                    cmd.contains("unmute") -> {

                audio.adjustStreamVolume(
                    AudioManager.STREAM_RING,
                    AudioManager.ADJUST_UNMUTE,
                    AudioManager.FLAG_SHOW_UI
                )
            }

            // Call
            cmd.startsWith("call ") ||
                    cmd.contains("ko call") ||
                    cmd.contains("call karo") -> {

                val name = extractContactName(cmd)

                callContact(name)
            }

            // Unknown command
            else -> {

                val searchUrl =
                    "https://www.google.com/search?q=" +
                            Uri.encode(command)

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(searchUrl)
                )

                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )

                context.startActivity(intent)
            }
        }
    }

    private fun openApp(packageName: String) {

        val intent =
            context.packageManager
                .getLaunchIntentForPackage(packageName)

        if (intent != null) {

            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )

            context.startActivity(intent)
        }
    }

    private fun openGallery() {

        val intent = Intent(
            Intent.ACTION_VIEW
        )

        intent.type = "image/*"

        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

        context.startActivity(
            Intent.createChooser(
                intent,
                "Gallery choose karo"
            )
        )
    }

    private fun extractContactName(
        command: String
    ): String {

        return command
            .replace("ko call karo", "")
            .replace("ko call", "")
            .replace("call karo", "")
            .replace("call kar", "")
            .replace("call", "")
            .trim()
    }

    private fun callContact(
        name: String
    ) {

        if (name.isBlank()) return

        val cursor =
            context.contentResolver.query(

                ContactsContract
                    .CommonDataKinds
                    .Phone.CONTENT_URI,

                arrayOf(
                    ContactsContract
                        .CommonDataKinds
                        .Phone.NUMBER,

                    ContactsContract
                        .CommonDataKinds
                        .Phone.DISPLAY_NAME
                ),

                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",

                arrayOf("%$name%"),

                null
            )

        cursor?.use {

            if (it.moveToFirst()) {

                val number = it.getString(0)

                val intent =
                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CALL_PHONE
                        ) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {

                        Intent(
                            Intent.ACTION_CALL,
                            Uri.parse("tel:$number")
                        )

                    } else {

                        Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:$number")
                        )
                    }

                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )

                context.startActivity(intent)
            }
        }
    }
}
