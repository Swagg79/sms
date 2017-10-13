package xyz.klinker.messenger.shared.util

import android.telephony.SmsMessage

import com.klinker.android.send_message.StripAccents

import xyz.klinker.messenger.shared.data.MmsSettings
import xyz.klinker.messenger.shared.data.Settings

object MessageCountHelper {

    /**
     * Returns null if the message counter should be hidden, or the count (Ex: 1 / 17) if the count
     * should be shown.
     *
     * @param settings the settings for the app
     * @param mmsSettings the MMS settings for the app
     * @param text the text that the user is sending
     *
     * @return a string to be applied to the counter TextView
     */
    fun getMessageCounterText(settings: Settings, mmsSettings: MmsSettings, text: String): String? {
        var text = text
        val stripUnicode = settings.stripUnicode
        val convertToMMS = mmsSettings.convertLongMessagesToMMS
        val convertAfterXMessages = mmsSettings.numberOfMessagesBeforeMms
        var numberOfMessages = 0
        var charRemaining = 0

        if (stripUnicode) {
            text = StripAccents.stripAccents(text)
        }

        try {
            val count = SmsMessage.calculateLength(text, false)
            numberOfMessages = count[0]
            charRemaining = count[2]
        } catch (e: Exception) {
            return null
        }

        // when they are running out of room on their first message,
        // we ALWAYS want to display this
        if (numberOfMessages == 1 && charRemaining < 30) {
            return formatCount(numberOfMessages, charRemaining)
        }

        if (!convertToMMS) {
            return if (numberOfMessages > 1) {
                formatCount(numberOfMessages, charRemaining)
            } else {
                null
            }
        }

        return if (numberOfMessages > 1 && numberOfMessages <= convertAfterXMessages) {
            formatCount(numberOfMessages, charRemaining)
        } else {
            null
        }
    }

    private fun formatCount(numberOfMessages: Int, charRemaining: Int): String {
        return numberOfMessages.toString() + "/" + charRemaining
    }
}