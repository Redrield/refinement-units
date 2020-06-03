package com.redrield.units

import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeProjectionImpl
import org.jetbrains.kotlin.types.typeUtil.substitute
import kotlin.math.absoluteValue

private val log = Logger.getInstance("#refinement.refiner")

fun refineNumericType(ty: KotlinType, cx: ModuleDescriptor): KotlinType {
    val fqName = ty.getJetTypeFqName(false)

    log.info("type fqName in refiner ${ty.getJetTypeFqName(true)}")

    if (fqName.contains("Pure") || fqName.contains("Neg")) {
        return ty
    }

    val args = ty.arguments.map { it.substitute { refineNumericType(it, cx) } }
        .map { it.type.getJetTypeFqName(false) to it.type.arguments[0].type.getJetTypeFqName(false) }
        .map { (wrapper, number) ->
            numericTypeToNumber(wrapper, number)
        }

    if(args.isEmpty()) return ty

    val pureEquivalent = when {
        fqName.contains("Add") -> args.sum()
        fqName.contains("Sub") -> args[0] - args[1]
        fqName.contains("Mul") -> args[0] * args[1]
        fqName.contains("Div") -> args[0] / args[1]
        else -> return ty
    }

    log.info("Trying to create refined value N$pureEquivalent")
    val numericClass =
        cx.findClassAcrossModuleDependencies(ClassId(FqName("frc.team4069.saturn.units"), FqName("N${pureEquivalent.absoluteValue}"), false))!!
    return if(pureEquivalent >= 0) {
        val pureClass = cx.findClassAcrossModuleDependencies(ClassId(FqName("frc.team4069.saturn.units"), FqName("Pure"), false))!!
        KotlinTypeFactory.simpleNotNullType(
            Annotations.EMPTY,
            pureClass,
            listOf(TypeProjectionImpl(KotlinTypeFactory.simpleNotNullType(Annotations.EMPTY, numericClass, listOf())))
        )
    } else {
        val negClass = cx.findClassAcrossModuleDependencies(ClassId(FqName("frc.team4069.saturn.units"), FqName("Neg"), false))!!
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
