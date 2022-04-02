package uz.perfectalgorithm.projects.tezkor.utils.dragdrop_views

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.view.DragEvent
import android.view.View
import androidx.cardview.widget.CardView
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

private const val maskDragMessage = ""
private const val maskOn = "Bingo! Mask On"
private const val maskOff = "Mask off"


fun attachViewDragListener(view: CardView) {

    // Create a new ClipData.Item with custom text data
    val item = ClipData.Item(maskDragMessage)

    // Create a new ClipData using a predefined label, the plain text MIME type, and
    // the already-created item. This will create a new ClipDescription object within the
    // ClipData, and set its MIME type entry to "text/plain"
    val dataToDrag = ClipData(
        maskDragMessage,
        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
        item
    )

    // Instantiates the drag shadow builder.
    val maskShadow = MaskDragShadowBuilder(view)

    // Starts the drag
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        //support pre-Nougat versions
        @Suppress("DEPRECATION")
        view.startDrag(dataToDrag, maskShadow, view, 0)
    } else {
        //supports Nougat and beyond
        view.startDragAndDrop(dataToDrag, maskShadow, view, 0)
    }

    view.visibility = View.INVISIBLE
    true
}


class MaskDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {

    //set shadow to be the actual mask
    private val shadow = view

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        // Sets the width of the shadow to full width of the original View
        val width: Int = view.width

        // Sets the height of the shadow to full height of the original View
        val height: Int = view.height

        // The drag shadow is a Drawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide. As a result, the drag shadow will fill the
        // Canvas.
        //shadow?.setBounds(0, 0, width, height)

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set((width / 1.2).toInt(), (height / 1.2).toInt())

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / 2, height / 2)
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {
        // Draws the Drawable in the Canvas passed in from the system.
        shadow?.draw(canvas)
    }

}
