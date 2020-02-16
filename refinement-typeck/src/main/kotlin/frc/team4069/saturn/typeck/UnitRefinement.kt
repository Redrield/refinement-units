package frc.team4069.saturn.typeck

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters2
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs


fun CompilerContext.suppressTypeInferenceExpectedTypeMismatch(diagnostic: Diagnostic): Boolean =
    diagnostic.factory == Errors.TYPE_INFERENCE_EXPECTED_TYPE_MISMATCH &&
            diagnostic.safeAs<DiagnosticWithParameters2<KtElement, KotlinType, KotlinType>>()?.let { diagnosticWithParameters ->
                val subType = diagnosticWithParameters.a
                val superType = diagnosticWithParameters.b

                subType.getJetTypeFqName(false).contains("SIUnit") || superType.getJetTypeFqName(false).contains("SIUnit")
            } == true

val Meta.unitTypeck: Plugin
    get() = "Refinement Units Type Checker" {
        meta(
            enableIr(),
            typeChecker { UnitTypeck(this) },
            suppressDiagnostic { ctx.suppressTypeInferenceExpectedTypeMismatch(it) }
        )
    }

class UnitRefinement : Meta {
    override fun intercept(ctx: CompilerContext): List<Plugin> {
        return listOf(unitTypeck)
    }

}