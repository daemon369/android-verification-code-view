package me.daemon.plugin

object Configuration {
    const val minSdkVersion = 19
    const val compileSdkVersion = 30
    const val targetSdkVersion = 29
    const val buildToolsVersion = "30.0.3"
    const val groupId = "io.github.daemon369"
    const val artifactId = "verification-code-view"
    const val version = "0.1.2"

    @Suppress("ClassName")
    object pom {
        const val name = artifactId
        const val description = "Verification code input view for android"
        const val url = "https://github.com/daemon369/VerificationCodeView"

        @Suppress("ClassName")
        object scm {
            const val connection = "scm:git:github.com/daemon369/VerificationCodeView.git"
            const val developerConnection =
                "scm:git:ssh://github.com/daemon369/VerificationCodeView.git"
            const val url = "https://github.com/daemon369/VerificationCodeView/tree/main"
        }
    }
}