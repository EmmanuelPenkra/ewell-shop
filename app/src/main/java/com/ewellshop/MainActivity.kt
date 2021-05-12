package com.ewellshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ewellshop.helpers.Actions
import com.ewellshop.helpers.DataType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val orders = Orders()
        val inventory = Inventory()
        val settings = Settings()

        makeCurrentFragment(orders)

        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.orders -> makeCurrentFragment(orders)
                R.id.inventory -> makeCurrentFragment(inventory)
                R.id.settings -> makeCurrentFragment(settings)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, fragment)
            commit()
        }

    override fun onResume() {
        super.onResume()
        if (Actions().getInfo("employeeID", DataType.INTEGER)?.equals(0) == true) {
            startActivity(Intent(this, Starter::class.java))
        }
    }
}