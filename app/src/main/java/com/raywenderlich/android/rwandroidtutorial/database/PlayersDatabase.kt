/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.rwandroidtutorial.database

import android.content.Context
import android.content.res.Resources
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raywenderlich.android.rwandroidtutorial.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(version = 1, entities = [Player::class], exportSchema = false)
abstract class PlayersDatabase : RoomDatabase() {

  abstract fun playerDao(): PlayerDao

  private class PlayerDatabaseCallback(
      private val scope: CoroutineScope,
      private val resources: Resources
  ) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch {
          val playerDao = database.playerDao()
          prePopulateDatabase(playerDao)
        }
      }
    }

    private suspend fun prePopulateDatabase(playerDao: PlayerDao) {
      val jsonString = resources.openRawResource(R.raw.players).bufferedReader().use {
        it.readText()
      }
      val typeToken = object : TypeToken<List<Player>>() {}.type
      val tennisPlayers = Gson().fromJson<List<Player>>(jsonString, typeToken)
      playerDao.insertAllPlayers(tennisPlayers)
    }
  }

  companion object {

    @Volatile
    private var INSTANCE: PlayersDatabase? = null

    fun getDatabase(context: Context, coroutineScope: CoroutineScope, resources: Resources): PlayersDatabase {
      val tempInstance = INSTANCE
      if (tempInstance != null) {
        return tempInstance
      }

      synchronized(this) {
        val instance = Room.databaseBuilder(context.applicationContext,
            PlayersDatabase::class.java,
            "players_database")
            .addCallback(PlayerDatabaseCallback(coroutineScope, resources))
            .build()
        INSTANCE = instance
        return instance
      }
    }
  }
}