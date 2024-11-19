package com.EWPMS

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.EWPMS.databinding.ActivityMainBinding
import com.EWPMS.fragments.CreateNewWorkFragment
import com.EWPMS.fragments.DashBoardFragment
import com.EWPMS.fragments.MyWorksFragment
import com.EWPMS.fragments.ReportsFragment
import com.EWPMS.utilities.AppConstants
import com.EWPMS.utilities.AppSharedPreferences

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottom_navigation()
        onClickListeners()
        if(intent.getStringExtra("screen")!=null) {
            call_MyWorkersFragment()
        }else{
            call_dashboardFragment()
        }
    }

    private fun onClickListeners() {
        binding.backIconLayout.setOnClickListener{
            call_dashboardFragment()
        }
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
            /* call_ChatFragment()*/
            Toast.makeText(this@MainActivity, getString(R.string.comming_soon), Toast.LENGTH_SHORT).show()
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    private fun call_dashboardFragment() {
        replaceFragment(DashBoardFragment())

        if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
            binding.userNameTv.text =
                AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                    .toString()
        }

        if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERID)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERID).isNotEmpty()) {
            binding.userIdTv.text =
                AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERID)
                    .toString()
        }

        binding.titleBarHomeLayout.visibility=View.VISIBLE
        binding.titleBarFragmentLayout.visibility=View.GONE

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
        binding.titleBarHomeLayout.visibility=View.INVISIBLE
        binding.titleBarFragmentLayout.visibility=View.VISIBLE
        binding.fragmentName.text=getString(R.string.work_report)

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
        binding.titleBarHomeLayout.visibility=View.INVISIBLE
        binding.titleBarFragmentLayout.visibility=View.VISIBLE
        binding.fragmentName.text=getString(R.string.my_works_list)

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
        binding.titleBarHomeLayout.visibility=View.INVISIBLE
        binding.titleBarFragmentLayout.visibility=View.VISIBLE
        binding.fragmentName.text=getString(R.string.chat_menu)

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
        replaceFragment(CreateNewWorkFragment())
        binding.titleBarHomeLayout.visibility=View.INVISIBLE
        binding.titleBarFragmentLayout.visibility=View.VISIBLE
        binding.fragmentName.text=getString(R.string.create_new_work)

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

    override fun onBackPressed() {
        super.onBackPressed()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment is DashBoardFragment) {
            finishAffinity()
        }

        if(currentFragment is ReportsFragment){
            replaceFragment(DashBoardFragment())
            /*if((!AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USER_NAME).isEmpty()) && (AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USER_NAME)!=null)) {
                binding.userNameTv.setText(getString(R.string.hello_text)+" "+AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USER_NAME).toString())
            }*/
        }

        if(currentFragment is MyWorksFragment){
            replaceFragment(DashBoardFragment())
          /*  if((!AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USER_NAME).isEmpty()) && (AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USER_NAME)!=null)) {
                binding.userNameTv.setText(getString(R.string.hello_text)+" "+AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USER_NAME).toString())
            }*/
        }
    }

}