package frc.team4069.saturn.typeck

import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.get
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.OverridingUtil
import org.jetbrains.kotlin.resolve.calls.tower.TowerResolver
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import org.jetbrains.kotlin.types.checker.NewKotlinTypeCheckerImpl
import org.jetbrains.kotlin.types.refinement.TypeRefinement
import org.jetbrains.kotlin.types.typeUtil.replaceArgumentsWithNothing
import org.jetbrains.kotlin.types.typeUtil.replaceArgumentsWithStarProjections
import org.jetbrains.kotlin.types.typeUtil.substitute

val baselineTypeck: NewKotlinTypeChecker = NewKotlinTypeCheckerImpl(KotlinTypeRefiner.Default)

class UnitTypeck(private val ctx: CompilerContext) : NewKotlinTypeChecker {
    @TypeRefinement
    override val kotlinTypeRefiner: KotlinTypeRefiner
        get() = baselineTypeck.kotlinTypeRefiner

    @TypeRefinement
    override val overridingUtil: OverridingUtil
        get() = baselineTypeck.overridingUtil

    override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean {
        ctx.messageCollector!!.report(CompilerMessageSeverity.WARNING, "BEFORE SUB ${p1.arguments}")
        val mappedArgs = p1.arguments.map { arg ->
            val typeName = arg.type.getJetTypeFqName(false)
            ctx.messageCollector!!.report(CompilerMessageSeverity.WARNING, typeName)
            if(typeName.contains("Add") || typeName.contains("Sub") || typeName.contains("Mul") ||
                    typeName.contains("Div")) {
                arg.substitute {
                    refineNumericType(it, ctx)
                }
            } else arg
        }
        ctx.messageCollector!!.report(CompilerMessageSeverity.WARNING, "AFTER SUB $mappedArgs")
        if(baselineTypeck.equalTypes(p0.replaceArgumentsWithStarProjections(), p1.replaceArgumentsWithStarProjections())) {
            return mappedArgs.mapIndexed { i, arg -> baselineTypeck.equalTypes(arg.type, p1.arguments[i].type) }
                .all { it }
        }
        return baselineTypeck.equalTypes(p0, p1)
    }

    override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
        return baselineTypeck.isSubtypeOf(p0, p1)
    }

    override fun transformToNewType(type: UnwrappedType): UnwrappedType {
        return baselineTypeck.transformToNewType(type)
    }

}
