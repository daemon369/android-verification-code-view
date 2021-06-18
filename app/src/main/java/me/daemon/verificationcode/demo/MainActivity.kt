package me.daemon.verificationcode.demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log.i
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import me.daemon.verificationcode.VerificationCodeView
import me.daemon.view.common.dp2px

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val vc = findViewById<VerificationCodeView>(R.id.vc)
        val vc2 = findViewById<VerificationCodeView>(R.id.vc2)
        val vc3 = findViewById<VerificationCodeView>(R.id.vc3)
        val root = findViewById<ViewGroup>(R.id.root)

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

        vc3.listener = VerificationCodeView.Listener { view, content, isFullFilled ->
            i(
                MainActivity::class.java.name,
                "on verification code view changed 3: $view, $content, $isFullFilled"
            )
        }
        vc3.postDelayed({ vc3.requestFocus() }, 3000)
        root.setOnClickListener { vc3.clearFocus() }
    }
}
