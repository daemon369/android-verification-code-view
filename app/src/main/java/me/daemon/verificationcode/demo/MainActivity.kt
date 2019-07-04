package me.daemon.verificationcode.demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.daemon.verificationcode.VerificationCodeView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vc.DRAW_AUXILIARY_LINE = true
        vc.gridDividerSize = 10
        vc.gridBackground = ColorDrawable(Color.YELLOW)
        vc.listener = object : VerificationCodeView.Listener {
            override fun onChanged(
                view: VerificationCodeView,
                content: String,
                isFullFilled: Boolean
            ) {
                Log.i(
                    MainActivity::class.java.name,
                    "on verification code view changed: $view, $content, $isFullFilled"
                )
            }
        }
    }
}
