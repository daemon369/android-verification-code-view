# VerificationCodeView

 [ ![Download](https://api.bintray.com/packages/daemon336699/maven/verificationcodeview/images/download.svg) ](https://bintray.com/daemon336699/maven/verificationcodeview/_latestVersion)

----

# 使用方法

## 1. 项目依赖

项目根目录下`build.gradle`中加入：

```
allprojects {
    repositories {
        ...
        jcenter()
    }
}
```

添加依赖：

```
dependencies {
    implementation 'me.daemon:verificationcodeview:x.y.z'
}
```

将其中的`x`、`y`、`z`替换为真实版本号：[ ![Download](https://api.bintray.com/packages/daemon336699/maven/verificationcodeview/images/download.svg) ](https://bintray.com/daemon336699/maven/verificationcodeview/_latestVersion)

## 2. 使用

### 1) 使用XML

```xml
<me.daemon.verificationcode.VerificationCodeView
    android:layout_width="300dp"
    android:layout_height="100dp"
    android:background="#ff0000"
    app:daemon_vc_capacity="8"
    app:daemon_vc_gridBackground="@drawable/ic_launcher_background"
    app:daemon_vc_gridDividerSize="15dp"
    app:daemon_vc_textColor="@android:color/holo_green_light"
    app:daemon_vc_textSize="30sp" />
```

### 2) 代码方式

```kotlin
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
    }
}
```

# 属性

|  XML属性                    | 说明              |
| --------------------------- | ---------------- |
| `daemon_vc_capacity`        | 验证码长度，默认为4 |
| `daemon_vc_gridBackground`  | 验证码输入框背景    |
| `daemon_vc_gridDividerSize` | 验证码输入框间隔    |
| `daemon_vc_textSize`        | 验证码文本字体大小  |
| `daemon_vc_textColor`       | 验证码文本字体颜色  |
