package me.daemon.verificationcode.demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log.i
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.daemon.verificationcode.VerificationCodeView
import me.daemon.view.common.dp2px

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vc.capacity = 6
        vc.gridDividerSize = 10
        vc.gridBackground = ColorDrawable(Color.YELLOW)
        vc.textSize = dp2px(30f)
        vc.textColor = Color.CYAN
        vc.listener = object : VerificationCodeView.Listener {
            override fun onChanged(
                view: VerificationCodeView,
                content: String,
                isFullFilled: Boolean
            ) {
                i(
                    MainActivity::class.java.name,
                    "on verification code view changed: $view, $content, $isFullFilled"
                )
            }
        }

        vc2.DRAW_AUXILIARY_LINE = true
        vc2.listener = object : VerificationCodeView.Listener {
            override fun onChanged(
                view: VerificationCodeView,
                content: String,
                isFullFilled: Boolean
            ) {
                i(
                    MainActivity::class.java.name,
                    "on verification code view changed 2: $view, $content, $isFullFilled"
                )
            }
        }
    }
}
