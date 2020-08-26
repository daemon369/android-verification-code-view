package me.daemon.verificationcode.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class IssueRegistry : IssueRegistry() {

    override val issues: List<Issue>
        get() = VerificationCodeViewUsageDetector.issues

    override val api: Int
        get() = CURRENT_API

}