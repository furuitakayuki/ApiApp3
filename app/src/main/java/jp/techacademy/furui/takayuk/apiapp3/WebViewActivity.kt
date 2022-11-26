package jp.techacademy.furui.takayuk.apiapp3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import jp.techacademy.furui.takayuk.apiapp3.R.drawable
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity: AppCompatActivity() {

    var isFavorite : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)


        val shop = intent.getSerializableExtra(KEY_URL)
        if(shop is Shop){
            webView.loadUrl(if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc)
            isFavorite = FavoriteShop.findBy(shop.id) != null

            favoriteImageView.apply {
                setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border) // Picassoというライブラリを使ってImageVIewに画像をはめ込む
                setOnClickListener {
                    if (!isFavorite) {
                        FavoriteShop.insert(FavoriteShop().apply {
                            id = shop.id
                            name = shop.name
                            address = shop.address
                            imageUrl = shop.logoImage
                            url = if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc
                        })
                        isFavorite = true
                        setImageResource(R.drawable.ic_star)
                    } else {
                        showConfirmDeleteFavoriteDialog(shop.id)
                    }
                }
            }
        }
    }

    private fun showConfirmDeleteFavoriteDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deleteFavorite(id)
                favoriteImageView.setImageResource(drawable.ic_star_border)
                isFavorite = false
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->}
            .create()
            .show()
    }

    private fun deleteFavorite(id: String) {
        FavoriteShop.delete(id)
    }

    companion object {
        private const val KEY_URL = "key_url"
        fun start(activity: Activity, shop: Shop) {
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY_URL, shop))
        }
    }
}