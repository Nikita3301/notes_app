package com.example.pmd2

import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.util.*


class MainActivity : AppCompatActivity() {

    private var myList: ViewGroup? = null
    private var menuToChoose: Int = R.menu.main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myList = findViewById(R.id.list)

        //create checkboxes
        val l = layoutInflater
        val strings = Vector<String>()
        for (i in 0..5) {
            strings.add("check box item $i")
        }
        for (s in strings) {
            val item: View = l.inflate(R.layout.list_item, null)
            (item.findViewById(R.id.item_text) as TextView).text = s
            myList?.addView(item)
        }

        // set home arrow for action bar
        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // click listener
        val saveButton = findViewById<MaterialButton>(R.id.save_button)
        saveButton.setOnClickListener {
            val detailview: View = findViewById(R.id.detail)
            val anim = TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f)
            anim.duration = 700
            anim.fillAfter = false
            detailview.bringToFront()
            detailview.startAnimation(anim)
            detailview.visibility = View.VISIBLE
            detailview.isEnabled = true
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    detailview.visibility = View.GONE
                }
                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
    }




    //theme action bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(menuToChoose, menu)
        return super.onCreateOptionsMenu(menu)
    }


    // buttons in action bar
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            menuToChoose = R.menu.main2;
            setContentView(R.layout.notes_page);
            invalidateOptionsMenu();
            true
        }
        R.id.item1 -> {
            Toast.makeText(this, "qr", Toast.LENGTH_SHORT).show();
            true
        }
        R.id.item2 -> {
            Toast.makeText(this, "pin", Toast.LENGTH_SHORT).show();
            true
        }

        R.id.item4 -> {
            Toast.makeText(this, "note background", Toast.LENGTH_SHORT).show();
            true
        }
        R.id.item5 -> {
            Toast.makeText(this, "change font", Toast.LENGTH_SHORT).show();
            true
        }
        R.id.item6 -> {
            Toast.makeText(this, "change font color", Toast.LENGTH_SHORT).show();
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }








}