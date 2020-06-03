package com.redrield.units

import com.intellij.ide.ApplicationInitializedListener
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.impl.ProjectLifecycleListener
import com.intellij.testFramework.registerServiceInstance
import com.intellij.util.messages.Topic
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters2
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.diagnostics.DiagnosticSuppressor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun suppressTypeInferenceExpectedTypeMismatch(diagnostic: Diagnostic): Boolean {
    return diagnostic.factory == Errors.TYPE_INFERENCE_EXPECTED_TYPE_MISMATCH || diagnostic.factory == Errors.TYPE_MISMATCH &&
            diagnostic.safeAs<DiagnosticWithParameters2<KtElement, KotlinType, KotlinType>>()
                ?.let { diagnosticWithParameters ->
                    val subType = diagnosticWithParameters.a
                    val superType = diagnosticWithParameters.b

                    subType.getJetTypeFqName(false).contains("SIUnit") || superType.getJetTypeFqName(false)
                        .contains("SIUnit")
                } == true
}

class RefinementInitListener : ApplicationInitializedListener {
    private val log = Logger.getInstance("#refinement.ideMain")
    override fun componentsInitialized() {
        log.info("Refinement componentsInitialized")
        val app = ApplicationManager.getApplication()
        log.info("Registering ProjectLifecycle topic")
        app.registerTopic(ProjectLifecycleListener.TOPIC, object : ProjectLifecycleListener {
            override fun projectComponentsInitialized(project: Project) {
                log.info("Getting KotlinCacheService")
                val srv = project.getService(KotlinCacheService::class.java)
                log.info("Registering delegate service")
                project.registerServiceInstance(KotlinCacheService::class.java, RefinementCacheService(srv))
            }
        })

        Extensions.getRootArea().getExtensionPoint(DiagnosticSuppressor.EP_NAME)
            .registerExtension(object : DiagnosticSuppressor {
                override fun isSuppressed(diagnostic: Diagnostic): Boolean {
                    return suppressTypeInferenceExpectedTypeMismatch(diagnostic)
                }
            }, LoadingOrder.ANY, app)
    }
}

fun <A> Application.registerTopic(topic: Topic<A>, listeners: A): Unit =
    messageBus.connect(this).subscribe(topic, listeners)
