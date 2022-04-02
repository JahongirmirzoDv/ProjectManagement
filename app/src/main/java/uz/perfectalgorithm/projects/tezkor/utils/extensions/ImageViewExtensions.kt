package uz.perfectalgorithm.projects.tezkor.utils.extensions

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.utils.BASE_URL
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import java.io.File

/**
 * Created by Botirali Kozimov on 12.03.21
 **/

fun ImageView.loadImageFile(file: File) {
    Glide.with(this).load(file).centerCrop().into(this)
}

fun ImageView.loadImageUri(uri: Uri) {
    Glide.with(this).load(uri).placeholder(R.drawable.ic_img_business_decisions_intro)
        .centerCrop()
        .into(this)
}

fun ImageView.loadImageUriSendImg(uri: Uri) {
    Glide.with(this).load(uri).placeholder(R.drawable.ic_img_business_decisions_intro)
        .into(this)
}


fun ImageView.loadImageUrlAll(url: String) {
    GlideToVectorYou.init()
        .with(App.instance)
        .withListener(object : GlideToVectorYouListener {
            override fun onLoadFailed() {
                Toast.makeText(App.instance, "Load failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResourceReady() {
                Toast.makeText(App.instance, "Image ready", Toast.LENGTH_SHORT).show()
            }

        }).load(Uri.parse(url), this)
}


fun ImageView.loadImageUrl(url: String, @DrawableRes placeholderRes: Int = R.drawable.ic_user) {
    if (url.endsWith(".svg", true)) {
        GlideToVectorYou.init()
            .with(App.instance)
            .setPlaceHolder(placeholderRes, placeholderRes)
            .withListener(object : GlideToVectorYouListener {
                override fun onLoadFailed() {
                    Toast.makeText(App.instance, "Load failed", Toast.LENGTH_SHORT).show()
                }

                override fun onResourceReady() {
                    Toast.makeText(App.instance, "Image ready", Toast.LENGTH_SHORT).show()
                }

            }).load(Uri.parse(BASE_URL + url), this)
    } else
        Glide.with(this)
            .load(BASE_URL + url)
            .placeholder(placeholderRes)
            .into(this)
}

fun ImageView.loadImageUrlWithBaseUrl(url: String) {
    if (url.endsWith(".svg", true)) {
        GlideToVectorYou.init()
            .with(App.instance)
            .withListener(object : GlideToVectorYouListener {
                override fun onLoadFailed() {
                    Toast.makeText(App.instance, "Load failed", Toast.LENGTH_SHORT).show()
                }

                override fun onResourceReady() {
                    Toast.makeText(App.instance, "Image ready", Toast.LENGTH_SHORT).show()
                }

            }).load(Uri.parse(url), this)
    } else
        Glide.with(this)
            .load(url)
            .into(this)
}

fun ImageView.loadPictureUrl(url: String) {
    if (url.endsWith(".svg", true)) {
        GlideToVectorYou.init()
            .with(App.instance)
            .setPlaceHolder(R.drawable.ic_user, R.drawable.ic_user)
            .load(Uri.parse(BASE_URL + url), this)
    } else
        Glide.with(this)
            .load(BASE_URL + url)
            .placeholder(R.drawable.ic_user)
            .into(this)
}

fun ImageView.loadImageUrlWithPlaceholder(url: String, placeHolder: Int) {
    Glide.with(this).load(BASE_URL + url).centerCrop().placeholder(placeHolder).into(this)
}

fun ImageView.loadImageUrlUniversal(url: String, placeHolder: Int = R.drawable.empty_image) {
    if (url.isNotEmpty()) {
        if (url[0].toString() == "/") {
            Glide.with(this).load(BASE_URL + url).centerCrop().placeholder(placeHolder).into(this)
        } else {
            Glide.with(this).load(url).centerCrop().placeholder(placeHolder).into(this)
        }
    }
}


fun loadImageUrlUniversalWithCallback(
    imageView: ImageView,
    url: String,
    placeHolder: Int = R.drawable.empty_image,
    singleBlock: SingleBlock<Int>
) {
    if (url.isNotEmpty()) {
        var mUrl = ""
        mUrl = if (url[0].toString() == "/") {
            BASE_URL + url
        } else {
            url
        }
        Glide.with(imageView)
            .asBitmap()
            .load(mUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    imageView.setImageBitmap(resource)
                    singleBlock.invoke(200)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

    }
}


fun ImageView.loadImageUrlShowImg(url: String, placeHolder: Int) {
    Glide.with(this).load(BASE_URL + url).placeholder(placeHolder).into(this)
}

fun ImageView.loadImageUrlN(url: String) {
    Glide.with(this).load(BASE_URL + url).centerCrop().into(this)
}

fun ImageView.loadImageUrlAll(url: String, placeHolder: Int) {
    Glide.with(this).load(url).centerCrop().placeholder(placeHolder).into(this)
}

fun ImageView.loadImageUrlAllForA(url: String, placeHolder: Int) {
    Glide.with(this).load(url).placeholder(placeHolder).into(this)
}

fun ImageView.loadImageUrlAllAvatar(url: String, placeHolder: Int) {
    Glide.with(this).load(url).placeholder(placeHolder).into(this)
}

fun ImageView.loadImageWithResponse(url: String, isReady: SingleBlock<Boolean>) {

    Glide.with(this).load(BASE_URL + url).centerCrop()
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                myLogD("LOG LOG IMG DOWNLOAD FAILED", "TAG_IMG")
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                isReady.invoke(true)
                myLogD("LOG LOG IMG DOWNLOAD SUCCESSFULLY", "TAG_IMG")
                return false
            }
        }).submit()
}
