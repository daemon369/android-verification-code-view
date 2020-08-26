@file:Suppress("UnstableApiUsage")

package me.daemon.verificationcode.lint

import com.android.SdkConstants.AUTO_URI
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.uast.UCallExpression
import org.w3c.dom.Element

class VerificationCodeViewUsageDetector : Detector(), Detector.XmlScanner, Detector.UastScanner {

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.LAYOUT
    }

    override fun getApplicableElements(): Collection<String>? = listOf(CLASS_NAME)

    override fun visitElement(context: XmlContext, element: Element) {
        val node = element.getAttributeNodeNS(AUTO_URI, "daemon_vc_capacity") ?: return
        val capacity = node.value.toIntOrNull()
        if (capacity == null || (capacity in 1..100).not()) context.report(
            issue = ISSUE_CAPACITY,
            location = context.getElementLocation(element),
            message = CAPACITY_MESSAGE
        )
    }

    override fun getApplicableMethodNames(): List<String>?  = listOf(
        "capacity"
    )

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        println("visitMethodCall: $context, $node, $method")
        val evaluator = context.evaluator
        if (!evaluator.isMemberInClass(method, CLASS_NAME)) return
        throw IllegalArgumentException("visitMethodCall")
    }

    override fun visitMethod(
        context: JavaContext,
        visitor: JavaElementVisitor?,
        call: PsiMethodCallExpression,
        method: PsiMethod
    ) {
        println("visitMethodCall: $context, $visitor, $call, $method")
    }

    companion object {

        const val CLASS_NAME = "me.daemon.verificationcode.VerificationCodeView"

        private const val CAPACITY_MESSAGE = "Capacity should be between 1 to 100"

        private val RESOURCE_FILE_IMPL =
            Implementation(VerificationCodeViewUsageDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        private val UAST_IMPL =
            Implementation(VerificationCodeViewUsageDetector::class.java, Scope.JAVA_FILE_SCOPE)

        val ISSUE_CAPACITY = Issue.create(
            id = "VerificationCodeViewCapacity", // Every issue has it's own unique id, also used when trying to suppress a lint
            briefDescription = CAPACITY_MESSAGE,
            explanation = CAPACITY_MESSAGE,
            category = Category.USABILITY,
            priority = 1,
            severity = Severity.WARNING,
            implementation = RESOURCE_FILE_IMPL
        )

        val issues = listOf(
            ISSUE_CAPACITY
        )
    }

}