package frc.team4069.saturn.typeck

import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.ktFile
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.incremental.KotlinLookupLocation
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.load.kotlin.toSourceElement
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtElementImpl
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeProjectionImpl
import org.jetbrains.kotlin.types.typeUtil.substitute

fun refineNumericType(ty: KotlinType, cx: CompilerContext): KotlinType {
    val fqName = ty.getJetTypeFqName(false)

    cx.messageCollector!!.report(CompilerMessageSeverity.WARNING, "REFINING ${ty.getJetTypeFqName(true)}")
    if (fqName.contains("Pure")) {
        return ty
    }

    val args = ty.arguments.map { it.substitute { refineNumericType(it, cx) } }
        .map { it.type.getJetTypeFqName(false) to it.type.arguments[0].type.getJetTypeFqName(false) }
        .map { (wrapper, number) ->
            numericTypeToNumber(wrapper, number)
        }

    val pureEquivalent = when {
        fqName.contains("Add") -> args.sum()
        fqName.contains("Sub") -> args[0] - args[1]
        fqName.contains("Mul") -> args[0] * args[1]
        fqName.contains("Div") -> args[0] / args[1]
        else -> throw RuntimeException("Trying to refine invalid type $fqName")
    }


    val numericClass =
        cx.module.findClassAcrossModuleDependencies(ClassId(FqName("frc.team4069.saturn.units"), FqName("N$pureEquivalent"), false))!!
    return if(pureEquivalent >= 0) {
        val pureClass = cx.module.findClassAcrossModuleDependencies(ClassId(FqName("frc.team4069.saturn.units"), FqName("Pure"), false))!!
        KotlinTypeFactory.simpleNotNullType(
            Annotations.EMPTY,
            pureClass,
            listOf(TypeProjectionImpl(KotlinTypeFactory.simpleNotNullType(Annotations.EMPTY, numericClass, listOf())))
        )
    } else {
        val negClass = cx.module.findClassAcrossModuleDependencies(ClassId(FqName("frc.team4069.saturn.units"), FqName("Neg"), false))!!
        KotlinTypeFactory.simpleNotNullType(
            Annotations.EMPTY,
            negClass,
            listOf(TypeProjectionImpl(KotlinTypeFactory.simpleNotNullType(Annotations.EMPTY, numericClass, listOf())))
        )
    }
}

fun numericTypeToNumber(wrapperName: String, n: String): Int {
    val scale = if(wrapperName.contains("Neg")) -1 else 1
    return scale * when {
        n.contains("N0") -> 0
        n.contains("N1") -> 1
        n.contains("N2") -> 2
        n.contains("N3") -> 3
        n.contains("N4") -> 4
        n.contains("N5") -> 5
        n.contains("N6") -> 6
        n.contains("N7") -> 7
        n.contains("N8") -> 8
        n.contains("N9") -> 9
        n.contains("N10") -> 10
        n.contains("N11") -> 11
        n.contains("N12") -> 12
        n.contains("N13") -> 13
        n.contains("N14") -> 14
        n.contains("N15") -> 15
        n.contains("N16") -> 16
        n.contains("N17") -> 17
        n.contains("N18") -> 18
        n.contains("N19") -> 19
        n.contains("N20") -> 20
        else -> throw RuntimeException("Invalid refined numeric type name $n")
    }
}