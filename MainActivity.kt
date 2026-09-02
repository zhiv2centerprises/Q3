package com.q3.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import java.io.Serializable

data class Q3Object(
    var name: String,
    var type: String,
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var sx: Float = 1f,
    var sy: Float = 1f,
    var sz: Float = 1f,
    var rx: Float = 0f,
    var ry: Float = 0f,
    var rz: Float = 0f
) : Serializable

class MainActivity : Activity() {
    private val objects = mutableListOf<Q3Object>()
    private lateinit var viewport: View
    private lateinit var outliner: LinearLayout
    private var selected: Q3Object? = null
    private var downX = 0f
    private var downY = 0f

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        buildUi()
        addPrimitive("Cube")
        addPrimitive("Camera", "CAMERA")
        addPrimitive("Sun", "LIGHT")
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(16,17,20))
        }

        val top = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(16, 8, 16, 8)
        }
        val title = TextView(this).apply {
            text = "Q3  •  3D Studio"
            textSize = 20f
            setTextColor(Color.WHITE)
        }
        top.addView(title, LinearLayout.LayoutParams(0, 56, 1f))
        top.addView(button("＋", "Add"), LinearLayout.LayoutParams(60,56))
        top.addView(button("↶", "Undo"), LinearLayout.LayoutParams(60,56))
        top.addView(button("⚙", "Settings"), LinearLayout.LayoutParams(70,56))
        root.addView(top)

        val body = LinearLayout(this)
        viewport = object : View(this) {
            override fun onDraw(c: android.graphics.Canvas) {
                super.onDraw(c)
                c.drawColor(Color.rgb(24,25,29))
                val p = android.graphics.Paint().apply {
                    color = Color.rgb(45,47,53)
                    strokeWidth = 1f
                }
                val step = 64
                for (x in 0 until width step step) c.drawLine(x.toFloat(),0f,x.toFloat(),height.toFloat(),p)
                for (y in 0 until height step step) c.drawLine(0f,y.toFloat(),width.toFloat(),y.toFloat(),p)
                val centerX = width/2f
                val centerY = height/2f
                p.color = Color.rgb(90,90,95)
                c.drawLine(centerX,0f,centerX,height.toFloat(),p)
                c.drawLine(0f,centerY,width.toFloat(),centerY,p)
                selected?.let {
                    p.color = Color.rgb(70,150,255)
                    p.style = android.graphics.Paint.Style.STROKE
                    p.strokeWidth = 4f
                    val s = 100f
                    c.drawRect(centerX-s,centerY-s,centerX+s,centerY+s,p)
                    p.style = android.graphics.Paint.Style.FILL
                }
            }
            override fun onTouchEvent(e: MotionEvent): Boolean {
                when(e.action) {
                    MotionEvent.ACTION_DOWN -> { downX=e.x; downY=e.y; return true }
                    MotionEvent.ACTION_UP -> {
                        if (kotlin.math.abs(e.x-downX)<20 && kotlin.math.abs(e.y-downY)<20) {
                            selected = objects.firstOrNull { it.type == "MESH" }
                            invalidate(); refreshOutliner()
                        }
                        return true
                    }
                }
                return true
            }
        }
        body.addView(viewport, LinearLayout.LayoutParams(0,0,1f))
        outliner = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12,12,12,12)
            setBackgroundColor(Color.rgb(28,29,34))
        }
        body.addView(outliner, LinearLayout.LayoutParams(280,0))
        root.addView(body, LinearLayout.LayoutParams(-1,0,1f))

        val timeline = LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL
            setPadding(12,6,12,6)
            setBackgroundColor(Color.rgb(20,21,24))
        }
        timeline.addView(TextView(this).apply {
            text="Timeline     ◀  ▶    Frame 1 / 250       FPS 24"
            setTextColor(Color.LTGRAY)
        })
        root.addView(timeline, LinearLayout.LayoutParams(-1,72))

        setContentView(root)
        refreshOutliner()
    }

    private fun button(label:String, action:String): Button =
        Button(this).apply {
            text=label
            setTextColor(Color.WHITE)
            setOnClickListener {
                when(action) {
                    "Add" -> addPrimitive("Cube")
                    "Settings" -> showSettings()
                }
            }
        }

    private fun addPrimitive(name:String, type:String="MESH") {
        val n = if (objects.any{it.name==name}) "$name.${objects.size.toString().padStart(3,'0')}" else name
        val o=Q3Object(n,type)
        objects.add(o); selected=o
        refreshOutliner(); viewport.invalidate()
    }

    private fun refreshOutliner() {
        outliner.removeAllViews()
        outliner.addView(TextView(this).apply {
            text="OUTLINER"; textSize=15f; setTextColor(Color.WHITE)
        })
        objects.forEach { o ->
            outliner.addView(TextView(this).apply {
                text="  ${if(o==selected) "●" else "○"}  ${o.name}  [${o.type}]"
                textSize=14f; setPadding(4,10,4,10)
                setTextColor(if(o==selected) Color.rgb(90,170,255) else Color.LTGRAY)
                setOnClickListener { selected=o; refreshOutliner(); viewport.invalidate() }
            })
        }
    }

    private fun showSettings() {
        AlertDialog.Builder(this)
            .setTitle("Q3 Settings")
            .setItems(arrayOf("Interface","Viewport","Rendering","Animation","Storage","Auto Save","Performance / GPU","Controls","Theme","Language","Privacy","About")) { _, which ->
                if(which==11) AlertDialog.Builder(this)
                    .setTitle("About Q3")
                    .setMessage("Q3 — Mobile 3D Modeling & Animation Studio\n\nCopyright © Qazi Hissam Ud Din")
                    .setPositiveButton("OK",null).show()
            }.show()
    }
}
