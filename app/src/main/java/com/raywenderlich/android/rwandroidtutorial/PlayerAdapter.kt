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

package com.raywenderlich.android.rwandroidtutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.rwandroidtutorial.database.PlayerListItem
import com.raywenderlich.android.rwandroidtutorial.ui.CircleTransformation
import com.squareup.picasso.Picasso
import java.util.*

class PlayerAdapter(
    private val players: MutableList<PlayerListItem>
) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

  private var listener: ((PlayerListItem) -> Unit)? = null

  fun swapData(players: List<PlayerListItem>) {
    this.players.clear()
    this.players.addAll(players)
    notifyDataSetChanged()
  }

  fun setOnPlayerTapListener(listener: ((PlayerListItem) -> Unit)) {
    this.listener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.player_row, parent, false)
    return ViewHolder(view)
  }

  override fun getItemCount() = players.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.textViewPlayerName.text =
        String.format(Locale.getDefault(), "%s %s", players[position].firstName,
            players[position].lastName)
    holder.textViewPlayerCountry.text = players[position].country


    Picasso.get()
        .load(players[position].imageUrl)
        .error(R.drawable.error_list_image)
        .placeholder(R.drawable.default_list_image)
        .transform(CircleTransformation())
        .into(holder.imageViewPlayer)

    val resourceId = if (players[position].favorite) {
      R.drawable.ic_star
    } else {
      R.drawable.ic_star_border
    }
    holder.imageViewFavorite.setImageResource(resourceId)

    holder.itemView.setOnClickListener {
      listener?.invoke(players[position])
    }
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val imageViewPlayer: ImageView = itemView.findViewById(R.id.imageViewPlayer)
    val textViewPlayerName: TextView = itemView.findViewById(R.id.textViewPlayerName)
    val textViewPlayerCountry: TextView = itemView.findViewById(R.id.textViewPlayerCountry)
    val imageViewFavorite: ImageView = itemView.findViewById(R.id.imageViewFavorite)
  }
}