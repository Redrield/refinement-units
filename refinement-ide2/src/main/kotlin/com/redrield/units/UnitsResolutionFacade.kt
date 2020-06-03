package com.redrield.units

import com.intellij.openapi.diagnostic.Logger
import com.jetbrains.rd.util.string.printToString
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.substitute

class UnitsResolutionFacade(private val delegate: ResolutionFacade) : ResolutionFacade by delegate {
    val log = Logger.getInstance("#refinement.resFacade")
    override fun analyze(element: KtElement, bodyResolveMode: BodyResolveMode): BindingContext {
        val default = delegate.analyze(element, bodyResolveMode)
        return object : BindingContext by default {
            override fun getType(expr: KtExpression): KotlinType? {
                val ty = default.getType(expr) ?: return null
                val name = ty.getJetTypeFqName(false)
                if(name.contains("SIUnit")) {
                    log.info("Refining numeric type")
                    val args = ty.arguments.map { tyProj -> tyProj.substitute { refineNumericType(it, moduleDescriptor) } }
                    return KotlinTypeFactory.simpleType(ty.annotations, ty.constructor, args, ty.isNullable(), null)
                } else {
                    return ty
                }
            }
        }
    }

    override fun analyzeWithAllCompilerChecks(elements: Collection<KtElement>): AnalysisResult {
        val default = delegate.analyzeWithAllCompilerChecks(elements)
        if(default.isError()) {
            log.info("ANALYZE WITH ALL COMPILER CHECKS: $elements, ${default.error}.")
        }
        return default
    }
}