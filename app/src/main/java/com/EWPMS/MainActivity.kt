package com.EWPMS

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.EWPMS.databinding.ActivityMainBinding
import com.EWPMS.fragments.DashBoardFragment
import com.EWPMS.fragments.MyWorksFragment
import com.EWPMS.fragments.ReportsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottom_navigation()
        call_dashboardFragment()
    }

    private fun bottom_navigation() {
        binding.dashBoardMenu.setOnClickListener{
             call_dashboardFragment()
         }
        binding.reportsMenu.setOnClickListener{
             call_reportsFragment()
         }
        binding.myWorksMenu.setOnClickListener{
             call_MyWorkersFragment()
         }
        binding.addNewMenu.setOnClickListener{
             call_AddNewFragment()
         }
        binding.chatsMenu.setOnClickListener{
             call_ChatFragment()
         }
    }


    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    private fun call_dashboardFragment() {
        replaceFragment(DashBoardFragment())
        binding.dashboardMenuBg.visibility= View.VISIBLE
        binding.reportsMenuBg.visibility= View.INVISIBLE
        binding.myWorksImgBg.visibility= View.INVISIBLE
        binding.addNewMenuImgBg.visibility= View.INVISIBLE
        binding.chatMenuImgBg.visibility= View.INVISIBLE

        binding.dashboardMenuText.visibility=View.GONE
        binding.reportsMenuText.visibility=View.VISIBLE
        binding.myWorksMenuText.visibility=View.VISIBLE
        binding.addNewMenuText.visibility=View.VISIBLE
        binding.chatMenuText.visibility=View.VISIBLE

        binding.dashboardMenuImg.setImageResource(R.drawable.dashboard_active)
        binding.reportsMenuImg.setImageResource(R.drawable.reports_inactive)
        binding.myWorksImg.setImageResource(R.drawable.my_works_inactive)
        binding.addNewMenuImg.setImageResource(R.drawable.add_new_inactive)
        binding.chatMenuImg.setImageResource(R.drawable.chat_inactive)
    }

    private fun call_reportsFragment() {
        replaceFragment(ReportsFragment())
        binding.reportsMenuBg.visibility= View.VISIBLE
        binding.dashboardMenuBg.visibility= View.INVISIBLE
        binding.myWorksImgBg.visibility= View.INVISIBLE
        binding.addNewMenuImgBg.visibility= View.INVISIBLE
        binding.chatMenuImgBg.visibility= View.INVISIBLE

        binding.reportsMenuText.visibility=View.GONE
        binding.dashboardMenuText.visibility=View.VISIBLE
        binding.myWorksMenuText.visibility=View.VISIBLE
        binding.addNewMenuText.visibility=View.VISIBLE
        binding.chatMenuText.visibility=View.VISIBLE

        binding.reportsMenuImg.setImageResource(R.drawable.reportactive)
        binding.dashboardMenuImg.setImageResource(R.drawable.dashboard_inactive)
        binding.myWorksImg.setImageResource(R.drawable.my_works_inactive)
        binding.addNewMenuImg.setImageResource(R.drawable.add_new_inactive)
        binding.chatMenuImg.setImageResource(R.drawable.chat_inactive)
    }

    private fun call_MyWorkersFragment() {
        replaceFragment(MyWorksFragment())
        binding.myWorksImgBg.visibility= View.VISIBLE
        binding.dashboardMenuBg.visibility= View.INVISIBLE
        binding.reportsMenuBg.visibility= View.INVISIBLE
        binding.addNewMenuImgBg.visibility= View.INVISIBLE
        binding.chatMenuImgBg.visibility= View.INVISIBLE

        binding.myWorksMenuText.visibility=View.GONE
        binding.dashboardMenuText.visibility=View.VISIBLE
        binding.reportsMenuText.visibility=View.VISIBLE
        binding.addNewMenuText.visibility=View.VISIBLE
        binding.chatMenuText.visibility=View.VISIBLE

        binding.myWorksImg.setImageResource(R.drawable.my_works_active)
        binding.dashboardMenuImg.setImageResource(R.drawable.dashboard_inactive)
        binding.reportsMenuImg.setImageResource(R.drawable.reports_inactive)
        binding.addNewMenuImg.setImageResource(R.drawable.add_new_inactive)
        binding.chatMenuImg.setImageResource(R.drawable.chat_inactive)
    }

    private fun call_ChatFragment() {
        replaceFragment(MyWorksFragment())
        binding.chatMenuImgBg.visibility= View.VISIBLE
        binding.dashboardMenuBg.visibility= View.INVISIBLE
        binding.reportsMenuBg.visibility= View.INVISIBLE
        binding.addNewMenuImgBg.visibility= View.INVISIBLE
        binding.myWorksImgBg.visibility= View.INVISIBLE

        binding.chatMenuText.visibility=View.GONE
        binding.dashboardMenuText.visibility=View.VISIBLE
        binding.reportsMenuText.visibility=View.VISIBLE
        binding.addNewMenuText.visibility=View.VISIBLE
        binding.myWorksMenuText.visibility=View.VISIBLE

        binding.chatMenuImg.setImageResource(R.drawable.chat_active)
        binding.dashboardMenuImg.setImageResource(R.drawable.dashboard_inactive)
        binding.reportsMenuImg.setImageResource(R.drawable.reports_inactive)
        binding.addNewMenuImg.setImageResource(R.drawable.add_new_inactive)
        binding.myWorksImg.setImageResource(R.drawable.my_works_inactive)
    }

    private fun call_AddNewFragment() {
        replaceFragment(DashBoardFragment())
        binding.addNewMenuImgBg.visibility= View.VISIBLE
        binding.dashboardMenuBg.visibility= View.INVISIBLE
        binding.reportsMenuBg.visibility= View.INVISIBLE
        binding.myWorksImgBg.visibility= View.INVISIBLE
        binding.chatMenuImgBg.visibility= View.INVISIBLE

        binding.addNewMenuText.visibility=View.GONE
        binding.dashboardMenuText.visibility=View.VISIBLE
        binding.reportsMenuText.visibility=View.VISIBLE
        binding.myWorksMenuText.visibility=View.VISIBLE
        binding.chatMenuText.visibility=View.VISIBLE

        binding.addNewMenuImg.setImageResource(R.drawable.add_new_active)
        binding.dashboardMenuImg.setImageResource(R.drawable.dashboard_inactive)
        binding.reportsMenuImg.setImageResource(R.drawable.reports_inactive)
        binding.myWorksImg.setImageResource(R.drawable.my_works_inactive)
        binding.chatMenuImg.setImageResource(R.drawable.chat_inactive)
    }

}