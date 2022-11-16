/*
 * Copyright 2017-2021 The "Open Radio" Project. Author: Chernyshov Yuriy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yuriy.openradio.shared.model.storage

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.yuriy.openradio.shared.model.media.MediaId
import com.yuriy.openradio.shared.vo.RadioStation

/**
 * Created by Yuriy Chernyshov
 * At Android Studio
 * On 6/4/15
 * E-Mail: chernyshov.yuriy@gmail.com
 */
class FavoritesStorage(context: Context) : AbstractRadioStationsStorage(context) {

    /**
     * Cache key of the Favorite Radio Station in order to ease load from preferences.
     */
    private val mSet = HashMap<String, Boolean>()

    /**
     * Add provided [RadioStation] to the Favorites preferences.
     *
     * @param radioStation [RadioStation] to add to Favorites.
     */
    @Synchronized
    fun add(radioStation: RadioStation) {
        normalizeMediaId(radioStation)
        val key = createKeyForRadioStation(radioStation)
        mSet[key] = true
        if (radioStation.sortId == MediaSessionCompat.QueueItem.UNKNOWN_ID) {
            radioStation.sortId = mSet.size
        }
        add(radioStation, FILE_NAME)
    }

    /**
     * Remove provided [RadioStation] from the Favorites preferences
     * by the provided media Id.
     *
     * @param radioStation [RadioStation] to remove from Favorites.
     */
    @Synchronized
    fun remove(radioStation: RadioStation) {
        val key = createKeyForRadioStation(radioStation)
        mSet.remove(key)
        remove(radioStation, FILE_NAME)
    }

    /**
     * Return collection of the Favorite Radio Stations which are stored in the persistent storage.
     *
     * @return Collection of the Favorites Radio stations.
     */
    fun getAll(): ArrayList<RadioStation> {
        return getAll(FILE_NAME)
    }

    fun addAll(list: List<RadioStation>) {
        return addAll(FILE_NAME, list)
    }

    /**
     * Return Favorite Radio Stations which are stored in the persistent storage represented in a single String.
     *
     * @return Favorite Radio Stations in a String representation.
     */
    fun getAllFavoritesAsString(): String {
        return getAllAsString(FILE_NAME)
    }

    /**
     * Check whether provided [RadioStation] is in Favorites preferences.
     *
     * @param radioStation [RadioStation] to check in the Favorites.
     * @return True in case of success, False - otherwise.
     */
    fun isFavorite(radioStation: RadioStation): Boolean {
        val key = createKeyForRadioStation(radioStation)
        if (mSet[key] != null) {
            return mSet[key] ?: false
        }
        val list = getAll()
        for (station in list) {
            if (station.id == radioStation.id) {
                mSet[key] = true
                return true
            }
        }
        mSet[key] = false
        return false
    }

    companion object {
        /**
         * Name of the file for the Favorite Preferences.
         */
        private const val FILE_NAME = "FavoritesPreferences"

        private fun normalizeMediaId(radioStation: RadioStation) {
            radioStation.id = MediaId.normalizeFromSearchId(radioStation.id)
        }
    }
}
