package com.example.passwordmanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helpers.PreferencesManager
import com.example.passwordmanager.adapters.AccountAdapter
import com.example.passwordmanager.models.AccountModel
import com.example.passwordmanager.ui.AccountAddActivity
import com.example.passwordmanager.ui.LoginActivity
import com.example.passwordmanager.ui.MyActivity
import timber.log.Timber
import java.lang.Exception
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        lateinit var activity: Activity
        var clientId: String? = null
        var clientPw: String? = null
        var clientNickname: String? = null
        var isUser: Boolean = true
    }

    private var backKeyPressedTime: Long = 0
    private lateinit var toast: Toast
    private lateinit var context: Context

    // note. widgets
    // note. user info
    private lateinit var mainActivity__header__item_nickname: TextView

    // note. body
    private lateinit var mainActivity__body__list: RecyclerView
    private lateinit var mainActivity__footer__add_btn: Button
    private lateinit var mainActivity__header__item_mySetting: ImageButton

    // note. item list
    private lateinit var accountList: ArrayList<AccountModel>
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // note. init
        init()
        // note. check has arguments
        checkArgs()
        // note. if no has user info, showing login view
        if (!isUser) displayLoginView()
        // note. apply info
        applyView()
        // note. get user account list
        getAccountList()
    }

    private fun applyView() {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)

        mainActivity__header__item_nickname.text = clientNickname
    }

    private fun getAccountList() {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)

        // note. for test
        val model = AccountModel()
        model.id = "아이디임"
        model.pw = "비밀번호임"
        model.hint = "힌트임"
        model.title = "타이틀임"

        accountList.add(model)
//        accountAdapter.notifyDataSetChanged()
        accountAdapter.notifyItemChanged(0)
    }

    private fun checkArgs() {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)

        val manager = PreferencesManager(activity, Protocol.ACCOUNT)
        clientId = manager[Protocol.CLIENT_ID]
        clientPw = manager[Protocol.CLIENT_PW]
        clientNickname = manager[Protocol.NICKNAME]

        Timber.i("clientId : $clientId, clientPw : $clientPw, clientNickname : $clientNickname")
        if (clientId == null || clientPw == null) isUser = false
        Timber.i("isUser : $isUser")
    }

    private fun displayLoginView() {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)

        val loginView = Intent(context, LoginActivity::class.java)
        startActivityForResult(loginView, Protocol.REQUEST_CODE_LOGIN)
    }

    private fun init() {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)

        // note. init libraries
        initLibraries()
        // note. init etc..
        initEtc()
        // note. init widgets
        initWidgets()
        // note. init adapters
        initAdapters()
    }

    private fun initLibraries() {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)
        // note. timber
        initLibrariesTimber()
    }

    private fun initLibrariesTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initWidgets() {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)

        // note. header
        mainActivity__header__item_mySetting = findViewById(R.id.mainActivity__header__item_mySetting)
        mainActivity__header__item_nickname = findViewById(R.id.mainActivity__header__item_nickname)
        // note. body
        mainActivity__body__list = findViewById(R.id.mainActivity__body__list)
        // note. footer
        mainActivity__footer__add_btn = findViewById(R.id.mainActivity__footer__add_btn)

        // note. set listeners
        // note. header
        mainActivity__header__item_mySetting.setOnClickListener(this)
        // note. footer
        mainActivity__footer__add_btn.setOnClickListener(this)
    }

    private fun initAdapters() {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)

        accountList = ArrayList()
        Timber.i("accountList : $accountList")
        accountAdapter = AccountAdapter()
        Timber.i("accountAdapter : $accountAdapter")
        accountAdapter.setList(accountList)

        // note. init layout manager
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        // note set recyclerview
        mainActivity__body__list.layoutManager = linearLayoutManager
        mainActivity__body__list.adapter = accountAdapter
    }

    private fun initEtc() {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)

        // note. activity
        activity = this
        // note. context
        context = applicationContext
        // note. toast
        toast = Toast(context)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)
        Timber.i("requestCode : $requestCode, resultCode : $resultCode, data : $data")

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Protocol.REQUEST_CODE_LOGIN -> {
                    val command = data?.getStringExtra(Protocol.COMMAND)
                    Timber.i("command : $command")

                    // note. terminate application
                    if (command == Protocol.APP_TERMINATE) finish()
                }

                Protocol.REQUEST_CODE_JOIN -> {

                }

                Protocol.REQUEST_CODE_MY -> {
                    val command = data?.getStringExtra(Protocol.COMMAND)
                    if (command == Protocol.SIGN_OUT) {
                        val mainActivity = activity

                        val manager = PreferencesManager(mainActivity, Protocol.ACCOUNT)
                        manager.remove(Protocol.CLIENT_ID)
                        manager.remove(Protocol.CLIENT_PW)

                        displayLoginView()
                    } else {

                    }
                }

                Protocol.REQUEST_CODE_EXIT -> finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)
        try {
            Timber.i( "keyCode : $keyCode, event : $event, repeatCount : ${event?.repeatCount}")
            if (keyCode == KeyEvent.KEYCODE_BACK && event?.getRepeatCount() == 0) {

                // note. 2000 milliseconds = 2sec
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis()

                    toast = Toast.makeText(context, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
                    toast.show()
                    return false
                }
                // note. click again
                if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                    exitProcess(0)
                    toast.cancel()
                }

                return true
            }
        } catch (e: Exception) {e.printStackTrace()}

        return super.onKeyDown(keyCode, event)
    }

    override fun onClick(v: View) {
        Timber.w(object:Any(){}.javaClass.enclosingMethod!!.name)

        when (v.id) {
            R.id.mainActivity__header__item_mySetting -> {
                val myActivity = Intent(context, MyActivity::class.java)
                startActivityForResult(myActivity, Protocol.REQUEST_CODE_MY)
            }

            R.id.mainActivity__footer__add_btn -> {
                Timber.w("mainActivity__footer__add_btn_OnClick")
                val accountAddActivity = Intent(context, AccountAddActivity::class.java)
                startActivityForResult(accountAddActivity, Protocol.REQUEST_CODE_ADD_ACCOUNT)
            }
        }
    }
}